package app.geochat.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import app.geochat.R;
import app.geochat.beans.User;
import app.geochat.managers.ProfileManager;
import app.geochat.ui.adapters.UserListAdapter;
import app.geochat.util.CircularProgressView;
import app.geochat.util.Constants;

/**
 * Created by akshaymehta on 10/11/15.
 */
public class UsersListActivity extends AppCompatActivity {

    ListView usersListView;
    CircularProgressView progressBar;
    RelativeLayout emptyViewRelativeLayout;
    ProfileManager mProfileManager;
    String userId, friendId, flag;
    UserListAdapter mAdapter;
    private String geoChatId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list_activity);
        //Set up toolbar with back option

        geWidgetReferences();
        setWidgetEvent();
        initialization();
        flag = getIntent().getStringExtra(Constants.USER.USER_TYPE);
        userId = getIntent().getStringExtra(Constants.USER.USERID);

        if (flag.equalsIgnoreCase(Constants.USER.TRY_USERS)) {
            geoChatId = getIntent().getStringExtra(Constants.JsonKeys.GEOCHATID);
        } else {
            friendId = getIntent().getStringExtra(Constants.USER.FRIENDID);
        }

        setToolbar();

        fetchUsesrList();
    }


    /**
     * Setting toolbar
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) this;
        activity.setSupportActionBar(toolbar);
        final ActionBar ab = activity.getSupportActionBar();
        if (flag.equalsIgnoreCase(Constants.USER.FOLLOWERID))
            ab.setTitle("Followers");
        else if (flag.equalsIgnoreCase(Constants.USER.FOLLOWINGID))
            ab.setTitle("Following");
        else
            ab.setTitle("Try Users");

        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
    }

    private void initialization() {
        mProfileManager = new ProfileManager(this);
    }

    private void fetchUsesrList() {
        if (flag.equalsIgnoreCase(Constants.USER.TRY_USERS)){
            mProfileManager.fetchWishListUsersList(userId, geoChatId);
        } else {
            mProfileManager.fetchUsersList(userId, friendId, flag);
        }
    }

    private void setWidgetEvent() {
    }

    private void geWidgetReferences() {
        usersListView = (ListView) findViewById(R.id.usersListView);
        progressBar = (CircularProgressView) findViewById(R.id.progressBar);
        emptyViewRelativeLayout = (RelativeLayout) findViewById(R.id.emptyViewRelativeLayout);
    }

    public void sendUserListRsponse(ArrayList<User> result) {

        progressBar.setVisibility(View.GONE);
        usersListView.setVisibility(View.VISIBLE);
        if (result.size() > 0) {
            mAdapter = new UserListAdapter(this, result);
            usersListView.setAdapter(mAdapter);
        } else {
            usersListView.setEmptyView(emptyViewRelativeLayout);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
