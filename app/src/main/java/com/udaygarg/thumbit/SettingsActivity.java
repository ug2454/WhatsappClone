package com.udaygarg.thumbit;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.udaygarg.thumbit.adapters.SettingsListAdapter;
import com.udaygarg.thumbit.models.SettingsListData;

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

        userNameTextView.setText(getSharedPreferences("com.udaygarg.thumbit",MODE_PRIVATE).getString("nickname",""));


        settingsList.add(new SettingsListData("Account", R.drawable.ic_baseline_account_box_24));


        SettingsListAdapter chatListAdapter = new SettingsListAdapter(getApplicationContext(), settingsList);
        settingsListView.setAdapter(chatListAdapter);


    }
}