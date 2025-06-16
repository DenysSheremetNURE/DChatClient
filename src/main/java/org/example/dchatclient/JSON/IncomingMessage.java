package org.example.dchatclient.JSON;

import org.example.dchatclient.UIClasses.Message;

public class IncomingMessage extends BaseResponse{
    public Message data;

    public IncomingMessage() {}

    public Message getData() {
        return data;
    }
}
