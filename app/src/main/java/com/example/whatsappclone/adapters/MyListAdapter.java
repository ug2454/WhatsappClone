package com.example.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.ChatActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.models.MyListData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private final ArrayList<MyListData> listData;

    private static final String TAG = "INFO";

    public MyListAdapter(ArrayList<MyListData> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);

        return new ViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (listData.size() != 0) {
            final MyListData myListData = listData.get(position);
            Context context = holder.itemView.getContext();

            holder.itemView.setPadding(50, 20, 0, 20);
            holder.itemView.setBackgroundColor(0xFF172228);
            holder.textView.setTextSize(15);

            holder.textView.setTextColor(0xFFFFFFFF);
            holder.textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            holder.textView.setText(listData.get(position).getUserName().toUpperCase());
//        holder.lastMessageTextView.setText(listData.get(position).getLastMessage());
            if (listData.get(position).getImageUrl().isEmpty()) {


                holder.imageView.setImageResource(R.drawable.blankimage);
                holder.imageView.setBackgroundColor(0xFF172228);
            } else {


                System.out.println(listData.get(position).getImageUrl());
                Picasso.with(context.getApplicationContext())
                        .load(listData.get(position).getImageUrl())

                        .into(holder.imageView);

            }
            holder.linearLayout.setOnClickListener(view -> {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("nickname", myListData.getUserName());
                intent.putExtra("uid", myListData.getUid());
                context.startActivity(intent);

            });
        }

    }


    @Override
    public int getItemCount() {

        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;
        public LinearLayout linearLayout;
//        public TextView lastMessageTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.textView = itemView.findViewById(R.id.textView);
//            this.lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView);
            this.imageView = itemView.findViewById(R.id.userImage);
            linearLayout = itemView.findViewById(R.id.linearLayoutUserList);
        }
    }
}
