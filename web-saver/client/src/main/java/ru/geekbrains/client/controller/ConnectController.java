package ru.geekbrains.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectController implements Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectController.class);

    @FXML
    private Label statusLabel;
    @FXML
    private TextField authLogin;
    @FXML
    private PasswordField authPass;

    private boolean isAuthorized;

    private Model model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthentificate(false);
    }

    public void setModel(Model model) {
        this.model = model;
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

    public void setAuthentificate(boolean isAuthorized) {  // переключаем режим авторизации
        this.isAuthorized = isAuthorized;
        if (this.isAuthorized) {         // если пользователь не авторизован
            authPass.clear();
            model.createClientFolder();
        }
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }
}
