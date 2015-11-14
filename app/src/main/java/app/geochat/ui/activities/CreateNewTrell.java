package app.geochat.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import app.geochat.R;
import app.geochat.managers.GeoChatManagers;
import app.geochat.services.asynctask.MultimediaGeoChatAsyncTask;
import app.geochat.util.Constants;
import app.geochat.util.NetworkManager;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 11/11/15.
 */
public class CreateNewTrell extends AppCompatActivity implements View.OnClickListener, Constants.TRELL {

    private TextView locationTextView, aboutTextView, tagsTextView;
    private ImageView trellImageView;
    private String tags="";
    private String longitude="",latitude="",location="";
    private String imagePath="";
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trell);
        Utils.setActivityToolbar(this, R.string.new_trell, this);

        geWidgetReferences();
        setWidgetEvent();
        initializations();
    }

    private void initializations() {
    }

    private void setWidgetEvent() {
        locationTextView.setOnClickListener(this);
        aboutTextView.setOnClickListener(this);
        tagsTextView.setOnClickListener(this);
        trellImageView.setOnClickListener(this);

    }

    private void geWidgetReferences() {
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        aboutTextView = (TextView) findViewById(R.id.aboutTextView);
        tagsTextView = (TextView) findViewById(R.id.tagsTextView);
        trellImageView = (ImageView) findViewById(R.id.trellImageView);
    }

    @Override
    public void onClick(View v) {

        if (v == locationTextView) {
            Intent intent = new Intent(this, AddLocation.class);
            intent.setAction(Constants.TRELL.ACTION_LOCATION);
            startActivityForResult(intent, Constants.TRELL.ACTION_LOCATION_ID);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (v == aboutTextView) {
            Intent intent = new Intent(this, AddAbout.class);
            intent.putExtra(Constants.GEOCHAT.DESCRIPTION,aboutTextView.getText().toString().trim());
            intent.setAction(Constants.TRELL.ACTION_ABOUT);
            startActivityForResult(intent, Constants.TRELL.ACTION_ABOUT_ID);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


        } else if (v == tagsTextView) {
            Intent intent = new Intent(this, AddTags.class);
            intent.setAction(Constants.TRELL.ACTION_TAGS);
            startActivityForResult(intent, Constants.TRELL.ACTION_TAGS_ID);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


        } else if (v == trellImageView) {
            Intent intent = new Intent(this, AddTrellImage.class);
            intent.setAction(Constants.TRELL.ACTION_IMAGE);
            startActivityForResult(intent, Constants.TRELL.ACTION_IMAGE_ID);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }
    }

    /**
     * Call Back method  to get the station name form other Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Constants.TRELL.ACTION_ABOUT_ID) {
                String about = data.getStringExtra(Constants.GEOCHAT.DESCRIPTION);
                if(!about.isEmpty())
                    aboutTextView.setText(about);
                else {
                    aboutTextView.setText("");
                    aboutTextView.setHint("Write about the places in 200 chars");
                }
            } else if (requestCode == Constants.TRELL.ACTION_TAGS_ID) {
                tags = data.getStringExtra(Constants.GEOCHAT.TAGS);
                if(!tags.isEmpty() && null!=tags) {
                    String[] tagsArray = tags.split(",");
                    Spanned spannabaleTags = Utils.getFormattedTagsWithoutClicks(tagsArray, this);
                    tagsTextView.setText(spannabaleTags);
                } else {
                    tagsTextView.setText("");
                    tagsTextView.setHint("Add tags");
                }
            } else if (requestCode == Constants.TRELL.ACTION_IMAGE_ID) {
                imagePath = data.getStringExtra(Constants.LOCATIONKEYS.GEOCHATIMAGE);
                if(!imagePath.isEmpty())
                   trellImageView.setImageDrawable(Drawable.createFromPath(imagePath));
            } else if (requestCode == Constants.TRELL.ACTION_LOCATION_ID) {
                location = data.getStringExtra(Constants.LOCATIONKEYS.CHECKIN);
                latitude = data.getStringExtra(Constants.LOCATIONKEYS.LATITUDE);
                longitude = data.getStringExtra(Constants.LOCATIONKEYS.LONGITUDE);
                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(addresses.size()>0){
                    cityName = addresses.get(0).getLocality();
                }
                locationTextView.setText(location);
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if(NetworkManager.isConnectedToInternet(this)) {
                    if (imagePath.isEmpty() && location.isEmpty()) {
                        Utils.showToast(this, "Please add About/Image");
                        return true;
                    } else if (!imagePath.equalsIgnoreCase("")) {
                        imagePath = Utils.compressImage(imagePath, this);
                        new MultimediaGeoChatAsyncTask(this).execute(location, latitude, longitude, imagePath, aboutTextView.getText().toString(), tags, location, cityName);
                    } else {
                        new GeoChatManagers(this).createGeoChat(location, latitude, longitude, aboutTextView.getText().toString(), tags, location, cityName);
                    }
                }
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
