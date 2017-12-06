package ru.geekbrains.common.message;

import ru.geekbrains.common.dto.UsernamePassword;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class AuthDemandMessage extends Message {
    public AuthDemandMessage(Address from, Address to, byte[] payload) {
        super(from, to, payload, AuthDemandMessage.class);
    }

    public static byte[] getPayload(String username, String password) {
        UsernamePassword usernamePassword = new UsernamePassword(username, password);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeObject(usernamePassword);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }
}
