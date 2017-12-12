package ru.geekbrains.common.message.file;

import ru.geekbrains.common.dto.CommonAnswerStatus;
import ru.geekbrains.common.message.Address;
import ru.geekbrains.common.message.Message;

import java.util.List;

public class GetFileListAnswerMessage extends Message {
    private final List<String> fileNamesList;
    private final CommonAnswerStatus commonAnswerStatus;
    private final String additionalMessage;

    public GetFileListAnswerMessage(Address from, Address to, List<String> fileNamesList, CommonAnswerStatus commonAnswerStatus, String additionalMessage) {
        super(from, to, GetFileListAnswerMessage.class);
        this.fileNamesList = fileNamesList;
        this.commonAnswerStatus = commonAnswerStatus;
        this.additionalMessage = additionalMessage;
    }
}
