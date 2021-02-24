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

import com.example.whatsappclone.adapters.MyListAdapter;
import com.example.whatsappclone.models.MyListData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.whatsappclone.MainActivity.currentUserId;

public class UserListActivity extends AppCompatActivity {

    static ArrayList<MyListData> userArrayList = new ArrayList<>();
    String uid = "";

    private static final String TAG = UserListActivity.class.getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


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
        } else if (item.getItemId() == R.id.refresh) {
            refresh();
        } else if (item.getItemId() == R.id.profile) {
            openProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void refresh() {
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        setTitle("WhatsApp");
        Log.i(TAG, "onCreate: " + currentUserId);

        System.out.println("REFRESH");


        userArrayList.clear();


    }

    @Override
    protected void onResume() {
        userArrayList.clear();
        super.onResume();
        db.collection("users")
                .whereNotEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            userArrayList.add(new MyListData(
                                    document.getString("nickname"),
                                    document.getString("uid"),
                                    document.getString("imageUrl"),
                                    document.getString("lastMessage")));

                        }
                        Log.i(TAG, "onCreate: " + userArrayList.toString());

                        RecyclerView recyclerView = findViewById(R.id.userRecyclerView);
                        MyListAdapter myListAdapter = new MyListAdapter(userArrayList);
//
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(myListAdapter);


                    }
                });
    }
}