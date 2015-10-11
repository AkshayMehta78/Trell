package app.geochat;
/**
 * Created by akshay on 27/7/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import app.geochat.beans.SharedPreferences;
import app.geochat.db.managers.LoginManager;
import app.geochat.util.NetworkManager;
import app.geochat.util.Utils;

public class LoginActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private SignInButton mSignInButton;

    private LoginManager mLoginManager;
    private SharedPreferences mSharedPreferences;

    private LoginButton mFaceBookLoginButton;
    private GraphUser mGraphUser;
    private UiLifecycleHelper uiHelper;

    private boolean isResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        * facebook UI Activity life cycle manager
        */
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        getWidgetReferences();
        setWidgetEvents();
        initialization();
        /*
        *  Facebook Login Button
        */
        mFaceBookLoginButton.setReadPermissions(Arrays.asList("public_profile","email"));
        mFaceBookLoginButton
                .setUserInfoChangedCallback(new UserInfoChangedCallback() {
                    @Override
                    public void onUserInfoFetched(GraphUser user) {
                        mGraphUser = user;
                        if (user != null) {
                            String accessToken = Session.getActiveSession()
                                    .getAccessToken();
                            mSharedPreferences.setFBAccessToken(accessToken);
                            Log.i("graph user info", user.toString());
                            JSONObject userInfoJSon = user.getInnerJSONObject();
                            Log.i("user info", userInfoJSon.toString());

                            try {
                                LoginManager loginManager = new LoginManager();
                                loginManager.saveFacebookUserInfo(userInfoJSon, LoginActivity.this);
                                if (NetworkManager.isConnectedToInternet(LoginActivity.this)) {
                                        mLoginManager.executeSocialLogin(LoginActivity.this, LoginManager.MODE_FACEBOOK, mSharedPreferences.getFacebookId(), mSharedPreferences.getFacebookEmailId(), mSharedPreferences.getDeviceToken(),mSharedPreferences.getFacebookName());
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                });

        /*
        * Google Plus Button
        */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        setGooglePlusButtonText(mSignInButton, getString(R.string.connect_with_google_text));

    }
    // Session onChange callback
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        uiHelper.onActivityResult(requestCode, responseCode, intent);
        Session.getActiveSession().onActivityResult(this, requestCode, responseCode, intent);

        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        if (NetworkManager.isConnectedToInternet(this)) {
            // Update the user interface to reflect that the user is signed in.
            mSignInButton.setEnabled(false);

            // Retrieve some profile information to personalize our app for the user.
            Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            try {

                if (currentUser != null) {

                    String id = currentUser.getId();
                    String userName = currentUser.getDisplayName();
                    String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    String profilePicUrl = currentUser.getImage().getUrl();
                    String coverPhotourl = "";
                    if (currentUser.hasCover()) {
                        coverPhotourl = currentUser.getCover().getCoverPhoto().getUrl();
                    }

                    mLoginManager.saveGoogleUserInfo(this, id, userName, accountName, profilePicUrl, coverPhotourl);
                    mLoginManager.executeSocialLogin(this, LoginManager.MODE_GOOGLE, mSharedPreferences.getGooglePlusId(), mSharedPreferences.getGooglePlusEmailId(), mSharedPreferences.getDeviceToken(), mSharedPreferences.getGooglePlusName());
                    mIntentInProgress = false;
                } else
                    Utils.showToast(LoginActivity.this, getResources().getString(R.string.something_went_wrong));
            } catch (Exception e) {
                e.printStackTrace();
                Utils.showToast(LoginActivity.this, getResources().getString(R.string.something_went_wrong));
            }
        } else {
            Utils.showToast(LoginActivity.this, getResources().getString(R.string.no_internet));
        }
    }


    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signInWithGplus();
                break;
        }
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /*
        setLoggedIn Status
    */
    public void loginUser(String loginMode) {
        mSharedPreferences.setLoggedIn(true);
        mSharedPreferences.setLoginMode(loginMode);
        Utils.startHomeActivity(this);
    }

    protected void setGooglePlusButtonText(SignInButton signInButton,
                                           String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(16);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setText(buttonText);
                tv.setGravity(Gravity.CENTER);
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        uiHelper.onResume();
        isResumed = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (isResumed) {
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
//	                showFragment(SELECTION, false);

                Log.i("Login", "Session is now open");
            } else if (state.isClosed()) {
//	                showFragment(SPLASH, false);

                Log.i("Login", "Session is now closed");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    public void initialization() {
        mLoginManager = LoginManager.getInstance();
        mSharedPreferences = new SharedPreferences(LoginActivity.this);

    }
    public void setWidgetEvents() {
        mSignInButton.setOnClickListener(this);
    }
    public void getWidgetReferences() {
        mSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mFaceBookLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);

    }

}