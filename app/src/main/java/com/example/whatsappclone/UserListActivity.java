package com.example.whatsappclone;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.adapters.MyListAdapter;
import com.example.whatsappclone.models.MyListData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.whatsappclone.MainActivity.currentUserId;

public class UserListActivity extends AppCompatActivity {

    static ArrayList<MyListData> userArrayList = new ArrayList<>();
    String uid = "";
    String uniqueid = "";
    ProgressBar progressBarUserList;
    ArrayList<String> friendsList = new ArrayList<>();
    FloatingActionButton addNewFriendFloatingActionButton;
    private static final String TAG = UserListActivity.class.getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText editText;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.refresh) {
            refresh();
        } else if (item.getItemId() == R.id.profile) {
            openProfile();
        }
        else if(item.getItemId()==R.id.settings){
            openSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


    private void refresh() {
        finish();
        startActivity(getIntent());
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        progressBarUserList=findViewById(R.id.progressBarUserList);
        progressBarUserList.setVisibility(View.VISIBLE);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        setTitle("WhatsUp");
        Log.i(TAG, "onCreate: " + currentUserId);
        addNewFriendFloatingActionButton = findViewById(R.id.addNewFriendFloatingActionButton);
        System.out.println("REFRESH");
        addNewFriendFloatingActionButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.add_new_friend_dialog, viewGroup, false);
            builder.setView(dialogView);
            builder
                    .setPositiveButton("Add Friend", (dialogInterface, i) -> {

                        editText = dialogView.findViewById(R.id.friendIDEditText);
                        uniqueid = editText.getText().toString();
                        addFriend(editText.getText().toString());
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teal_200));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teal_200));
        });

        userArrayList.clear();
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("userFriends")

                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            friendsList.add(document.getString("uniqueID"));
                            Log.d(TAG, document.getId() + " => " + document.getData());


                        }
                        if (friendsList.size() != 0) {
                            for (int i = 0; i < friendsList.size(); i++) {
                                db.collection("users").whereEqualTo("uniqueID", friendsList.get(i)).orderBy("nickname").get().addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot document1 : queryDocumentSnapshots) {

                                        userArrayList.add(new MyListData(
                                                document1.getString("nickname"),
                                                document1.getString("uid"),
                                                document1.getString("imageUrl")
//                                    document.getString("lastMessage")
                                        ));
                                    }


                                    RecyclerView recyclerView = findViewById(R.id.userRecyclerView);
                                    MyListAdapter myListAdapter = new MyListAdapter(userArrayList);
//
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    recyclerView.setAdapter(myListAdapter);

                                }).addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                            }
                        }
                        else{
                            progressBarUserList.setVisibility(View.GONE);
                        }


                    }
                });

    }

    public void addFriend(String uniqueId) {
        if (!friendsList.contains(uniqueId)) {
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.whatsappclone", Context.MODE_PRIVATE);

            db.collection("users").whereEqualTo("uniqueID", uniqueId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                /*Add current data in friends users' subcollection*/
                                db.collection("users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("userFriends").document(document.getString("uniqueID")).set(document.getData())
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e)
                                        );
                                userArrayList.add(new MyListData(
                                        document.getString("nickname"),
                                        document.getString("uid"),
                                        document.getString("imageUrl")
//                                    document.getString("lastMessage")
                                ));

                                /*Add friend data in current users' subcollection*/

                                db.collection("users").whereEqualTo("uniqueID", sharedPreferences.getString("uniqueID", "")).get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                                db.collection("users")
                                                        .document(document.getString("uid"))
                                                        .collection("userFriends").document(sharedPreferences.getString("uniqueID", "")).set(doc.getData())
                                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e)
                                                        );
                                            }
                                        });
                            }

                            RecyclerView recyclerView = findViewById(R.id.userRecyclerView);
                            MyListAdapter myListAdapter = new MyListAdapter(userArrayList);
//
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(myListAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
        else{
            Toast.makeText(this, "Friend Already Exists", Toast.LENGTH_SHORT).show();
        }

    }



}