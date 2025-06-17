package org.example.dchatclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.dchatclient.JSON.ServerLoginResponse;
import org.example.dchatclient.JSON.ServerRegisterResponse;

import java.io.IOException;

public class LoginController {

    private final String host = "localhost";
    private final int port = 8080;
    private ClientConnection connection;

    @FXML
    public void initialize(){
        new Thread(()->{
            try{
                connection = new ClientConnection(host, port);
                if (!connection.isConnected()) {
                    Platform.runLater(() -> statusLabel.setText("Cannot connect to server"));
                    return;
                }

            } catch (Exception e){
                Platform.runLater(() -> statusLabel.setText("Server is not running"));
                e.printStackTrace();
            }
        }).start();

        usernameField.textProperty().addListener((obs, oldText, newText) -> statusLabel.setText(""));
        passwordField.textProperty().addListener((obs, oldText, newText) -> statusLabel.setText(""));

        passwordField.setOnAction(e -> onLoginClick());

        Platform.runLater(() -> usernameField.requestFocus());

        Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if(connection != null){
                    try{
                        connection.sendDisconnectRequest();
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });

    }

    @FXML
    private Parent rootPane;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private void onLoginClick() {
        loginButton.setDisable(true);

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        String valUsername = usernameValidation(username);
        String valPassword = passwordValidation(password);

        if(!valUsername.isEmpty()){
            statusLabel.setText(valUsername);
            Platform.runLater(() -> loginButton.setDisable(false));
            return;
        }

        if (!valPassword.isEmpty()){
            statusLabel.setText(valPassword);
            Platform.runLater(() -> loginButton.setDisable(false));
            return;
        }

        new Thread(()->{
            try{
                ServerLoginResponse response = connection != null && connection.isConnected() ? connection.sendLoginRequest(username, password) : new ServerLoginResponse("ERROR", "Server is not available.");

                Platform.runLater(() -> {
                    if (response.status.equals("OK")){
                        try{
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/dchatclient/client.fxml"));
                            Parent root = loader.load();
                            Scene scene = new Scene(root);

                            Stage stage = (Stage) loginButton.getScene().getWindow();
                            stage.setWidth(1020);
                            stage.setHeight(800);

                            stage.setMinWidth(1020);
                            stage.setMinHeight(800);
                            stage.setTitle("DChat " + username);

                            ClientAppController clientAppController = loader.getController();
                            clientAppController.setClientUsername(username);
                            clientAppController.setConnection(connection);



                            statusLabel.setText(response.message);
                            SceneManager.changeToScene(scene, usernameField);

                            Platform.runLater(() -> {
                                clientAppController.setLogoutHandler();
                            });
                        } catch (IOException e) {
                            statusLabel.setText("Unexpected error occurred.");
                            e.printStackTrace();
                        }
                    } else {
                        statusLabel.setText(response.message);
                    }
                    loginButton.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Server connection error.");
                    loginButton.setDisable(false);
                });
            }

        }).start();
    }

    @FXML
    private void onRegisterClick() {
        registerButton.setDisable(true);

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        String valUsername = usernameValidation(username);
        String valPassword = passwordValidation(password);

        if(!valUsername.isEmpty()){
            statusLabel.setText(valUsername);
            Platform.runLater(() -> registerButton.setDisable(false));
            return;
        }

        if (!valPassword.isEmpty()){
            statusLabel.setText(valPassword);
            Platform.runLater(() -> registerButton.setDisable(false));
            return;
        }

        new Thread(() -> {
            try{
                ServerRegisterResponse response = connection != null && connection.isConnected() ? connection.sendRegisterRequest(username, password) : new ServerRegisterResponse("ERROR", "Server is not available.");

                Platform.runLater(() -> {
                    if (response.status.equals("OK")){
                        try{
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/dchatclient/client.fxml"));
                            Parent root = loader.load();
                            Scene scene = new Scene(root);

                            Stage stage = (Stage) registerButton.getScene().getWindow();
                            stage.setWidth(1100);
                            stage.setHeight(800);

                            stage.setMinWidth(1100);
                            stage.setMinHeight(800);
                            stage.setTitle("DChat " + username);

                            ClientAppController clientAppController = loader.getController();
                            clientAppController.setClientUsername(username);
                            clientAppController.setConnection(connection);


                            statusLabel.setText(response.message);
                            SceneManager.changeToScene(scene, usernameField);

                            Platform.runLater(() -> {
                                clientAppController.setLogoutHandler();
                            });
                        } catch (IOException e) {
                            statusLabel.setText("Unexpected error occurred.");
                            e.printStackTrace();
                        }
                    } else {
                        statusLabel.setText(response.message);
                    }
                    registerButton.setDisable(false);
                });
            } catch (Exception e){
                Platform.runLater(() -> {
                    statusLabel.setText("Server connection error.");
                    registerButton.setDisable(false);
                });
            }


        }).start();
    }


    private String passwordValidation(String password){

        if(password.length() < 6 || password.length() > 30){
            return "Invalid password length (6 - 30)";
        }

        if (!password.matches(".*[A-Z].*")) {
            return "Password must have at least one capital letter";
        }

        if (!password.matches(".*[a-z].*")) {
            return "Password must have at least one letter";
        }

        if (!password.matches(".*[-_+].*")) {
            return "Password must contain at least one symbol like -_+";
        }

        if (!password.matches("^[a-zA-Z0-9\\-_+]+$")){
            return "Password contains restricted symbols";
        }

        return "";

    }

    private String usernameValidation(String username){

        if(username.length() < 4 || username.length() > 15){
            return "Invalid username length (4 - 15)";
        }

        if (!username.matches("^[a-zA-Z0-9]+$")){
            return "Username must be characters a-z and numbers only";
        }

        return "";

    }
}
