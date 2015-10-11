package app.geochat.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import app.geochat.R;
import app.geochat.services.asynctask.AllPhotosAsyncTask;
import app.geochat.ui.adapters.PhotoRecyclerViewAdapter;
import app.geochat.ui.widgets.SpacesItemDecoration;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 07/10/15.
 */
public class SelectPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_PICTURE = 1;
    private String latitude, longitude, location;
    private RecyclerView mRecyclerView;
    private PhotoRecyclerViewAdapter adapter;
    private TextView allPhotosTextView;
    private ArrayList<String> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        getIntentData();
        setToolbar();
        initialization();
        getWidgetReferences();
        setWidgetClickListener();
        new AllPhotosAsyncTask(this).execute();
    }

    private void initialization() {
        result = new ArrayList<String>();
    }

    private void setWidgetClickListener() {
        allPhotosTextView.setOnClickListener(this);
    }

    private void getWidgetReferences() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(5));
        mRecyclerView.setHasFixedSize(true);

        allPhotosTextView = (TextView) findViewById(R.id.allPhotosTextView);
    }

    private void getIntentData() {
        Intent data = getIntent();
        latitude = data.getStringExtra(Constants.LOCATIONKEYS.LATITUDE);
        longitude = data.getStringExtra(Constants.LOCATIONKEYS.LONGITUDE);
        location = data.getStringExtra(Constants.LOCATIONKEYS.LOCATION);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(location);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }


    public void renderCameraImages(ArrayList<String> resultArray) {
        this.result = resultArray;
        if (result.size() > 0) {
            adapter = new PhotoRecyclerViewAdapter(this, result);
            mRecyclerView.setAdapter(adapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                int count = adapter.isMultipleSelected();
                if (count == 1) {
                    Intent intent = new Intent(this, CreateGeoChatActivity.class);
                    intent.putExtra(Constants.LOCATIONKEYS.LOCATION,location);
                    intent.putExtra(Constants.LOCATIONKEYS.LATITUDE,latitude);
                    intent.putExtra(Constants.LOCATIONKEYS.LONGITUDE,longitude);
                    intent.putExtra(Constants.LOCATIONKEYS.GEOCHATIMAGE,adapter.getSelectedImage());
                    startActivity(intent);
                } else
                    Utils.showToast(this, getString(R.string.invalid_selection));
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == allPhotosTextView) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = Utils.getPath(this, selectedImageUri);
                if (selectedImagePath != null) {
                    result.add(selectedImagePath);
                    adapter.addMore();
                }
            }
        }
    }

}
