package com.example.whatsappclone.models;

public class ChatListData {
    private String nickname;
    private String message;

    public ChatListData(String nickname,String message){
        this.message=message;
        this.nickname=nickname;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
