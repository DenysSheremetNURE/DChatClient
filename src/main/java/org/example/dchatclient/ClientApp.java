package org.example.dchatclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/dchatclient/images/DChat-icon-image.png")));

        stage.setTitle("DChat login");
        stage.setScene(scene);
        stage.setResizable(false);


        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}