package ru.geekbrains.client.handler;

import ru.geekbrains.common.message.FileAnswer;
import ru.geekbrains.common.message.FileMessage;

public interface FileAnswerHandler {
    void handleMessage(FileAnswer message);

    void addFileDemandMessage(FileMessage message);
}
