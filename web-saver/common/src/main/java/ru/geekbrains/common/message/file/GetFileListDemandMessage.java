package ru.geekbrains.common.message.file;

import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

// получить список файлов в пользовательской папке
public class GetFileListDemandMessage extends Message {
    public GetFileListDemandMessage(Address from, Address to) {
        super(from, to, GetFileListDemandMessage.class);
    }
}
