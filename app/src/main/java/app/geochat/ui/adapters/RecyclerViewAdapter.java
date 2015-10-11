package app.geochat.ui.adapters;

/**
 * Created by akshaymehta on 06/09/15.
 */

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.ui.fragments.GeoChatListFragment;
import app.geochat.util.Constants;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    private List<GeoChat> feedItemList;
    private FragmentActivity activity;
    private GeoChatListFragment fragment;

    public RecyclerViewAdapter(FragmentActivity activity, List<GeoChat> feedItemList) {
        this.feedItemList = feedItemList;
        this.activity = activity;
        this.fragment= (GeoChatListFragment)activity.getSupportFragmentManager().findFragmentByTag(Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG);

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.updated_geochat_layout_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int i) {
        GeoChat item = feedItemList.get(i);

        holder.userNameTextView.setText(item.getCreatedByUserName());
        holder.locationTextView.setText("@ "+item.getCheckInLocation());
        if (!item.getUserAvatar().isEmpty())
            Picasso.with(activity).load(item.getUserAvatar()).placeholder(R.drawable.ic_default_profile_pic).into(holder.userImageImageView);
        else
            Picasso.with(activity).load(R.drawable.ic_default_profile_pic).placeholder(R.drawable.ic_default_profile_pic).into(holder.userImageImageView);


        if (!item.getGeoChatImage().isEmpty())
            Picasso.with(activity).load(item.getGeoChatImage()).placeholder(R.drawable.travel_bg).error(R.drawable.travel_bg).into(holder.geoChatImageView);
        else
            Picasso.with(activity).load(R.drawable.travel_bg).placeholder(R.drawable.travel_bg).into(holder.geoChatImageView);


    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userNameTextView, locationTextView;
        ImageView userImageImageView,geoChatImageView;

        public CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
            this.locationTextView = (TextView) view.findViewById(R.id.locationTextView);
            this.userImageImageView = (ImageView) view.findViewById(R.id.userImageImageView);
            this.geoChatImageView = (ImageView) view.findViewById(R.id.geoChatImageView);

        }

        @Override
        public void onClick(View v) {
            GeoChat item = feedItemList.get(getPosition());
            fragment.verifyUser(item);
        }
    }
}