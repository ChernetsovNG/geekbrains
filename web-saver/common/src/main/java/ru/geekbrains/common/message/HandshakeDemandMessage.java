package ru.geekbrains.common.message;

// запрос к серверу на установление связи
public class HandshakeDemandMessage extends Message {
    public HandshakeDemandMessage(Address from, Address to) {
        super(from, to, HandshakeDemandMessage.class);
    }
}
