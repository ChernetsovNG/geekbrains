package ru.geekbrains.common.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.message.AbstractMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

public class SocketClientChannel implements MessageChannel {
    private static final Logger LOG = LoggerFactory.getLogger(SocketClientChannel.class);
    private static final int WORKERS_COUNT = 2;

    private final BlockingQueue<AbstractMessage> outputMessages = new LinkedBlockingQueue<>();
    private final BlockingQueue<AbstractMessage> inputMessages = new LinkedBlockingQueue<>();

    private final ExecutorService executor;
    private final Socket client;
    private final List<Runnable> shutdownRegistrations;

    public SocketClientChannel(Socket client) {
        this.client = client;
        this.executor = Executors.newFixedThreadPool(WORKERS_COUNT);
        this.shutdownRegistrations = new CopyOnWriteArrayList<>();
    }

    public void init() {
        executor.execute(this::sendMessage);
        executor.execute(this::receiveMessage);
    }

    // прочитать сообщение из очереди и отправить его в сокетный канал
    private void sendMessage() {
        try (ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream())) {
            while (client.isConnected()) {
                AbstractMessage message = outputMessages.take();  // blocks here
                out.writeObject(message);
            }
        } catch (InterruptedException | IOException e) {
            LOG.error(e.getMessage());
        }
    }

    // получить сообщение из сокетного канала и записать его в очередь
    private void receiveMessage() {
        try (ObjectInputStream in = new ObjectInputStream(client.getInputStream())) {
            Object readObject;
            while ((readObject = in.readObject()) != null) {  // blocks here
                AbstractMessage message = (AbstractMessage) readObject;
                inputMessages.add(message);
            }
        } catch (ClassNotFoundException | IOException e) {
            LOG.error(e.getMessage());
        }
    }

    public void addShutdownRegistration(Runnable runnable) {
        this.shutdownRegistrations.add(runnable);
    }

    @Override
    public void send(AbstractMessage message) {
        outputMessages.add(message);
    }

    @Override
    public AbstractMessage poll() {
        return inputMessages.poll();
    }

    @Override
    public AbstractMessage take() throws InterruptedException {
        return inputMessages.take();
    }

    public void close() throws IOException {
        shutdownRegistrations.forEach(Runnable::run);
        shutdownRegistrations.clear();

        executor.shutdown();
    }
}
