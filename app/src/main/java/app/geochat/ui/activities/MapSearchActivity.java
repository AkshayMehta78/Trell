package app.geochat.ui.activities;

/**
 * Created by akshaymehta on 04/10/15.
 */

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.geochat.R;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

public class MapSearchActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    private MarkerOptions markerOptions;
    private LatLng latLng;
    private Button btnFind;
    private AutoCompleteTextView etLocation;
    private String latitude = "", longitude = "", location = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationmap);

        /**
         * set toolbar
         */
        Utils.setActivityToolbar(this, R.string.select_location, MapSearchActivity.this);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = supportMapFragment.getMap();
        etLocation = (AutoCompleteTextView) findViewById(R.id.et_location);
        btnFind = (Button) findViewById(R.id.btn_find);

        btnFind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = etLocation.getText().toString();
                if (location != null && !location.isEmpty()) {
                    new GeocoderTask().execute(location);
                }
            }
        });

        Intent data = getIntent();
        if (null != data) {
            String location = data.getStringExtra(Constants.GEOCHAT.SEARCH_TEXT);
            if(!location.isEmpty()) {
                etLocation.setText(location);
                new GeocoderTask().execute(location);
            }
        }

    }


    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }
            Address address = (Address) addresses.get(0);

            latitude = String.valueOf(address.getLatitude());
            longitude = String.valueOf(address.getLongitude());
            Log.e("address", address.toString());
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
            googleMap.clear();
            String addressText = String.format("%s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getCountryName());
            location = addressText;
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(addressText);
            googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                if(latitude!=null && longitude!=null && location!=null) {
                    if (!latitude.isEmpty() && !longitude.isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.LOCATIONKEYS.LOCATION, location);
                        bundle.putString(Constants.LOCATIONKEYS.LATITUDE, latitude);
                        bundle.putString(Constants.LOCATIONKEYS.LONGITUDE, longitude);
                        Intent intent = new Intent(MapSearchActivity.this, AboutLocationActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    Utils.showToast(this,getString(R.string.no_location));
                }
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next_menu, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.closeProgress();
    }
}

