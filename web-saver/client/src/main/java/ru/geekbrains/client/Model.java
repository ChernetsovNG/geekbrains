package ru.geekbrains.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.handler.ConnectAnswerHandler;
import ru.geekbrains.client.handler.ConnectAnswerHandlerImpl;
import ru.geekbrains.client.handler.FileAnswerHandler;
import ru.geekbrains.client.handler.FileAnswerHandlerImpl;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.channel.SocketClientManagedChannel;
import ru.geekbrains.common.dto.*;
import ru.geekbrains.common.message.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.CommonData.SERVER_PORT;

public class Model implements Addressee {
    private static final Logger LOG = LoggerFactory.getLogger(Model.class);

    private static final String HOST = "localhost";

    private static final int PAUSE_MS = 223;
    private static final int THREADS_NUMBER = 1;

    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUMBER);

    private final ConnectAnswerHandler connectAnswerHandler;
    private final FileAnswerHandler fileAnswerHandler;

    private SocketClientChannel client;

    private final Address address;

    public Model(Address address, Controller controller) {
        this.address = address;
        this.connectAnswerHandler = new ConnectAnswerHandlerImpl(controller);
        this.fileAnswerHandler = new FileAnswerHandlerImpl(controller);
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
        connectAnswerHandler.setHandshakeMessageUuid(handshakeDemandMessage.getUuid());
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
        connectAnswerHandler.setAuthMessageUuid(authDemandMessage.getUuid());
        client.send(authDemandMessage);
        LOG.debug("Послано сообщение об аутентификации на сервер");
        /*try {
            authLatch.await();
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }*/
    }

    public void createClientFolder() {
        FileMessage createFolderMessage = new FileMessage(this.address, SERVER_ADDRESS, FileObjectToOperate.FOLDER, FileOperation.CREATE, null);
        fileAnswerHandler.addFileDemandMessage(createFolderMessage);
        client.send(createFolderMessage);
        LOG.debug("Послан запрос на создание папки пользователя на сервер");
    }

    public void createNewFile(File file) {
        String fileName = file.getName();
        Path path = file.toPath();
        try {
            byte[] fileContent = Files.readAllBytes(path);
            FileDTO fileDTO = new FileDTO(fileName, fileContent);
            FileMessage createNewFileMessage = new FileMessage(this.address, SERVER_ADDRESS, FileObjectToOperate.FILE, FileOperation.CREATE, fileDTO);
            fileAnswerHandler.addFileDemandMessage(createNewFileMessage);
            client.send(createNewFileMessage);
            LOG.debug("Послан запрос на создание файла на сервере. Файл: {}", fileDTO);
        } catch (IOException e) {
            LOG.error("Ошибка при чтении содержимого файла. file: {}, path: {}", file, file.getAbsolutePath());
        }
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
