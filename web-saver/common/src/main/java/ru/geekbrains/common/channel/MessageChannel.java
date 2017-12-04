package ru.geekbrains.common.channel;

import ru.geekbrains.common.message.AbstractMessage;

import java.io.IOException;

// Канал для передачи и получения сообщений
public interface MessageChannel {
    void send(AbstractMessage message);
    AbstractMessage poll();
    AbstractMessage take() throws InterruptedException;
    void close() throws IOException;
}
