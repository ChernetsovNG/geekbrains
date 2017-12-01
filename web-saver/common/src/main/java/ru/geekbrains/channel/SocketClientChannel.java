package ru.geekbrains.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

public class SocketClientChannel {
    private static final Logger LOG = LoggerFactory.getLogger(SocketClientChannel.class);
    private static final int WORKERS_COUNT = 2;

    private final BlockingQueue<byte[]> output = new LinkedBlockingQueue<>();
    private final BlockingQueue<byte[]> input = new LinkedBlockingQueue<>();

    private final ExecutorService executor;
    private final Socket client;
    private final List<Runnable> shutdownRegistrations;

    public SocketClientChannel(Socket client) {
        this.client = client;
        this.executor = Executors.newFixedThreadPool(WORKERS_COUNT);
        this.shutdownRegistrations = new CopyOnWriteArrayList<>();
    }

    public void init() {
        // executor.execute(this::sendMessage);
    }

    public void addShutdownRegistration(Runnable runnable) {
        this.shutdownRegistrations.add(runnable);
    }

    public void close() throws IOException {
        shutdownRegistrations.forEach(Runnable::run);
        shutdownRegistrations.clear();

        executor.shutdown();
    }
}
