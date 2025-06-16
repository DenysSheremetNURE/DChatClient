package org.example.dchatclient.JSON;

import org.example.dchatclient.UIClasses.Message;

public class SendMessageResponse extends BaseResponse{
    public Message message;

    public SendMessageResponse(){}

    public SendMessageResponse(Message message){
        this.type = "MESSAGE";
        this.message = message;
    }
}
