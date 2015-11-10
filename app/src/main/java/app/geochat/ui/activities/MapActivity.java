package app.geochat.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.geochat.R;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 10/11/15.
 */
public class MapActivity extends AppCompatActivity {

    private String mapJsonArrayString;
    private GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_activity);
        //Set up toolbar with back option
        setToolbar();
        
        mapJsonArrayString = getIntent().getStringExtra(Constants.USER.MAPDATA);
        geWidgetReferences();
        setWidgetEvent();
        initialization();

        setAllLocationOnMap();
    }

    private void setAllLocationOnMap() {
        try {
            JSONArray mapArray = new JSONArray(mapJsonArrayString);
            if(mapArray.length()>0){
                for(int i=0;i<mapArray.length();i++){
                    JSONObject mapObject = mapArray.getJSONObject(i);
                    final LatLng locationPoint = new LatLng(Double.parseDouble(mapObject.getString(Constants.LOCATIONKEYS.LATITUDE)) ,Double.parseDouble(mapObject.getString(Constants.LOCATIONKEYS.LONGITUDE)));
                    drawMarkerOnMap(locationPoint,mapObject);
                    if(i==mapArray.length()){
                        // Moving CameraPosition to last clicked position
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(mapObject.getString(Constants.LOCATIONKEYS.LATITUDE)), Double.parseDouble(mapObject.getString(Constants.LOCATIONKEYS.LONGITUDE)))));
                        // Setting the zoom level in the map on last position  is clicked
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 14.0f));

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawMarkerOnMap(LatLng locationPoint, JSONObject mapObject) {
        try {
            // Creating an instance of MarkerOptions
            MarkerOptions markerOptions = new MarkerOptions();

            // Setting latitude and longitude for the marker
            markerOptions.position(locationPoint);

            markerOptions.snippet(mapObject.getString(Constants.GEOCHAT.DESCRIPTION));
            markerOptions.title(mapObject.getString(Constants.LOCATIONKEYS.CHECKIN));
            String url = mapObject.getString(Constants.JsonKeys.GEOCHATIMAGE);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Utils.getBitmapFromURL(url))); //call getbitmap() method to download image from url

            // Adding marker on the Google Map
            Marker marker = googleMap.addMarker(markerOptions);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Setting toolbar
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) this;
        activity.setSupportActionBar(toolbar);
        final ActionBar ab = activity.getSupportActionBar();
        ab.setTitle("Map View");
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
    }

    private void initialization() {
    }


    private void setWidgetEvent() {
    }

    private void geWidgetReferences() {
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

    }

}
