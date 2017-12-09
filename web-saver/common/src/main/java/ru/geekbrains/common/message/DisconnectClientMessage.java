package ru.geekbrains.common.message;

public class DisconnectClientMessage extends Message {
    public DisconnectClientMessage(Address from, Address to) {
        super(from, to, DisconnectClientMessage.class);
    }
}
