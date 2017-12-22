package ru.geekbrains.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.utils.ClientUtils;
import ru.geekbrains.client.utils.RandomString;
import ru.geekbrains.client.view_elements.FileView;
import ru.geekbrains.common.dto.FileInfo;
import ru.geekbrains.common.message.Address;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ru.geekbrains.common.message.StringCrypter.stringCrypter;

public class Controller implements Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    @FXML
    private HBox authPanel;
    @FXML
    private TextField authLogin;
    @FXML
    private PasswordField authPass;
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

    private boolean isAuthorized;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthentificate(false);
        try {
            startModel();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void startModel() {
        String macAddresses = ClientUtils.INSTANCE.getMacAddress();  // MAC-адреса клинта
        // на случай запуска нескольких клиентов на одном хосте ещё добавим случайную строку, чтобы адреса были разные
        RandomString randomStringGenerator = new RandomString(10);
        String randomString = randomStringGenerator.nextString();

        prepareFileTable();

        String clientAddress = stringCrypter.encrypt(randomString + macAddresses);

        model = new Model(new Address(clientAddress), this);
        model.start();

        model.handshakeOnServer();
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

    public void sendAuth() {
        if (model != null) {
            String username = authLogin.getText();
            String password = authPass.getText();
            model.authOnServer(username, password);
        } else {
            LOG.error("Модель не инициализирована");
        }
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

    public void setAuthentificate(boolean isAuthorized) {  // переключаем режим авторизации
        this.isAuthorized = isAuthorized;
        if (!this.isAuthorized) {         // если пользователь не авторизован
            authPanel.setVisible(true);   // включаем панель авторизации
            authPanel.setManaged(true);
        } else {
            authPass.clear();
            // authPanel.setVisible(false);  // выключаем панель авторизации
            // authPanel.setManaged(false);
            model.createClientFolder();
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
