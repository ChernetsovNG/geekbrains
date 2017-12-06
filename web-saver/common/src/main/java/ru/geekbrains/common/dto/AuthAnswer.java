package ru.geekbrains.common.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class AuthAnswer implements Serializable {
    private final AuthStatus authStatus;
    private final String message;

    public AuthAnswer(AuthStatus authStatus, String message) {
        this.authStatus = authStatus;
        this.message = message;
    }
}
