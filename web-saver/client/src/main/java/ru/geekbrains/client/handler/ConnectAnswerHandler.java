package ru.geekbrains.client.handler;

import ru.geekbrains.common.message.ConnectAnswerMessage;

import java.util.UUID;

public interface ConnectAnswerHandler {
    void handleMessage(ConnectAnswerMessage message);

    void setHandshakeMessageUuid(UUID handshakeMessageUuid);

    void setAuthMessageUuid(UUID authMessageUuid);
}
