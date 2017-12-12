package ru.geekbrains.common.message.file;

import ru.geekbrains.common.dto.CommonAnswerStatus;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

import java.util.List;

public class GetFilePayloadAnswerMessage extends Message {
    private final CommonAnswerStatus commonAnswerStatus;
    private final byte[] filePayload;
    private final String additionalMessage;

    public GetFilePayloadAnswerMessage(Address from, Address to, CommonAnswerStatus commonAnswerStatus, byte[] filePayload, String additionalMessage) {
        super(from, to, GetFilePayloadAnswerMessage.class);
        this.commonAnswerStatus = commonAnswerStatus;
        this.filePayload = filePayload;
        this.additionalMessage = additionalMessage;
    }
}
