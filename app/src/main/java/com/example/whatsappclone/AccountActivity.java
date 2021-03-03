package com.example.whatsappclone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import com.example.whatsappclone.adapters.AccountListAdapter;
import com.example.whatsappclone.adapters.SettingsListAdapter;
import com.example.whatsappclone.models.AccountListData;

import java.util.ArrayList;


public class AccountActivity extends AppCompatActivity {
ListView accountListView;
ArrayList<AccountListData> accountListData= new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle("Account");
        accountListView=findViewById(R.id.accountListView);

        accountListData.add(new AccountListData("Delete my account",R.drawable.ic_baseline_delete_24));

        AccountListAdapter accountListAdapter = new AccountListAdapter(getApplicationContext(), accountListData);
        accountListView.setAdapter(accountListAdapter);



    }
}