package org.example.dchatclient.UIClasses;

import java.time.ZonedDateTime;

public class Message {
    private long id;
    private long chatId;
    private String sender;
    private String content;
    private ZonedDateTime sentAt;

    public Message(){}

    public Message(long id, long chatId, String sender, String content, ZonedDateTime sentAt) {
        this.id = id;
        this.chatId = chatId;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
    }

    public long getId() { return id; }
    public long getChatId() { return chatId; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public ZonedDateTime getSentAt() { return sentAt; }

    public void setId(long id) { this.id = id; }
    public void setChatId(long chatId) { this.chatId = chatId; }
    public void setSender(String sender) { this.sender = sender; }
    public void setContent(String content) { this.content = content; }
    public void setSentAt(ZonedDateTime sentAt) { this.sentAt = sentAt; }

    @Override
    public String toString() {
        return content;
    }
}
