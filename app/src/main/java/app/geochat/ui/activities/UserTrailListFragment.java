package app.geochat.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.geochat.R;
import app.geochat.beans.SharedPreferences;
import app.geochat.beans.Trail;
import app.geochat.managers.ProfileManager;
import app.geochat.ui.adapters.RecyclerViewAdapter;
import app.geochat.ui.adapters.UserTrailListAdapter;
import app.geochat.ui.widgets.SpacesItemDecoration;
import app.geochat.util.CircularProgressView;
import app.geochat.util.NetworkManager;

/**
 * Created by akshaymehta on 29/11/15.
 */
public class UserTrailListFragment extends Fragment {

    private CircularProgressView progressBar;
    private RecyclerView recyclerView;
    private View mView;
    private ProfileManager mProfileManager;
    private SharedPreferences mSharedPreferences;
    private UserTrailListAdapter adapter;
    private String mUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.trail_list, container, false);
        getWidgetReferences();
        initialization();
        mUserId = mSharedPreferences.getUserProfileId();

        Log.e("User profile mUserId", mUserId);
        return mView;
    }

    private void initialization() {
        mProfileManager = new ProfileManager(getActivity());
        mSharedPreferences = new SharedPreferences(getActivity());
    }

    private void getWidgetReferences() {
        progressBar = (CircularProgressView) mView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,1));
        recyclerView.addItemDecoration(new SpacesItemDecoration(20));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserTrailList();
    }

    private void getUserTrailList() {
        if(NetworkManager.isConnectedToInternet(getActivity()))
        mProfileManager.getUserProfileTrailList(mUserId, UserTrailListFragment.this);
    }


    public void sendTrailList(ArrayList<Trail> result) {
        progressBar.setVisibility(View.GONE);
        if(result.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new UserTrailListAdapter(getActivity(),result);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }
}
