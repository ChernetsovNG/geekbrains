package ru.geekbrains;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.channel.SocketClientChannel;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public static final int PORT = 5050;

    public Server() {
    }

    public static void main(String[] args) {
        try {
            new Server().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        // Ждём подключения клиентов к серверу
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOG.info("Server started on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket client = serverSocket.accept();  // blocks
                LOG.info("Client connect: " + client);

                SocketClientChannel channel = new SocketClientChannel(client);
                channel.init();
                // channel.addShutdownRegistration(() -> channel.close());
            }
        }
    }
}