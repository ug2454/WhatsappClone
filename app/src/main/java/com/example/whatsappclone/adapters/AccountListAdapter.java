package com.example.whatsappclone.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.AccountListData;

import java.util.ArrayList;

public class AccountListAdapter implements ListAdapter {
    private final ArrayList<AccountListData> listData;
    Context context;

    public AccountListAdapter(Context context, ArrayList<AccountListData> listData) {
        this.context = context;
        this.listData = listData;
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
        return listData.size();
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
        AccountListData accountListData = listData.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.accounts_item, null);
            TextView accountTextView = view.findViewById(R.id.accountsText);
            ImageView accountImageView = view.findViewById(R.id.accountsImage);
            accountTextView.setText(accountListData.getAccountText());
            accountImageView.setImageResource(accountListData.getAccountImage());

            LinearLayout accountLinearLayout= view.findViewById(R.id.linearLayoutAccount);
            accountLinearLayout.setOnClickListener(view1 -> {
                Toast.makeText(context, ""+accountListData.getAccountText(), Toast.LENGTH_SHORT).show();
            });
        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return listData.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
