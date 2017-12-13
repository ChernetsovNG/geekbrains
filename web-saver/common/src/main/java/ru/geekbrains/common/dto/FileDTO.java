package ru.geekbrains.common.dto;

import lombok.Getter;

@Getter
public class FileDTO {
    private final String fileName;
    private final byte[] payload;

    public FileDTO(String fileName, byte[] payload) {
        this.fileName = fileName;
        this.payload = payload;
    }
}
