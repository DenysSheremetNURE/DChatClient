package org.example.dchatclient.JSON;

public class GetMessageRequest extends BaseRequest{
    public String userName;

    public GetMessageRequest(){}

    public GetMessageRequest(String command, String userName){
        this.command = command;
        this.userName = userName;
    }
}
