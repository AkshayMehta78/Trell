package app.geochat.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.geochat.R;
import app.geochat.beans.SharedPreferences;
import app.geochat.managers.ProfileManager;
import app.geochat.ui.adapters.UserExploreRecyclerViewAdapter;
import app.geochat.ui.widgets.RecyclerViewItemDivider;
import app.geochat.util.CircularProgressView;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 07/11/15.
 */
public class AchievementFragment extends Fragment {

    private ProfileManager mProfileManager;
    private SharedPreferences mSharedPreferences;

    private TextView nativeSubheaderTextView,veteranSubheaderTextView;
    private RecyclerView recyclerView,vRecyclerView;
    private View mView;
    private UserExploreRecyclerViewAdapter mPlacesAdapter,mCityAdapter;
    private int mPlacesCount,mCityCount;
    private String mUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.user_achievement_layout, container, false);
        initialization();
        mUserId = mSharedPreferences.getUserProfileId();
        Utils.showToast(getActivity(),mUserId);
        getWidgetReferences();
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchExploreDetails();
    }

    private void fetchExploreDetails() {
        mProfileManager.fetchUserExploreDetails(getActivity(), mUserId);
    }


    private void getWidgetReferences() {
        nativeSubheaderTextView = (TextView) mView.findViewById(R.id.nativeSubheaderTextView);
        veteranSubheaderTextView = (TextView) mView.findViewById(R.id.veteranSubheaderTextView);
    }


    private void initialization() {
        mProfileManager = new ProfileManager(getActivity());
        mSharedPreferences = new SharedPreferences(getActivity());
    }


    public void updateExploreResult(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject citiesObject = jsonObject.getJSONObject("cities");
            JSONArray citiesListArray = citiesObject.getJSONArray("citiesListArray");
            mCityCount = citiesListArray.length();
            JSONArray placesjsonArray = jsonObject.getJSONArray("nativePlacesListArray");
            mPlacesCount = placesjsonArray.length();
            setUpNativeRecyClerView(placesjsonArray, 0);
            setUpVeteranRecyClerView(citiesListArray,1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setUpNativeRecyClerView(JSONArray jsonArray, int tabIndex) {
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new RecyclerViewItemDivider(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mPlacesAdapter = new UserExploreRecyclerViewAdapter(getActivity(), jsonArray, tabIndex);
        recyclerView.setAdapter(mPlacesAdapter);
        mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        nativeSubheaderTextView.setText(mPlacesCount+" Neighbourhoods in "+mCityCount+" cities");
    }

    private void setUpVeteranRecyClerView(JSONArray jsonArray, int tabIndex) {
        vRecyclerView = (RecyclerView) mView.findViewById(R.id.v_recyclerView);
        vRecyclerView.setNestedScrollingEnabled(true);
        vRecyclerView.setHasFixedSize(false);
        vRecyclerView.addItemDecoration(new RecyclerViewItemDivider(getActivity()));
        vRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        vRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCityAdapter = new UserExploreRecyclerViewAdapter(getActivity(), jsonArray, tabIndex);
        vRecyclerView.setAdapter(mCityAdapter);
        mView.findViewById(R.id.v_progressBar).setVisibility(View.GONE);
        vRecyclerView.setVisibility(View.VISIBLE);

        veteranSubheaderTextView.setText(mCityCount+" cities in India");
    }
}
