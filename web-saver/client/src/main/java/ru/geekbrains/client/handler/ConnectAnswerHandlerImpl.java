package ru.geekbrains.client.handler;

import javafx.application.Platform;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.Controller;
import ru.geekbrains.common.dto.ConnectStatus;
import ru.geekbrains.common.message.ConnectAnswerMessage;

import java.util.UUID;

import static ru.geekbrains.common.dto.ConnectStatus.*;

public class ConnectAnswerHandlerImpl implements ConnectAnswerHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectAnswerHandler.class);

    @Setter
    private UUID handshakeMessageUuid;
    @Setter
    private UUID authMessageUuid;

    private final Controller controller;

    public ConnectAnswerHandlerImpl(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handleMessage(ConnectAnswerMessage message) {
        UUID toMessageUuid = message.getToMessage();  // по uuid проверяем, что это ответ именно на наш запрос
        if (toMessageUuid.equals(handshakeMessageUuid)) {
            handleHandshakeAnswer(message);
        } else if (toMessageUuid.equals(authMessageUuid)) {
            handleAuthAnswer(message);
        } else {
            LOG.info("Пришёл ответ от сервера с UUID не в ответ на наше сообщение! Message: {}", message);
        }
    }

    private void handleHandshakeAnswer(ConnectAnswerMessage connectAnswerMessage) {
        if (connectAnswerMessage.getConnectStatus().equals(ConnectStatus.HANDSHAKE_OK)) {
            LOG.info("Получен ответ об установлении связи от сервера");
            Platform.runLater(() -> controller.writeLogInTerminal("Установлено соединение с сервером"));
        } else {
            LOG.info("Получен ответ, но не HANDSHAKE_OK. Message: {}", connectAnswerMessage);
        }
    }

    private void handleAuthAnswer(ConnectAnswerMessage connectAnswerMessage) {
        LOG.info("Получен ответ об аутентификации от сервера");
        ConnectStatus connectStatus = connectAnswerMessage.getConnectStatus();
        if (connectStatus.equals(AUTH_OK)) {
            LOG.info("Успешная аутентификация");
            Platform.runLater(() -> {
                controller.writeLogInTerminal("Успешная аутентификация на сервере");
                controller.setAuthentificate(true);
            });
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
            LOG.info("Аутентификация: непонятный ответ!. Message: {}", connectAnswerMessage);
        }
    }
}
