package ru.geekbrains.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.utils.ClientUtils;
import ru.geekbrains.client.utils.RandomString;
import ru.geekbrains.client.view.FileView;
import ru.geekbrains.common.dto.FileInfo;
import ru.geekbrains.common.message.Address;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ru.geekbrains.common.message.StringCrypter.stringCrypter;

public class Controller implements Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    public HBox authPanel;
    public TextField authLogin;
    public PasswordField authPass;
    public TextArea clientTerminal;

    public TableView fileTable;
    private final ObservableList<FileView> tableData = FXCollections.observableArrayList();

    private Model model;

    private Stage stage;
    private final FileChooser fileChooser = new FileChooser();


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

    public void setStage(Stage stage) {
        this.stage = stage;
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
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            model.createNewFile(file);
        } else {
            LOG.error("Файл не выбран (file == null)");
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
}
