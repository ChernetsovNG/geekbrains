package ru.geekbrains.common.dto;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

@Getter
public class FileInfo implements Serializable {
    private final String fileName;
    private final boolean isDirectory;
    private final long sizeInBytes;
    private final Instant lastModifyTime;

    public FileInfo(String fileName, boolean isDirectory, long sizeInBytes, Instant lastModifyTime) {
        this.fileName = fileName;
        this.isDirectory = isDirectory;
        this.sizeInBytes = sizeInBytes;
        this.lastModifyTime = lastModifyTime;
    }
}
