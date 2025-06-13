package org.example.dchatclient.UIClasses;

public class Chat {
    private final long id;
    private final long userId;
    private final String userName;

    public Chat(long id, long userId, String userName){
        this.id = id;
        this.userId = userId;
        this.userName = userName;
    }

    public long getId() {return id;}

    public long getUserId() {return userId;}

    public String getUserName(){
        return userName;
    }



    @Override
    public String toString(){
        return userName;
    }
}
