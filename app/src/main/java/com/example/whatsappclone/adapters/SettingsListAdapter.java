package com.example.whatsappclone.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.SettingsListData;

import java.util.ArrayList;

public class SettingsListAdapter implements ListAdapter {
    private final ArrayList<SettingsListData> listData;
    Context context;

    public SettingsListAdapter(Context context, ArrayList<SettingsListData> listData) {
        this.listData = listData;
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
        SettingsListData settingsListData = listData.get(i);

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.settings_item, null);
            TextView settingsText = view.findViewById(R.id.settingsText);
            ImageView imageView = view.findViewById(R.id.settingsImage);
            Drawable resImg = ResourcesCompat.getDrawable(context.getResources(), settingsListData.getImage(), null);
            settingsText.setText(settingsListData.getSettingsText());
            imageView.setImageDrawable(resImg);
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
