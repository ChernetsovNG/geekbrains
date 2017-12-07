package ru.geekbrains.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class SerializeUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SerializeUtils.class);

    public static byte[] serializeObject(Object object) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeObject(object);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static  <T> T deserializeObject(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            try(ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                return (T) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                LOG.error(e.getMessage());
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }
}
