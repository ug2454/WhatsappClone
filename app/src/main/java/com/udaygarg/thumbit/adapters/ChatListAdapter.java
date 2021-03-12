package com.udaygarg.thumbit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.udaygarg.thumbit.R;
import com.udaygarg.thumbit.models.ChatListData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatListAdapter implements ListAdapter {
    ArrayList<ChatListData> chatDataList;
    Context context;

    public ChatListAdapter(Context context, ArrayList<ChatListData> chatListData) {

        this.chatDataList = chatListData;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {


    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return chatDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ChatListData chatListData = chatDataList.get(i);
        if (!chatDataList.isEmpty()) {
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view = layoutInflater.inflate(R.layout.chat_item, null);
                TextView name = view.findViewById(R.id.nameTextView);
                TextView message = view.findViewById(R.id.messageTextView);
                TextView timestampTextView = view.findViewById(R.id.timestampTextView);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                String date = sfd.format(new Date(String.valueOf(chatListData.getTimestamp())));
                timestampTextView.setText(date);
                name.setText(chatListData.getNickname());
                message.setText(chatListData.getMessage());
                if (chatListData.getUserType().equals("sender")) {
//                    name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
//                    message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.END;
                    message.setLayoutParams(params);
                    name.setLayoutParams(params);
                    timestampTextView.setLayoutParams(params);
                    message.setGravity(Gravity.START);
                    name.setPadding(400, 0, 50, 10);
                    message.setPadding(400, 0, 50, 10);
                    timestampTextView.setPadding(400, 0, 50, 10);
//                    timestampTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

                } else {

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.START;
                    message.setLayoutParams(params);
                    name.setLayoutParams(params);
                    timestampTextView.setLayoutParams(params);
                    message.setGravity(Gravity.START);
//                    name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//                    message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//                    timestampTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    message.setPadding(60, 0, 400, 10);
                    name.setPadding(60, 0, 400, 10);
                    timestampTextView.setPadding(60, 0, 400, 10);
                }
            }
        } else {
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view = layoutInflater.inflate(R.layout.chat_item, null);
                TextView name = view.findViewById(R.id.nameTextView);
                TextView message = view.findViewById(R.id.messageTextView);
                name.setText("");
                message.setText("");
            }
        }


        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        if (chatDataList.isEmpty()) {
            return 1;
        }
        return getCount();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
