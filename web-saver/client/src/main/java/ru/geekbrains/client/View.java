package ru.geekbrains.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.geekbrains.client.controller.ConnectController;
import ru.geekbrains.client.controller.FileController;

import java.io.IOException;

public class View extends Application {

    @Override
    public void start(Stage loginStage) throws IOException {

        // загружаем контроллеры
        FXMLLoader loginFxmlLoader = new FXMLLoader(getClass().getResource("/login-screen.fxml"));
        FXMLLoader clientFxmlLoader = new FXMLLoader(getClass().getResource("/client-window.fxml"));

        Parent loginScreen = loginFxmlLoader.load();
        Parent clientScreen = clientFxmlLoader.load();

        loginStage.setTitle("JavaFX web-saver login page");
        loginStage.setScene(new Scene(loginScreen, 500, 336));

        ConnectController connectController = loginFxmlLoader.getController();
        FileController fileController = clientFxmlLoader.getController();

        Stage clientStage = new Stage();
        clientStage.setTitle("JavaFX web-saver client page");
        clientStage.setScene(new Scene(clientScreen));

        // создаём модель

        Model model = new Model(connectController, fileController);

        connectController.setModel(model);
        fileController.setModel(model);

        connectController.setStage(loginStage);
        fileController.setStage(clientStage);

        loginStage.show();

        model.start();
        model.handshakeOnServer();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
