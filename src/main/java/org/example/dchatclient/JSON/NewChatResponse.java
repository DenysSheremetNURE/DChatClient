package org.example.dchatclient.JSON;

public class NewChatResponse extends BaseResponse{
    public String sender;
    public String recipient;
    public long recipientId;
    public long chatId;

    public NewChatResponse(){}

    public NewChatResponse(String sender, String recipient, long recipientId, long chatId){
        this.sender = sender;
        this.recipient = recipient;
        this.recipientId = recipientId;
        this.chatId = chatId;
    }
}
