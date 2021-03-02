package com.example.whatsappclone;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NavUtils;

import com.example.whatsappclone.adapters.ChatListAdapter;
import com.example.whatsappclone.models.ChatListData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();
    EditText sendMessageEditText;
    ArrayList<ChatListData> messages = new ArrayList<>();
    ListView listView;
    String nickname = "";
   static String receiverUid = "";


    String nicknameCurrentUser = "";

    Date currentTime = Calendar.getInstance().getTime();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    long messageCountSender;
    String uid = "";
    String emailFirebaseAuth = "";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        uid = user.getUid();
        emailFirebaseAuth = user.getEmail();
        sendMessageEditText = findViewById(R.id.sendMessageEditText);
        listView = findViewById(R.id.chatListView);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        nickname = intent.getStringExtra("nickname");
        receiverUid = intent.getStringExtra("uid");
        setTitle(nickname);



        getMessageCountFirebase();

        getNickNameFirebase();

        getChatMessagesFirebase();


    }

    private void getChatMessagesFirebase() {
        db.collection("message")
                .document(uid)
                .collection(receiverUid)
                .orderBy("messageCount", Query.Direction.ASCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    messages.clear();
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(value)) {
                        String messageContent = doc.getString("message");
                        String userType = doc.getString("userType");
                        Date timestamp = Objects.requireNonNull(doc.getTimestamp("timestamp")).toDate();
                        assert userType != null;
                        if (userType.equals("receiver")) {
//
                            messages.add(new ChatListData(nickname, messageContent, userType, timestamp));

                        } else {

                            messages.add(new ChatListData(doc.getString("nickname"), messageContent, userType, timestamp));
                        }


                    }
                    ChatListAdapter chatListAdapter = new ChatListAdapter(getApplicationContext(), messages);
                    listView.setAdapter(chatListAdapter);


                });
    }

    private void getNickNameFirebase() {
        db.collection("users").whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            nicknameCurrentUser = (String) document.get("nickname");

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void getMessageCountFirebase() {
        db.collection("messageCount")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            try {
                                messageCountSender = (long) document.get("messageCount");

                            } catch (Exception e) {
                                Log.i(TAG, "onComplete: Error");
                            }

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }


    public void sendMessage(View view) {
        if (!sendMessageEditText.getText().toString().isEmpty()) {
//            db.collection("users").document(receiverUid).update("lastMessage", sendMessageEditText.getText().toString())
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
//                    .addOnFailureListener(e -> Log.d(TAG, "DocumentSnapshot failed to update!"));
            db.collection("messageCount")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                messageCountSender = (long) document.get("messageCount");

                            }
                            Map<String, Object> messageDetails = new HashMap<>();
                            messageDetails.put("message", sendMessageEditText.getText().toString());
                            messageDetails.put("senderId", receiverUid);
                            messageDetails.put("timestamp", currentTime);
                            messageDetails.put("messageCount", messageCountSender);
                            messageDetails.put("userType", "sender");
                            messageDetails.put("email", emailFirebaseAuth);
                            messageDetails.put("nickname", nicknameCurrentUser);


                            db.collection("message").document(uid).collection(receiverUid).document()
                                    .set(messageDetails)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        db.collection("messageCount").document("message")
                                                .update("messageCount", messageCountSender + 1)
                                                .addOnSuccessListener(aVoid1 -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                                    })
                                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                            Map<String, Object> messageDetails1 = new HashMap<>();
                            messageDetails1.put("message", sendMessageEditText.getText().toString());
                            messageDetails1.put("senderId", receiverUid);
                            messageDetails1.put("timestamp", currentTime);
                            messageDetails1.put("messageCount", messageCountSender);
                            messageDetails1.put("userType", "receiver");
                            messageDetails1.put("email", emailFirebaseAuth);
                            messageDetails.put("nickname", nicknameCurrentUser);

                            db.collection("message").document(receiverUid).collection(uid).document()
                                    .set(messageDetails1)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        db.collection("messageCount").document("message")
                                                .update("messageCount", messageCountSender + 1)
                                                .addOnSuccessListener(aVoid12 -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                                    })
                                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));


                            sendMessageEditText.setText("");
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}