package ru.geekbrains.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.channel.MessageChannel;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.dto.*;
import ru.geekbrains.common.message.*;
import ru.geekbrains.server.db.Database;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.geekbrains.common.CommonData.*;
import static ru.geekbrains.server.db.Database.createServerDB;
import static ru.geekbrains.server.utils.FileUtils.*;

public class Server implements Addressee {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final int THREADS_COUNT = 4;
    private static final int MESSAGE_DELAY_MS = 100;

    private final Address address;

    private final Map<MessageChannel, Address> connectionMap;  // карта вида <Канал для сообщений -> соответствующий ему адрес>
    private final Map<MessageChannel, String> authMap;         // карта вида <Канал -> имя авторизованного пользователя>

    private final ExecutorService executor;

    public Server() {
        executor = Executors.newFixedThreadPool(THREADS_COUNT);

        connectionMap = new HashMap<>();
        authMap = new HashMap<>();

        address = SERVER_ADDRESS;
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        createServerDB();

        executor.submit(this::connectMessageHandle);
        executor.submit(this::fileMessageHandle);

        // Ждём подключения клиентов к серверу. Для подключённых клиентов создаём каналы для связи
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            LOG.info("Server started on port: " + serverSocket.getLocalPort());

            while (!executor.isShutdown()) {
                Socket client = serverSocket.accept();  // blocks

                LOG.info("Client connect: " + client);

                SocketClientChannel channel = new SocketClientChannel(client);
                channel.init();
                channel.addShutdownRegistration(() -> connectionMap.remove(channel));
                connectionMap.put(channel, null);
            }
        }
    }

    // Принимаем идентифицирующее сообщение ("рукопожатие") и сохраняем в карте соответствующий адрес
    private void connectMessageHandle() {
        try {
            LOG.info("Начат цикл приёма адресов клиентов на сервере (handshake)...");
            while (true) {
                for (Map.Entry<MessageChannel, Address> client : connectionMap.entrySet()) {
                    MessageChannel clientChannel = client.getKey();
                    Address clientAddress = client.getValue();
                    if (clientAddress == null) {
                        Message message = clientChannel.poll();
                        if (message != null) {
                            if (message.isClass(ConnectOperationMessage.class)) {
                                ConnectOperationMessage connectOperationMessage = (ConnectOperationMessage) message;
                                ConnectOperation connectOperation = connectOperationMessage.getConnectOperation();
                                clientAddress = connectOperationMessage.getFrom();
                                if (connectOperation.equals(ConnectOperation.HANDSHAKE)) {
                                    LOG.info("Получен запрос на установление соединения от: " + clientAddress + ", " + message);
                                    connectionMap.put(clientChannel, clientAddress);
                                    ConnectAnswerMessage handshakeAnswerMessage = new ConnectAnswerMessage(this.address, clientAddress, connectOperationMessage.getUuid(), ConnectStatus.HANDSHAKE_OK);
                                    clientChannel.send(handshakeAnswerMessage);
                                    LOG.info("Направлен ответ об успешном установлении соединения клиенту: " + clientAddress + ", " + handshakeAnswerMessage);
                                } else if (connectOperation.equals(ConnectOperation.AUTH)) {
                                    handleAuthDemandMessage(clientAddress, clientChannel, connectOperationMessage);
                                } else if (connectOperation.equals(ConnectOperation.DISCONNECT)) {
                                    LOG.info("Сообщение об отключении клиента: " + clientAddress + ", " + message);
                                    connectionMap.remove(clientChannel);
                                    authMap.remove(clientChannel);
                                    clientChannel.close();
                                }
                            }
                        }
                    }
                }
                TimeUnit.MILLISECONDS.sleep(MESSAGE_DELAY_MS);
            }
        } catch (InterruptedException | IOException e) {
            LOG.error(e.getMessage());
        }
    }

    private void fileMessageHandle() {
        try {
            LOG.info("Цикл приёма сообщений от клиентов на сервере");
            while (true) {
                for (Map.Entry<MessageChannel, Address> entry : connectionMap.entrySet()) {
                    MessageChannel clientChannel = entry.getKey();
                    Address clientAddress = entry.getValue();
                    // если соединение с этим клиентом уже было ранее установлено
                    if (clientAddress != null) {
                        Message message = clientChannel.poll();
                        if (message != null) {
                            if (message.isClass(FileMessage.class)) {
                                FileMessage fileMessage = (FileMessage) message;
                                FileObject fileObject = fileMessage.getFileObject();
                                FileOperation fileOperation = fileMessage.getFileOperation();
                                if (fileObject.equals(FileObject.FOLDER)) {
                                    if (fileOperation.equals(FileOperation.CREATE)) {
                                        LOG.info("Запрос на создание папки: " + clientAddress + ", " + message);
                                        handleCreateFolderDemandMessage(clientAddress, clientChannel, fileMessage);
                                    }
                                } else if (fileObject.equals(FileObject.FILE)) {
                                    if (fileOperation.equals(FileOperation.CREATE)) {
                                        LOG.info("Запрос на создание нового файла: " + clientAddress + ", " + message);
                                        handleCreateNewFileDemandMessage(clientAddress, clientChannel, fileMessage);
                                    } else if (fileOperation.equals(FileOperation.READ)) {
                                        LOG.info("Запрос содержимого файла: " + clientAddress + ", " + message);
                                        handleGetFilePayloadDemandMessage(clientAddress, clientChannel, fileMessage);
                                    } else if (fileOperation.equals(FileOperation.DELETE)) {
                                        LOG.info("Запрос на удаление файла: " + clientAddress + ", " + message);
                                        handleDeleteFileDemandMessage(clientAddress, clientChannel, fileMessage);
                                    } else if (fileOperation.equals(FileOperation.GET_LIST)) {
                                        LOG.info("Запрос списка файлов: " + clientAddress + ", " + message);
                                        handleGetFileListDemandMessage(clientAddress, clientChannel, fileMessage);
                                    }
                                }
                            }
                        }
                    }
                }
                TimeUnit.MILLISECONDS.sleep(MESSAGE_DELAY_MS);
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    // Обработка запроса на аутентификацию
    private void handleAuthDemandMessage(Address clientAddress, MessageChannel clientChannel, ConnectOperationMessage authDemandMessage) {
        UserDTO userDTO = (UserDTO) authDemandMessage.getAdditionalObject();
        LOG.info("Получен запрос на аутентификацию от: " + authDemandMessage.getFrom() + ", " + userDTO);
        ConnectStatus authStatus = Database.getAuthStatus(userDTO);
        ConnectAnswerMessage authAnswerMessage = new ConnectAnswerMessage(SERVER_ADDRESS, clientAddress, authDemandMessage.getUuid(), authStatus);
        if (authStatus.equals(ConnectStatus.AUTH_OK)) {
            authMap.put(clientChannel, userDTO.getName());  // сохраняем в карте авторизованного пользователя
        }
        clientChannel.send(authAnswerMessage);
        LOG.info("Направлен ответ об аутентификации клиенту: " + clientAddress + ", " + authAnswerMessage);
    }

    private void handleCreateFolderDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus = FileStatus.ERROR;
        String additionalMessage = null;
        if (!authMap.containsKey(clientChannel)) {
            fileStatus = FileStatus.NOT_AUTH;
            additionalMessage = "Пользователь не аутентифицирован";
        } else {
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
        }
        FileAnswer createFolderAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, fileMessage.getUuid(), fileStatus, additionalMessage, null);
        clientChannel.send(createFolderAnswerMessage);
    }

    private void handleCreateNewFileDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus;
        String additionalMessage;
        if (!authMap.containsKey(clientChannel)) {
            fileStatus = FileStatus.NOT_AUTH;
            additionalMessage = "Пользователь не аутентифицирован";
        } else {
            String folderPath = getClientFolderPath(clientChannel);
            FileDTO fileDTO = (FileDTO) fileMessage.getAdditionalObject();
            String fileName = fileDTO.getFileName();
            if (isFileExists(folderPath, fileName)) {
                fileStatus = FileStatus.ERROR;
                additionalMessage = "Файл уже существует";
            } else {
                boolean isCreationOK = createNewFile(folderPath, fileName, fileDTO.getPayload());
                if (isCreationOK) {
                    fileStatus = FileStatus.OK;
                    additionalMessage = null;
                } else {
                    fileStatus = FileStatus.ERROR;
                    additionalMessage = "Ошибка при создании файла";
                }
            }
        }
        FileAnswer createNewFileAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, fileMessage.getUuid(), fileStatus, additionalMessage, null);
        clientChannel.send(createNewFileAnswerMessage);
    }

    private void handleGetFileListDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus;
        String additionalMessage;
        List<String> fileNamesList = Collections.emptyList();
        if (!authMap.containsKey(clientChannel)) {
            fileStatus = FileStatus.NOT_AUTH;
            additionalMessage = "Пользователь не аутентифицирован";
        } else {
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
        }
        FileAnswer getFileListAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, fileMessage.getUuid(), fileStatus, additionalMessage, fileNamesList);
        clientChannel.send(getFileListAnswerMessage);
    }

    private void handleDeleteFileDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus = FileStatus.ERROR;
        String additionalMessage = null;
        if (!authMap.containsKey(clientChannel)) {
            fileStatus = FileStatus.NOT_AUTH;
            additionalMessage = "Пользователь не аутентифицирован";
        } else {
            String folderPath = getClientFolderPath(clientChannel);
            FileDTO fileDTO = (FileDTO) fileMessage.getAdditionalObject();
            try {
                Files.delete(Paths.get(folderPath, fileDTO.getFileName()));
                fileStatus = FileStatus.OK;
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        FileAnswer deleteFileAnswerMessage = new FileAnswer(SERVER_ADDRESS, clientAddress, fileMessage.getUuid(), fileStatus, additionalMessage, null);
        clientChannel.send(deleteFileAnswerMessage);
    }

    private void handleGetFilePayloadDemandMessage(Address clientAddress, MessageChannel clientChannel, FileMessage fileMessage) {
        FileStatus fileStatus = FileStatus.ERROR;
        String additionalMessage = null;
        byte[] filePayload = null;
        if (!authMap.containsKey(clientChannel)) {
            fileStatus = FileStatus.NOT_AUTH;
            additionalMessage = "Пользователь не аутентифицирован";
        } else {
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
    }

    @Override
    public Address getAddress() {
        return address;
    }

    private String getClientFolderPath(MessageChannel clientChannel) {
        String folderName = authMap.get(clientChannel);  // имя папки на сервере равно имени клиента (по соглашению)
        return CLIENTS_FOLDERS_PATH + folderName;
    }
}
