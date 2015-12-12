package app.geochat.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.android.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.managers.GeoChatManagers;
import app.geochat.services.asynctask.LocationService;
import app.geochat.ui.activities.ChatActivity;
import app.geochat.ui.activities.CheckInActivity;
import app.geochat.ui.activities.HomeActivity;
import app.geochat.ui.adapters.RecyclerViewAdapter;
import app.geochat.ui.widgets.SpacesItemDecoration;
import app.geochat.util.CircularProgressView;
import app.geochat.util.Constants;
import app.geochat.util.NetworkManager;
import app.geochat.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author akshay
 */
public class GeoChatListFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences mSharedPreferences;
    private RecyclerView mRecylcerlistView;
    private CircularProgressView loadingProgressBar;
    private GeoChatManagers geoChatManager;
    private RecyclerViewAdapter adapter;
    private View rootView;
    private TextView locationTextView;

    public GeoChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_geochatlist, container, false);

        getWidgetReferences();
        setWidgetEvents();
        initialization();
        getAllGeoChats(Constants.LOCATIONKEYS.LOCATIONFEEDS);

        return rootView;
    }

    private void setWidgetEvents() {
        locationTextView.setOnClickListener(this);
    }

    private void initialization() {
        mSharedPreferences = new SharedPreferences(getActivity());
        geoChatManager = new GeoChatManagers(getActivity());
    }

    private void getWidgetReferences() {
        mRecylcerlistView = (RecyclerView) rootView.findViewById(R.id.listView);
        mRecylcerlistView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecylcerlistView.addItemDecoration(new SpacesItemDecoration(20));
        loadingProgressBar = (CircularProgressView) rootView.findViewById(R.id.loadingProgressBar);
        locationTextView = (TextView) rootView.findViewById(R.id.locationTextView);
    }

    public void getAllGeoChats(String tag) {
        if(NetworkManager.isConnectedToInternet(getActivity())) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            mRecylcerlistView.setVisibility(View.GONE);
                double[] locationData = Utils.getLocationDetails(getActivity());
                Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(locationData[0], locationData[1], 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (tag.equalsIgnoreCase(Constants.LOCATIONKEYS.LOCATIONFEEDS)) {
                if (addresses.size() > 0) {
                    locationTextView.setText(addresses.get(0).getAddressLine(1));
                }
            } else {
                locationTextView.setText(getString(R.string.select_location));
            }
            geoChatManager.fetchAllGeoChats(getActivity(), locationData[0] + "", locationData[1] + "", tag);
        }
    }

    public void renderGeoChatListView(ArrayList<GeoChat> result, String status) {
        loadingProgressBar.setVisibility(View.GONE);
        mRecylcerlistView.setVisibility(View.VISIBLE);
        if (result.size() > 0) {
            if (status.equalsIgnoreCase(Constants.LOCATIONKEYS.LOCATIONFEEDS)) {
                Collections.sort(result, new CustomComparator());
            }
            adapter = new RecyclerViewAdapter(getActivity(), result);
            mRecylcerlistView.setAdapter(adapter);
        } else
            Utils.showToast(getActivity(), getString(R.string.no_geochat));
    }

    public void failResult() {
        loadingProgressBar.setVisibility(View.GONE);
        mRecylcerlistView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void openFullScreenNote(FragmentActivity activity, GeoChat item) {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(Constants.Preferences.GEOCHAT, item);
        getActivity().startActivity(chatIntent);
    }

    @Override
    public void onClick(View v) {
        if (locationTextView == v) {
            Intent intent = new Intent(getActivity(), CheckInActivity.class);
            intent.setAction(Constants.LOCATIONKEYS.CHECKIN);
            startActivityForResult(intent, Constants.LOCATIONKEYS.CHECKINID);
        }
    }


    /**
     * Call Back method  to get the location name form other Activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String location = data.getStringExtra(Constants.LOCATIONKEYS.CHECKIN);
            String latitude = data.getStringExtra(Constants.LOCATIONKEYS.LATITUDE);
            String longitude = data.getStringExtra(Constants.LOCATIONKEYS.LONGITUDE);

            locationTextView.setText(location);
            mRecylcerlistView.setAdapter(null);
            getLocationWiseGeoNotes(latitude, longitude);
        }
    }

    private void getLocationWiseGeoNotes(String latitude, String longitude) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        mRecylcerlistView.setVisibility(View.GONE);
        geoChatManager.fetchAllGeoChats(getActivity(), latitude, longitude, Constants.LOCATIONKEYS.LOCATIONFEEDS);
    }

    public void refreshGeoNotes() {
        locationTextView.setText(getString(R.string.select_location));
        getAllGeoChats(Constants.LOCATIONKEYS.REFRESH);
    }

    public void updateGeoNoteList(int position) {
        adapter.removeItemFromList(position);
    }

    public void openFullScreenNoteWithComment(GeoChat item) {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(Constants.Preferences.GEOCHAT, item);
        chatIntent.putExtra(Constants.Preferences.COMMENT, "1");
        getActivity().startActivity(chatIntent);
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

    public void updateWishList(int position, String wishlist){
        adapter.updateWishList(position,wishlist);
    }
}
