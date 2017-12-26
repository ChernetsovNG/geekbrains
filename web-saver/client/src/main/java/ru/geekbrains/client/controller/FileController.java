package ru.geekbrains.client.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.Model;
import ru.geekbrains.client.view_elements.FileView;
import ru.geekbrains.common.dto.FileInfo;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileController {
    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    @FXML
    private HBox authPanel;
    @FXML
    private TextArea clientTerminal;

    @FXML
    private TableView fileTable;
    private final ObservableList<FileView> tableData = FXCollections.observableArrayList();

    private Model model;

    private Stage primaryStage;
    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    private static final int PAUSE_MS = 249;
    private static final int THREADS_NUMBER = 1;

    public void setModel(Model model) {
        this.model = model;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void prepareFileTable() {
        TableColumn nameCol = new TableColumn("Name");
        TableColumn sizeCol = new TableColumn("Size, kB");
        TableColumn lastModifyTimeCol = new TableColumn("Modify time");

        fileTable.setEditable(false);

        nameCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("name"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("sizeInKb"));
        lastModifyTimeCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("lastModifyTime"));

        nameCol.setMinWidth(300);
        nameCol.setMaxWidth(300);
        sizeCol.setMinWidth(200);
        nameCol.setMaxWidth(200);
        lastModifyTimeCol.setMinWidth(200);
        nameCol.setMaxWidth(200);

        fileTable.setItems(tableData);
        fileTable.getColumns().addAll(nameCol, sizeCol, lastModifyTimeCol);

        fileTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);  // чтобы можно было выбирать несколько строк

        fileTable.refresh();
    }

    public void getFileList() {
        model.getFileList();
    }

    public void addNewFile() {
        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        if (files != null) {
            model.createNewFiles(files);
        } else {
            LOG.error("Файл не выбран (file == null)");
        }
    }

    public void deleteFiles() {
        // Если в таблице выбран какой-то файл, то удаляем его
        List<FileView> selectedFiles = getSelectedFiles();
        List<String> fileNames = selectedFiles.stream().map(FileView::getName).collect(Collectors.toList());
        model.deleteFiles(fileNames);
    }

    public void downloadFiles() {
        List<FileView> selectedFiles = getSelectedFiles();
        File directoryToSaveFiles = directoryChooser.showDialog(primaryStage);
        selectedFiles.forEach(fileView -> model.downloadFile(fileView.getName(), directoryToSaveFiles));
    }

    public void renameFile() {
        List<FileView> selectedFiles = getSelectedFiles();
        if (selectedFiles.size() > 1) {
            LOG.error("Выбрано больше одного файла. Нельзя переименовать");
            writeLogInTerminal("Выбрано больше одного файла. Нельзя переименовать");
        } else {
            FileView selectedFile = selectedFiles.get(0);
            String selectedFileName = selectedFile.getName();

            TextInputDialog renameDialog = new TextInputDialog(selectedFileName);

            renameDialog.setTitle("File rename dialog");
            renameDialog.setHeaderText("Введите имя файла");
            renameDialog.setContentText("File name");

            Optional<String> result = renameDialog.showAndWait();

            result.ifPresent(newName -> {
                if (!newName.equals(selectedFileName)) {
                    model.renameFile(selectedFileName, newName);
                }
            });
        }
    }

    public void writeLogInTerminal(String text) {
        clientTerminal.appendText(Instant.now() + ": " + text + "\n");
    }

    public void writeFileListInTable(List<FileInfo> fileInfoList) {
        tableData.clear();

        List<FileView> fileViewList = fileInfoList.stream()
            .map(FileView::fromFileInfo)
            .collect(Collectors.toList());

        tableData.addAll(fileViewList);
        fileTable.refresh();

        writeLogInTerminal("Вывод списка файлов: ОК");
    }

    private List<FileView> getSelectedFiles() {
        return fileTable.getSelectionModel().getSelectedItems();
    }
}
