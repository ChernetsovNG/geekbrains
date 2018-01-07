package ru.geekbrains.common.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ChangeFileDTO implements Serializable {
    private final FileDTO oldFile;  // заменить что
    private final FileDTO newFile;  // чем

    public ChangeFileDTO(FileDTO oldFile, FileDTO newFile) {
        this.oldFile = oldFile;
        this.newFile = newFile;
    }
}
