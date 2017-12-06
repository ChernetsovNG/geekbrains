package ru.geekbrains.common.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class UsernamePassword implements Serializable {
    private final String username;
    private final String password;

    public UsernamePassword(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
