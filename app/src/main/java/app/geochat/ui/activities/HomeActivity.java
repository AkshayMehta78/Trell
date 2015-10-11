package app.geochat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.db.managers.LoginManager;
import app.geochat.managers.GeoChatManagers;
import app.geochat.services.asynctask.LocationService;
import app.geochat.ui.adapters.RecyclerViewAdapter;
import app.geochat.ui.widgets.SpacesItemDecoration;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 07/08/15.
 */
public class HomeActivity extends AppCompatActivity{

    private static final String TAG = HomeActivity.class.getSimpleName();

    private ImageView user_image;
    private SharedPreferences mSharedPreferences;
    private RecyclerView listView;
    private ProgressBar loadingProgressBar;
    private GeoChatManagers geoChatManager;
    private RecyclerViewAdapter adapter;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userhome);
        mSharedPreferences = new SharedPreferences(this);
        geoChatManager = new GeoChatManagers(this);
        getWidgetReferences();
        setUserprofile();
        setToolbar();
        getAllGeoChats();
    }


    private void getWidgetReferences() {
        user_image = (ImageView) findViewById(R.id.user_image);
        listView = (RecyclerView) findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.addItemDecoration(new SpacesItemDecoration(20));

        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
    }

    private void setUserprofile() {
        if (mSharedPreferences.getLoginMode().equalsIgnoreCase(LoginManager.MODE_FACEBOOK)) {
            userName = mSharedPreferences.getFacebookName();
            String avatar= mSharedPreferences.getFacebookProfilePicture();
            Picasso.with(this).load(avatar).fit().into(user_image);
        } else {
            String avatar= mSharedPreferences.getGooglePlusProfilePicUrl();
            userName = mSharedPreferences.getGooglePlusName();
            Picasso.with(this).load(avatar).fit().into(user_image);
        }
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(userName);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.create:
                Utils.createGeoChat(HomeActivity.this);
                break;
        }
        return true;

    }


    private void getAllGeoChats() {
        geoChatManager.fetchAllGeoChats(this);
    }

    public void renderGeoChatListView(ArrayList<GeoChat> result) {
        loadingProgressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        if(result.size()>0) {
            adapter = new RecyclerViewAdapter(this, result);
            listView.setAdapter(adapter);
        }else
            Utils.showToast(this,getString(R.string.no_geochat));
    }

    public void failResult() {
        loadingProgressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }


    public void verifyUser(GeoChat item) {
        final LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            Utils.showToast(getApplicationContext(), "GPS is disabled. Please Enable to Find your Location.");
        else
        {
            LocationService appLocationService = new LocationService(getApplicationContext());
            Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

            if (gpsLocation != null) {
                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                geoChatManager.verifyUserToJoin(latitude,longitude,item.getUserId(),item.getGeoChatId(),item);
            }
            else
            {
                double latitude = 19.24;
                double longitude = 72.85;
                geoChatManager.verifyUserToJoin(latitude,longitude,item.getUserId(),item.getGeoChatId(),item);

            }
        }
    }

    public void openChat(GeoChat item) {
        Intent chatIntent = new Intent(HomeActivity.this,ChatActivity.class);
        chatIntent.putExtra(Constants.Preferences.GEOCHAT,item);
        startActivity(chatIntent);
    }
}
