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
    public void start(Stage primaryStage) throws IOException {

        // загружаем контроллеры
        FXMLLoader loginFxmlLoader = new FXMLLoader(getClass().getResource("/login-screen.fxml"));
        FXMLLoader clientFxmlLoader = new FXMLLoader(getClass().getResource("/client-window.fxml"));

        Parent loginScreen = loginFxmlLoader.load();
        Parent clientScreen = clientFxmlLoader.load();

        primaryStage.setTitle("JavaFX web-saver login page");
        primaryStage.setScene(new Scene(loginScreen, 500, 336));

        ConnectController connectController = loginFxmlLoader.getController();
        FileController fileController = clientFxmlLoader.getController();

        // создаём модель

        Model model = new Model(connectController, fileController);

        connectController.setModel(model);
        fileController.setModel(model);

        primaryStage.show();

        model.start();
        model.handshakeOnServer();

        /*
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client-window.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("JavaFX web-saver client");
        primaryStage.setScene(new Scene(root, 1024, 768));

        ClientController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.show();
        */
    }

    public static void main(String[] args) {
        launch(args);
    }
}
