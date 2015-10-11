package app.geochat.managers;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.android.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.geochat.DesidimeApplication;
import app.geochat.LoginActivity;
import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.beans.UserChats;
import app.geochat.db.managers.LoginManager;
import app.geochat.ui.activities.BaseActivity;
import app.geochat.ui.activities.ChatActivity;
import app.geochat.ui.activities.CreateGeoChatActivity;
import app.geochat.ui.activities.HomeActivity;
import app.geochat.ui.fragments.GeoChatListFragment;
import app.geochat.util.Constants;
import app.geochat.util.Utils;
import app.geochat.util.VolleyController;
import app.geochat.util.api.GeoChatParser;

/**
 * Created by akshaymehta on 30/08/15.
 */
public class GeoChatManagers implements Constants.LOCATIONKEYS, Constants.JsonKeys {
    Context mContext;
    SharedPreferences mSharedPreferences;

    public GeoChatManagers(Context context) {
        mContext = context;
        mSharedPreferences = new SharedPreferences(mContext);
    }

    public void createGeoChat(final String location, final String latitude, final String longitude) {
        Utils.showProgress(mContext);
        // Tag used to cancel the request
        String tag_create_geochat = "create_geochat";

        String url = Constants.API_CREATE_GEOCHAT;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                try {
                    JSONObject json = new JSONObject(response);
                    Utils.showToast(mContext, json.getString(Constants.JsonKeys.MESSAGE));
                    Utils.startHomeActivity(mContext);
                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeProgress();
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, mSharedPreferences.getUserId());
                params.put(CHECKIN, location);
                params.put(LATITUDE, latitude);
                params.put(LONGITUDE, longitude);
                return params;
            }
        };

        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_create_geochat);

    }

    public void fetchAllGeoChats(final FragmentActivity activity) {
        // Tag used to cancel the request
        String tag_fetch_geochat = "fetch_geochat";
        final GeoChatListFragment fragment= (GeoChatListFragment)activity.getSupportFragmentManager().findFragmentByTag(Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG);

        String url = Constants.API_FETCH_GEOCHAT;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Log.d("json",json.toString());
                    Utils.showToast(mContext, json.getString(Constants.JsonKeys.MESSAGE));
                    ArrayList<GeoChat> result = new GeoChatParser().getGeoChatsInList(json);
                    fragment.renderGeoChatListView(result);
                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
                fragment.failResult();
            }
        });

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);
    }

    public void verifyUserToJoin(final double latitude, final double longitude, final String userid, final String geoChatId, final GeoChat item) {
        String verify_user = "verify_user_geochat";
        Utils.showProgress(mContext);

        String url = Constants.API_VERIFY_USER_GEOCHAT;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    Utils.showToast(mContext,status);
                    if(status.equalsIgnoreCase(SUCCESS))
                    {
                        ((HomeActivity)mContext).openChat(item);
                    }

                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
                Utils.closeProgress();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, userid);
                params.put(GEOCHATID, geoChatId);
                params.put(LATITUDE, latitude + "");
                params.put(LONGITUDE, longitude + "");
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, verify_user);
    }

    public void fetchAllChats(final Activity activity, final GeoChat mChatItem) {
        // Tag used to cancel the request
        String tag_fetch_geochat = "fetch_chat";

        String url = Constants.API_FETCH_ALLCHATS;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    if (status.equalsIgnoreCase(SUCCESS)) {
                        ArrayList<UserChats> result = new GeoChatParser().getAllChatsInList(json);
                        ((ChatActivity) activity).renderChatListView(result);
                    } else
                        ((ChatActivity) activity).failResult();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
                ((ChatActivity) activity).failResult();

            }
        }

        )

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(GEOCHATID, mChatItem.getGeoChatId());
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);
    }

    public void sendUserMessage(final Activity activity,final GeoChat mChatItem, final String userMessage, final String userId) {
        // Tag used to cancel the request
        String tag_send_msg = "send_msg";
        Utils.showProgress(activity);
        String url = Constants.API_SEND_CHAT;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Utils.closeProgress();
                    Log.e("response",response);
                    JSONObject json = new JSONObject(response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    if (status.equalsIgnoreCase(SUCCESS)) {
                        ((ChatActivity) activity).updateResult();

                    } else
                        Utils.showToast(activity,message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeProgress();
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
            }
        }

        )

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(GEOCHATID, mChatItem.getGeoChatId());
                params.put(USER_ID, userId);
                params.put(GEO_CHAT_MESSAGE, userMessage);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_send_msg);
    }
}
