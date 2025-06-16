package org.example.dchatclient.JSON;

public class NewChatRequest extends BaseRequest{
    public String sender;
    public String recipient;

    public NewChatRequest(){}

    public NewChatRequest(String sender, String recipient){
        this.sender = sender;
        this.recipient = recipient;
    }
}
