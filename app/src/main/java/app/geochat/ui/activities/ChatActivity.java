package app.geochat.ui.activities;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anton46.collectionitempicker.CollectionPicker;
import com.anton46.collectionitempicker.Item;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.beans.UserChats;
import app.geochat.db.managers.LoginManager;
import app.geochat.managers.GeoChatManagers;
import app.geochat.ui.adapters.ChatListAdapter;
import app.geochat.util.CircularProgressView;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 12/09/15.
 */
public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences mSharedPreferences;
    private GeoChat mChatItem;
    private CircularProgressView loadingProgressBar;
    private GeoChatManagers mManager;
    private ChatListAdapter adapter;
    private ArrayList<UserChats> result;
    private EditText textEditTextView;
    private ImageView sendImageView;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private ImageView geoChatImageView;
    private CollapsingToolbarLayout mCollapsingToolbar;

    private SlidingUpPanelLayout mSlidingLayout;
    private TextView mLocationTextView,mAboutLocationTextView;
    private ListView mChatListView;
    private EditText mCommentEditText;
    private ImageView mSendImageView;
    private CollectionPicker mPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updated_geochat);
        mSharedPreferences = new SharedPreferences(this);

        getWidgetReferences();
        setWidgetEvents();
        initialization();
        setToolbar();
        String isComment = getIntent().getStringExtra(Constants.Preferences.COMMENT);
        if(isComment!=null){
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            setGeoComments();
        }
        setNotesData();
        mSlidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                Drawable image = getResources().getDrawable(R.drawable.down_arrow );
                int h = image.getIntrinsicHeight();
                int w = image.getIntrinsicWidth();
                image.setBounds( 0, 0, w, h );
                mLocationTextView.setText(getString(R.string.slide_up_text));
                mLocationTextView.setCompoundDrawables(image, null, null, null);
                mLocationTextView.setCompoundDrawablePadding(5);
            }

            @Override
            public void onPanelExpanded(View view) {
                setGeoComments();
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });


    }

    private void setGeoComments() {
        getAllChats();
        mLocationTextView.setText(mChatItem.getCheckInLocation());
        mLocationTextView.setCompoundDrawables(null, null, null, null);
    }

    private void setNotesData() {
        if(!mChatItem.getDescripton().isEmpty()) {
            mAboutLocationTextView.setText(mChatItem.getDescripton());
        } else {
            mAboutLocationTextView.setVisibility(View.GONE);
        }

        String tags = mChatItem.getTags();
        String age = Utils.dateDiff(Utils.getDate(mChatItem.getCreatedDateTime()));

        if(!tags.isEmpty() && null!=tags) {
            String[] tagsArray = tags.split(",");
            List<Item> tagsListArray = Utils.getItemListArray(tagsArray);
            mPicker.setItems(tagsListArray);
        } else {
            mPicker.setVisibility(View.GONE);
        }
    }

    private void setWidgetEvents() {
        mSendImageView.setOnClickListener(this);
        geoChatImageView.setOnClickListener(this);
    }

    private void getAllChats() {
        mManager.fetchAllChats(this, mChatItem);
    }

    private void initialization() {
        mChatItem = getIntent().getParcelableExtra(Constants.Preferences.GEOCHAT);
        mManager = new GeoChatManagers(this);
        result = new ArrayList<UserChats>();
    }

    private void getWidgetReferences() {
        mAppBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        geoChatImageView = (ImageView) findViewById(R.id.geoChatImageView);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLocationTextView = (TextView) findViewById(R.id.locationTextView);
        mChatListView = (ListView) findViewById(R.id.chatListView);
        mCommentEditText = (EditText) findViewById(R.id.commentEditText);
        mSendImageView = (ImageView) findViewById(R.id.sendImageView);
        mAboutLocationTextView = (TextView) findViewById(R.id.aboutLocationTextView);
        mPicker = (CollectionPicker) findViewById(R.id.collection_picker);
    }

    /**
     * Setting toolbar
     */
    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setCollapsibleToolBar(mChatItem.getCheckInLocation(),mChatItem.getGeoChatImage());

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowTitleEnabled(true);
            ab.setTitle(mChatItem.getCaption());
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Setting collapsible toolbar
     *
     * @param title
     */
    private void setCollapsibleToolBar(String title, String geoChatUrl) {
        mCollapsingToolbar.setTitle(title);
        if(geoChatUrl!=null && !geoChatUrl.isEmpty())
            Picasso.with(this).load(geoChatUrl).placeholder(R.drawable.travel_bg).into(geoChatImageView);
        //TODO : uncomment this  mCategoryImageView.setImageUrl(categoryImageUrl, mImageLoader);
    }

    @Override
    public void onClick(View v) {
        if (v == mSendImageView) {
            String userMessage = mCommentEditText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                Utils.hide_keyboard(ChatActivity.this);
                mManager.sendUserMessage(ChatActivity.this, mChatItem, userMessage, mSharedPreferences.getUserId());
            } else
                Utils.showToast(this, getString(R.string.no_user_message));
        } else if (v == geoChatImageView) {
            openDialog(mChatItem.getGeoChatImage());
        }
    }


    public void renderChatListView(ArrayList<UserChats> result) {
        if (result.size() > 0) {
            this.result = result;
            adapter = new ChatListAdapter(this, result);
            mChatListView.setAdapter(adapter);
        } else
            Utils.showToast(this, getString(R.string.no_chat));
    }

    public void failResult(String message) {
        Utils.showToast(this, message);
    }

    public void updateResult() {
        UserChats item = new UserChats();
        item.setUserId(mSharedPreferences.getUserId());
        if (mSharedPreferences.getLoginMode().equalsIgnoreCase(LoginManager.MODE_FACEBOOK)) {
            item.setUserName(mSharedPreferences.getFacebookName());
            item.setUserAvatar(mSharedPreferences.getFacebookProfilePicture());
        } else {
            item.setUserName(mSharedPreferences.getGooglePlusName());
            item.setUserAvatar(mSharedPreferences.getGooglePlusProfilePicUrl());
        }
        item.setUserMessage(mCommentEditText.getText().toString().trim());
        item.setDate("");
        if (result.size() == 0) {
            result.add(item);
            adapter = new ChatListAdapter(this, result);
            mChatListView.setAdapter(adapter);
        } else {
            result.add(item);
            adapter.notifyDataSetChanged();
        }
        mCommentEditText.setText("");
    }

    @Override
    public void onBackPressed() {
        if(mSlidingLayout.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED)
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else
            super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;

    }


    public void openDialog(String imageUrl) {
        Dialog mSplashDialog = new Dialog(this,R.style.FullScreenDialogTheme);
        mSplashDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        mSplashDialog.setContentView(R.layout.activity_imageview);
        mSplashDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSplashDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mSplashDialog.setCancelable(true);
        mSplashDialog.show();

        ImageView fullScreenImageview = (ImageView) mSplashDialog.findViewById(R.id.fullScreenImageview);
        if (!imageUrl.isEmpty()) {
            Picasso.with(this).load(imageUrl).into(fullScreenImageview);
        }
    }
}
