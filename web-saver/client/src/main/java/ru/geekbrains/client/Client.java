package ru.geekbrains.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.channel.SocketClientChannel;
import ru.geekbrains.common.channel.SocketClientManagedChannel;
import ru.geekbrains.common.dto.AuthStatus;
import ru.geekbrains.common.message.*;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.geekbrains.common.CommonData.SERVER_ADDRESS;
import static ru.geekbrains.common.CommonData.SERVER_PORT;
import static ru.geekbrains.common.dto.AuthStatus.*;
import static ru.geekbrains.common.message.StringCrypter.stringCrypter;

public class Client implements Addressee {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    private static final String HOST = "localhost";

    private static final int PAUSE_MS = 250;
    private static final int THREADS_NUMBER = 2;

    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUMBER);

    private final CountDownLatch handshakeLatch = new CountDownLatch(1);  // блокировка до установления соединения с сервером
    private final CountDownLatch authentificationLatch = new CountDownLatch(1);  // блокировка до аутентификации клиента на сервере

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

        executor.submit(this::handshake);
        executor.submit(this::authentification);

        // Отправляем на сервер HandshakeDemand сообщение
        client.send(new HandshakeDemandMessage(this.address, SERVER_ADDRESS));
        LOG.debug("Послано сообщение об установлении соединения на сервер");
        handshakeLatch.await();  // ждём handshake-ответа от сервера

        String username = "TestUser1";
        String password = "qwerty";

        client.send(new AuthDemandMessage(this.address, SERVER_ADDRESS, username, password));
        LOG.debug("Послано сообщение об аутентификации на сервер");
        authentificationLatch.await();  // ждём успешной аутентификации

        LOG.debug("Послано сообщение об отключении от сервера");
        client.send(new DisconnectClientMessage(this.address, SERVER_ADDRESS));

        TimeUnit.MILLISECONDS.sleep(2000);

        client.close();
        executor.shutdown();
    }

    // Ожидаем от сервера ответа об успешном установлении соединения
    private void handshake() {
        try {
            while (true) {
                Message handshakeAnswer = client.take();
                if (handshakeAnswer.isClass(HandshakeAnswerMessage.class)) {
                    LOG.info("Получен ответ об установлении связи от сервера");
                    handshakeLatch.countDown();  // Отпускаем блокировку
                    break;
                } else {
                    TimeUnit.MILLISECONDS.sleep(PAUSE_MS);
                }
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
    }

    // Ожидаем от сервера ответа об аутентификации
    private void authentification() {
        try {
            while (true) {
                Message authAnswer = client.take();
                if (authAnswer.isClass(AuthAnswerMessage.class)) {
                    LOG.info("Получен ответ об аутентификации от сервера");
                    AuthStatus authStatus = ((AuthAnswerMessage) authAnswer).getAuthStatus();
                    if (authStatus != null) {
                        if (authStatus.equals(AUTH_OK)) {
                            System.out.println("Успешная аутентификация на сервере. AuthStatus: " + authStatus);
                            authentificationLatch.countDown();  // Отпускаем блокировку
                            break;
                        } else if (authStatus.equals(INCORRECT_USERNAME)) {
                            System.out.println("Неправильное имя пользователя. AuthStatus: " + authStatus);
                            break;
                        } else if (authStatus.equals(INCORRECT_PASSWORD)) {
                            System.out.println("Неправильный пароль. AuthStatus: " + authStatus);
                            break;
                        }
                    }
                } else {
                    TimeUnit.MILLISECONDS.sleep(PAUSE_MS);
                }
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return "";
    }
}
