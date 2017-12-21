package ru.geekbrains.client.view_elements;

import javafx.beans.property.SimpleStringProperty;
import ru.geekbrains.common.dto.FileInfo;

import java.time.Instant;

public class FileView {
    private final SimpleStringProperty name;
    private final SimpleStringProperty sizeInKb;  // размер файла в кБ
    private final SimpleStringProperty lastModifyTime;

    private FileView(String name, String sizeInKb, String lastModifyTime) {
        this.name = new SimpleStringProperty(name);
        this.sizeInKb = new SimpleStringProperty(sizeInKb);
        this.lastModifyTime = new SimpleStringProperty(lastModifyTime);
    }

    // преобразуем FileInfo для отображения в таблице
    public static FileView fromFileInfo(FileInfo fileInfo) {
        String fileName = fileInfo.getFileName();
        long sizeInBytes = fileInfo.getSizeInBytes();
        Instant lastModifyTime = fileInfo.getLastModifyTime();

        String sizeInKbString = String.format("%.2f", sizeInBytes * 1.0 / 1024.0);  // переводим байты в килобайты
        String lastModifyTimeString = lastModifyTime.toString();

        return new FileView(fileName, sizeInKbString, lastModifyTimeString);
    }

    public String getName() {
        return this.name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSizeInKb() {
        return this.sizeInKb.get();
    }

    public void setSizeInKb(String sizeInKb) {
        this.sizeInKb.set(sizeInKb);
    }

    public String getLastModifyTime() {
        return this.lastModifyTime.get();
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime.set(lastModifyTime);
    }
}
