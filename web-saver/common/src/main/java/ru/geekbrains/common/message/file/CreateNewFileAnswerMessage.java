package ru.geekbrains.common.message.file;

import ru.geekbrains.common.dto.CreationStatus;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

public class CreateNewFileAnswerMessage extends Message {
    private final CreationStatus creationStatus;
    private final String additionalMessage;  // дополнительное сообщение

    public CreateNewFileAnswerMessage(Address from, Address to, CreationStatus creationStatus, String additionalMessage) {
        super(from, to, CreateNewFileAnswerMessage.class);
        this.creationStatus = creationStatus;
        this.additionalMessage = additionalMessage;
    }
}
