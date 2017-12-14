package ru.geekbrains.common.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class FileDTO implements Serializable {
    private final String fileName;
    private final byte[] content;

    public FileDTO(String fileName, byte[] content) {
        this.fileName = fileName;
        this.content = content;
    }
}
