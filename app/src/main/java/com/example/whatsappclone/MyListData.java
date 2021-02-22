package com.example.whatsappclone;

public class MyListData {

    private String userName;
    private String uid;
    private String imageUrl;


    public MyListData(String userName, String uid, String imageUrl) {
        this.userName = userName;
        this.uid = uid;
        this.imageUrl=imageUrl;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
