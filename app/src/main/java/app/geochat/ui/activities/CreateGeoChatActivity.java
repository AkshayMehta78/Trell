package app.geochat.ui.activities;

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
import android.widget.Button;
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
    private String latitude,longitude,location,geoChatImage;
    private TextView locationTextView,allPhotosTextView;
    private static final int SELECT_PICTURE = 1;
    private ImageView geoChatImageView;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_geochat);
        setToolbar();
        getWidgetReferences();
        setWidgetEvents();
        initializations();
    }

    private void setWidgetEvents() {
        allPhotosTextView.setOnClickListener(this);
    }

    private void initializations() {
        mSharedPreferences = new SharedPreferences(this);
        Intent data = getIntent();
        latitude = data.getStringExtra(Constants.LOCATIONKEYS.LATITUDE);
        longitude = data.getStringExtra(Constants.LOCATIONKEYS.LONGITUDE);
        location = data.getStringExtra(Constants.LOCATIONKEYS.LOCATION);
        setLocationMarkeronMap();
    }

    private void setLocationMarkeronMap() {
        final LatLng locationPoint = new LatLng(Double.parseDouble(latitude) ,Double.parseDouble(longitude));
        googleMap.addMarker(new MarkerOptions().position(locationPoint).title(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 14.0f) );
        locationTextView.setText(location);
    }

    private void getWidgetReferences() {
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        allPhotosTextView = (TextView) findViewById(R.id.allPhotosTextView);
        geoChatImageView = (ImageView) findViewById(R.id.geoChatImageView);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getText(R.string.create_geochat));
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_done:
                if(!selectedImagePath.equalsIgnoreCase("")) {
                    selectedImagePath= Utils.compressImage(selectedImagePath,this);
                    new MultimediaGeoChatAsyncTask(this).execute(location, latitude, longitude, selectedImagePath);
                }
                else
                    new GeoChatManagers(this).createGeoChat(location,latitude,longitude);
                break;
        }
        return true;

    }

    @Override
    public void onClick(View v) {
        if (v == allPhotosTextView) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = Utils.getPath(this,selectedImageUri);
                if (selectedImageUri != null) {
                    geoChatImageView.setImageBitmap(Utils.getBitmap(this, selectedImageUri));
                }
            }
        }
    }
}
