package org.example.dchatclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ClientAppController {

    private ClientConnection connection;
    private String clientUsername;

    @FXML
    public void initialize(){

    }

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

    public void setClientUsername(String username){this.clientUsername = username;}

    public void setLogoutHandler(){
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            if (connection != null) {
                try {
                    connection.sendLogoutRequest();
                    connection.close();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getClientUsername(){
        return clientUsername;
    }

    @FXML
    private AnchorPane rootPane;


}