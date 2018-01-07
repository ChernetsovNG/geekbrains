package ru.geekbrains.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.Model;

public class ConnectController {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectController.class);

    @FXML
    private Label statusLabel;
    @FXML
    private TextField authLogin;
    @FXML
    private PasswordField authPass;

    private Model model;

    private Stage stage;

    public void setModel(Model model) {
        this.model = model;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void createAccount() {
        if (model != null) {
            String username = authLogin.getText();
            String password = authPass.getText();
            model.registerNewClient(username, password);
        } else {
            LOG.error("Модель не инициализирована");
        }
    }

    public void loginClient() {
        if (model != null) {
            String username = authLogin.getText();
            String password = authPass.getText();
            model.authOnServer(username, password);
        } else {
            LOG.error("Модель не инициализирована");
        }
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    public void authClient() {
        Platform.runLater(() -> stage.hide());
        model.authClient();
    }
}
