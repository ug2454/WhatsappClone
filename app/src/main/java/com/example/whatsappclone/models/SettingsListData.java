package com.example.whatsappclone.models;

import android.graphics.drawable.Drawable;

public class SettingsListData {

    private String settingsText;
    private int image;


    public SettingsListData(String settingsText, int image) {
        this.settingsText = settingsText;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getSettingsText() {
        return settingsText;
    }

    public void setSettingsText(String settingsText) {
        this.settingsText = settingsText;
    }
}
