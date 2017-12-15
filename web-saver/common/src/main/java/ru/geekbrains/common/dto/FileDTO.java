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

    @Override
    public String toString() {
        return "FileDTO{" +
            "fileName = '" + fileName + '\'' +
            ", size = " + content.length * 1.0 / 1000 + " kB" +
            '}';
    }
}
