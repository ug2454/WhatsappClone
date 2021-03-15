package com.udaygarg.thumbit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.udaygarg.thumbit.adapters.AccountListAdapter;
import com.udaygarg.thumbit.models.AccountListData;

import java.util.ArrayList;


public class AccountActivity extends AppCompatActivity {
    ListView accountListView;
    ArrayList<AccountListData> accountListData = new ArrayList<>();
   public static String imageName;
ProgressBar progressBarActivity;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle("Account");
        accountListView = findViewById(R.id.accountListView);
        progressBarActivity=findViewById(R.id.progressBarActivity);
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.udaygarg.thumbit", Context.MODE_PRIVATE);
        imageName = sharedPreferences.getString("imageName", "");

        accountListData.add(new AccountListData("Delete my account", R.drawable.ic_baseline_delete_24));
        accountListData.add(new AccountListData("Privacy Policy", R.drawable.ic_baseline_lock_24));
        progressBarActivity.setVisibility(View.GONE);
        AccountListAdapter accountListAdapter = new AccountListAdapter(this, accountListData, accountListView,progressBarActivity);
        accountListView.setAdapter(accountListAdapter);


    }
}