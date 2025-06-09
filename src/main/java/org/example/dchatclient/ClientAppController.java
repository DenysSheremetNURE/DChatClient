package org.example.dchatclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ClientAppController {

    private ClientConnection connection;
    private String clientUsername;

    public void setConnection(ClientConnection connection){
        this.connection = connection;

        Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (connection != null) {
                    connection.close();
                }
            });
        });
    }

    public void setClientUsername(String username){
        this.clientUsername = username;
    }

    public String getClientUsername(){
        return clientUsername;
    }

    @FXML
    private Parent rootPane;


}