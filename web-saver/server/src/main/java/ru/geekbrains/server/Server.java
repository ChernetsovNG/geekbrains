package ru.geekbrains.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.channel.MessageChannel;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.dto.AuthStatus;
import ru.geekbrains.common.dto.CommonAnswerStatus;
import ru.geekbrains.common.dto.CreationStatus;
import ru.geekbrains.common.message.*;
import ru.geekbrains.common.message.file.*;
import ru.geekbrains.server.db.Database;
import ru.geekbrains.server.db.dto.User;

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

import static ru.geekbrains.common.CommonData.CLIENTS_FOLDERS_PATH;
import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.CommonData.SERVER_PORT;
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

        executor.submit(this::handshake);
        executor.submit(this::clientMessageHandle);

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
    private void handshake() {
        LOG.info("Начат цикл приёма адресов клиентов на сервере (handshake)...");
        while (true) {
            for (Map.Entry<MessageChannel, Address> client : connectionMap.entrySet()) {
                MessageChannel clientChannel = client.getKey();
                Address clientAddress = client.getValue();
                if (clientAddress == null) {
                    Message message = clientChannel.poll();
                    if (message != null) {
                        if (message.isClass(HandshakeDemandMessage.class)) {
                            clientAddress = message.getFrom();
                            LOG.info("Получен запрос на установление соединения от: " + clientAddress + ", " + message);
                            connectionMap.put(clientChannel, clientAddress);
                            Message handshakeAnswerMessage = new HandshakeAnswerMessage(this.address, clientAddress);
                            clientChannel.send(handshakeAnswerMessage);
                            LOG.info("Направлен ответ об успешном установлении соединения клиенту: " + clientAddress + ", " + handshakeAnswerMessage);
                        }
                    }
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(MESSAGE_DELAY_MS);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    private void clientMessageHandle() {
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
                            if (message.isClass(AuthDemandMessage.class)) {
                                handleAuthDemandMessage(clientAddress, clientChannel, (AuthDemandMessage) message);
                            } else if (message.isClass(DisconnectClientMessage.class)) {
                                LOG.info("Сообщение об отключении клиента: " + clientAddress + ", " + message);
                                clientChannel.close();
                                connectionMap.remove(clientChannel);
                            } else if (message.isClass(CreateFolderDemandMessage.class)) {
                                LOG.info("Запрос на создание папки: " + clientAddress + ", " + message);
                                handleCreateFolderDemandMessage(clientAddress, clientChannel, (CreateFolderDemandMessage) message);
                            } else if (message.isClass(CreateNewFileDemandMessage.class)) {
                                LOG.info("Запрос на создание нового файла: " + clientAddress + ", " + message);
                                handleCreateNewFileDemandMessage(clientAddress, clientChannel, (CreateNewFileDemandMessage) message);
                            } else if (message.isClass(GetFileListDemandMessage.class)) {
                                LOG.info("Запрос списка файлов: " + clientAddress + ", " + message);
                                handleGetFileListDemandMessage(clientAddress, clientChannel, (GetFileListDemandMessage) message);
                            } else if (message.isClass(DeleteFileDemandMessage.class)) {
                                LOG.info("Запрос на удаление файла: " + clientAddress + ", " + message);
                                handleDeleteFileDemandMessage(clientAddress, clientChannel, (DeleteFileDemandMessage) message);
                            } else if (message.isClass(GetFilePayloadDemandMessage.class)) {
                                LOG.info("Запрос содержимого файла: " + clientAddress + ", " + message);
                                handleGetFilePayloadDemandMessage(clientAddress, clientChannel, (GetFilePayloadDemandMessage) message);
                            } else {
                                LOG.debug("Получено сообщение необрабатываемого класса: " + message);
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

    // Обработка запроса на аутентификацию
    private void handleAuthDemandMessage(Address clientAddress, MessageChannel clientChannel, AuthDemandMessage authDemandMessage) {
        String username = authDemandMessage.getUsername();
        String password = authDemandMessage.getPassword();
        User user = new User(username, password);
        LOG.info("Получен запрос на аутентификацию от: " + authDemandMessage.getFrom() + ", " + user);
        AuthStatus authStatus = Database.getAuthStatus(user);
        AuthAnswerMessage authAnswerMessage = new AuthAnswerMessage(SERVER_ADDRESS, clientAddress, authStatus, "");
        if (authStatus.equals(AuthStatus.AUTH_OK)) {
            authMap.put(clientChannel, username);  // сохраняем в карте авторизованного пользователя
        }
        clientChannel.send(authAnswerMessage);
        LOG.info("Направлен ответ об аутентификации клиенту: " + clientAddress + ", " + authAnswerMessage);
    }

    private void handleCreateFolderDemandMessage(Address clientAddress, MessageChannel clientChannel, CreateFolderDemandMessage createFolderDemandMessage) {
        CreateFolderAnswerMessage createFolderAnswerMessage = null;
        if (!authMap.containsKey(clientChannel)) {
            createFolderAnswerMessage = new CreateFolderAnswerMessage(SERVER_ADDRESS, clientAddress, CreationStatus.CREATE_ERROR, "Пользователь не аутентифицирован");
        } else {
            String folderPath = getClientFolderPath(clientChannel);
            if (isFolderExists(folderPath)) {
                createFolderAnswerMessage = new CreateFolderAnswerMessage(SERVER_ADDRESS, clientAddress, CreationStatus.CREATE_ERROR, "Папка уже существует");
            } else {
                try {
                    Files.createDirectories(Paths.get(folderPath));
                    createFolderAnswerMessage = new CreateFolderAnswerMessage(SERVER_ADDRESS, clientAddress, CreationStatus.CREATE_OK, "");
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
        }
        clientChannel.send(createFolderAnswerMessage);
    }

    private void handleCreateNewFileDemandMessage(Address clientAddress, MessageChannel clientChannel, CreateNewFileDemandMessage createNewFileDemandMessage) {
        CreateNewFileAnswerMessage createNewFileAnswerMessage;
        if (!authMap.containsKey(clientChannel)) {
            createNewFileAnswerMessage = new CreateNewFileAnswerMessage(SERVER_ADDRESS, clientAddress, CreationStatus.CREATE_ERROR, "Пользователь не аутентифицирован");
        } else {
            String folderPath = getClientFolderPath(clientChannel);
            String fileName = createNewFileDemandMessage.getFileName();
            if (isFileExists(folderPath, fileName)) {
                createNewFileAnswerMessage = new CreateNewFileAnswerMessage(SERVER_ADDRESS, clientAddress, CreationStatus.CREATE_ERROR, "Файл уже существует");
            } else {
                boolean isCreationOK = createNewFile(folderPath, fileName, createNewFileDemandMessage.getFilePayload());
                if (isCreationOK) {
                    createNewFileAnswerMessage = new CreateNewFileAnswerMessage(SERVER_ADDRESS, clientAddress, CreationStatus.CREATE_OK, "");
                } else {
                    createNewFileAnswerMessage = new CreateNewFileAnswerMessage(SERVER_ADDRESS, clientAddress, CreationStatus.CREATE_ERROR, "Ошибка при создании файла");
                }
            }
        }
        clientChannel.send(createNewFileAnswerMessage);
    }

    private void handleGetFileListDemandMessage(Address clientAddress, MessageChannel clientChannel, GetFileListDemandMessage getFileListDemandMessage) {
        GetFileListAnswerMessage getFileListAnswerMessage;
        if (!authMap.containsKey(clientChannel)) {
            getFileListAnswerMessage = new GetFileListAnswerMessage(SERVER_ADDRESS, clientAddress, Collections.emptyList(), CommonAnswerStatus.ERROR, "Пользователь не аутентифицирован");
        } else {
            String folderPath = getClientFolderPath(clientChannel);
            List<String> filesList = getFileList(folderPath);
            if (filesList != null) {
                getFileListAnswerMessage = new GetFileListAnswerMessage(SERVER_ADDRESS, clientAddress, filesList, CommonAnswerStatus.OK, "");
            } else {
                getFileListAnswerMessage = new GetFileListAnswerMessage(SERVER_ADDRESS, clientAddress, Collections.emptyList(), CommonAnswerStatus.ERROR, "Ошибка чтения списка файлов");
            }
        }
        clientChannel.send(getFileListAnswerMessage);
    }

    private void handleDeleteFileDemandMessage(Address clientAddress, MessageChannel clientChannel, DeleteFileDemandMessage deleteFileDemandMessage) {
        DeleteFileAnswerMessage deleteFileAnswerMessage = new DeleteFileAnswerMessage(SERVER_ADDRESS, clientAddress, CommonAnswerStatus.ERROR, "Ошибка при удалении файла");
        if (!authMap.containsKey(clientChannel)) {
            deleteFileAnswerMessage = new DeleteFileAnswerMessage(SERVER_ADDRESS, clientAddress, CommonAnswerStatus.ERROR, "Пользователь не аутентифицирован");
        } else {
            String folderPath = getClientFolderPath(clientChannel);
            try {
                Files.delete(Paths.get(folderPath, deleteFileDemandMessage.getFileName()));
                deleteFileAnswerMessage = new DeleteFileAnswerMessage(SERVER_ADDRESS, clientAddress, CommonAnswerStatus.OK, "");
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        clientChannel.send(deleteFileAnswerMessage);
    }

    private void handleGetFilePayloadDemandMessage(Address clientAddress, MessageChannel clientChannel, GetFilePayloadDemandMessage getFilePayloadDemandMessage) {
        GetFilePayloadAnswerMessage getFilePayloadAnswerMessage = new GetFilePayloadAnswerMessage(SERVER_ADDRESS, clientAddress, CommonAnswerStatus.ERROR, new byte[0], "Ошибка при получении содержимого файла");
        if (!authMap.containsKey(clientChannel)) {
            getFilePayloadAnswerMessage = new GetFilePayloadAnswerMessage(SERVER_ADDRESS, clientAddress, CommonAnswerStatus.ERROR, new byte[0], "Пользователь не аутентифицирован");
        } else {
            String folderPath = getClientFolderPath(clientChannel);
            String fileName = getFilePayloadDemandMessage.getFileName();
            if (!isFileExists(folderPath, fileName)) {
                getFilePayloadAnswerMessage = new GetFilePayloadAnswerMessage(SERVER_ADDRESS, clientAddress, CommonAnswerStatus.ERROR, new byte[0], "Файл не найден");
            } else {
                try {
                    byte[] filePayload = Files.readAllBytes(Paths.get(folderPath, fileName));
                    getFilePayloadAnswerMessage = new GetFilePayloadAnswerMessage(SERVER_ADDRESS, clientAddress, CommonAnswerStatus.OK, filePayload, "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
