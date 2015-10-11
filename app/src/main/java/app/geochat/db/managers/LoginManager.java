package app.geochat.db.managers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.android.Util;

import app.geochat.DesidimeApplication;
import app.geochat.R;
import app.geochat.beans.SharedPreferences;
import app.geochat.LoginActivity;
import app.geochat.util.Constants;
import app.geochat.util.Utils;
import app.geochat.util.VolleyController;
import app.geochat.util.api.ResponseException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by akshaymehta on 30/07/15.
 */
public class LoginManager extends Manager implements Constants.UserInfoKeys, Constants.JsonKeys,Constants.Config,Constants.LoginKeys {



    private static LoginManager mLoginManager = new LoginManager();

    private SharedPreferences mSharedPreferences;
    private int mResponseStatusCode = 0;

    public static LoginManager getInstance() {
        return mLoginManager;
    }

    public static String getAPIStatus(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString(STATUS);

    }

    public static String getAPIMessage(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString(MESSAGE);
    }

    public void saveGoogleUserInfo(Context context, String id, String userName, String accountName, String profilePicUrl, String coverPhotoUrl) throws Exception {
        SharedPreferences mSharedPreferences = new SharedPreferences(context);
        mSharedPreferences.setGooglePlusId(id);
        mSharedPreferences.setGooglePlusName(userName);
        mSharedPreferences.setGooglePlusEmailId(accountName);
        mSharedPreferences.setGooglePlusProfilePicUrl(profilePicUrl);
        mSharedPreferences.setProfileImage(profilePicUrl);
        mSharedPreferences.setGooglePlusCoverPicUrl(coverPhotoUrl);
    }

    public void saveDDUserInfo(String userInfo, Context context, String accessToken, String refreshToken) throws JSONException {
        JSONObject jsonObject = new JSONObject(userInfo);
        SharedPreferences desiDimePreferences = new SharedPreferences(context);

        desiDimePreferences.setUserId(String.valueOf(jsonObject.getInt(ID)));
        desiDimePreferences.setUserName(jsonObject.getString(USERNAME));
        desiDimePreferences.setCurrentDimes(jsonObject.getString(CURRENT_DIMES));
        desiDimePreferences.setRank(jsonObject.getString(RANK));

        if (!jsonObject.getString(IMAGE_LARGE).equals("") && desiDimePreferences.getProfileImage().equals(""))
            desiDimePreferences.setProfileImage(jsonObject.getString(IMAGE_LARGE));

        desiDimePreferences.setDob(jsonObject.getString(DATE_OF_BIRTH));
        desiDimePreferences.setGender(jsonObject.getString(GENDER));
        desiDimePreferences.setOccupation(jsonObject.getString("occupation"));
        desiDimePreferences.setInterests(jsonObject.getString("interests"));
        desiDimePreferences.setWebsite(jsonObject.getString("website"));
        desiDimePreferences.setBio(jsonObject.getString(BIO));
        desiDimePreferences.setUnreadMessageCount(jsonObject.getInt(UNREAD_MESSAGES_COUNT));
        desiDimePreferences.setReferralLink(jsonObject.getString(REFERRAL_LINK));
        desiDimePreferences.setReferralCode(jsonObject.getString(REFERRAL_CODE));
        desiDimePreferences.setReferralCount(jsonObject.getInt(REFERRAL_COUNT));
        desiDimePreferences.setWebNotificationCount(jsonObject.getInt(UNREAD_NOTIFICATION_COUNT));

        JSONArray jsonArray = jsonObject.getJSONArray("notifications_settings");
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject object = jsonArray.getJSONObject(i);
            String parmalink = object.getString("id");

            switch (parmalink) {

                case LEADER_BOARD_PARMA_LINK:
                    desiDimePreferences.setLeaderBoardSite(object.getBoolean("notify_on_app"));
                    desiDimePreferences.setLeaderBoardEmail(object.getBoolean("notify_on_email"));
                    break;

                case THREAD_ACTIVITY_PARMA_LINK:
                    desiDimePreferences.setThreadActivitySite(object.getBoolean("notify_on_app"));
                    desiDimePreferences.setThreadActivityEmail(object.getBoolean("notify_on_email"));
                    break;

                case PROFILE_SETTING_PARMA_LINK:
                    desiDimePreferences.setProfileSite(object.getBoolean("notify_on_app"));
                    desiDimePreferences.setProfileEmail(object.getBoolean("notify_on_email"));
                    break;

                case MILESTONE_PARMA_LINK:
                    desiDimePreferences.setMilestoneSite(object.getBoolean("notify_on_app"));
                    desiDimePreferences.setMilestoneEmail(object.getBoolean("notify_on_email"));
                    break;
            }
        }
    }


    public void saveFacebookUserInfo(JSONObject userInfoJSon, Context context) throws JSONException {
        SharedPreferences desiDimePreferences = new SharedPreferences(context);

        if (!userInfoJSon.isNull(FACEBOOK_ID)) {
            desiDimePreferences.setFacebookId(userInfoJSon.getString(FACEBOOK_ID));
            String imageUrl = "https://graph.facebook.com/" + userInfoJSon.getString(FACEBOOK_ID) + "/picture?type=small";
            desiDimePreferences.setFacebookProfilePicture(imageUrl);
            desiDimePreferences.setProfileImage(imageUrl);
        }

        if (!userInfoJSon.isNull(FACEBOOK_EMAIL_ID)) {
            desiDimePreferences.setFacebookEmailId(userInfoJSon.getString(FACEBOOK_EMAIL_ID));
        }

        if (!userInfoJSon.isNull(FACEBOOK_NAME)) {
            desiDimePreferences.setFacebookName(userInfoJSon.getString(FACEBOOK_NAME));
        }
    }

    /*
    Social Login
    */
    public void executeSocialLogin(final Activity activity, final String mode, final String socialId, final String EmailId, final String deviceToken, final String userName) {
        Utils.showProgress(activity);
        // Tag used to cancel the request
        String tag_social_login = "social_login";

        String url = Constants.API_AUTH_TOKEN;
        Log.e("url",url);
        final String deviceId = Utils.getDeviceId(activity);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = getAPIStatus(response);
                    String message = getAPIMessage(response);
                    Utils.showToast(activity,message);
                    if(status.equalsIgnoreCase(SUCCESS))
                    {
                        String UserID = jsonObject.getString(USERID);
                        new SharedPreferences(activity).setUserId(UserID);
                        ((LoginActivity)activity).loginUser(mode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeProgress();
                Utils.showToast(activity,activity.getString(R.string.something_went_wrong));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(SOCIALUID, socialId);
                params.put(USEREMAIL, EmailId);
                params.put(USERNAME, userName);
                params.put(DEVICE_NUMBER, deviceId);
                params.put(DEVICE_TOKEN,deviceToken);
                params.put(PLATFORM, Constants.ANDROID);
                params.put(MODE,LoginManager.MODE_FACEBOOK);
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_social_login);

    }

}
