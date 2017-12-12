package ru.geekbrains.common.message.file;

import lombok.Getter;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

@Getter
public class CreateNewFileDemandMessage extends Message {
    private final String fileName;
    private final byte[] filePayload;  // собственно, само содержимое файла

    public CreateNewFileDemandMessage(Address from, Address to, String fileName, byte[] filePayload) {
        super(from, to, CreateNewFileDemandMessage.class);
        this.fileName = fileName;
        this.filePayload = filePayload;
    }
}
