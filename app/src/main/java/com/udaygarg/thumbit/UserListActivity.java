package com.udaygarg.thumbit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.udaygarg.thumbit.adapters.MyListAdapter;
import com.udaygarg.thumbit.models.MyListData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.udaygarg.thumbit.MainActivity.currentUserId;

public class UserListActivity extends AppCompatActivity {

    static ArrayList<MyListData> userArrayList = new ArrayList<>();
    String uid = "";
    String uniqueid = "";

    ProgressBar progressBarUserList;
    ArrayList<String> friendsList = new ArrayList<>();
    ArrayList<String> friendId = new ArrayList<>();
    ListView userListView;
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
        } else if (item.getItemId() == R.id.settings) {
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
        userListView = findViewById(R.id.userListView);
        progressBarUserList = findViewById(R.id.progressBarUserList);
        progressBarUserList.setVisibility(View.VISIBLE);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        setTitle("ThumbIt");
        Log.i(TAG, "onCreate: " + currentUserId);
        addNewFriendFloatingActionButton = findViewById(R.id.addNewFriendFloatingActionButton);

        addNewFriendFloatingActionButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.add_new_friend_dialog, viewGroup, false);   //customized dialog
            builder.setView(dialogView);
            builder
                    .setPositiveButton("Add Friend", (dialogInterface, i) -> {

                        editText = dialogView.findViewById(R.id.friendIDEditText);
                        editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                        uniqueid = editText.getText().toString();
                        if (!uniqueid.trim().equals("")) {
                            addFriend(editText.getText().toString());
                        } else {
                            Toast.makeText(this, "Please enter a valid ID", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teal_200));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teal_200));
        });

        userArrayList.clear();
        friendsList.clear();
        friendId.clear();




        db.collection("users").document(uid).collection("userFriends")

                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            friendsList.add(document.getString("uniqueID"));
                            friendId.add(document.getString("uid"));
                            Log.d(TAG, document.getId() + " => " + document.getData());


                        }
                        if (friendsList.size() != 0) {
                            for (int i = 0; i < friendsList.size(); i++) {


                                db.collection("users").whereEqualTo("uniqueID", friendsList.get(i)).get().addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot document1 : queryDocumentSnapshots) {


                                        userArrayList.add(new MyListData(
                                                document1.getString("nickname"),
                                                document1.getString("uid"),
                                                document1.getString("imageUrl"),
                                                ""
                                        ));


                                    }


                                    MyListAdapter myListAdapter = new MyListAdapter(this, userArrayList, userListView);
                                    userListView.setAdapter(myListAdapter);

                                }).addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                            }


                        }
                        progressBarUserList.setVisibility(View.GONE);


                    }
                });

    }

    public void addFriend(String uniqueId) {
        if (!friendsList.contains(uniqueId)) {
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.udaygarg.thumbit", Context.MODE_PRIVATE);

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
                                        document.getString("imageUrl"),
                                        ""
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
                            MyListAdapter myListAdapter = new MyListAdapter(this, userArrayList, userListView);
                            userListView.setAdapter(myListAdapter);
                            //
                        } else {

                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }).addOnFailureListener(e -> {

                Log.d(TAG, "addFriend: FRIEND ID DOES NOT EXIST");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Friend Already Exists", Toast.LENGTH_SHORT).show();
        }

    }


}