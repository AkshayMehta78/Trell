package app.geochat.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.managers.GeoChatManagers;
import app.geochat.ui.adapters.RecyclerViewAdapter;
import app.geochat.ui.fragments.GeoChatListFragment;
import app.geochat.ui.widgets.SpacesItemDecoration;
import app.geochat.util.CircularProgressView;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 06/11/15.
 */
public class FollowingFeedsActivity extends AppCompatActivity{

    private SharedPreferences mSharedPreferences;
    private RecyclerView mRecylcerlistView;
    private CircularProgressView loadingProgressBar;
    private GeoChatManagers geoChatManager;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geochatlist);
        getWidgetReferences();
        bindWidgetEvents();
        initialization();

        setToolbar();
        getAllGeoChats();
    }

    public void getAllGeoChats() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        mRecylcerlistView.setVisibility(View.GONE);
        double[] locationData = Utils.getLocationDetails(this);
        geoChatManager.fetchFollowingUserFeeds(FollowingFeedsActivity.this, locationData[0] + "", locationData[1] + "");
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Friend Feeds");
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    private void getWidgetReferences() {
        mRecylcerlistView = (RecyclerView) findViewById(R.id.listView);
        mRecylcerlistView.setLayoutManager(new LinearLayoutManager(this));
        mRecylcerlistView.addItemDecoration(new SpacesItemDecoration(20));
        loadingProgressBar = (CircularProgressView) findViewById(R.id.loadingProgressBar);
    }

    private void bindWidgetEvents() {
    }

    private void initialization() {
        mSharedPreferences = new SharedPreferences(this);
        geoChatManager = new GeoChatManagers(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void renderGeoChatListView(ArrayList<GeoChat> result) {
        loadingProgressBar.setVisibility(View.GONE);
        mRecylcerlistView.setVisibility(View.VISIBLE);
        if (result.size() > 0) {
            Collections.sort(result,new CustomComparator());
            adapter = new RecyclerViewAdapter(this, result);
            mRecylcerlistView.setAdapter(adapter);
        } else
            Utils.showToast(this, getString(R.string.no_geochat));
    }

    public void failResult() {
        loadingProgressBar.setVisibility(View.GONE);
        mRecylcerlistView.setVisibility(View.VISIBLE);
    }

    public class CustomComparator implements Comparator<GeoChat> {
        @Override
        public int compare(GeoChat geoChat1, GeoChat geoChat2) {
            int returnVal = 0;
            int distance1 = Integer.parseInt(geoChat1.getDistance());
            int distance2 = Integer.parseInt(geoChat2.getDistance());


            if (distance1 < distance2) {
                returnVal = -1;
            } else if (distance1 > distance2) {
                returnVal = 1;
            } else if (distance1 == distance1) {
                returnVal = 0;
            }
            return returnVal;
        }
    }

}
