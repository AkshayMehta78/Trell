package app.geochat.ui.fragments;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.managers.GeoChatManagers;
import app.geochat.services.asynctask.LocationService;
import app.geochat.ui.adapters.RecyclerViewAdapter;
import app.geochat.ui.widgets.SpacesItemDecoration;
import app.geochat.util.CircularProgressView;
import app.geochat.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author akshay
 */
public class GeoChatListFragment extends Fragment {

    private SharedPreferences mSharedPreferences;
    private RecyclerView mRecylcerlistView;
    private CircularProgressView loadingProgressBar;
    private GeoChatManagers geoChatManager;
    private RecyclerViewAdapter adapter;
    private View rootView;

    public GeoChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_geochatlist, container, false);

        getWidgetReferences();
        initialization();
        getAllGeoChats();

        return rootView;
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
    }

    private void getAllGeoChats() {
        geoChatManager.fetchAllGeoChats(getActivity());
    }

    public void renderGeoChatListView(ArrayList<GeoChat> result) {
        loadingProgressBar.setVisibility(View.GONE);
        mRecylcerlistView.setVisibility(View.VISIBLE);
        if (result.size() > 0) {
            adapter = new RecyclerViewAdapter(getActivity(), result);
            mRecylcerlistView.setAdapter(adapter);
        } else
            Utils.showToast(getActivity(), getString(R.string.no_geochat));
    }

    public void failResult() {
        loadingProgressBar.setVisibility(View.GONE);
        mRecylcerlistView.setVisibility(View.VISIBLE);
    }


    public void verifyUser(GeoChat item) {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Utils.showToast(getActivity(), "GPS is disabled. Please Enable to Find your Location.");
        else {
            LocationService appLocationService = new LocationService(getActivity());
            Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

            if (gpsLocation != null) {
                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                geoChatManager.verifyUserToJoin(latitude, longitude, item.getUserId(), item.getGeoChatId(), item);
            } else {
                double latitude = 19.24;
                double longitude = 72.85;
                geoChatManager.verifyUserToJoin(latitude, longitude, item.getUserId(), item.getGeoChatId(), item);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllGeoChats();
    }
}
