package com.example.whatsappclone;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.adapters.ChatListAdapter;
import com.example.whatsappclone.adapters.SettingsListAdapter;
import com.example.whatsappclone.models.SettingsListData;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    ArrayList<SettingsListData> settingsList = new ArrayList<>();
    ListView settingsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        settingsListView = findViewById(R.id.settingsListView);


        settingsList.add(new SettingsListData("Account",R.drawable.settings));


        SettingsListAdapter chatListAdapter = new SettingsListAdapter(getApplicationContext(), settingsList);
        settingsListView.setAdapter(chatListAdapter);


    }
}