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

/**
 * Created by akshaymehta on 07/11/15.
 */
public class AchievementFragment extends Fragment {

    private ProfileManager mProfileManager;
    private SharedPreferences mSharedPreferences;

    private TextView nativeSubheaderTextView;
    private CircularProgressView progressBar;
    private RecyclerView recyclerView;
    private View mView;
    private UserExploreRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.user_achievement_layout, container, false);
        getWidgetReferences();
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialization();
        fetchExploreDetails();
    }

    private void fetchExploreDetails() {
        mProfileManager.fetchUserExploreDetails(getActivity(), mSharedPreferences.getUserId());
    }


    private void getWidgetReferences() {
        nativeSubheaderTextView = (TextView) mView.findViewById(R.id.nativeSubheaderTextView);
        progressBar = (CircularProgressView) mView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
    }


    private void initialization() {
        mProfileManager = new ProfileManager(getActivity());
        mSharedPreferences = new SharedPreferences(getActivity());
    }


    public void updateExploreResult(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
         //   JSONObject citiesObject = jsonObject.getJSONObject("cities");
            JSONArray jsonArray = jsonObject.getJSONArray("nativePlacesListArray");
            setUpRecyClerView(jsonArray,0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setUpRecyClerView(JSONArray jsonArray, int tabIndex) {
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new RecyclerViewItemDivider(getActivity()));
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new WrappingLinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new UserExploreRecyclerViewAdapter(getActivity(), jsonArray, tabIndex);
        recyclerView.setAdapter(mAdapter);
        mView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
