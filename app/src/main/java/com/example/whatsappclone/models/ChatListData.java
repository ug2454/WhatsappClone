package com.example.whatsappclone.models;

import java.util.Date;

public class ChatListData {
    private String nickname;
    private String message;
    private String userType;
    private Date timestamp;

    public ChatListData(String nickname, String message, String userType, Date timestamp) {
        this.message = message;
        this.nickname = nickname;
        this.userType = userType;
        this.timestamp = timestamp;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
