package app.geochat.ui.activities;

/**
 * Created by akshaymehta on 30/08/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import app.geochat.util.Utils;

public class AddLocation extends AppCompatActivity implements View.OnClickListener {
    private AutoCompleteTextView et_feedLocation;
    private Button mapSearchButton;
    private static final String API_KEY = "AIzaSyDp5u0Z3h9abid8puEy-sqdrFLZ3fONHN8";
    private LocationService appLocationService;
    private boolean isGPSEnablesManually = false;
    private ArrayList<LocationModel> result;
    private LocationAdapter adapter;
    private ListView lv_locations;
    ProgressDialog pd;
    private ArrayList<LocationModel> mFilteredLocation;
    private RelativeLayout mapViewRelativeLayout;
    private String mSearchKey="";
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        getWidgetReferences();
        bindWidgetEvents();
        initialization();
        setToolbar();

        /**
         * get Previous activity's action either Source or Destination
         */
        action =getIntent().getAction();


        lv_locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationModel item = adapter.getItem(position);
                if(action!=null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.LOCATIONKEYS.CHECKIN, item.getLocationName());
                    intent.putExtra(Constants.LOCATIONKEYS.LATITUDE, item.getLatitude());
                    intent.putExtra(Constants.LOCATIONKEYS.LONGITUDE, item.getLongitude());
                    setResult(Constants.TRELL.ACTION_LOCATION_ID, intent);
                    finish();
                }
            }
        });
    }

    private void getWidgetReferences() {
        lv_locations = (ListView) findViewById(R.id.lv_locations);
        mapViewRelativeLayout = (RelativeLayout) findViewById(R.id.mapViewRelativeLayout);
        mapSearchButton = (Button) findViewById(R.id.mapSearchButton);
    }

    private void bindWidgetEvents() {
        mapSearchButton.setOnClickListener(this);
    }

    private void initialization() {
        pd = new ProgressDialog(this);
        mFilteredLocation = new ArrayList<LocationModel>();
        appLocationService = new LocationService(AddLocation.this);
        Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            double latitude = gpsLocation.getLatitude();
            double longitude = gpsLocation.getLongitude();
            fetchNearbyLocation(latitude, longitude);
        } else {
            gpsLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
            if (gpsLocation != null) {
                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                fetchNearbyLocation(latitude, longitude);
            } else {
                double latitude = 19.23;
                double longitude = 72.84;
                fetchNearbyLocation(latitude, longitude);
            }
        }
//
//        lv_locations.setEmptyView(mapViewRelativeLayout);

    }

    private void fetchNearbyLocation(double latitude, double longitude) {
        new fetchNearbyLocationAsynTask().execute(latitude + "", longitude + "");
    }

    @Override
    public void onClick(View v) {
        if (v == mapSearchButton) {
            Intent intent = new Intent(AddLocation.this, MapSearchActivity.class);
            intent.putExtra(Constants.GEOCHAT.SEARCH_TEXT,mSearchKey);
            startActivity(intent);
        }
    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddLocation.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        AddLocation.this.startActivity(intent);
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
                    mFilteredLocation = result;
                    displayAllLocation();
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

    private void displayAllLocation() {
        if(result.size()>0) {
            adapter = new LocationAdapter(AddLocation.this, result);
            lv_locations.setAdapter(adapter);
        }else {
            lv_locations.setEmptyView(mapViewRelativeLayout);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) AddLocation.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(AddLocation.this.getComponentName()));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(null!=adapter) {
                    mSearchKey = s;
                    result = performSearch(mFilteredLocation, s);
                    if (result.size() > 0) {
                        displayAllLocation();
                    } else {
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Goes through the given list and filters it according to the given query.
     *
     * @param result list given as search sample
     * @param query       to be searched
     * @return new filtered list
     */
    private ArrayList<LocationModel> performSearch(ArrayList<LocationModel> result, String query) {

        String[] queryByWords = query.toLowerCase().split("\\s+");
        ArrayList<LocationModel> filteredList = new ArrayList<LocationModel>();
        for (LocationModel location : result) {
            String content = (location.getLocationName()).toLowerCase();
            for (String word : queryByWords) {
                int numberOfMatches = queryByWords.length;
                if (content.contains(word)) {
                    numberOfMatches--;
                } else {
                    break;
                }
                if (numberOfMatches == 0) {
                    filteredList.add(location);
                }
            }
        }
        return filteredList;
    }


}