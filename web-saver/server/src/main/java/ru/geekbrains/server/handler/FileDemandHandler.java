package ru.geekbrains.server.handler;

import ru.geekbrains.common.channel.MessageChannel;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.FileMessage;

public interface FileDemandHandler {
    void handleFileDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage message);
}
