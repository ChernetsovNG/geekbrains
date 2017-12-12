package ru.geekbrains.common.message.file;

import lombok.Getter;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

@Getter
public class DeleteFileDemandMessage extends Message {
    private final String fileName;

    public DeleteFileDemandMessage(Address from, Address to, String fileName) {
        super(from, to, DeleteFileDemandMessage.class);
        this.fileName = fileName;
    }
}
