package com.example.whatsappclone.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.ChatListData;

import java.util.ArrayList;

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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ChatListData chatListData = chatDataList.get(i);
        if (!chatDataList.isEmpty()){
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view=layoutInflater.inflate(R.layout.chat_item,null);
                TextView name=view.findViewById(R.id.nameTextView);
                TextView message = view.findViewById(R.id.messageTextView);
                name.setText(chatListData.getNickname());
                message.setText(chatListData.getMessage());
            }
        }
        else {
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view=layoutInflater.inflate(R.layout.chat_item,null);
                TextView name=view.findViewById(R.id.nameTextView);
                TextView message = view.findViewById(R.id.messageTextView);
                name.setText("");
                message.setText("");
            }
        }


        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        if(chatDataList.isEmpty()){
            return 1;
        }
        return chatDataList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
