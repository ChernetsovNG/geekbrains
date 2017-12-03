package ru.geekbrains.common.message;

import lombok.Getter;
import lombok.ToString;

// Сообщение - содержит адресацию (откуда -> куда) и содержимое
@Getter
@ToString
public abstract class Message {
    private final Address from;
    private final Address to;
    private final String className;
    private final byte[] payload;

    public static final String CLASS_NAME_VARIABLE = "className";

    protected Message(Address from, Address to, byte[] payload, Class<?> clazz) {
        this.from = from;
        this.to = to;
        this.payload = payload;
        this.className = clazz.getName();
    }

}
