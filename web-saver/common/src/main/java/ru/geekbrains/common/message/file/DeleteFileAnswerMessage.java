package ru.geekbrains.common.message.file;

import ru.geekbrains.common.dto.CommonAnswerStatus;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

public class DeleteFileAnswerMessage extends Message {
    private final CommonAnswerStatus commonAnswerStatus;
    private final String additionalMessage;

    public DeleteFileAnswerMessage(Address from, Address to, CommonAnswerStatus commonAnswerStatus, String additionalMessage) {
        super(from, to, DeleteFileAnswerMessage.class);
        this.commonAnswerStatus = commonAnswerStatus;
        this.additionalMessage = additionalMessage;
    }
}
