package com.example.whatsappclone;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "INFO";
    EditText sendMessageEditText;
    ArrayList<String> messages = new ArrayList<>();
    ListView listView;
    String emailReceiver = "";
    String receiverUid = "";
    ArrayAdapter adapter;
    ConstraintLayout constraintLayout;
    Date currentTime = Calendar.getInstance().getTime();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int messageCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendMessageEditText = findViewById(R.id.sendMessageEditText);
        listView = findViewById(R.id.chatListView);

//        constraintLayout=findViewById(R.id.parent);
//        listView.setOnClickListener((View.OnClickListener) this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        emailReceiver = intent.getStringExtra("email");
        receiverUid = intent.getStringExtra("uid");
        setTitle(emailReceiver);
        centerTitle();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);

        listView.setAdapter(adapter);


        db.collection("message")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(receiverUid)
                .orderBy("messageCount", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        messages.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String messageContent = doc.getString("message");
                            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(doc.getString("uid"))) {
                                messageContent = "> " + messageContent;

                            }

                            messages.add(messageContent);

                        }
                        adapter.notifyDataSetChanged();

                    }
                });


    }


    public void sendMessage(View view) {

        Map<String, Object> messageDetails = new HashMap<>();
        messageDetails.put("message", sendMessageEditText.getText().toString());
        messageDetails.put("senderId", receiverUid);
        messageDetails.put("timestamp", currentTime);
        messageDetails.put("messageCount", messageCount);


        db.collection("message").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(receiverUid).document()
                .set(messageDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        messageCount++;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        db.collection("message").document(receiverUid).collection(FirebaseAuth.getInstance().getCurrentUser().getUid()).document()
                .set(messageDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        sendMessageEditText.setText("");

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if (textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if (textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for (View v : textViews) {
                    if (v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if (appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
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


//    @Override
//    public void onClick(View view) {
//        if(view.getId()==R.id.chatListView){
//            InputMethodManager inm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
//        }
//    }
}