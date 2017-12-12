package ru.geekbrains.common.message.file;

import lombok.Getter;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

// Получить с сервера содержимое файла
@Getter
public class GetFilePayloadDemandMessage extends Message {
    private final String fileName;

    public GetFilePayloadDemandMessage(Address from, Address to, String fileName) {
        super(from, to, GetFilePayloadDemandMessage.class);
        this.fileName = fileName;
    }
}
