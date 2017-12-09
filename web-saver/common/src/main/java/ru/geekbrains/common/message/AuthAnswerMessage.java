package ru.geekbrains.common.message;

import lombok.Getter;
import lombok.ToString;
import ru.geekbrains.common.dto.AuthStatus;

@Getter
@ToString
public class AuthAnswerMessage extends Message {
    private final AuthStatus authStatus;
    private final String message;  // дополнительное сообщение

    public AuthAnswerMessage(Address from, Address to, AuthStatus authStatus, String message) {
        super(from, to, AuthAnswerMessage.class);
        this.authStatus = authStatus;
        this.message = message;
    }
}
