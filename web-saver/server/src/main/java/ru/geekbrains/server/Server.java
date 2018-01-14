package ru.geekbrains.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.channel.MessageChannel;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.message.*;
import ru.geekbrains.server.handler.ConnectDemandHandler;
import ru.geekbrains.server.handler.ConnectDemandHandlerImpl;
import ru.geekbrains.server.handler.FileDemandHandler;
import ru.geekbrains.server.handler.FileDemandHandlerImpl;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.CommonData.SERVER_PORT;
import static ru.geekbrains.server.db.Database.createServerDB;

public class Server implements Addressee {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final int THREADS_COUNT = 1;
    private static final int MESSAGE_DELAY_MS = 117;

    private final Address address;

    private final ExecutorService executor;

    private final FileDemandHandler fileDemandHandler;
    private final ConnectDemandHandler connectDemandHandler;

    public Server() {
        executor = Executors.newFixedThreadPool(THREADS_COUNT);

        address = SERVER_ADDRESS;

        connectDemandHandler = new ConnectDemandHandlerImpl();
        fileDemandHandler = new FileDemandHandlerImpl(connectDemandHandler);
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            String pathToDB = "server/src/main/java/ru/geekbrains/server/db/data.db";
            server.start(pathToDB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(String pathToDB) throws Exception {
        createServerDB(pathToDB);

        executor.submit(this::clientMessageHandle);

        // Ждём подключения клиентов к серверу. Для подключённых клиентов создаём каналы для связи
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            LOG.info("Server started on port: " + serverSocket.getLocalPort());

            while (!executor.isShutdown()) {
                Socket client = serverSocket.accept();  // blocks

                LOG.info("Client connect: " + client);

                SocketClientChannel channel = new SocketClientChannel(client);
                channel.init();
                channel.addShutdownRegistration(() -> connectDemandHandler.removeClientChannel(channel));

                connectDemandHandler.addNewClientChannel(channel);
            }
        }
    }

    public void stop() {
        executor.shutdownNow();
    }

    // Обработка сообщений о соединении и аутентификации клиентов
    private void clientMessageHandle() {
        try {
            LOG.info("Начат цикл обработки соединений клиентов");
            while (true) {
                for (Map.Entry<MessageChannel, Address> client : connectDemandHandler.getClientAddressMap().entrySet()) {
                    MessageChannel clientChannel = client.getKey();
                    Address clientAddress = client.getValue();
                    Message message = clientChannel.poll();
                    if (message != null) {
                        if (message.isClass(ConnectOperationMessage.class)) {
                            connectDemandHandler.handleConnectDemand(clientAddress, clientChannel, (ConnectOperationMessage) message);
                        } else if (message.isClass(FileMessage.class)) {
                            fileDemandHandler.handleFileDemandMessage(clientAddress, clientChannel, (FileMessage) message);
                        } else {
                            LOG.warn("От клиента получено сообщение необрабатываемог класса. Message: {}", message);
                        }
                    }
                }
                TimeUnit.MILLISECONDS.sleep(MESSAGE_DELAY_MS);
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public Address getAddress() {
        return address;
    }
}
