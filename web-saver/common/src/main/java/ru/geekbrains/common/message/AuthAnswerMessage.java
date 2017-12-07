package ru.geekbrains.common.message;

import ru.geekbrains.common.dto.AuthAnswer;

import static ru.geekbrains.common.SerializeUtils.deserializeObject;
import static ru.geekbrains.common.SerializeUtils.serializeObject;

public class AuthAnswerMessage extends Message {
    public AuthAnswerMessage(Address from, Address to, byte[] payload) {
        super(from, to, payload, AuthAnswerMessage.class);
    }

    public static byte[] serializeAuthAnswer(AuthAnswer authAnswer) {
        return serializeObject(authAnswer);
    }

    public static AuthAnswer deserializeAuthAnswer(byte[] payload) {
        return deserializeObject(payload, AuthAnswer.class);
    }
}
