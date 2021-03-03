package com.example.whatsappclone.models;

public class AccountListData {
    private String accountText;
    private int accountImage;

    public AccountListData(String accountText, int accountImage) {
        this.accountText = accountText;
        this.accountImage = accountImage;
    }

    public int getAccountImage() {
        return accountImage;
    }

    public void setAccountImage(int accountImage) {
        this.accountImage = accountImage;
    }

    public String getAccountText() {
        return accountText;
    }

    public void setAccountText(String accountText) {
        this.accountText = accountText;
    }
}
