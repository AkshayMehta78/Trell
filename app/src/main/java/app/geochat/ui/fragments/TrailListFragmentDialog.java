package app.geochat.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.beans.Trail;
import app.geochat.managers.GeoChatManagers;
import app.geochat.ui.adapters.TrailListAdapter;
import app.geochat.util.CircularProgressView;
import app.geochat.util.NetworkManager;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 20/11/15.
 */
public class TrailListFragmentDialog extends DialogFragment implements View.OnClickListener {

    private GeoChat mItem;
    private GeoChatManagers mManagers;
    private SharedPreferences mSharedPreferences;
    private FragmentActivity fragmentActivity;
    private TrailListAdapter mAdapter;

    private ListView trailsListView;
    private CircularProgressView loadingProgressBar;
    private RelativeLayout emptyLayout;
    private ImageView emptyImageview;
    private TextView errorTextView;
    private EditText nameEditText;
    private ImageView addTrailImageView;


    public TrailListFragmentDialog(GeoChat item, FragmentActivity activity) {
        this.mItem = item;
        this.fragmentActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializations();
    }

    private void initializations() {
        mSharedPreferences = new SharedPreferences(getActivity());
        mManagers = new GeoChatManagers(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setTitle("Add to Trail");
        getDialog().setCanceledOnTouchOutside(true);

        View rootView = inflater.inflate(R.layout.fragment_add_trail_layout, container, false);
        trailsListView = (ListView) rootView.findViewById(R.id.trailsListView);
        loadingProgressBar = (CircularProgressView) rootView.findViewById(R.id.loadingProgressBar);
        loadingProgressBar = (CircularProgressView) rootView.findViewById(R.id.loadingProgressBar);
        emptyImageview = (ImageView) rootView.findViewById(R.id.emptyImageview);
        errorTextView = (TextView) rootView.findViewById(R.id.errorTextView);
        nameEditText = (EditText) rootView.findViewById(R.id.nameEditText);
        addTrailImageView = (ImageView) rootView.findViewById(R.id.addTrailImageView);
        addTrailImageView.setOnClickListener(this);


        trailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trail item = mAdapter.getItem(position);
                mManagers.addTrellToTrail(item.getTrailId(),mSharedPreferences.getUserId(),mItem.getGeoChatId(),fragmentActivity);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllTrails();

    }

    private void getAllTrails() {
        if (NetworkManager.isConnectedToInternet(getActivity())) {
            mManagers.fetchTrailList(mSharedPreferences.getUserId(), fragmentActivity,mItem);
        }
    }

    public void sendTrailList(ArrayList<Trail> result) {
        loadingProgressBar.setVisibility(View.GONE);
        trailsListView.setVisibility(View.VISIBLE);
        if (result.size() > 0) {
            //   Collections.reverse(result);
            mAdapter = new TrailListAdapter(getActivity(), result);
            trailsListView.setAdapter(mAdapter);
        } else {
            sendEmptyTrailList(getString(R.string.no_trail));
        }
    }

    public void sendEmptyTrailList(String message) {
        loadingProgressBar.setVisibility(View.GONE);
        trailsListView.setVisibility(View.GONE);
        emptyImageview.setImageResource(R.drawable.empty_box);
        errorTextView.setText(message);
        trailsListView.setEmptyView(emptyLayout);
    }

    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 800);
        window.setGravity(Gravity.CENTER);
        //TODO:
    }

    @Override
    public void onClick(View v) {
        if (v == addTrailImageView) {
            String name = nameEditText.getText().toString().trim();
            if (!name.isEmpty()) {
                mManagers.creatTrail(name, fragmentActivity);
            } else {
                nameEditText.setError(getString(R.string.please_add_trail_name));
            }
        }
    }

    public void addTrailToList(Trail item) {
        nameEditText.setText("");
        if (mAdapter != null) {
            mAdapter.updateList(item);
            trailsListView.smoothScrollToPosition(mAdapter.getCount() - 1);
        } else {
            ArrayList<Trail> result = new ArrayList<Trail>();
            result.add(item);
            sendTrailList(result);
        }
    }

    public void onSuccess(String message) {
        Utils.showToast(getActivity(),message);
        getDialog().dismiss();
    }
}