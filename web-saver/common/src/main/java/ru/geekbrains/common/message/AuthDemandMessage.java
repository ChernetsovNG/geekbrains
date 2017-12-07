package ru.geekbrains.common.message;

import ru.geekbrains.common.dto.UsernamePassword;

import static ru.geekbrains.common.SerializeUtils.deserializeObject;
import static ru.geekbrains.common.SerializeUtils.serializeObject;

public class AuthDemandMessage extends Message {
    public AuthDemandMessage(Address from, Address to, byte[] payload) {
        super(from, to, payload, AuthDemandMessage.class);
    }

    public static byte[] serializeUsernamePassword(String username, String password) {
        UsernamePassword usernamePassword = new UsernamePassword(username, password);
        return serializeObject(usernamePassword);
    }

    public static UsernamePassword deserializeUsernamePassword(byte[] payload) {
        return deserializeObject(payload, UsernamePassword.class);
    }
}
