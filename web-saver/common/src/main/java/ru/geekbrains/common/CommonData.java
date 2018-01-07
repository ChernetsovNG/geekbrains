package ru.geekbrains.common;

import ru.geekbrains.common.message.Address;

public class CommonData {
    public static final int SERVER_PORT = 5050;
    public static final Address SERVER_ADDRESS = new Address("Server");
    public static final String CLIENTS_FOLDERS_PATH;  // путь к папкам клиентов на сервере
    public static final String FILE_SEPARATOR;

    static {
        FILE_SEPARATOR = System.getProperty("file.separator");
        String homeDirectory = System.getProperty("user.home");
        String webSaverClientFolder = "web-saver-server";

        CLIENTS_FOLDERS_PATH = homeDirectory + FILE_SEPARATOR + webSaverClientFolder + FILE_SEPARATOR;
    }

}
