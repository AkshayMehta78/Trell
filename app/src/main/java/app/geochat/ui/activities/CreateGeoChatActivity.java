package app.geochat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.File;

import app.geochat.R;
import app.geochat.beans.SharedPreferences;
import app.geochat.managers.GeoChatManagers;
import app.geochat.services.asynctask.MultimediaGeoChatAsyncTask;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 30/08/15.
 */
public class CreateGeoChatActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences mSharedPreferences;
    private GoogleMap googleMap;
    private String latitude,longitude,location,tags,selectedImagePath,description,captionText;
    private Button publishButton;
    private EditText captionEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.activity_create_geochat);
        getWidgetReferences();
        setWidgetEvents();
        initializations();
        setToolbar();
    }

    private void setWidgetEvents() {
        publishButton.setOnClickListener(this);
    }

    private void initializations() {
        mSharedPreferences = new SharedPreferences(this);
        Intent data = getIntent();
        latitude = data.getStringExtra(Constants.LOCATIONKEYS.LATITUDE);
        longitude = data.getStringExtra(Constants.LOCATIONKEYS.LONGITUDE);
        location = data.getStringExtra(Constants.LOCATIONKEYS.LOCATION);
        selectedImagePath = data.getStringExtra(Constants.LOCATIONKEYS.GEOCHATIMAGE);
        description = data.getStringExtra(Constants.GEOCHAT.DESCRIPTION);
        tags = data.getStringExtra(Constants.GEOCHAT.TAGS);
        setLocationMarkeronMap();
    }

    private void setLocationMarkeronMap() {
        final LatLng locationPoint = new LatLng(Double.parseDouble(latitude) ,Double.parseDouble(longitude));
        googleMap.addMarker(new MarkerOptions().position(locationPoint).title(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 14.0f));
    }

    private void getWidgetReferences() {
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        captionEditText = (EditText) findViewById(R.id.captionEditText);
        publishButton = (Button) findViewById(R.id.publishButton);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(location);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;

    }

    @Override
    public void onClick(View v) {
            if(v==publishButton){
                captionText = captionEditText.getText().toString().trim();
                if(!captionText.isEmpty()) {
                    if (!selectedImagePath.equalsIgnoreCase("")) {
                        selectedImagePath = Utils.compressImage(selectedImagePath, this);
                        new MultimediaGeoChatAsyncTask(this).execute(location, latitude, longitude, selectedImagePath, description, tags, captionText);
                    } else {
                        captionText = "";
                        new GeoChatManagers(this).createGeoChat(location, latitude, longitude, description, tags, captionText);
                    }
                }
            }
    }
}
