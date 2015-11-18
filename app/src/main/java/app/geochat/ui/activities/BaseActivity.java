package app.geochat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import app.geochat.DesidimeApplication;
import app.geochat.R;
import app.geochat.beans.SharedPreferences;
import app.geochat.ui.fragments.GeoChatListFragment;
import app.geochat.util.Constants;
import app.geochat.util.StringUtils;
import app.geochat.util.Utils;
import app.geochat.util.VolleyController;

/**
 * BaseActivity handles the main layout and menus
 *
 * @author akshay
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = BaseActivity.class.getSimpleName();


    private DrawerLayout mDrawerLayout;
    private RelativeLayout mHeaderView;
    private ImageView mUserImage;
    private ImageView mCoverImageImageView,mCreateGeoNoteImageView;
    private TextView mUserNameTextView, mUserEmailTextView;
    private SharedPreferences mDesidimeSharedPreferences;
    private NavigationView navigationView;
    private Menu mMenu;
    private ImageView mRefreshImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mDesidimeSharedPreferences = new SharedPreferences(this);
        geWidgetReferences();
        setWidgetEvent();
        Utils.setToolbar(this, R.string.app_name, this);
        initializeNavigationDrawer();
        setUpHeaderView();
        callDefaultEvent();
    }

    private void setWidgetEvent() {
        mRefreshImageView.setOnClickListener(this);
        mCreateGeoNoteImageView.setOnClickListener(this);
    }

    private void geWidgetReferences() {
        mRefreshImageView = (ImageView) findViewById(R.id.refreshImageView);
        mCreateGeoNoteImageView = (ImageView) findViewById(R.id.createGeoNoteImageView);
    }


    /**
     * Call Deal by default
     */
    private void callDefaultEvent() {
        // Open Deal fragment by default
        navigationView.getMenu().findItem(R.id.item_deal).setChecked(true);
        createView(new GeoChatListFragment(), Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG);
    }

    /**
     * set up navigation drawer header
     */
    private void setUpHeaderView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mHeaderView = (RelativeLayout) inflater.inflate(R.layout.layout_drawer_header, null, false);
        // get widgets
        mUserImage = (ImageView) mHeaderView.findViewById(R.id.user_image);
        mCoverImageImageView = (ImageView) mHeaderView.findViewById(R.id.coverPicImageView);
        mUserNameTextView = (TextView) mHeaderView.findViewById(R.id.userNameTextView);
        mUserEmailTextView = (TextView) mHeaderView.findViewById(R.id.userEmailTextView);

        //set views
        String loginMode = mDesidimeSharedPreferences.getLoginMode();
        String userImage, userName, coverPic, userEmail;
        switch (loginMode) {
            case Constants.LoginKeys.MODE_FACEBOOK:
                userImage = mDesidimeSharedPreferences.getFacebookProfilePicture();
                userName = mDesidimeSharedPreferences.getFacebookName();
                coverPic = mDesidimeSharedPreferences.getFacebookCoverPic();
                userEmail = mDesidimeSharedPreferences.getFacebookEmailId();
                break;
            case Constants.LoginKeys.MODE_GOOGLE:
                userImage = mDesidimeSharedPreferences.getGooglePlusProfilePicUrl();
                userName = mDesidimeSharedPreferences.getGooglePlusName();
                coverPic = mDesidimeSharedPreferences.getGooglePlusCoverPicUrl();
                userEmail = mDesidimeSharedPreferences.getGooglePlusEmailId();
                break;
            default:
                userImage = null;
                userName = null;
                coverPic = null;
                userEmail = null;
                break;
        }
        setProfileImage(userImage);
        setProfileName(userName);
        setCoverPic(coverPic);
        serProfileEmail(userEmail);
        mUserImage.setOnClickListener(this);
        navigationView.addHeaderView(mHeaderView);

    }

    /**
     * set profile email
     */
    private void serProfileEmail(String userEmail) {
        if (StringUtils.isNotEmpty(userEmail))
            mUserEmailTextView.setText(userEmail);
    }

    /**
     * set Cover pic
     */
    private void setCoverPic(String coverPic) {
        if (StringUtils.isNotEmpty(coverPic)) {
            Log.e("cover pic", coverPic);
            Picasso.with(this).load(coverPic).into(mCoverImageImageView);
        } else
            mCoverImageImageView.setBackgroundResource(R.drawable.menu_header_bg);
    }

    /**
     * set profile name
     */
    private void setProfileName(String userName) {
        if (StringUtils.isNotEmpty(userName)) {
            mUserNameTextView.setText(userName);
        } else
            mUserNameTextView.setText(getString(R.string.sign_in_text));

    }

    /**
     * set profile image
     */
    private void setProfileImage(String userImage) {
        if (StringUtils.isNotEmpty(userImage)) {
            Log.e("userImage", userImage);
            Picasso.with(this).load(userImage).into(mUserImage);
        } else
            Picasso.with(this).load(userImage).into(mUserImage);
    }

    /**
     * Initialize navigation drawer
     */
    protected void initializeNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    /**
     * Setup menus action
     *
     * @param navigationView
     */
    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_deal:
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    /**
     * Create fragment view : returns on click menu item
     *
     * @param fragment
     * @param fragmentTag
     */
    private void createView(Fragment fragment, String fragmentTag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_layout, fragment, fragmentTag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        if(v==mRefreshImageView){
            GeoChatListFragment fragment =  (GeoChatListFragment)getSupportFragmentManager().findFragmentByTag(Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG);
            fragment.refreshGeoNotes();
        } else if (v==mCreateGeoNoteImageView){
            Intent trellIntent = new Intent(this,CreateNewTrell.class);
            startActivity(trellIntent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create:
                Intent intent = new Intent(this,CheckInActivity.class);
                startActivity(intent);
                break;
            case R.id.profile:
                mDesidimeSharedPreferences.setUserProfileId(mDesidimeSharedPreferences.getUserId());
                Intent profileintent = new Intent(this,UserProfileActivity.class);
                startActivity(profileintent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Call Back method  to get the station name form other Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Constants.LOCATIONKEYS.CHECKINID) {
                getSupportFragmentManager().findFragmentByTag(Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG).startActivityForResult(data,requestCode);
            }
        }
    }


    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getFragments().size()==1)
            finish();
        else
            super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        GeoChatListFragment fragment = (GeoChatListFragment) getSupportFragmentManager().findFragmentByTag(Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG);
        if(fragment.isAdded()){
            fragment.getAllGeoChats(Constants.LOCATIONKEYS.REFRESH);
        }
    }
}
