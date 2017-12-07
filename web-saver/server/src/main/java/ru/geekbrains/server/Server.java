package ru.geekbrains.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.channel.MessageChannel;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.dto.AuthAnswer;
import ru.geekbrains.common.dto.AuthStatus;
import ru.geekbrains.common.dto.UsernamePassword;
import ru.geekbrains.common.message.*;
import ru.geekbrains.server.db.Database;
import ru.geekbrains.server.db.dto.User;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.CommonData.SERVER_PORT;

public class Server implements Addressee {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final int THREADS_COUNT = 5;
    private static final int MESSAGE_DELAY_MS = 100;

    private final Address address;
    // карта вида <Канал для сообщений -> соответствующий ему адрес>
    private final Map<MessageChannel, Address> connectionMap;
    private final ExecutorService executor;

    public Server() {
        executor = Executors.newFixedThreadPool(THREADS_COUNT);
        connectionMap = new HashMap<>();
        address = SERVER_ADDRESS;
    }

    public static void main(String[] args) {
        try {
            new Server().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        executor.submit(this::handshake);
        executor.submit(this::authentification);

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
    private void handshake() {
        LOG.info("Начат цикл приёма адресов клиентов на сервере (handshake)...");
        while (true) {
            for (Map.Entry<MessageChannel, Address> client : connectionMap.entrySet()) {
                MessageChannel clientChannel = client.getKey();
                Address clientAddress = client.getValue();
                if (clientAddress == null) {
                    Message message = clientChannel.poll();
                    if (message != null) {
                        if (message.isClass(HandshakeDemandMessage.class)) {
                            clientAddress = message.getFrom();
                            LOG.info("Получен запрос на установление соединения от: " + clientAddress + ", " + message);
                            connectionMap.put(clientChannel, clientAddress);
                            Message handshakeAnswerMessage = new HandshakeAnswerMessage(this.address, clientAddress);
                            clientChannel.send(handshakeAnswerMessage);
                            LOG.info("Направлен ответ об успешном установлении соединения клиенту: " + clientAddress + ", " + handshakeAnswerMessage);
                        }
                    }
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(MESSAGE_DELAY_MS);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    private void authentification() {
        try {
            LOG.info("Цикл аутентификации клиентов на сервере");
            while (true) {
                for (Map.Entry<MessageChannel, Address> entry : connectionMap.entrySet()) {
                    MessageChannel clientChannel = entry.getKey();
                    Address clientAddress = entry.getValue();
                    // если соединение с этим клиентом уже было ранее установлено
                    if (clientAddress != null) {
                        Message message = clientChannel.poll();
                        if (message != null) {
                            if (message.isClass(AuthDemandMessage.class)) {
                                UsernamePassword usernamePassword = AuthDemandMessage.deserializeUsernamePassword(message.getPayload());
                                String username = usernamePassword.getUsername();
                                String password = usernamePassword.getPassword();
                                User user = new User(username, password);
                                boolean isUserExists = Database.checkUserExistence(user);
                                AuthAnswer authAnswer;
                                if (!isUserExists) {
                                    authAnswer = new AuthAnswer(AuthStatus.INCORRECT_USERNAME, usernamePassword, "");
                                } else {
                                    boolean isAuthentificate = Database.checkUserAuthentification(user);
                                    if (!isAuthentificate) {
                                        authAnswer = new AuthAnswer(AuthStatus.INCORRECT_PASSWORD, usernamePassword, "");
                                    } else {
                                        authAnswer = new AuthAnswer(AuthStatus.AUTH_OK, usernamePassword, "");
                                    }
                                }
                                AuthAnswerMessage authAnswerMessage = new AuthAnswerMessage(
                                    SERVER_ADDRESS, clientAddress, AuthAnswerMessage.serializeAuthAnswer(authAnswer));
                                clientChannel.send(authAnswerMessage);
                                LOG.info("Направлен ответ об аутентификации клиенту: " + clientAddress + ", " + authAnswerMessage);
                            }
                        }
                    }
                }
                TimeUnit.MILLISECONDS.sleep(MESSAGE_DELAY_MS);
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    // Находим по адресу соответствующий ему канал
    private MessageChannel getChannelByAddress(Address address) {
        for (Map.Entry<MessageChannel, Address> entry : connectionMap.entrySet()) {
            if (entry.getValue().equals(address)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Address getAddress() {
        return address;
    }
}