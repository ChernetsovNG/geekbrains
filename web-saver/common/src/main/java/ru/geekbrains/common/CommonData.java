package ru.geekbrains.common;

import ru.geekbrains.common.message.Address;

public class CommonData {
    public static final int SERVER_PORT = 5050;
    public static final Address SERVER_ADDRESS = new Address("Server");
    public static final String CLIENTS_FOLDERS_PATH;  // путь к папкам клиентов на сервере

    static {
        String currentUserName = System.getProperty("user.name");
        CLIENTS_FOLDERS_PATH = "/home/" + currentUserName + "/web-saver-server";
    }

}
