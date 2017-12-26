package ru.geekbrains.client.handler;

import ru.geekbrains.common.message.FileAnswer;
import ru.geekbrains.common.message.FileMessage;

import java.io.File;

public interface FileAnswerHandler {
    void handleMessage(FileAnswer message);

    void addFileDemandMessage(FileMessage message);

    void addDownloadFileMessage(FileMessage message, File directoryToSave);
}
