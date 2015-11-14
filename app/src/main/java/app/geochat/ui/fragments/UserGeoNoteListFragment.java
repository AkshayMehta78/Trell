package app.geochat.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.managers.ProfileManager;
import app.geochat.ui.adapters.RecyclerViewAdapter;
import app.geochat.ui.widgets.SpacesItemDecoration;
import app.geochat.util.CircularProgressView;
import app.geochat.util.Constants;
import app.geochat.util.NetworkManager;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 07/11/15.
 */
public class UserGeoNoteListFragment extends Fragment {

    private CircularProgressView progressBar;
    private RecyclerView recyclerView;
    private View mView;
    private ProfileManager mProfileManager;
    private SharedPreferences mSharedPreferences;
    private RecyclerViewAdapter adapter;
    private String mUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.geonote_list, container, false);
        getWidgetReferences();
        initialization();
        mUserId = mSharedPreferences.getUserProfileId();

        Log.e("User profile mUserId",mUserId);
        return mView;
    }

    private void initialization() {
        mProfileManager = new ProfileManager(getActivity());
        mSharedPreferences = new SharedPreferences(getActivity());
    }

    private void getWidgetReferences() {
        progressBar = (CircularProgressView) mView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SpacesItemDecoration(20));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllUserFeedDetails();
    }

    private void getAllUserFeedDetails() {
        if(NetworkManager.isConnectedToInternet(getActivity()))
            mProfileManager.fetchUserFeedDetails(mUserId);
    }


    public void renderGeoChatListView(ArrayList<GeoChat> response) {
        renderUserTimeLine(response);
    }

    private void renderUserTimeLine(ArrayList<GeoChat> result) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if (result.size() > 0) {
            adapter = new RecyclerViewAdapter(getActivity(), result);
            recyclerView.setAdapter(adapter);
        } else
            Utils.showToast(getActivity(), getString(R.string.no_geochat));
    }
}
