package ru.geekbrains.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class View extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main-window.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("JavaFX web-saver client");
        primaryStage.setScene(new Scene(root, 1024, 768));

        Controller controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
