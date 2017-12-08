package ru.geekbrains.common.message;

import lombok.Getter;

@Getter
public class AuthDemandMessage extends Message {
    private final String username;
    private final String password;

    public AuthDemandMessage(Address from, Address to, String username, String password) {
        super(from, to, AuthDemandMessage.class);
        this.username = username;
        this.password = password;
    }
}
