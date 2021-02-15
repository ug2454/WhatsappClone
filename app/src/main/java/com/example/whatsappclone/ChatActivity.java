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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NavUtils;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "INFO";
    EditText sendMessageEditText;
    ArrayList<String> messages = new ArrayList<>();
    ListView listView;
    String usernameReceiver = "";
    ArrayAdapter adapter;

    @Override
    protected void onStop() {
        super.onStop();
        messages.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendMessageEditText = findViewById(R.id.sendMessageEditText);
        listView = findViewById(R.id.chatListView);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        usernameReceiver = intent.getStringExtra("data");
        setTitle(usernameReceiver);
        centerTitle();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);

        listView.setAdapter(adapter);
        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient", usernameReceiver);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");

        query1.whereEqualTo("sender", usernameReceiver);
        query1.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");

        query.findInBackground((objects, e) -> {
            if (e == null) {
                if (objects.size() > 0) {
                    System.out.println("ARRAY"+objects.toString());
                    messages.clear();
                    for (ParseObject message : objects) {
                        String messageContent = message.getString("message");

                        if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                            messageContent = "> " + messageContent;
                        }
                        messages.add(messageContent);


                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

    public void sendMessage(View view) {
        ParseObject message = new ParseObject("Message");
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient", usernameReceiver);
        message.put("message", sendMessageEditText.getText().toString());

        message.saveInBackground(e -> {
            if (e == null) {
                Log.i(TAG, "sendMessage: SAVED ");
                messages.add(sendMessageEditText.getText().toString());
                adapter.notifyDataSetChanged();
            }
        });
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


}