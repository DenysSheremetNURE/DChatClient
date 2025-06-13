package org.example.dchatclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.dchatclient.UIClasses.Chat;
import org.example.dchatclient.UIClasses.Message;

public class ClientAppController {

    private ClientConnection connection;
    private static String clientUsername;
    private static long clientId;

    private final ObservableList<Chat> chatItems = FXCollections.observableArrayList();
    private final ObservableList<Message> allMessages = FXCollections.observableArrayList();
    private final ObservableList<Message> filteredMessages = FXCollections.observableArrayList();

    public static String getClientUserName(){return clientUsername;}
    public static long getClientId(){return clientId;}

    @FXML
    private ListView<Chat> chatListView;

    @FXML
    private ListView<Message> messageListView;

    @FXML
    private Label usernameProfileLabel;

    @FXML
    private Button newChatButton;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button attachmentButton;



    @FXML
    public void initialize(){
        chatListView.setItems(chatItems);
        messageListView.setItems(filteredMessages);



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

    public void setClientUsername(String username){clientUsername = username;}

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

    public void initMessages(){
        if (connection != null && clientUsername != null) {
            allMessages.setAll(connection.sendGetMessagesRequest(clientUsername));
        }
    }

    public String getClientUsername(){
        return clientUsername;
    }

    @FXML
    private AnchorPane rootPane;


}