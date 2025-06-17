package org.example.dchatclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.dchatclient.JSON.*;
import org.example.dchatclient.UIClasses.Message;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

public class ClientConnection {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private final ObjectMapper mapper;

    public ClientConnection(String host, int port){
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try{
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    public String sendRequest(String message) {
        try {
            out.write(message);
            out.newLine();
            out.flush();
            return in.readLine();
        } catch (IOException e) {
            return "Server: no response";
        }
    }

    public void send(String message) {
        try {
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            System.err.println("Send failed: " + e.getMessage());
        }
    }

    public void sendMessage(String recipient, Message message){
        try{
            SendMessageRequest request = new SendMessageRequest(recipient, message);
            String json = mapper.writeValueAsString(request);
            send(json);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }

    public ServerLoginResponse sendLoginRequest(String username, String password){
        try{

            LoginRequest request = new LoginRequest();
            request.command = "LOGIN";
            request.username = username;
            request.password = password;

            String json = mapper.writeValueAsString(request);

            String responseJson = sendRequest(json);
            return mapper.readValue(responseJson, ServerLoginResponse.class);

        }  catch (IOException e) {
            return new ServerLoginResponse("ERROR", "Unexpected error occurred.");
        }
    }

    public ServerRegisterResponse sendRegisterRequest(String username, String password){
        try{

            RegisterRequest request = new RegisterRequest();
            request.command = "REGISTER";
            request.username = username;
            request.password = password;

            String json = mapper.writeValueAsString(request);

            String responseJson = sendRequest(json);
            return mapper.readValue(responseJson, ServerRegisterResponse.class);

        } catch (IOException e){
            return new ServerRegisterResponse("ERROR", "Unexpected error occurred.");
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void sendLogoutRequest() throws JsonProcessingException {
        LogoutRequest logout = new LogoutRequest();

        logout.command = "LOGOUT";
        logout.user = socket.toString();

        String json = mapper.writeValueAsString(logout);

        sendRequest(json);
    }

    public void sendDisconnectRequest() throws JsonProcessingException{
        DisconnectRequest disconnect = new DisconnectRequest();

        disconnect.command = "DISCONNECT";
        disconnect.client = socket.getInetAddress().toString();

        String json = mapper.writeValueAsString(disconnect);

        sendRequest(json);
    }

    public void close() {
        try {
            in.close();
            out.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }

    public void sendGetMessagesRequest(String userName){
        try{
            GetMessageRequest request = new GetMessageRequest("GET_MESSAGES", userName);
            String json = mapper.writeValueAsString(request);
            send(json);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenToServer(Consumer<String> messageHandler){
        new Thread(() -> {
            try{
                String line;
                while((line = in.readLine()) != null){
                    //TODO delete
                    System.out.println("Raw from server: " + line);
                    //
                    messageHandler.accept(line);
                }
            } catch (IOException e){
                System.err.println("Stream reading error: " + e.getMessage());
            }
        }).start();
    }

    public void sendGetChatsRequest(String username) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            GetChatsRequest request = new GetChatsRequest(username);
            String json = mapper.writeValueAsString(request);
            send(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
