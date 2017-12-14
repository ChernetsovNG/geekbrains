package ru.geekbrains.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.channel.MessageChannel;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.dto.ConnectOperation;
import ru.geekbrains.common.dto.ConnectStatus;
import ru.geekbrains.common.dto.UserDTO;
import ru.geekbrains.common.message.*;
import ru.geekbrains.server.db.Database;
import ru.geekbrains.server.operation.FileOperationHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.CommonData.SERVER_PORT;
import static ru.geekbrains.server.db.Database.createServerDB;

public class Server implements Addressee {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final int THREADS_COUNT = 4;
    private static final int MESSAGE_DELAY_MS = 100;

    private final Address address;

    private final Map<MessageChannel, Address> connectionMap;  // карта вида <Канал для сообщений -> соответствующий ему адрес>

    private final ExecutorService executor;
    private final FileOperationHandler fileOperationHandler;

    public Server() {
        executor = Executors.newFixedThreadPool(THREADS_COUNT);

        connectionMap = new HashMap<>();

        address = SERVER_ADDRESS;
        fileOperationHandler = new FileOperationHandler();
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        createServerDB();

        executor.submit(this::connectMessageHandle);
        executor.submit(this::fileMessageHandle);

        // Ждём подключения клиентов к серверу. Для подключённых клиентов создаём каналы для связи
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            LOG.info("Server started on port: " + serverSocket.getLocalPort());

            while (!executor.isShutdown()) {
                Socket client = serverSocket.accept();  // blocks

                LOG.info("Client connect: " + client);

                SocketClientChannel channel = new SocketClientChannel(client);
                channel.init();
                channel.addShutdownRegistration(() -> connectionMap.remove(channel));
                connectionMap.put(channel, null);
            }
        }
    }

    // Принимаем идентифицирующее сообщение ("рукопожатие") и сохраняем в карте соответствующий адрес
    private void connectMessageHandle() {
        try {
            LOG.info("Начат цикл обработки соединений клиентов");
            while (true) {
                for (Map.Entry<MessageChannel, Address> client : connectionMap.entrySet()) {
                    MessageChannel clientChannel = client.getKey();
                    Address clientAddress = client.getValue();
                    if (clientAddress == null) {
                        Message message = clientChannel.poll();
                        if (message != null) {
                            if (message.isClass(ConnectOperationMessage.class)) {
                                ConnectOperationMessage connectOperationMessage = (ConnectOperationMessage) message;
                                ConnectOperation connectOperation = connectOperationMessage.getConnectOperation();
                                clientAddress = connectOperationMessage.getFrom();
                                if (connectOperation.equals(ConnectOperation.HANDSHAKE)) {
                                    LOG.info("Получен запрос на установление соединения от: " + clientAddress + ", " + message);
                                    connectionMap.put(clientChannel, clientAddress);
                                    ConnectAnswerMessage handshakeAnswerMessage = new ConnectAnswerMessage(this.address, clientAddress, connectOperationMessage.getUuid(), ConnectStatus.HANDSHAKE_OK);
                                    clientChannel.send(handshakeAnswerMessage);
                                    LOG.info("Направлен ответ об успешном установлении соединения клиенту: " + clientAddress + ", " + handshakeAnswerMessage);
                                } else if (connectOperation.equals(ConnectOperation.AUTH)) {
                                    handleAuthDemandMessage(clientAddress, clientChannel, connectOperationMessage);
                                } else if (connectOperation.equals(ConnectOperation.DISCONNECT)) {
                                    LOG.info("Сообщение об отключении клиента: " + clientAddress + ", " + message);
                                    connectionMap.remove(clientChannel);
                                    fileOperationHandler.removeAuthClient(clientChannel);
                                    clientChannel.close();
                                }
                            }
                        }
                    }
                }
                TimeUnit.MILLISECONDS.sleep(MESSAGE_DELAY_MS);
            }
        } catch (InterruptedException | IOException e) {
            LOG.error(e.getMessage());
        }
    }

    // Обработка запроса на аутентификацию
    private void handleAuthDemandMessage(Address clientAddress, MessageChannel clientChannel, ConnectOperationMessage authDemandMessage) {
        UserDTO userDTO = (UserDTO) authDemandMessage.getAdditionalObject();
        LOG.info("Получен запрос на аутентификацию от: " + authDemandMessage.getFrom() + ", " + userDTO);
        ConnectStatus authStatus = Database.getAuthStatus(userDTO);
        ConnectAnswerMessage authAnswerMessage = new ConnectAnswerMessage(SERVER_ADDRESS, clientAddress, authDemandMessage.getUuid(), authStatus);
        if (authStatus.equals(ConnectStatus.AUTH_OK)) {
            fileOperationHandler.addAuthClient(clientChannel, userDTO.getName());  // сохраняем в карте авторизованного пользователя
        }
        clientChannel.send(authAnswerMessage);
        LOG.info("Направлен ответ об аутентификации клиенту: " + clientAddress + ", " + authAnswerMessage);
    }

    private void fileMessageHandle() {
        try {
            LOG.info("Цикл приёма сообщений о работе с файлами от клиентов");
            while (true) {
                for (Map.Entry<MessageChannel, Address> entry : connectionMap.entrySet()) {
                    MessageChannel clientChannel = entry.getKey();
                    Address clientAddress = entry.getValue();
                    // если соединение с этим клиентом уже было ранее установлено
                    if (clientAddress != null) {
                        Message message = clientChannel.poll();
                        if (message != null && message.isClass(FileMessage.class)) {
                            fileOperationHandler.handleFileMessage(clientAddress, clientChannel, (FileMessage) message);
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
