package ru.geekbrains.client;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.controller.ConnectController;
import ru.geekbrains.client.controller.FileController;
import ru.geekbrains.client.handler.ConnectAnswerHandler;
import ru.geekbrains.client.handler.ConnectAnswerHandlerImpl;
import ru.geekbrains.client.handler.FileAnswerHandler;
import ru.geekbrains.client.handler.FileAnswerHandlerImpl;
import ru.geekbrains.client.utils.ClientUtils;
import ru.geekbrains.client.utils.RandomString;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.channel.SocketClientManagedChannel;
import ru.geekbrains.common.dto.*;
import ru.geekbrains.common.message.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.CommonData.SERVER_PORT;
import static ru.geekbrains.common.message.StringCrypter.stringCrypter;

public class Model implements Addressee {
    private static final Logger LOG = LoggerFactory.getLogger(Model.class);

    private static final String HOST = "localhost";

    private static final int PAUSE_MS = 223;
    private static final int THREADS_NUMBER = 1;

    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUMBER);

    private final FileController fileController;
    private final ConnectController connectController;

    private final ConnectAnswerHandler connectAnswerHandler;
    private final FileAnswerHandler fileAnswerHandler;

    private SocketClientChannel client;

    private final Address address;

    public Model(ConnectController connectController, FileController fileController) {
        String macAddresses = ClientUtils.INSTANCE.getMacAddress();  // MAC-адреса клинта
        // на случай запуска нескольких клиентов на одном хосте ещё добавим случайную строку, чтобы адреса были разные
        RandomString randomStringGenerator = new RandomString(10);
        String randomString = randomStringGenerator.nextString();

        String clientAddress = stringCrypter.encrypt(randomString + macAddresses);

        this.address = new Address(clientAddress);

        this.connectAnswerHandler = new ConnectAnswerHandlerImpl(connectController);
        this.fileAnswerHandler = new FileAnswerHandlerImpl(fileController);

        this.connectController = connectController;
        this.fileController = fileController;
    }

    public void authClient() {
        Platform.runLater(fileController::authClient);
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
        Message handshakeDemandMessage = new ConnectOperationMessage(address, SERVER_ADDRESS, ConnectOperation.HANDSHAKE, null);
        connectAnswerHandler.setHandshakeMessageUuid(handshakeDemandMessage.getUuid());
        client.send(handshakeDemandMessage);
        LOG.debug("Отправлено сообщение об установлении соединения на сервер");
    }

    public void authOnServer(String username, String password) {
        Message authDemandMessage = new ConnectOperationMessage(address, SERVER_ADDRESS, ConnectOperation.AUTH, new UserDTO(username, password));
        connectAnswerHandler.setAuthMessageUuid(authDemandMessage.getUuid());
        client.send(authDemandMessage);
        LOG.debug("Отправлено сообщение об аутентификации на сервер");
    }

    public void registerNewClient(String username, String password) {
        Message registerNewClientMessage = new ConnectOperationMessage(address, SERVER_ADDRESS, ConnectOperation.REGISTER, new UserDTO(username, password));
        connectAnswerHandler.setRegisterMessageUuid(registerNewClientMessage.getUuid());
        client.send(registerNewClientMessage);
        LOG.debug("Отправлено сообщение о регистрации нового клиента");
    }

    public void getFileList() {
        FileMessage getFileListMessage = new FileMessage(address, SERVER_ADDRESS, FileObjectToOperate.FILE, FileOperation.GET_LIST, null, false);
        fileAnswerHandler.addFileDemandMessage(getFileListMessage);
        client.send(getFileListMessage);
        LOG.debug("Отправлен запрос на получение списка файлов");
    }

    public void createClientFolder() {
        FileMessage createFolderMessage = new FileMessage(address, SERVER_ADDRESS, FileObjectToOperate.FOLDER, FileOperation.CREATE, null, true);
        fileAnswerHandler.addFileDemandMessage(createFolderMessage);
        client.send(createFolderMessage);
        LOG.debug("Отправлен запрос на создание папки пользователя на сервер");
    }

    public void createNewFiles(List<File> files) {
        // для всех файлов кроме последнего отправляем сообщение о создании, а для последнего - ещё и обновляем список файлов
        for (int i = 0; i < files.size() - 1; i++) {
            File file = files.get(i);
            createNewFile(file, false);
        }
        File file = files.get(files.size() - 1);
        createNewFile(file, true);
    }

    public void deleteFiles(List<String> fileNames) {
        // для всех файлов кроме последнего отправляем сообщение об удалении, а для последнего - ещё и обновляем список файлов
        for (int i = 0; i < fileNames.size() - 1; i++) {
            String fileName = fileNames.get(i);
            deleteFile(fileName, false);
        }
        String fileName = fileNames.get(fileNames.size() - 1);
        deleteFile(fileName, true);
    }

    private void createNewFile(File file, boolean updateFileListAfter) {
        String fileName = file.getName();
        Path path = file.toPath();
        try {
            byte[] fileContent = Files.readAllBytes(path);
            FileDTO fileDTO = new FileDTO(fileName, fileContent);
            FileMessage createNewFileMessage = new FileMessage(address, SERVER_ADDRESS, FileObjectToOperate.FILE, FileOperation.CREATE, fileDTO, updateFileListAfter);
            fileAnswerHandler.addFileDemandMessage(createNewFileMessage);
            client.send(createNewFileMessage);
            LOG.debug("Отправлен запрос на создание файла на сервере. Файл: {}", fileDTO);
        } catch (IOException e) {
            LOG.error("Ошибка при чтении содержимого файла. file: {}, path: {}", file, file.getAbsolutePath());
        }
    }

    private void deleteFile(String fileName, boolean updateFileListAfter) {
        FileDTO fileDTO = new FileDTO(fileName, new byte[0]);
        FileMessage deleteFileMessage = new FileMessage(address, SERVER_ADDRESS, FileObjectToOperate.FILE, FileOperation.DELETE, fileDTO, updateFileListAfter);
        fileAnswerHandler.addFileDemandMessage(deleteFileMessage);
        client.send(deleteFileMessage);
        LOG.debug("Отправлен запрос на удаление файла. Файл: {}", fileName);
    }

    public void renameFile(String fileName, String newFileName) {
        ChangeFileDTO renameFileDTO = new ChangeFileDTO(new FileDTO(fileName, new byte[0]), new FileDTO(newFileName, new byte[0]));
        FileMessage renameFileMessage = new FileMessage(address, SERVER_ADDRESS, FileObjectToOperate.FILE, FileOperation.RENAME, renameFileDTO, false);
        fileAnswerHandler.addFileDemandMessage(renameFileMessage);
        client.send(renameFileMessage);
        LOG.debug("Отправлен запрос на переименование файла. Файл: {}, новое имя: {}", fileName, newFileName);
    }

    public void downloadFile(String fileName, File directoryToSave) {
        FileDTO fileDTO = new FileDTO(fileName, new byte[0]);
        FileMessage getFileMessage = new FileMessage(address, SERVER_ADDRESS, FileObjectToOperate.FILE, FileOperation.READ, fileDTO, false);
        fileAnswerHandler.addDownloadFileMessage(getFileMessage, directoryToSave);
        client.send(getFileMessage);
        LOG.debug("Отправлен запрос на скачивание файла. Файл: {}, папка: {}", fileName, directoryToSave.getPath());
    }

    // Обработка ответов от сервера
    private void serverMessageHandle() {
        try {
            while (true) {
                Message serverMessage = client.take();
                if (serverMessage != null) {
                    if (serverMessage.isClass(ConnectAnswerMessage.class)) {
                        connectAnswerHandler.handleMessage((ConnectAnswerMessage) serverMessage);
                    } else if (serverMessage.isClass(FileAnswer.class)) {
                        fileAnswerHandler.handleMessage((FileAnswer) serverMessage);
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

    @Override
    public Address getAddress() {
        return address;
    }
}
