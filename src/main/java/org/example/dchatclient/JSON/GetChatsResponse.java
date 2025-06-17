package org.example.dchatclient.JSON;

import org.example.dchatclient.UIClasses.Chat;

import java.util.List;

public class GetChatsResponse extends BaseResponse{
    public List<Chat> chats;

    public GetChatsResponse(){}

    public GetChatsResponse(List<Chat> chats) {
        this.type = "CHATS";
        this.chats = chats;
    }
}
