package ru.geekbrains.common.message;

public class HandshakeAnswerMessage extends AbstractMessage {
    public HandshakeAnswerMessage(Address from, Address to) {
        super(from, to, new byte[0], HandshakeAnswerMessage.class);
    }
}
