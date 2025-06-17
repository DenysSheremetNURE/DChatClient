package org.example.dchatclient.JSON;

public class GetChatsRequest extends BaseRequest{
    public String username;

    public GetChatsRequest(){}

    public GetChatsRequest(String username){
        this.command = "GET_CHATS";
        this.username = username;
    }
}
