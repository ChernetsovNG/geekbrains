package ru.geekbrains.common.message;

import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

// Сообщение - содержит адресацию (откуда -> куда) и класс
@Getter
@ToString
public abstract class Message implements Serializable {
    public static final Logger LOG = LoggerFactory.getLogger(Message.class);

    private final Address from;
    private final Address to;
    private final String className;

    public static final String CLASS_NAME_VARIABLE = "className";

    protected Message(Address from, Address to, Class<?> clazz) {
        this.from = from;
        this.to = to;
        this.className = clazz.getName();
    }

    // Проверяем класс сообщения
    public boolean isClass(Class clazz) {
        return this.className.equals(clazz.getName());
    }

}
