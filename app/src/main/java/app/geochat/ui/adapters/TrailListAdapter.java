package app.geochat.ui.adapters;

/**
 * Created by akshaymehta on 02/09/15.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import app.geochat.R;
import app.geochat.beans.Trail;


public class TrailListAdapter extends ArrayAdapter<Trail> {
    Activity activity;
    ArrayList<Trail> result;

    public TrailListAdapter(Activity activity, ArrayList<Trail> result) {
        super(activity, R.layout.trail_layout_row, result);
        this.activity=activity;
        this.result=result;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Trail item = getItem(position);
        ViewHolder holder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trail_layout_row, parent, false);
            holder.nameTextView= (TextView) convertView.findViewById(R.id.nameTextView);
            holder.tickImageView= (ImageView) convertView.findViewById(R.id.tickImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(!item.getName().isEmpty())
            holder.nameTextView.setText(item.getName());

        if(item.isAdded()){
            holder.tickImageView.setVisibility(View.VISIBLE);
        } else {
            holder.tickImageView.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void updateList(Trail item) {
        result.add(item);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView nameTextView;
        ImageView tickImageView;
    }
}