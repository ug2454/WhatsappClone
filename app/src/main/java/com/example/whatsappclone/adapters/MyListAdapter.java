package com.example.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.whatsappclone.ChatActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.models.MyListData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MyListAdapter implements ListAdapter {
    ListView parentListView;
    ArrayList<MyListData> myListData;
    Context context;

    public MyListAdapter(Context context, ArrayList<MyListData> myListData, ListView parentListView) {

        this.myListData = myListData;
        this.context = context;
        this.parentListView = parentListView;
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
        return myListData.size();
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
        MyListData myDataList = myListData.get(i);
        if (!myListData.isEmpty()) {
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view = layoutInflater.inflate(R.layout.list_item, parentListView, false);
                TextView textView = view.findViewById(R.id.textView);
                TextView lastMessageTextView = view.findViewById(R.id.lastMessageTextView);
                ImageView imageView = view.findViewById(R.id.userImage);
                textView.setTextSize(15);
                textView.setTextColor(0xFFFFFFFF);
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                textView.setText(myDataList.getUserName().toUpperCase());
                lastMessageTextView.setText(myDataList.getLastMessage());
                if (myDataList.getImageUrl().isEmpty()) {


                    imageView.setImageResource(R.drawable.blankimage);
                   imageView.setBackgroundColor(0xFF172228);
                } else {



                    Picasso.with(context.getApplicationContext())
                            .load(myDataList.getImageUrl())

                            .into(imageView);

                }
                view.setOnClickListener(view1 -> {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("nickname", myDataList.getUserName());
                    intent.putExtra("uid", myDataList.getUid());
                    context.startActivity(intent);

                });
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
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        if (myListData.isEmpty()) {
            return 1;
        }
        return getCount();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
