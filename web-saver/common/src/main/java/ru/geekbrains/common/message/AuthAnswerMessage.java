package ru.geekbrains.common.message;

import lombok.Getter;
import ru.geekbrains.common.dto.AuthStatus;

@Getter
public class AuthAnswerMessage extends Message {
    private final AuthStatus authStatus;
    private final String message;  // дополнительное сообщение

    public AuthAnswerMessage(Address from, Address to, AuthStatus authStatus, String message) {
        super(from, to, AuthAnswerMessage.class);
        this.authStatus = authStatus;
        this.message = message;
    }
}
