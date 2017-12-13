package ru.geekbrains.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.utils.ExecutorUtils;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.channel.SocketClientManagedChannel;
import ru.geekbrains.common.dto.*;
import ru.geekbrains.common.message.*;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.CommonData.SERVER_PORT;
import static ru.geekbrains.common.dto.ConnectStatus.*;
import static ru.geekbrains.common.message.StringCrypter.stringCrypter;

public class Client implements Addressee {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    private static final String HOST = "localhost";

    private static final int PAUSE_MS = 250;
    private static final int THREADS_NUMBER = 2;

    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUMBER);

    private final CountDownLatch handshakeLatch = new CountDownLatch(1);  // блокировка до установления соединения с сервером
    private final CountDownLatch authLatch = new CountDownLatch(1);  // блокировка до аутентификации клиента на сервере
    private UUID handshakeMessageUUID;
    private UUID authMessageUUID;

    private SocketClientChannel client;

    private final Address address;

    public Client(Address address) {
        this.address = address;
    }

    public static void main(String[] args) throws Exception {
        String address = stringCrypter.encrypt("Client:" + getMacAddress());
        new Client(new Address(address)).start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void start() throws Exception {
        LOG.info("Client process started");

        client = new SocketClientManagedChannel(HOST, SERVER_PORT);
        client.init();

        executor.submit(this::serverConnect);
        // executor.submit(this::serverMessageHandle);

        // 1. HandshakeDemand на сервере

        Message handshakeDemandMessage = new ConnectOperationMessage(this.address, SERVER_ADDRESS, ConnectOperation.HANDSHAKE, null);
        handshakeMessageUUID = handshakeDemandMessage.getUuid();
        client.send(handshakeDemandMessage);
        LOG.debug("Послано сообщение об установлении соединения на сервер");
        handshakeLatch.await();  // ждём handshake-ответа от сервера

        // 2. аутентификация на сервере

        String username = "TestUser1";
        String password = "qwerty";

        Message authDemandMessage = new ConnectOperationMessage(this.address, SERVER_ADDRESS, ConnectOperation.AUTH, new UserDTO(username, password));
        authMessageUUID = authDemandMessage.getUuid();
        client.send(authDemandMessage);
        LOG.debug("Послано сообщение об аутентификации на сервер");
        authLatch.await();  // ждём успешной аутентификации

        // 3. создание папки на сервере

        FileAnswer createFolderAnswer = (FileAnswer) ExecutorUtils.INSTANCE.sendMessageAndAwaitAnswer(client, new FileMessage(this.address, SERVER_ADDRESS, FileObject.FOLDER, FileOperation.CREATE, null));
        System.out.println(createFolderAnswer.getFileStatus());

        // 4. создание нескольких файлов на сервере

        //client.send(new CreateNewFileDemand(this.address, SERVER_ADDRESS, "File2", new byte[0]));
        //TimeUnit.MILLISECONDS.sleep(100);

        // 5. получение списка файлов

        //client.send(new GetFileNameList(this.address, SERVER_ADDRESS));
        //TimeUnit.MILLISECONDS.sleep(100);

        // 6. удаление файла с сервера

        //client.send(new DeleteFileDemand(this.address, SERVER_ADDRESS, "File1"));
        //TimeUnit.MILLISECONDS.sleep(100);

        // 7. получение списка файлов

        //client.send(new GetFileNameList(this.address, SERVER_ADDRESS));
        //TimeUnit.MILLISECONDS.sleep(100);

        // 8. отключение от сервера

        LOG.debug("Послано сообщение об отключении от сервера");
        client.send(new ConnectOperationMessage(this.address, SERVER_ADDRESS, ConnectOperation.DISCONNECT, null));

        TimeUnit.MILLISECONDS.sleep(2000);

        client.close();
        executor.shutdown();
    }

    // Ожидаем от сервера ответа об успешном установлении соединения
    private void serverConnect() {
        try {
            while (true) {
                Message handshakeAnswer = client.take();
                if (handshakeAnswer.isClass(ConnectAnswerMessage.class)) {
                    ConnectAnswerMessage connectAnswerMessage = (ConnectAnswerMessage) handshakeAnswer;
                    if (connectAnswerMessage.getToMessage().equals(handshakeMessageUUID)) {  // проверяем, что это ответ именно нам
                        if (connectAnswerMessage.getConnectStatus().equals(ConnectStatus.HANDSHAKE_OK)) {
                            LOG.info("Получен ответ об установлении связи от сервера");
                            handshakeLatch.countDown();  // Отпускаем блокировку
                            break;
                        }
                    } else if (connectAnswerMessage.getToMessage().equals(authMessageUUID)) {
                        LOG.info("Получен ответ об аутентификации от сервера");
                        ConnectStatus connectStatus = connectAnswerMessage.getConnectStatus();
                        if (connectStatus.equals(AUTH_OK)) {
                            System.out.println("Успешная аутентификация на сервере");
                            authLatch.countDown();  // Отпускаем блокировку
                            break;
                        } else if (connectStatus.equals(INCORRECT_USERNAME)) {
                            System.out.println("Неправильное имя пользователя");
                        } else if (connectStatus.equals(INCORRECT_PASSWORD)) {
                            System.out.println("Неправильный пароль");
                        }
                    }
                } else {
                    TimeUnit.MILLISECONDS.sleep(PAUSE_MS);
                }
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    // Обработка ответов от сервера
    private void serverMessageHandle() {
        try {
            handshakeLatch.await();
            authLatch.await();  // блокируемся до соединения с сервером и аутентификации
            LOG.info("Цикл приёма сообщений с сервера");
            while (true) {
                Message message = client.take();
                if (message != null) {
                    if (message.isClass(FileAnswer.class)) {
                        FileAnswer fileAnswer = (FileAnswer) message;
                        System.out.println(fileAnswer.getFileStatus() + " : " + fileAnswer.getAdditionalMessage());
                    } else {
                        LOG.debug("Получено сообщение необрабатываемого класса: " + message);
                    }
                }
                TimeUnit.MILLISECONDS.sleep(PAUSE_MS);
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public Address getAddress() {
        return address;
    }

    // Находим список MAC-адресов данного хоста. Будем считать его уникальными именем клиента
    private static String getMacAddress() {
        try {
            List<String> macList = new ArrayList<>();

            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();

            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();

                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    macList.add(sb.toString());
                }
            }

            return macList.stream().collect(Collectors.joining("|"));
        } catch (SocketException e) {
            LOG.error(e.getMessage());
        }
        return "";
    }
}
