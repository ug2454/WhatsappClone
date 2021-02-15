package com.example.whatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import static com.parse.ParseUser.logOut;

public class UserListActivity extends AppCompatActivity {

    ArrayList<MyListData> userArrayList = new ArrayList<>();

    private static final String TAG = "INFO";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.logout) {
            logOut();
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground((objects, e) -> {
            if (e == null && objects.size() > 0) {
                for (int i = 0; i < objects.size(); i++) {
                    userArrayList.add(new MyListData(objects.get(i).getUsername()));
                    Log.i(TAG, "onCreate: " + objects.get(i).getUsername());

                }
                RecyclerView recyclerView = findViewById(R.id.userRecyclerView);
                MyListAdapter myListAdapter = new MyListAdapter(userArrayList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(myListAdapter);
            }
        });


    }
}