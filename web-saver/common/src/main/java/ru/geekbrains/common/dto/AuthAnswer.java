package ru.geekbrains.common.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class AuthAnswer implements Serializable {
    private final AuthStatus authStatus;
    private final UsernamePassword usernamePassword;
    private final String message;

    public AuthAnswer(AuthStatus authStatus, UsernamePassword usernamePassword, String message) {
        this.authStatus = authStatus;
        this.usernamePassword = usernamePassword;
        this.message = message;
    }
}
