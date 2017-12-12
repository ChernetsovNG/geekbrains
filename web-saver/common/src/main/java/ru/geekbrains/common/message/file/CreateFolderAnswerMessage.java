package ru.geekbrains.common.message.file;

import ru.geekbrains.common.dto.CreationStatus;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

public class CreateFolderAnswerMessage extends Message {
    private final CreationStatus creationStatus;
    private final String additionalMessage;  // дополнительное сообщение

    public CreateFolderAnswerMessage(Address from, Address to, CreationStatus creationStatus, String additionalMessage) {
        super(from, to, CreateFolderAnswerMessage.class);
        this.creationStatus = creationStatus;
        this.additionalMessage = additionalMessage;
    }
}
