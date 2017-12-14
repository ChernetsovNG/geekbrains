package ru.geekbrains.client;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.client.utils.ClientUtils;
import ru.geekbrains.common.message.Address;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.geekbrains.common.message.StringCrypter.stringCrypter;

public class Controller implements Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    public HBox authPanel;
    public TextField authLogin;
    public PasswordField authPass;
    public TextField clientTerminal;

    private Model model;

    private static final int PAUSE_MS = 249;
    private static final int THREADS_NUMBER = 1;

    private boolean isAuthorized;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
        try {
            startModel();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private void startModel() {
        String clientAddress = stringCrypter.encrypt(ClientUtils.INSTANCE.getMacAddress());

        model = new Model(new Address(clientAddress));
        model.start();

        model.handshakeOnServer();
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

    public void setAuthorized(boolean isAuthorized) {  // переключаем режим авторизации
        this.isAuthorized = isAuthorized;
        if (!this.isAuthorized) {         // если пользователь не авторизован
            authPanel.setVisible(true);   // включаем панель авторизации
            authPanel.setManaged(true);
        } else {
            authPass.clear();
            authPanel.setVisible(false);  // выключаем панель авторизации
            authPanel.setManaged(false);
        }
    }

    public void writeTextInTerminal(String text) {
        clientTerminal.setText(text);
        clientTerminal.requestFocus();
        clientTerminal.selectEnd();
    }
}
