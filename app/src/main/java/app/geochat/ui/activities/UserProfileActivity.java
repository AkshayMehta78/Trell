package app.geochat.ui.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.managers.ProfileManager;
import app.geochat.ui.adapters.TabsPagerAdapter;
import app.geochat.ui.adapters.ViewPagerAdapter;
import app.geochat.ui.fragments.AchievementFragment;
import app.geochat.ui.fragments.GeoChatListFragment;
import app.geochat.ui.fragments.UserGeoNoteListFragment;
import app.geochat.util.Constants;
import app.geochat.util.NetworkManager;

/**
 * Created by akshaymehta on 07/11/15.
 */
public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    private static final int[] TITLES = {R.string.achievements, R.string.explore};

    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>() {{
        add(new AchievementFragment());
        add(new UserGeoNoteListFragment());
    }};
    private ImageView userImageView;
    private TextView userNameTextView,followersTextView,followingTextView;
    private ProfileManager mProfileManager;
    private SharedPreferences mSharedPreferences;
    private ViewPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
        //Set up toolbar with back option
        setToolbar();

        geWidgetReferences();
        setWidgetEvent();
        initialization();
        mUserId = mSharedPreferences.getUserProfileId();
        Log.e("mUserId", mUserId);
        if(NetworkManager.isConnectedToInternet(this))
            fetchUserProfileDetails();
        setViewPager(0);
    }

    private void initialization() {
        mProfileManager = new ProfileManager(this);
        mSharedPreferences = new SharedPreferences(this);
    }

    private void fetchUserProfileDetails() {
            mProfileManager.fetchUserDetails(mUserId);
    }

    private void setWidgetEvent() {
    }

    private void geWidgetReferences() {
        userImageView = (ImageView) findViewById(R.id.userImageImageView);
        userNameTextView = (TextView) findViewById(R.id.userNameTextView);
        followersTextView = (TextView) findViewById(R.id.followersTextView);
        followingTextView = (TextView) findViewById(R.id.followingTextView);

    }


    /**
     * Setting toolbar
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) this;
        activity.setSupportActionBar(toolbar);
        final ActionBar ab = activity.getSupportActionBar();
        ab.setTitle("Profile");
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Set Collapsible collapsed and extended toolbar
     *
     * @param
     * @param selectedTab
     */
    private void setViewPager(int selectedTab) {

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        /* fetching tab title from string resource */
        List<String> titlesList = new ArrayList<>();
        for (int titleResId : TITLES) {
            titlesList.add(getString(titleResId));

        }
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.USER.USERID,mUserId);
        mAdapter = new ViewPagerAdapter(this.getSupportFragmentManager(), mFragments, titlesList,mBundle);
        mViewPager.setAdapter(mAdapter);
        /* tabLayout.setupWithViewPager(viewPager);
        - Tab titles not showing immediately-this
        is bug in support library(temp solution below) */
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setOffscreenPageLimit(1);
                tabLayout.setupWithViewPager(mViewPager);
            }
        });
        mViewPager.setCurrentItem(selectedTab);
    }


    public void sendFetchedResult(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject userProfile = jsonObject.getJSONObject(Constants.JsonKeys.USER_PROFILE_DETAILS);
            userNameTextView.setText(userProfile.getString(Constants.LoginKeys.USERNAME));
            String userAvatar = userProfile.getString(Constants.JsonKeys.USERAVATAR);
            if(!userAvatar.isEmpty()){
                Picasso.with(this).load(userAvatar).placeholder(R.drawable.travel_bg).error(R.drawable.ic_default_profile_pic).into(userImageView);
            } else {
                Picasso.with(this).load(R.drawable.travel_bg).placeholder(R.drawable.travel_bg).into(userImageView);
            }

            JSONObject friendsObject = jsonObject.getJSONObject(Constants.JsonKeys.FRIENDS);
            String followersCount = friendsObject.getString(Constants.JsonKeys.FOLLOWERSCOUNT);
            String followingsCount = friendsObject.getString(Constants.JsonKeys.FOLLOWINGSCOUNT);

            followersTextView.setText(followersCount+"\nFollower");
            followingTextView.setText(followingsCount+"\nFollowing");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendFetchedExploreResult(String response) {
        AchievementFragment fragment = (AchievementFragment) mAdapter.getItem(mViewPager.getCurrentItem());
        fragment.updateExploreResult(response);
    }


    public void sendFetchedFeedResult(ArrayList<GeoChat> response) {
        UserGeoNoteListFragment fragment = (UserGeoNoteListFragment) mAdapter.getItem(1);
        fragment.renderGeoChatListView(response);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
