package org.example.dchatclient.JSON;

public class ServerLoginResponse {
    public String status;
    public String message;

    public ServerLoginResponse(){}

    public ServerLoginResponse(String status, String message){
        this.status = status;
        this.message = message;
    }
}
