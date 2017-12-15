package ru.geekbrains.client.handler;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.Controller;
import ru.geekbrains.common.dto.FileObjectToOperate;
import ru.geekbrains.common.dto.FileOperation;
import ru.geekbrains.common.dto.FileStatus;
import ru.geekbrains.common.message.FileAnswer;
import ru.geekbrains.common.message.FileMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileAnswerHandlerImpl implements FileAnswerHandler {
    private static final Logger LOG = LoggerFactory.getLogger(FileAnswerHandler.class);

    private final Map<UUID, FileMessage> fileOperationDemandMessages;  // сохраняем в карте запросы, чтобы понять, но что приходят ответы

    private final Controller controller;

    public FileAnswerHandlerImpl(Controller controller) {
        this.controller = controller;
        fileOperationDemandMessages = new HashMap<>();
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
            LOG.info("Ответ на file запрос: object: {}, operation: {}, answerStatus: {}, additionalMessage: {}",
                demandFileObjectToOperate, demandMessage.getFileOperation(), answerStatus, additionalMessage);
            switch (demandFileObjectToOperate) {
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
            fileOperationDemandMessages.remove(answerOnDemand);  // после обработки ответа на запрос удаляем запрос
        } else {
            LOG.info("Пришёл ответ не на наш запрос");
        }
    }

    @Override
    public void addFileDemandMessage(FileMessage message) {
        fileOperationDemandMessages.put(message.getUuid(), message);
    }
}
