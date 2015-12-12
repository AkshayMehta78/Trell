package app.geochat.ui.adapters;

/**
 * Created by akshaymehta on 06/09/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.List;

import app.geochat.R;
import app.geochat.beans.Trail;
import app.geochat.managers.GeoChatManagers;
import app.geochat.managers.ProfileManager;
import app.geochat.ui.activities.UserTrailListActivity;
import app.geochat.ui.fragments.GeoChatListFragment;
import app.geochat.util.Constants;

public class UserTrailListAdapter extends RecyclerView.Adapter<UserTrailListAdapter.CustomViewHolder> {
    private List<Trail> trailList;
    private Activity activity;
    private GeoChatListFragment fragment;
    private ProfileManager mProfileManager;
    private GeoChatManagers mGeoChatManager;
    private JSONArray trailArray;

    public UserTrailListAdapter(Activity activity, List<Trail> trailList) {
        this.trailList = trailList;
        this.activity = activity;
        mProfileManager = new ProfileManager(activity);
        mGeoChatManager = new GeoChatManagers(activity);
    }

    @Override
    public UserTrailListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_user_trail_row, null);
        UserTrailListAdapter.CustomViewHolder viewHolder = new UserTrailListAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserTrailListAdapter.CustomViewHolder holder, final int i) {
        final Trail item = trailList.get(i);
        holder.name = item.getName();
        holder.thumbImage = item.getThumbImage();
        holder.trailListId = item.getTrailId();
        Log.e("!thumbImage", holder.thumbImage);
        if(!holder.thumbImage.isEmpty()) {
            Picasso.with(activity).load(holder.thumbImage).error(R.drawable.travel_bg).into(holder.thumbImageView);
        } else {
            Picasso.with(activity).load(R.drawable.travel_bg).error(R.drawable.travel_bg).into(holder.thumbImageView);
        }

        holder.trailNameTextView.setText(holder.name);
    }

    @Override
    public int getItemCount() {
        return (null != trailList ? trailList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView trailNameTextView, followTextView;
        ImageView thumbImageView;
        private String name,thumbImage,trailListId;

        public CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            trailNameTextView = (TextView) view.findViewById(R.id.trailNameTextView);
            followTextView = (TextView) view.findViewById(R.id.followTextView);
            thumbImageView = (ImageView) view.findViewById(R.id.thumbImageView);

        }

        @Override
        public void onClick(View v) {
            Intent trailGeoChats = new Intent(activity,UserTrailListActivity.class);
            trailGeoChats.putExtra(Constants.JsonKeys.NAME,name);
            trailGeoChats.putExtra(Constants.JsonKeys.TRAILLISTID,trailListId);
            activity.startActivity(trailGeoChats);
        }
    }

}