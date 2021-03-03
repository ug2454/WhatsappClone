package com.example.whatsappclone;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.adapters.SettingsListAdapter;
import com.example.whatsappclone.models.SettingsListData;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    ArrayList<SettingsListData> settingsList = new ArrayList<>();
    ListView settingsListView;
    TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        settingsListView = findViewById(R.id.settingsListView);
        userNameTextView = findViewById(R.id.settingsUserNameTextView);

        userNameTextView.setText(getSharedPreferences("com.example.whatsappclone",MODE_PRIVATE).getString("nickname",""));


        settingsList.add(new SettingsListData("Account", R.drawable.ic_baseline_account_box_24));


        SettingsListAdapter chatListAdapter = new SettingsListAdapter(getApplicationContext(), settingsList);
        settingsListView.setAdapter(chatListAdapter);


    }
}