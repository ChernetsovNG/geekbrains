package ru.geekbrains.common.message.file;

import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

// создать на сервере папку для пользователя, прошедшего аутентификацию
public class CreateFolderDemandMessage extends Message {
    public CreateFolderDemandMessage(Address from, Address to) {
        super(from, to, CreateFolderDemandMessage.class);
    }
}
