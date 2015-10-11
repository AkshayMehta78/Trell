package app.geochat.ui.activities;

/**
 * Created by akshaymehta on 30/08/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.maps.GoogleMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.geochat.R;
import app.geochat.adapter.LocationAdapter;
import app.geochat.beans.LocationModel;
import app.geochat.services.asynctask.LocationService;
import app.geochat.util.Constants;
import app.geochat.util.JSONParser;

public class CheckInActivity extends AppCompatActivity implements View.OnClickListener {
    private AutoCompleteTextView et_feedLocation;
    private Button btn_Next;
    private static final String API_KEY = "AIzaSyDp5u0Z3h9abid8puEy-sqdrFLZ3fONHN8";
    private LocationService appLocationService;
    private boolean isGPSEnablesManually = false;
    private ArrayList<LocationModel> result;
    private LocationAdapter adapter;
    private ListView lv_locations;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        getWidgetReferences();
        bindWidgetEvents();
        initialization();
        setToolbar();

        lv_locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationModel item = adapter.getItem(position);
                Intent intent = new Intent(CheckInActivity.this,CreateGeoChatActivity.class);
                intent.putExtra(Constants.LOCATIONKEYS.LOCATION,item.getLocationName());
                intent.putExtra(Constants.LOCATIONKEYS.LATITUDE,item.getLatitude());
                intent.putExtra(Constants.LOCATIONKEYS.LONGITUDE,item.getLongitude());
                startActivity(intent);
            }
        });
    }

    private void getWidgetReferences() {
        et_feedLocation = (AutoCompleteTextView) findViewById(R.id.et_feedLocation);
        lv_locations = (ListView) findViewById(R.id.lv_locations);
        btn_Next = (Button) findViewById(R.id.btn_Next);
    }

    private void bindWidgetEvents() {
        btn_Next.setOnClickListener(this);
    }

    private void initialization() {
        pd = new ProgressDialog(this);
        appLocationService = new LocationService(CheckInActivity.this);
        Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            double latitude = gpsLocation.getLatitude();
            double longitude = gpsLocation.getLongitude();
            fetchNearbyLocation(latitude, longitude);
        } else {
            showSettingsAlert("NETWORK");
//            double latitude = 19.24;
//            double longitude = 72.85;
//            fetchNearbyLocation(latitude, longitude);
        }
    }

    private void fetchNearbyLocation(double latitude, double longitude) {
        new fetchNearbyLocationAsynTask().execute(latitude + "", longitude + "");
    }

    @Override
    public void onClick(View v) {
        if (v == btn_Next) {
        }
    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckInActivity.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        CheckInActivity.this.startActivity(intent);
                        isGPSEnablesManually = true;
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private class fetchNearbyLocationAsynTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            // Building Parameters
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            String url = Constants.LOCATIONURL1 + "location=" + params[0] + "," + params[1] + Constants.LOCATIONURL2;
            Log.e("url",url);
            JSONObject json = jsonParser.getJSONFromUrl(url, parameters);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (pd.isShowing() && pd != null)
                pd.dismiss();
            try {
                if (!json.getString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    JSONArray resultArray = json.getJSONArray("results");
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject data = resultArray.getJSONObject(i);
                        String latitude = data.getJSONObject("geometry").getJSONObject("location").getString("lat");
                        String longitude = data.getJSONObject("geometry").getJSONObject("location").getString("lng");
                        LocationModel item = new LocationModel(data.getString("name"), data.getString("vicinity"), latitude,longitude);
                        result.add(item);
                    }
                    adapter = new LocationAdapter(CheckInActivity.this, result);
                    lv_locations.setAdapter(adapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            result = new ArrayList<LocationModel>();
            pd.setMessage("Loading");
            pd.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getText(R.string.select_location));
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
}