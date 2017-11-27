package ru.geekbrains.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.channel.SocketClientChannel;
import ru.geekbrains.channel.SocketClientManagedChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static ru.geekbrains.server.Server.PORT;

public class Client {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    private static final String HOST = "localhost";
    private static final int THREADS_NUMBER = 2;

    private SocketClientChannel client;

    public Client() {
    }

    public static void main(String[] args) throws Exception {
        new Client().start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void start() throws Exception {
        LOG.info("Client process started");

        client = new SocketClientManagedChannel(HOST, PORT);
        client.init();

        ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUMBER);

        client.close();
        executor.shutdown();
    }
}
