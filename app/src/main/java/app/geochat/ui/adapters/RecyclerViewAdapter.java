package app.geochat.ui.adapters;

/**
 * Created by akshaymehta on 06/09/15.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.anton46.collectionitempicker.CollectionPicker;
import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.android.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.managers.GeoChatManagers;
import app.geochat.managers.ProfileManager;
import app.geochat.ui.activities.UserProfileActivity;
import app.geochat.ui.fragments.GeoChatListFragment;
import app.geochat.ui.fragments.TrailListFragmentDialog;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    private List<GeoChat> feedItemList;
    private FragmentActivity activity;
    private GeoChatListFragment fragment;
    private ProfileManager mProfileManager;
    private GeoChatManagers mGeoChatManager;
    private JSONArray trailArray;

    public RecyclerViewAdapter(FragmentActivity activity, List<GeoChat> feedItemList) {
        this.feedItemList = feedItemList;
        this.activity = activity;
        mProfileManager = new ProfileManager(activity);
        mGeoChatManager = new GeoChatManagers(activity);
        this.fragment= (GeoChatListFragment)activity.getSupportFragmentManager().findFragmentByTag(Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.updated_geochat_layout_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int i) {
        final GeoChat item = feedItemList.get(i);
        String description = item.getDescripton();
        String caption = item.getCaption();
        String tags = item.getTags();
        String age = Utils.dateDiff(Utils.getDate(item.getCreatedDateTime()));

        if(!tags.isEmpty() && null!=tags) {
            String[] tagsArray = tags.split(",");
            Spanned spannabaleTags = Utils.getFormattedTags(tagsArray,activity);
            holder.spannableTagsTextView.setText(spannabaleTags);
            holder.spannableTagsTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        int distaneInKM = Integer.parseInt(item.getDistance())/1000;
        holder.distanceTextView.setText(distaneInKM+" km");
        holder.userNameTextView.setText(item.getCreatedByUserName());
        holder.locationTextView.setText("@ "+item.getCheckInLocation());
        if (!item.getUserAvatar().isEmpty())
            Picasso.with(activity).load(item.getUserAvatar()).placeholder(R.drawable.background_blue).into(holder.userImageImageView);
        else
            Picasso.with(activity).load(R.drawable.ic_default_profile_pic).placeholder(R.drawable.background_blue).into(holder.userImageImageView);


        if (!item.getGeoChatImage().isEmpty())
            Picasso.with(activity).load(item.getGeoChatImage()).placeholder(R.drawable.travel_bg).error(R.drawable.travel_bg).into(holder.geoChatImageView);
        else
            Picasso.with(activity).load(R.drawable.travel_bg).placeholder(R.drawable.travel_bg).into(holder.geoChatImageView);

        if(description.isEmpty() || description==null){
            holder.descriptionTextView.setVisibility(View.GONE);
        }else {
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            holder.descriptionTextView.setText(description);
        }

        holder.ageTextView.setText(age);
        holder.captionTextView.setText(caption);


        holder.locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startLocationFeedActivity(activity,item.getLatitude(),item.getLongitude(),item.getCheckInLocation());
            }
        });


        try {
            JSONObject trailJson = new JSONObject(item.getUserTrails());
            trailArray = trailJson.getJSONArray(Constants.JsonKeys.TRAILS);
            if (Utils.isMyPost(item.getUserId(), activity)) {
                    holder.addTrailTextView.setVisibility(View.VISIBLE);
                if(trailArray.length()>0){
                    holder.addTrailTextView.setText("Edit Trail");
                } else {
                    holder.addTrailTextView.setText("Add to Trail");
                }
            } else {
                holder.addTrailTextView.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.moreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isMyPost(item.getUserId(), activity)) {
                    openSelfMoreOptionDialog(item, i);
                } else {
                    openOthersMoreOptionDialog(item);
                }
            }
        });

        holder.wishListTextView.setText(item.getWishListCount() + " " + activity.getString(R.string.want_to_try));
        holder.commentTextView.setText(item.getCommentCount()+" "+activity.getString(R.string.discussions));

        holder.wishListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isWishListAdded().equalsIgnoreCase("false")) {
                    mGeoChatManager.addToWishList(activity, item.getGeoChatId(), Constants.GEOCHAT.ADD_WISHLIST, i);
                } else {
                    mGeoChatManager.addToWishList(activity, item.getGeoChatId(), Constants.GEOCHAT.REMOVE_WISHLIST, i);
                    //        Utils.showToast(activity, activity.getString(R.string.already_added_wishlist));
                }
            }
        });

        holder.commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoChat item = feedItemList.get(i);
                Utils.openFullScreenNoteWithComment(activity,item);
            }
        });

        holder.userImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SharedPreferences(activity).setUserProfileId(item.getUserId());
                Intent profileintent = new Intent(activity,UserProfileActivity.class);
                activity.startActivity(profileintent);
            }
        });

        holder.addTrailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentDialogForList(item);
            }
        });
    }



    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public void updateWishList(int position, String wishlist) {
        final GeoChat item = feedItemList.get(position);
        int count = Integer.parseInt(item.getWishListCount());
        if(wishlist.equalsIgnoreCase(Constants.GEOCHAT.ADD_WISHLIST)) {
            item.setWishListCount((count + 1) + "");
            item.setIsWishListAdded(true + "");
        }else {
            item.setWishListCount((count - 1) + "");
            item.setIsWishListAdded(false + "");
        }
        notifyDataSetChanged();
    }

    public void removeItemFromList(int position) {
        feedItemList.remove(position);
        notifyDataSetChanged();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userNameTextView, locationTextView,ageTextView,descriptionTextView,captionTextView,spannableTagsTextView,distanceTextView;
        TextView wishListTextView,commentTextView,addTrailTextView;
        ImageView userImageImageView,geoChatImageView,moreImageView;
        CollectionPicker collection_item_picker;

        public CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
            this.locationTextView = (TextView) view.findViewById(R.id.locationTextView);
            this.ageTextView = (TextView) view.findViewById(R.id.ageTextView);
            this.descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
            this.captionTextView = (TextView) view.findViewById(R.id.captionTextView);
            this.userImageImageView = (ImageView) view.findViewById(R.id.userImageImageView);
            this.geoChatImageView = (ImageView) view.findViewById(R.id.geoChatImageView);
            this.collection_item_picker = (CollectionPicker) view.findViewById(R.id.collection_picker);
            this.moreImageView = (ImageView) view.findViewById(R.id.moreImageView);
            this.spannableTagsTextView = (TextView) view.findViewById(R.id.spannableTagsTextView);
            this.distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
            this.wishListTextView = (TextView) view.findViewById(R.id.wishListTextView);
            this.commentTextView = (TextView) view.findViewById(R.id.commentTextView);
            this.addTrailTextView = (TextView) view.findViewById(R.id.addTrailTextView);
        }

        @Override
        public void onClick(View v) {
            GeoChat item = feedItemList.get(getPosition());
            Utils.openFullScreenNote(activity,item);
        }
    }



    private void openSelfMoreOptionDialog(final GeoChat item, final int position) {

        new BottomSheet.Builder(activity,R.style.BottomSheet_StyleDialog).title(item.getCheckInLocation()).sheet(R.menu.self_menu).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.other:
                        Utils.openShareIntent(item, activity);
                        break;
                    case R.id.instshare:
                        Utils.openInstagramShareIntent(item, activity);
                        break;
                    case R.id.fbshare:
                        Utils.openFacebookShareIntent(item, activity);
                        break;
                    case R.id.delete:
                        openConfirmationDialog(item, position);
                        break;
                    case R.id.twittershare:
                        Utils.openTwitterShareIntent(item, activity);
                        break;
                }
            }
        }).show();

    }

    private void openConfirmationDialog(final GeoChat item, final int position) {

        new AlertDialogWrapper.Builder(activity)
                .setTitle("Remove this Note")
                .setMessage("Are you sure you want to remove this note?")
                .setNegativeButton(R.string.cancel_txt, null)
                .setPositiveButton(R.string.yes_txt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGeoChatManager.removeGeoNoteAPI(activity, item.getGeoChatId(), position);
                    }
                }).show();

    }

    private void openOthersMoreOptionDialog(final GeoChat item) {
        int id;
        if(item.isFollowing().equalsIgnoreCase("true")){
            id = R.menu.other_more_options2;
        } else {
            id = R.menu.other_more_options;
        }

        new BottomSheet.Builder(activity,R.style.BottomSheet_StyleDialog).title(item.getCheckInLocation()).sheet(id).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.other:
                        Utils.openShareIntent(item, activity);
                        break;
                    case R.id.follow:
                        item.setIsFollowing("true");
                        mProfileManager.followUser(item.getUserId(), Constants.USER.FOLLOWUSER);
                        notifyDataSetChanged();
                        break;
                    case R.id.unfollow:
                        item.setIsFollowing("false");
                        mProfileManager.followUser(item.getUserId(), Constants.USER.UNFOLLOWUSER);
                        break;
                    case R.id.report:
                        Utils.reportContent(item, activity);
                        break;
                    case R.id.instshare:
                        Utils.openInstagramShareIntent(item, activity);
                        break;
                    case R.id.fbshare:
                        Utils.openFacebookShareIntent(item, activity);
                        break;
                    case R.id.twittershare:
                        Utils.openTwitterShareIntent(item, activity);
                        break;
                }
            }
        }).show();
    }

    private void openFragmentDialogForList(GeoChat item) {

        FragmentManager fm = activity.getSupportFragmentManager();
        TrailListFragmentDialog fragmentDialog = new TrailListFragmentDialog(item,activity);
        fragmentDialog.show(fm,Constants.FragmentTags.FRAGMENT_TRAIL_LIST);

    }

}