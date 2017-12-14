package ru.geekbrains.client;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.channel.SocketClientManagedChannel;
import ru.geekbrains.common.dto.*;
import ru.geekbrains.common.message.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.CommonData.SERVER_PORT;
import static ru.geekbrains.common.dto.ConnectStatus.*;

public class Model implements Addressee {
    private static final Logger LOG = LoggerFactory.getLogger(Model.class);

    private static final String HOST = "localhost";

    private static final int PAUSE_MS = 223;
    private static final int THREADS_NUMBER = 1;

    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUMBER);

    // private final CountDownLatch handshakeLatch = new CountDownLatch(1);   // блокировка до установления соединения с сервером
    // private final CountDownLatch authLatch = new CountDownLatch(1);        // блокировка до аутентификации клиента на сервере
    // private final CountDownLatch disconnectLatch = new CountDownLatch(1);  // блокировка до отключения от сервера

    private UUID handshakeMessageUUID;
    private UUID authMessageUUID;
    private final Map<UUID, FileMessage> fileOperationDemandMessages = new HashMap<>();  // сохраняем запросы, чтобы понять, но что приходят ответы

    private SocketClientChannel client;

    private final Controller controller;

    private final Address address;

    public Model(Address address, Controller controller) {
        this.address = address;
        this.controller = controller;
    }

    public void start() {
        LOG.info("Client process started");

        try {
            client = new SocketClientManagedChannel(HOST, SERVER_PORT);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        client.init();

        executor.submit(this::serverMessageHandle);
    }

    public void stop() throws InterruptedException, IOException {
        // disconnectLatch.await();

        client.close();
        executor.shutdown();
    }

    public void handshakeOnServer() {
        Message handshakeDemandMessage = new ConnectOperationMessage(this.address, SERVER_ADDRESS, ConnectOperation.HANDSHAKE, null);
        handshakeMessageUUID = handshakeDemandMessage.getUuid();
        client.send(handshakeDemandMessage);
        LOG.debug("Послано сообщение об установлении соединения на сервер");
        /*try {
            handshakeLatch.await();  // ждём handshake-ответа от сервера
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }*/
    }

    public void authOnServer(String username, String password) {
        Message authDemandMessage = new ConnectOperationMessage(this.address, SERVER_ADDRESS, ConnectOperation.AUTH, new UserDTO(username, password));
        authMessageUUID = authDemandMessage.getUuid();
        client.send(authDemandMessage);
        LOG.debug("Послано сообщение об аутентификации на сервер");
        /*try {
            authLatch.await();
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }*/
    }

    public void createClientFolder() {
        FileMessage createFolderDemandMessage = new FileMessage(this.address, SERVER_ADDRESS, FileObjectToOperate.FOLDER, FileOperation.CREATE, null);
        fileOperationDemandMessages.put(createFolderDemandMessage.getUuid(), createFolderDemandMessage);
        client.send(createFolderDemandMessage);
        LOG.debug("Послан запрос на создание папки пользователя на сервер");
    }

    // Обработка ответов от сервера
    private void serverMessageHandle() {
        try {
            while (true) {
                Message serverMessage = client.take();
                if (serverMessage != null) {
                    if (serverMessage.isClass(ConnectAnswerMessage.class)) {
                        handleConnectAnswer(serverMessage);
                    } else if (serverMessage.isClass(FileAnswer.class)) {
                        handleFileAnswer(serverMessage);
                    } else {
                        LOG.debug("Получено сообщение необрабатываемого класса. Message: {}", serverMessage);
                    }
                } else {
                    TimeUnit.MILLISECONDS.sleep(PAUSE_MS);
                }
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    private void handleConnectAnswer(Message serverMessage) {
        ConnectAnswerMessage connectAnswerMessage = (ConnectAnswerMessage) serverMessage;
        UUID toMessageUuid = connectAnswerMessage.getToMessage();  // по uuid проверяем, что это ответ именно нам

        if (toMessageUuid.equals(handshakeMessageUUID)) {
            if (connectAnswerMessage.getConnectStatus().equals(ConnectStatus.HANDSHAKE_OK)) {
                LOG.info("Получен ответ об установлении связи от сервера");
                Platform.runLater(() -> controller.writeLogInTerminal("Установлено соединение с сервером"));
                // handshakeLatch.countDown();  // Отпускаем блокировку
            } else {
                LOG.info("Получен ответ, но не HANSHAKE_OK. Message: {}", serverMessage);
            }
        } else if (toMessageUuid.equals(authMessageUUID)) {
            LOG.info("Получен ответ об аутентификации от сервера");
            ConnectStatus connectStatus = connectAnswerMessage.getConnectStatus();
            if (connectStatus.equals(AUTH_OK)) {
                LOG.info("Успешная аутентификация");
                Platform.runLater(() -> {
                    controller.writeLogInTerminal("Успешная аутентификация на сервере");
                    controller.setAuthentificate(true);
                });
                // authLatch.countDown();  // Отпускаем блокировку
            } else if (connectStatus.equals(INCORRECT_USERNAME)) {
                LOG.info("Неправильное имя пользователя");
                Platform.runLater(() -> controller.writeLogInTerminal("Аутентификация: неправильное имя пользователя"));
            } else if (connectStatus.equals(INCORRECT_PASSWORD)) {
                LOG.info("Неправильный пароль");
                Platform.runLater(() -> controller.writeLogInTerminal("Аутентификация: неправильный пароль"));
            } else if (connectStatus.equals(ALREADY_AUTH)) {
                LOG.info("Клиент уже аутентифицирован на сервере");
                Platform.runLater(() -> controller.writeLogInTerminal("Аутентификация: клиент уже аутентифицирован на сервере"));
            } else {
                LOG.info("Непонятный ответ об аутентификации. Message: {}", serverMessage);
            }
        } else {
            LOG.info("Пришёл ответ от сервера с UUID не в ответ на наше сообщение! Message: {}", serverMessage);
        }
    }

    private void handleFileAnswer(Message serverMessage) {
        LOG.info("Получен ответ о файловой операции от сервера");
        FileAnswer fileAnswer = (FileAnswer) serverMessage;

        UUID answerOnDemand = fileAnswer.getToMessage();
        if (fileOperationDemandMessages.containsKey(answerOnDemand)) {
            FileMessage demandMessage = fileOperationDemandMessages.get(answerOnDemand);
            FileOperation demandFileOperation = demandMessage.getFileOperation();
            FileStatus answerStatus = fileAnswer.getFileStatus();
            String additionalMessage = fileAnswer.getAdditionalMessage();
            LOG.info("Ответ на запрос: object: {}, operation: {}, answerStatus: {}, additionalMessage: {}",
                demandMessage.getFileObjectToOperate(), demandMessage.getFileOperation(), answerStatus, additionalMessage);
            switch (demandMessage.getFileObjectToOperate()) {
                case FOLDER:
                    if (demandFileOperation.equals(FileOperation.CREATE)) {
                        switch (answerStatus) {
                            case OK:
                                Platform.runLater(() -> controller.writeLogInTerminal("Создание папки: ОК"));
                                break;
                            case ERROR:
                                Platform.runLater(() -> controller.writeLogInTerminal("Создание папки: Error; additionalMessage: " + additionalMessage));
                                break;
                            case NOT_AUTH:
                                Platform.runLater(() -> controller.writeLogInTerminal("Создание папки: пользователь не авторизован"));
                                break;
                        }
                    }
                    break;
                case FILE:
                    break;
            }
        } else {
            LOG.info("Пришёл ответ не на наш запрос");
        }

        fileAnswer.getFileStatus();
        System.out.println(fileAnswer.getFileStatus() + " : " + fileAnswer.getAdditionalMessage());
    }

    @Override
    public Address getAddress() {
        return address;
    }
}
