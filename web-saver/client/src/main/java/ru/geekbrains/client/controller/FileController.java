package ru.geekbrains.client.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TextArea clientTerminal;

    @FXML
    private TableView fileTable;
    private final ObservableList<FileView> tableData = FXCollections.observableArrayList();

    private Model model;

    private Stage stage;

    private String activeFolder;  // активная папка, в которой сейчас находится пользователь

    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    public void setModel(Model model) {
        this.model = model;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void authClient() {
        activeFolder = "";  // пустая строка соответствует операциям в корневой директории
        prepareFileTable();
        stage.show();
        model.createClientFolder();
    }

    private void prepareFileTable() {
        TableColumn nameCol = new TableColumn("Name");
        TableColumn sizeCol = new TableColumn("Size, kB");
        TableColumn lastModifyTimeCol = new TableColumn("Modify time");
        TableColumn isFolderCol = new TableColumn("folder?");

        fileTable.setEditable(false);

        nameCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("name"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("sizeInKb"));
        lastModifyTimeCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("lastModifyTime"));
        isFolderCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("folder"));

        nameCol.setMinWidth(300);
        nameCol.setMaxWidth(300);
        sizeCol.setMinWidth(200);
        isFolderCol.setMinWidth(150);

        nameCol.setMaxWidth(200);
        lastModifyTimeCol.setMinWidth(200);
        nameCol.setMaxWidth(200);
        isFolderCol.setMaxWidth(150);

        fileTable.setItems(tableData);
        fileTable.getColumns().addAll(nameCol, sizeCol, lastModifyTimeCol, isFolderCol);

        fileTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);  // чтобы можно было выбирать несколько строк

        fileTable.refresh();
    }

    public void getFileList() {
        model.getFileList(activeFolder);
    }

    public void addNewFile() {
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null) {
            model.createNewFiles(activeFolder, files);
        } else {
            LOG.error("Файл не выбран (file == null)");
        }
    }

    public void deleteFiles() {
        // Если в таблице выбран какой-то файл, то удаляем его
        List<FileView> selectedFiles = getSelectedFiles();
        List<String> fileNames = selectedFiles.stream().map(FileView::getName).collect(Collectors.toList());
        model.deleteFiles(activeFolder, fileNames);
    }

    public void downloadFiles() {
        List<FileView> selectedFiles = getSelectedFiles();
        File directoryToSaveFiles = directoryChooser.showDialog(stage);
        selectedFiles.forEach(fileView -> model.downloadFile(activeFolder, fileView.getName(), directoryToSaveFiles));
    }

    public void createNewFolder() {
        TextInputDialog createNewFolderDialog = new TextInputDialog();

        createNewFolderDialog.setTitle("Create new folder dialog");
        createNewFolderDialog.setHeaderText("Введите имя папки");
        createNewFolderDialog.setContentText("Folder name");

        Optional<String> result = createNewFolderDialog.showAndWait();

        result.ifPresent(folderName -> model.createNewFolder(activeFolder, folderName));
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
                    model.renameFile(activeFolder, selectedFileName, newName);
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

    public void setActiveFolder(String activeFolder) {
        this.activeFolder = activeFolder;
    }

    private List<FileView> getSelectedFiles() {
        return fileTable.getSelectionModel().getSelectedItems();
    }
}
