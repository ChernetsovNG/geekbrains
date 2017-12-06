package ru.geekbrains.common.message;

import ru.geekbrains.common.dto.AuthAnswer;

import java.io.*;

public class AuthAnswerMessage extends Message {
    public AuthAnswerMessage(Address from, Address to, byte[] payload) {
        super(from, to, payload, AuthAnswerMessage.class);
    }

    public static AuthAnswer getAuthAnswer(byte[] payload) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(payload)) {
            try(ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                return (AuthAnswer) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                LOG.error(e.getMessage());
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }
}
