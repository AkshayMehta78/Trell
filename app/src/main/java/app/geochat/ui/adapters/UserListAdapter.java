package app.geochat.ui.adapters;

/**
 * Created by akshaymehta on 02/09/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.LocationModel;
import app.geochat.beans.SharedPreferences;
import app.geochat.beans.User;
import app.geochat.managers.ProfileManager;
import app.geochat.ui.activities.UserProfileActivity;
import app.geochat.util.Constants;


public class UserListAdapter extends ArrayAdapter<User> {
    Activity activity;
    ArrayList<User> result;
    ProfileManager mProfileManager;
    public UserListAdapter(Activity activity, ArrayList<User> result) {
        super(activity, R.layout.user_layout_row, result);
        this.activity=activity;
        this.result=result;
        mProfileManager = new ProfileManager(activity);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final User item = getItem(position);
        ViewHolder holder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_layout_row, parent, false);
            holder.userNameTextView= (TextView) convertView.findViewById(R.id.userNameTextView);
            holder.statusButton= (Button) convertView.findViewById(R.id.statusButton);
            holder.userImageImageView = (ImageView) convertView.findViewById(R.id.userImageImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String userName = item.getUserName();
        String userAvatar = item.getUserAvatar();
        final String userStatus = item.getUserStatus();

        if(!userAvatar.isEmpty()){
            Picasso.with(activity).load(userAvatar).placeholder(R.drawable.ic_default_profile_pic).into(holder.userImageImageView);
        } else {
            Picasso.with(activity).load(R.drawable.ic_default_profile_pic).placeholder(R.drawable.ic_default_profile_pic).into(holder.userImageImageView);
        }

        holder.userNameTextView.setText(userName);
        if(userStatus.equalsIgnoreCase("true")){
            holder.statusButton.setText(activity.getString(R.string.unfollow));
        } else {
            holder.statusButton.setText(activity.getString(R.string.follow));
        }

        holder.userImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SharedPreferences(activity).setUserProfileId(item.getUserId());
                Intent profileintent = new Intent(activity,UserProfileActivity.class);
                activity.startActivity(profileintent);
            }
        });

        holder.statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userStatus.equalsIgnoreCase("true")){
                    mProfileManager.followUser(item.getUserId(),Constants.USER.UNFOLLOWUSER);
                    item.setUserStatus("false");
                }else{
                    mProfileManager.followUser(item.getUserId(),Constants.USER.FOLLOWUSER);
                    item.setUserStatus("true");
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView userNameTextView;
        ImageView userImageImageView;
        Button statusButton;
    }
}