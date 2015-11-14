package app.geochat.ui.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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

import java.util.ArrayList;

import app.geochat.R;
import app.geochat.beans.ClusterGeoChat;
import app.geochat.util.Constants;
import app.geochat.util.Utils;
import io.nlopez.clusterer.Cluster;
import io.nlopez.clusterer.Clusterer;
import io.nlopez.clusterer.MarkerAnimation;
import io.nlopez.clusterer.OnPaintingClusterListener;
import io.nlopez.clusterer.OnPaintingClusterableMarkerListener;

/**
 * Created by akshaymehta on 10/11/15.
 */
public class MapActivity extends AppCompatActivity {

    private String mapJsonArrayString;
    private GoogleMap googleMap;

    private ArrayList<ClusterGeoChat> mapAreas;
    private Clusterer<ClusterGeoChat> mClusterer;
    private int mLocationPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_activity);
        //Set up toolbar with back option
        setToolbar();
        if(getIntent()!=null) {
            mapJsonArrayString = getIntent().getStringExtra(Constants.USER.MAPDATA);
            mLocationPoint = getIntent().getIntExtra(Constants.LOCATIONKEYS.POSITION, 0);
        }
        geWidgetReferences();
        setWidgetEvent();
        initialization();

     //   setAllLocationOnMap();
        prepareArrayList();
        initClusterer();
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
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Utils.getBitmapFromURL(url,this))); //call getbitmap() method to download image from url

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
        mapAreas = new ArrayList<ClusterGeoChat>();
    }


    private void setWidgetEvent() {
    }

    private void geWidgetReferences() {
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

    }


    private void prepareArrayList() {
        try {

            JSONArray mapArray = new JSONArray(mapJsonArrayString);
            for (int i = 0; i < mapArray.length(); i++) {
                JSONObject mapObject = mapArray.getJSONObject(i);
                ClusterGeoChat item = new ClusterGeoChat();
                item.setCheckIn(mapObject.getString(Constants.LOCATIONKEYS.CHECKIN));
                item.setDescription(mapObject.getString(Constants.GEOCHAT.DESCRIPTION));
                item.setGeoChatImage(mapObject.getString(Constants.JsonKeys.GEOCHATIMAGE));
                item.setLocationLatLng(new LatLng(Double.parseDouble(mapObject.getString(Constants.LOCATIONKEYS.LATITUDE)), Double.parseDouble(mapObject.getString(Constants.LOCATIONKEYS.LONGITUDE))));
                mapAreas.add(item);
                if(i==mLocationPoint)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mapObject.getString(Constants.LOCATIONKEYS.LATITUDE)), Double.parseDouble(mapObject.getString(Constants.LOCATIONKEYS.LONGITUDE))), 11.0f));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initClusterer() {
        mClusterer = new Clusterer<ClusterGeoChat>(this, googleMap);
        mClusterer.addAll(mapAreas);

        mClusterer.setAnimationEnabled(true);
        mClusterer.setMarkerAnimation(new MarkerAnimation() {
            @Override
            public void animateMarker(Marker marker, float interpolation) {
                // Basic fading animation
                marker.setAlpha(interpolation);
            }
        });

        mClusterer.setClustererListener(new Clusterer.ClustererClickListener<ClusterGeoChat>() {
            @Override
            public void markerClicked(ClusterGeoChat marker) {
                Log.e("Clusterer", "marker clicked");
            }

            @Override
            public void clusterClicked(Cluster position) {
                Log.e("Clusterer", "cluster clicked");
            }
        });

        mClusterer.setOnPaintingMarkerListener(new OnPaintingClusterableMarkerListener<ClusterGeoChat>() {

            @Override
            public void onMarkerCreated(Marker marker, ClusterGeoChat clusterable) {

            }

            @Override
            public MarkerOptions onCreateMarkerOptions(ClusterGeoChat poi) {

                return new MarkerOptions().position(poi.getPosition()).title(poi.getCheckIn()).snippet(poi.getDescription()).icon(BitmapDescriptorFactory.fromBitmap(Utils.getBitmapFromURL(poi.getGeoChatImage(), getApplicationContext())));
            }
        });

        mClusterer.setOnPaintingClusterListener(new OnPaintingClusterListener<ClusterGeoChat>() {

            @Override
            public void onMarkerCreated(Marker marker, Cluster<ClusterGeoChat> cluster) {

            }

            @Override
            public MarkerOptions onCreateClusterMarkerOptions(Cluster<ClusterGeoChat> cluster) {
                return new MarkerOptions()
                        .title("Clustering " + cluster.getWeight() + " items")
                        .position(cluster.getCenter())
                        .icon(BitmapDescriptorFactory.fromBitmap(getClusteredLabel(cluster.getWeight(),
                                MapActivity.this)));

            }
        });







    }


    private Bitmap getClusteredLabel(Integer count, Context ctx) {

        float density = getResources().getDisplayMetrics().density;

        Resources r = ctx.getResources();
        Bitmap res = BitmapFactory.decodeResource(r, R.drawable.square_icon);
        res = res.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(res);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(12 * density);

        c.drawText(String.valueOf(count.toString()), res.getWidth() / 2, res.getHeight() / 2 + textPaint.getTextSize() / 3, textPaint);

        return res;
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
