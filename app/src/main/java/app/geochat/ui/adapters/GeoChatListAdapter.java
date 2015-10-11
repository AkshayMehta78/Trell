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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.LocationModel;


public class GeoChatListAdapter extends ArrayAdapter<GeoChat> {
    Activity activity;
    ArrayList<GeoChat> result;
    public GeoChatListAdapter(Activity activity, ArrayList<GeoChat> result) {
        super(activity, R.layout.geochat_layout_row, result);
        this.activity=activity;
        this.result=result;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final GeoChat item = getItem(position);
        ViewHolder holder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.geochat_layout_row, parent, false);
            holder.userNameTextView= (TextView) convertView.findViewById(R.id.userNameTextView);
            holder.locationTextView= (TextView) convertView.findViewById(R.id.locationTextView);
            holder.userImageImageView = (ImageView) convertView.findViewById(R.id.userImageImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.userNameTextView.setText(item.getCreatedByUserName());
        holder.locationTextView.setText(item.getCheckInLocation());
        if(!item.getUserAvatar().isEmpty())
            Picasso.with(activity).load(item.getUserAvatar()).placeholder(R.drawable.ic_user).into(holder.userImageImageView);
        else
            Picasso.with(activity).load(R.drawable.ic_user).placeholder(R.drawable.ic_user).into(holder.userImageImageView);

        return convertView;
    }

    private static class ViewHolder {
        TextView userNameTextView,locationTextView;
        ImageView userImageImageView;
    }
}