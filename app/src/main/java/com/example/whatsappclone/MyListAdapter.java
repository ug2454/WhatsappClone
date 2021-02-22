package com.example.whatsappclone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.Color;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private final ArrayList<MyListData> listData;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        final MyListData myListData = listData.get(position);
        Context context = holder.itemView.getContext();

        holder.itemView.setPadding(50,20,0,20);
        holder.itemView.setBackgroundColor(0xFF172228);
        holder.textView.setTextSize(15);

        holder.textView.setTextColor(0xFFFFFFFF);
        holder.textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        holder.textView.setText(listData.get(position).getUserName().toUpperCase());

        if(listData.get(position).getImageUrl().isEmpty()){
            System.out.println("IN IF");
            holder.imageView.setImageResource(R.drawable.blankimage);
            holder.imageView.setBackgroundColor(0xFF172228);
        }
        else{
            System.out.println(listData.get(position).getImageUrl());
            Picasso.with(context.getApplicationContext())
                    .load(listData.get(position).getImageUrl())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.imageView);

        }
        holder.relativeLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("nickname", myListData.getUserName());
            intent.putExtra("uid", myListData.getUid());
            context.startActivity(intent);

        });
    }




    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            this.textView = itemView.findViewById(R.id.textView);
            this.imageView=itemView.findViewById(R.id.userImage);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
