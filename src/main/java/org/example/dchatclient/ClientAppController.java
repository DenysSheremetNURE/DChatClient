package org.example.dchatclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.dchatclient.JSON.*;
import org.example.dchatclient.UIClasses.Chat;
import org.example.dchatclient.UIClasses.Message;

public class ClientAppController {

    private ClientConnection connection;
    private static String clientUsername;
    private static long clientId;

    private String currentChatUsername; //help variable to identify which chat is open
    //TODO update it while clicking on listview cell

    private final ObservableList<Chat> chatItems = FXCollections.observableArrayList();
    private final ObservableList<Message> allMessages = FXCollections.observableArrayList();
    private final ObservableList<Message> filteredMessages = FXCollections.observableArrayList();

    public static String getClientUserName(){return clientUsername;}
    public static long getClientId(){return clientId;}

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ListView<Chat> chatListView;

    @FXML
    private ListView<Message> messageListView;

    @FXML
    private Button newChatButton;

    @FXML
    private void onNewChatClick(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Chat");
        dialog.setHeaderText("Start a new chat");
        dialog.setContentText("Enter recipient username:");

        dialog.showAndWait().ifPresent(recipient -> {
            recipient = recipient.trim();
            if (recipient.isEmpty() || recipient.equals(clientUsername)) {
                showAlert("Invalid username", "Please enter a valid username different from your own.");
                return;
            }

            try {
                NewChatRequest request = new NewChatRequest(clientUsername, recipient);
                request.command = "NEW_CHAT";

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(request);

                connection.send(json);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to send new chat request.");
            }
        });
    }

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendButton;

    @FXML
    private void sendCurrentMessage(){
        String content = messageInput.getText().trim();
        if(content.isEmpty() || currentChatUsername == null) return;

        Message message = new Message(
                0,
                0,
                clientUsername,
                content,
                java.time.ZonedDateTime.now()
        );

        connection.sendMessage(currentChatUsername, message);

        messageInput.clear();
    }

    @FXML
    private Button attachmentButton;



    @FXML
    public void initialize(){
        //initialization of eventlisteners
        messageInput.setOnAction(event -> sendCurrentMessage());

        chatListView.setItems(chatItems);
        messageListView.setItems(filteredMessages);

        initChats();

        chatListView.setOnMouseClicked(event -> {
            Chat selectedChat = chatListView.getSelectionModel().getSelectedItem();
            if (selectedChat != null) {
                currentChatUsername = selectedChat.getUserName();

                filteredMessages.setAll(
                        allMessages.filtered(this::isMessageForCurrentChat)
                );
            }
        });
        chatListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Chat chat, boolean empty) {
                super.updateItem(chat, empty);
                if (empty || chat == null) {
                    setText(null);
                } else {
                    setText(chat.getUserName());
                }
            }
        });
        //initialization of eventlisteners




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

        connection.listenToServer((msg) -> {
            try{
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                BaseResponse base = mapper.readValue(msg, BaseResponse.class);

                switch (base.getType()){
                    case "MESSAGE" -> {
                        SendMessageResponse incoming = mapper.readValue(msg, SendMessageResponse.class);
                        Message message = incoming.message;
                        Platform.runLater(() -> addIncomingMessage(message));
                    }

                    case "MESSAGES" -> {
                        GetMessageResponse response = mapper.readValue(msg, GetMessageResponse.class);
                        Platform.runLater(() -> {
                            allMessages.setAll(response.messages);
                            filteredMessages.setAll(allMessages.filtered(this::isMessageForCurrentChat));
                            messageListView.scrollTo(filteredMessages.size() - 1);
                        });
                    }

                    case "NEW_CHAT_OK" -> {
                        NewChatResponse response = mapper.readValue(msg, NewChatResponse.class);
                        addNewChat(response.chatId, response.recipientId, response.recipient);
                    }

                    case "NEW_CHAT_ERR" -> {
                        Platform.runLater(() ->
                                showAlert("Chat Error", "Chat creation failed: user not found or already in chat.")
                        );
                    }

                    case "CHATS" -> {
                        GetChatsResponse response = mapper.readValue(msg, GetChatsResponse.class);
                        Platform.runLater(() -> {
                            chatItems.setAll(response.chats);

                            if (!response.chats.isEmpty()) {
                                Chat firstChat = response.chats.getFirst();
                                chatListView.getSelectionModel().select(firstChat);
                                currentChatUsername = firstChat.getUserName();

                                filteredMessages.setAll(
                                        allMessages.filtered(this::isMessageForCurrentChat)
                                );
                            } else {
                                currentChatUsername = null;
                                filteredMessages.clear();
                            }
                        });
                    }

                    default -> System.out.println("Unknown type: " + base.getType());
                }
            } catch (JsonProcessingException e){
                e.printStackTrace();
            }
        });

    }

    public void addIncomingMessage(Message message){
        allMessages.add(message);

        if (isMessageForCurrentChat(message)){
            filteredMessages.add(message);
        }

        messageListView.scrollTo(filteredMessages.size() - 1);

    }

    private boolean isMessageForCurrentChat(Message message){
        if (currentChatUsername == null || message == null || message.getSender() == null)
            return false;

        return message.getSender().equals(currentChatUsername) || message.getSender().equals(clientUsername);
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

        filteredMessages.setAll(
                allMessages.filtered(this::isMessageForCurrentChat)
        );
    }

    public String getClientUsername(){
        return clientUsername;
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void addNewChat(long chatId, long userId, String username){
        Platform.runLater(() -> {
            Chat chat = new Chat(chatId, userId, username);
            chatItems.add(chat);
            chatListView.getSelectionModel().select(chat);
            currentChatUsername = username;

            filteredMessages.setAll(
                    allMessages.filtered(this::isMessageForCurrentChat)
            );
        });

    }

    public void initChats() {
        if (connection != null && clientUsername != null) {
            connection.sendGetChatsRequest(clientUsername);
        }
    }


}