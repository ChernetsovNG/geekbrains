package ru.geekbrains.common.message;

import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

// Сообщение - содержит адресацию (откуда -> куда) и содержимое
@Getter
@ToString
public abstract class Message implements Serializable {
    public static final Logger LOG = LoggerFactory.getLogger(Message.class);

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

    // Проверяем класс сообщения
    public boolean isClass(Class clazz) {
        return this.className.equals(clazz.getName());
    }

}
