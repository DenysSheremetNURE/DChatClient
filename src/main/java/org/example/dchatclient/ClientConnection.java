package org.example.dchatclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.dchatclient.JSON.*;
import org.example.dchatclient.UIClasses.Message;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientConnection {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ClientConnection(String host, int port){
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

    public ServerLoginResponse sendLoginRequest(String username, String password){
        try{
            ObjectMapper mapper = new ObjectMapper();

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
            ObjectMapper mapper = new ObjectMapper();

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
        ObjectMapper mapper = new ObjectMapper();
        LogoutRequest logout = new LogoutRequest();

        logout.command = "LOGOUT";
        logout.user = socket.toString();

        String json = mapper.writeValueAsString(logout);

        sendRequest(json);
    }

    public void sendDisconnectRequest() throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
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

    public List<Message> sendGetMessagesRequest(String userName){
        try{
            ObjectMapper mapper = new ObjectMapper();
            GetMessageRequest request = new GetMessageRequest("GET_MESSAGES", userName);

            String json = mapper.writeValueAsString(request);
            String responseJson = sendRequest(json);

            return mapper.readValue(responseJson, mapper.getTypeFactory().constructCollectionType(List.class, Message.class));
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
