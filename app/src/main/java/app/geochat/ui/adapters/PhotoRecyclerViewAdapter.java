package app.geochat.ui.adapters;

/**
 * Created by akshaymehta on 06/09/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.android.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;


import app.geochat.R;
import app.geochat.util.Utils;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.PhotoCustomViewHolder> {
    private ArrayList<String> photoItemList;
    private ArrayList<Boolean> selectedList = new ArrayList<Boolean>();
    private Activity activity;
    private String item;
    private int position;
    private boolean isSelected;
    private ArrayList keys;

    public PhotoRecyclerViewAdapter(Activity activity, ArrayList<String> photoItemList) {
        this.photoItemList = photoItemList;
        this.activity = activity;
        prepareSelectionList();
    }

    private void prepareSelectionList() {
        for (int i = 0; i < photoItemList.size(); i++)
            selectedList.add(i, false);
    }


    @Override
    public void onBindViewHolder(PhotoCustomViewHolder holder, int i) {
        item = photoItemList.get(i);
        position = i;
        if (!item.isEmpty())
            Picasso.with(activity).load(new File(item)).resize(100,100).centerCrop().into(holder.geoChatImageView);
        else
            Picasso.with(activity).load(R.drawable.travel_bg).resize(100,100).centerCrop().into(holder.geoChatImageView);

        if (selectedList.get(i))
            holder.tickImageView.setVisibility(View.VISIBLE);
        else
            holder.tickImageView.setVisibility(View.GONE);
    }

    @Override
    public PhotoCustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_layout_row, null);
        PhotoCustomViewHolder viewHolder = new PhotoCustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return (null != photoItemList ? photoItemList.size() : 0);
    }

    public void addMore() {
        selectedList.set(0,true);
        notifyDataSetChanged();
    }

    public String getSelectedImage() {
        int position = selectedList.indexOf(true);
        return photoItemList.get(position);
    }

    public class PhotoCustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView geoChatImageView, tickImageView;

        public PhotoCustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            geoChatImageView = (ImageView) view.findViewById(R.id.geoChatImageView);
            tickImageView = (ImageView) view.findViewById(R.id.tickImageView);
        }

        @Override
        public void onClick(View v) {
            if (selectedList.get(this.getAdapterPosition())) {
                selectedList.set(this.getAdapterPosition(), false);
                v.findViewById(R.id.tickImageView).setVisibility(View.GONE);
            }
            else {
                selectedList.set(this.getAdapterPosition(), true);
                v.findViewById(R.id.tickImageView).setVisibility(View.VISIBLE);
            }

           // notifyDataSetChanged();
        }
    }


    public int isMultipleSelected() {
        int count = 0;
        for (int i = 0; i < selectedList.size(); i++) {
            if(selectedList.get(i))
                count++;
        }
        return count;
    }
}