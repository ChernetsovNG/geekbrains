package ru.geekbrains.common.dto;

import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

@Getter
public class FileDTO implements Serializable {
    private final String folder;
    private final String fileName;
    private final byte[] content;

    public FileDTO(String folder, String fileName, byte[] content) {
        this.folder = folder;
        this.fileName = fileName;
        this.content = content;
    }

    @Override
    public String toString() {
        return "FileDTO{" +
            "folder='" + folder + '\'' +
            ", fileName='" + fileName + '\'' +
            ", content=" + Arrays.toString(content) +
            '}';
    }
}
