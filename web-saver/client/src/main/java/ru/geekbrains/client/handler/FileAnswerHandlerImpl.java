package ru.geekbrains.client.handler;

import javafx.application.Platform;
import kotlin.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.Controller;
import ru.geekbrains.common.dto.*;
import ru.geekbrains.common.message.FileAnswer;
import ru.geekbrains.common.message.FileMessage;
import ru.geekbrains.common.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileAnswerHandlerImpl implements FileAnswerHandler {
    private static final Logger LOG = LoggerFactory.getLogger(FileAnswerHandler.class);

    private final Map<UUID, FileMessage> fileOperationDemandMessages;  // сохраняем в карте запросы, чтобы понять, но что приходят ответы

    // для скачивания файлов надо сохранить ещё и папку, куда записывать пришедший от сервера ответ
    private final Map<UUID, Pair<FileMessage, File>> fileDownloadDemandMessages;

    private final Controller controller;

    public FileAnswerHandlerImpl(Controller controller) {
        this.controller = controller;
        fileOperationDemandMessages = new HashMap<>();
        fileDownloadDemandMessages = new HashMap<>();
    }

    @Override
    public void handleMessage(FileAnswer message) {
        LOG.info("Получен ответ о файловой операции от сервера");
        UUID answerOnDemand = message.getToMessage();
        if (fileOperationDemandMessages.containsKey(answerOnDemand)) {
            FileMessage demandMessage = fileOperationDemandMessages.get(answerOnDemand);
            FileObjectToOperate demandFileObjectToOperate = demandMessage.getFileObjectToOperate();
            FileOperation demandFileOperation = demandMessage.getFileOperation();
            FileStatus answerStatus = message.getFileStatus();
            String additionalMessage = message.getAdditionalMessage();
            Object additionalObject = message.getAdditionalObject();
            LOG.info("Ответ на file запрос: object: {}, operation: {}, answerStatus: {}, additionalMessage: {}",
                demandFileObjectToOperate, demandMessage.getFileOperation(), answerStatus, additionalMessage);
            switch (demandFileObjectToOperate) {
                case FOLDER:
                    handleFolderAnswer(demandFileOperation, answerStatus, additionalMessage);
                    break;
                case FILE:
                    handleFileAnswer(demandFileOperation, answerStatus, additionalMessage, additionalObject);
                    break;
            }
            fileOperationDemandMessages.remove(answerOnDemand);  // после обработки ответа на запрос удаляем запрос
        } else if (fileDownloadDemandMessages.containsKey(answerOnDemand)) {
            Pair<FileMessage, File> demandMessageAndDirectory = fileDownloadDemandMessages.get(answerOnDemand);
            FileMessage demandMessage = demandMessageAndDirectory.getFirst();
            FileDTO demandFileDTO = (FileDTO) demandMessage.getAdditionalObject();
            String downloadFileName = demandFileDTO.getFileName();
            File directoryToSave = demandMessageAndDirectory.getSecond();
            FileObjectToOperate demandFileObjectToOperate = demandMessage.getFileObjectToOperate();
            FileOperation demandFileOperation = demandMessage.getFileOperation();
            FileStatus answerStatus = message.getFileStatus();
            String additionalMessage = message.getAdditionalMessage();
            Object additionalObject = message.getAdditionalObject();
            LOG.info("Ответ на file запрос: object: {}, operation: {}, answerStatus: {}, additionalMessage: {}",
                demandFileObjectToOperate, demandMessage.getFileOperation(), answerStatus, additionalMessage);
            if (demandFileObjectToOperate.equals(FileObjectToOperate.FILE)) {
                handleDownloadFileAnswer(downloadFileName, directoryToSave, answerStatus, additionalMessage, additionalObject);
                fileDownloadDemandMessages.remove(answerOnDemand);
            } else {
                LOG.error("Скачивать можно только файлы. demandMessage: {}", demandMessage);
            }
        } else {
            LOG.info("Пришёл ответ не на наш запрос");
        }
    }

    private void handleFolderAnswer(FileOperation demandFileOperation, FileStatus answerStatus, String additionalMessage) {
        if (answerStatus.equals(FileStatus.NOT_AUTH)) {
            Platform.runLater(() -> controller.writeLogInTerminal("Операция с папкой: пользователь не авторизован"));
        } else {
            if (answerStatus.equals(FileStatus.OK)) {
                switch (demandFileOperation) {
                    case CREATE:
                        Platform.runLater(() -> controller.writeLogInTerminal("Создание папки: ОК"));
                        break;
                }
            } else if (answerStatus.equals(FileStatus.ALREADY_EXISTS)) {
                switch (demandFileOperation) {
                    case CREATE:
                        Platform.runLater(() -> {
                            controller.getFileList();  // получаем список файлов из папки
                            controller.writeLogInTerminal("Создание папки: Папка уже существует");
                        });
                        break;
                }
            } else if (answerStatus.equals(FileStatus.ERROR)) {
                switch (demandFileOperation) {
                    case CREATE:
                        Platform.runLater(() -> controller.writeLogInTerminal("Создание папки: Error; additionalMessage: " + additionalMessage));
                        break;
                }
            }
        }
    }

    private void handleFileAnswer(FileOperation demandFileOperation, FileStatus answerStatus, String additionalMessage, Object additionalObject) {
        if (answerStatus.equals(FileStatus.NOT_AUTH)) {
            Platform.runLater(() -> controller.writeLogInTerminal("Операция с файлами: пользователь не авторизован"));
        } else {
            if (answerStatus.equals(FileStatus.OK)) {
                switch (demandFileOperation) {
                    case CREATE:
                        Platform.runLater(() -> {
                            controller.getFileList();  // после создания нового файла обновляем таблицу со списком файлов
                            controller.writeLogInTerminal("Создание нового файла: ОК");
                        });
                        break;
                    case GET_LIST:
                        List<FileInfo> fileInfoList = (List<FileInfo>) additionalObject;
                        Platform.runLater(() -> controller.writeFileListInTable(fileInfoList));
                        break;
                    case DELETE:
                        Platform.runLater(() -> {
                            controller.getFileList();  // после удаления файла обновляем таблицу со списком файлов
                            controller.writeLogInTerminal("Удаление файла: ОК. " + additionalMessage);
                        });
                        break;
                }
            } else if (answerStatus.equals(FileStatus.ERROR)) {
                switch (demandFileOperation) {
                    case CREATE:
                        Platform.runLater(() -> controller.writeLogInTerminal("Создание нового файла: Error; additionalMessage: " + additionalMessage));
                        break;
                    case DELETE:
                        Platform.runLater(() -> controller.writeLogInTerminal("Удаление файла: Error. additionalMessage: " + additionalMessage));
                        break;
                }
            }
        }
    }

    private void handleDownloadFileAnswer(String fileName, File directoryToSave, FileStatus answerStatus, String additionalMessage, Object additionalObject) {
        if (answerStatus.equals(FileStatus.NOT_AUTH)) {
            Platform.runLater(() -> controller.writeLogInTerminal("Операция с файлами: пользователь не авторизован"));
        } else {
            if (answerStatus.equals(FileStatus.OK)) {
                byte[] payload = (byte[]) additionalObject;
                FileUtils.createNewOrUpdateFile(directoryToSave.getAbsolutePath(), fileName, payload);
                Platform.runLater(() -> controller.writeLogInTerminal("Скачивание файла: ОК. Файл: " + fileName));
            } else if (answerStatus.equals(FileStatus.ERROR)) {
                Platform.runLater(() -> controller.writeLogInTerminal("Скачивание файла: Error; additionalMessage: " + additionalMessage));
            }
        }
    }

    @Override
    public void addFileDemandMessage(FileMessage message) {
        fileOperationDemandMessages.put(message.getUuid(), message);
    }

    @Override
    public void addDownloadFileMessage(FileMessage message, File directoryToSave) {
        fileDownloadDemandMessages.put(message.getUuid(), new Pair<>(message, directoryToSave));
    }
}
