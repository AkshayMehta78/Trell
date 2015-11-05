package app.geochat.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.facebook.android.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private static final int CAMERA_REQUEST = 2;

    private String latitude, longitude, location;
    private RecyclerView mRecyclerView;
    private PhotoRecyclerViewAdapter adapter;
    private TextView allPhotosTextView,cameraTextView;
    private ArrayList<String> result;
    private String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        getIntentData();
        setToolbar();
        initialization();
        getWidgetReferences();
        setWidgetClickListener();
        description = getIntent().getExtras().getString(Constants.GEOCHAT.DESCRIPTION);
        new AllPhotosAsyncTask(this).execute();
    }

    private void initialization() {
        result = new ArrayList<String>();
    }

    private void setWidgetClickListener() {
        allPhotosTextView.setOnClickListener(this);
        cameraTextView.setOnClickListener(this);
    }

    private void getWidgetReferences() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(5));
        mRecyclerView.setHasFixedSize(true);
        cameraTextView = (TextView) findViewById(R.id.cameraTextView);
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
            Collections.reverse(result);
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
                boolean flag = true;
                Bundle bundle = getIntent().getExtras();
                if(null!=description && description.isEmpty()) {
                    int count = adapter.isMultipleSelected();
                    if (count == 1){
                        bundle.putString(Constants.LOCATIONKEYS.GEOCHATIMAGE, adapter.getSelectedImage());
                    } else if (count>1){
                        flag = false;
                        Utils.showToast(this, getString(R.string.invalid_selection));
                    } else {
                        flag = false;
                        Utils.showToast(this, getString(R.string.geo_notes_compulsory_data));
                    }
                } else {
                    int count = adapter.isMultipleSelected();
                    if (count == 1) {
                        bundle.putString(Constants.LOCATIONKEYS.GEOCHATIMAGE, adapter.getSelectedImage());
                    } else if (count>1){
                        flag = false;
                        Utils.showToast(this, getString(R.string.invalid_selection));
                    } else {
                        bundle.putString(Constants.LOCATIONKEYS.GEOCHATIMAGE, "");
                    }

                }
                if(flag) {
                    Intent intent = new Intent(this, TagsActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        } else if (v==cameraTextView){
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                try {
                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = Utils.getPath(this, selectedImageUri);
                    if (selectedImagePath != null) {
                        selectedImagePath = Utils.compressImage(selectedImagePath, this);
                        result.set(0, selectedImagePath);
                        adapter.addMore();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if(requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = Utils.getImageURI(this, photo);
                File file = new File(Utils.getRealPathFromURI(tempUri, this));
                String selectedImagePath = Utils.compressImage(file.getAbsolutePath(), this);
                result.set(0, selectedImagePath);
                adapter.addMore();
            }
        }
    }

}
