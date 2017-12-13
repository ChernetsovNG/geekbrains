package ru.geekbrains.server.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.channel.MessageChannel;
import ru.geekbrains.common.dto.FileDTO;
import ru.geekbrains.common.dto.FileObject;
import ru.geekbrains.common.dto.FileOperation;
import ru.geekbrains.common.dto.FileStatus;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.FileAnswer;
import ru.geekbrains.common.message.FileMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.geekbrains.common.CommonData.CLIENTS_FOLDERS_PATH;
import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.server.utils.FileUtils.*;

// Класс для работы с файлами клиентов на сервере
public class FileOperationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(FileOperationHandler.class);

    private final Map<MessageChannel, String> authMap;  // карта вида <Канал -> имя авторизованного пользователя>

    public FileOperationHandler() {
        authMap = new HashMap<>();
    }

    // сохраняем в карте авторизованного пользователя
    public void addAuthClient(MessageChannel clientChannel, String userName) {
        authMap.put(clientChannel, userName);
    }

    public void removeAuthClient(MessageChannel clientChannel) {
        authMap.remove(clientChannel);
    }

    public void handleFileMessage(Address clientAddress, MessageChannel clientChannel, FileMessage message) {
        // вначале проверяем аутентификацию
        if (!authMap.containsKey(clientChannel)) {
            FileAnswer createFolderAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, message.getUuid(), FileStatus.NOT_AUTH, "Пользователь не аутентифицирован", null);
            clientChannel.send(createFolderAnswerMessage);
            return;
        }
        FileObject fileObject = message.getFileObject();
        FileOperation fileOperation = message.getFileOperation();
        switch (fileObject) {
            case FOLDER:
                handleFolderOperation(clientAddress, clientChannel, message, fileOperation);
                break;
            case FILE:
                handleFileOperation(clientAddress, clientChannel, message, fileOperation);
                break;
        }
    }

    private void handleFolderOperation(Address clientAddress, MessageChannel clientChannel, FileMessage message, FileOperation fileOperation) {
        switch (fileOperation) {
            case CREATE:
                LOG.info("Запрос на создание папки: " + clientAddress + ", " + message);
                handleCreateFolderDemandMessage(clientAddress, clientChannel, message);
                break;
        }
    }

    private void handleFileOperation(Address clientAddress, MessageChannel clientChannel, FileMessage message, FileOperation fileOperation) {
        switch (fileOperation) {
            case CREATE:
                LOG.info("Запрос на создание нового файла: " + clientAddress + ", " + message);
                handleCreateNewFileDemandMessage(clientAddress, clientChannel, message);
                break;
            case READ:
                LOG.info("Запрос содержимого файла: " + clientAddress + ", " + message);
                handleGetFilePayloadDemandMessage(clientAddress, clientChannel, message);
                break;
            case DELETE:
                LOG.info("Запрос на удаление файла: " + clientAddress + ", " + message);
                handleDeleteFileDemandMessage(clientAddress, clientChannel, message);
                break;
            case GET_LIST:
                LOG.info("Запрос списка файлов: " + clientAddress + ", " + message);
                handleGetFileListDemandMessage(clientAddress, clientChannel, message);
                break;
        }
    }

    private void handleCreateFolderDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus = FileStatus.ERROR;
        String additionalMessage = null;

        String folderPath = getClientFolderPath(clientChannel);
        if (isFolderExists(folderPath)) {
            fileStatus = FileStatus.ERROR;
            additionalMessage = "Папка уже существует";
        } else {
            try {
                Files.createDirectories(Paths.get(folderPath));
                fileStatus = FileStatus.OK;
                additionalMessage = null;
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }

        FileAnswer createFolderAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, fileMessage.getUuid(), fileStatus, additionalMessage, null);
        clientChannel.send(createFolderAnswerMessage);
    }

    private void handleCreateNewFileDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus;
        String additionalMessage;

        String folderPath = getClientFolderPath(clientChannel);
        FileDTO fileDTO = (FileDTO) fileMessage.getAdditionalObject();
        String fileName = fileDTO.getFileName();
        if (isFileExists(folderPath, fileName)) {
            fileStatus = FileStatus.ERROR;
            additionalMessage = "Файл уже существует";
        } else {
            boolean isFileCreate = createNewFile(folderPath, fileName, fileDTO.getPayload());
            if (isFileCreate) {
                fileStatus = FileStatus.OK;
                additionalMessage = null;
            } else {
                fileStatus = FileStatus.ERROR;
                additionalMessage = "Ошибка при создании файла";
            }
        }

        FileAnswer createNewFileAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, fileMessage.getUuid(), fileStatus, additionalMessage, null);
        clientChannel.send(createNewFileAnswerMessage);
    }

    private void handleGetFileListDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus;
        String additionalMessage;
        List<String> fileNamesList = Collections.emptyList();

        String folderPath = getClientFolderPath(clientChannel);
        List<String> filesList = getFileList(folderPath);
        if (filesList != null) {
            fileStatus = FileStatus.OK;
            additionalMessage = null;
            fileNamesList = filesList;
        } else {
            fileStatus = FileStatus.ERROR;
            additionalMessage = "Ошибка чтения списка файлов";
        }

        FileAnswer getFileListAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, fileMessage.getUuid(), fileStatus, additionalMessage, fileNamesList);
        clientChannel.send(getFileListAnswerMessage);
    }

    private void handleDeleteFileDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus = FileStatus.ERROR;
        String additionalMessage = null;

        String folderPath = getClientFolderPath(clientChannel);
        FileDTO fileDTO = (FileDTO) fileMessage.getAdditionalObject();
        try {
            Files.delete(Paths.get(folderPath, fileDTO.getFileName()));
            fileStatus = FileStatus.OK;
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        FileAnswer deleteFileAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, fileMessage.getUuid(), fileStatus, additionalMessage, null);
        clientChannel.send(deleteFileAnswerMessage);
    }

    private void handleGetFilePayloadDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus = FileStatus.ERROR;
        String additionalMessage = null;
        byte[] filePayload = null;

        String folderPath = getClientFolderPath(clientChannel);
        FileDTO fileDTO = (FileDTO) fileMessage.getAdditionalObject();
        String fileName = fileDTO.getFileName();
        if (!isFileExists(folderPath, fileName)) {
            fileStatus = FileStatus.ERROR;
            additionalMessage = "Файл не найден";
        } else {
            try {
                filePayload = Files.readAllBytes(Paths.get(folderPath, fileName));
                fileStatus = FileStatus.OK;
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        FileAnswer getFilePayloadAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, fileMessage.getUuid(), fileStatus, additionalMessage, filePayload);
        clientChannel.send(getFilePayloadAnswerMessage);
    }

    private String getClientFolderPath(MessageChannel clientChannel) {
        String folderName = authMap.get(clientChannel);  // имя папки на сервере равно имени клиента (по соглашению)
        return CLIENTS_FOLDERS_PATH + folderName;
    }
}
