package app.geochat.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.beans.UserChats;
import app.geochat.db.managers.LoginManager;
import app.geochat.managers.GeoChatManagers;
import app.geochat.ui.adapters.ChatListAdapter;
import app.geochat.ui.adapters.RecyclerViewAdapter;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 12/09/15.
 */
public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences mSharedPreferences;
    private GeoChat mChatItem;
    private ListView chatListView;
    private ProgressBar loadingProgressBar;
    private GeoChatManagers mManager;
    private ChatListAdapter adapter;
    private ArrayList<UserChats> result;
    private EditText textEditTextView;
    private ImageView sendImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geochat);
        mSharedPreferences = new SharedPreferences(this);

        getWidgetReferences();
        setWidgetEvents();
        initialization();
        setToolbar();
        getAllChats();
    }

    private void setWidgetEvents() {
        sendImageView.setOnClickListener(this);
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
        chatListView = (ListView) findViewById(R.id.chatListView);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        textEditTextView = (EditText) findViewById(R.id.textEditTextView);
        sendImageView = (ImageView) findViewById(R.id.sendImageView);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle(mChatItem.getCheckInLocation());
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }


    public void renderChatListView(ArrayList<UserChats> result) {
        loadingProgressBar.setVisibility(View.GONE);
        chatListView.setVisibility(View.VISIBLE);
        if (result.size() > 0) {
            this.result = result;
            adapter = new ChatListAdapter(this, result);
            chatListView.setAdapter(adapter);
        } else
            Utils.showToast(this, getString(R.string.no_chat));
    }

    public void failResult() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == sendImageView) {
            String userMessage = textEditTextView.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                mManager.sendUserMessage(ChatActivity.this, mChatItem, userMessage, mSharedPreferences.getUserId());
            } else
                Utils.showToast(this, getString(R.string.no_user_message));
        }
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
        item.setUserMessage(textEditTextView.getText().toString().trim());
        item.setDate("");
        if (result.size() == 0) {
            result.add(item);
            loadingProgressBar.setVisibility(View.GONE);
            chatListView.setVisibility(View.VISIBLE);
            adapter = new ChatListAdapter(this, result);
            chatListView.setAdapter(adapter);
        } else {
            result.add(item);
            adapter.notifyDataSetChanged();
        }
        textEditTextView.setText("");
    }
}
