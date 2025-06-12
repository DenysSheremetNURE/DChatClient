package org.example.dchatclient.JSON;

public class ServerRegisterResponse {
    public String status;
    public String message;

    public ServerRegisterResponse(){}

    public ServerRegisterResponse(String status, String message){
        this.status = status;
        this.message = message;
    }
}
