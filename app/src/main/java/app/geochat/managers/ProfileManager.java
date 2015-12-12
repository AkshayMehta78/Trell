package app.geochat.managers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.beans.Trail;
import app.geochat.beans.User;
import app.geochat.db.managers.LoginManager;
import app.geochat.ui.activities.ChatActivity;
import app.geochat.ui.activities.UserProfileActivity;
import app.geochat.ui.activities.UserTrailListFragment;
import app.geochat.ui.activities.UsersListActivity;
import app.geochat.ui.fragments.AchievementFragment;
import app.geochat.ui.fragments.GeoChatListFragment;
import app.geochat.util.Constants;
import app.geochat.util.Utils;
import app.geochat.util.VolleyController;
import app.geochat.util.api.GeoChatParser;
import app.geochat.util.api.UserListParser;

/**
 * Created by akshaymehta on 01/11/15.
 */
public class ProfileManager implements Constants.JsonKeys,Constants.USER {
    Context mContext;
    SharedPreferences mSharedPreferences;

    public ProfileManager(Context context) {
        mContext = context;
        mSharedPreferences = new SharedPreferences(mContext);
    }

    public void followUser(final String userId, final int status) {
        // Tag used to cancel the request
        String tag_send_msg = "set_user_status";
        Utils.showProgress(mContext);
        String url = Constants.API_FOLLOW_USER_SATUS;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Utils.closeProgress();
                    Log.e("response", response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    Utils.showToast(mContext, message);
//                    if(mContext instanceof UserProfileActivity){
//                        ((UserProfileActivity)mContext).update()
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
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
                params.put(FRIENDID, userId);
                params.put(USER_ID, mSharedPreferences.getUserId());
                params.put(USER_STATUS, status+"");
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_send_msg);
    }

    public void fetchUserDetails(final String userId) {
        // Tag used to cancel the request
        String tag_send_msg = "fetch_user_details";
    //    Utils.showProgress(mContext);
        String url = Constants.API_FETCH_USER_DETAILS_API;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
    //                Utils.closeProgress();
                    Log.e("response", response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    if(status.equalsIgnoreCase(Constants.JsonKeys.SUCCESS)){
                        ((UserProfileActivity)mContext).sendFetchedResult(response);
                    } else {
                        Utils.showToast(mContext,message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
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
                params.put(USER_ID, userId);
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_send_msg);
    }

    public void fetchUserExploreDetails(final FragmentActivity activity, final String userId) {
        // Tag used to cancel the request
        String tag_send_msg = "fetch_user_details";
        String url = Constants.API_FETCH_USER_EXPLORE_DETAILS_API;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response", response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    if(status.equalsIgnoreCase(Constants.JsonKeys.SUCCESS)){
                        ((UserProfileActivity)activity).sendFetchedExploreResult(response);
                    } else {
                        Utils.showToast(mContext,message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, userId);
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_send_msg);
    }

    public void fetchUserFeedDetails(final String userId) {
        String tag_fetch_geochat = "fetch_geochat";
        String url = Constants.API_FETCH_USER_FEED_DETAILS_API;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    if(status.equalsIgnoreCase(Constants.JsonKeys.SUCCESS)){
                        double[] locationData = Utils.getLocationDetails(mContext);
                        ArrayList<GeoChat> result = new GeoChatParser().getGeoChatsInList(jsonObject,locationData[0]+"",locationData[1]+"");
                        ((UserProfileActivity)mContext).sendFetchedFeedResult(result);
                    } else {
                        Utils.showToast(mContext,message);
                    }

                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, userId);
                return params;
            }
        };
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);
    }

    public void fetchUsersList(final String userId, final String friendId, final String flag) {
        String tag_fetch_geochat = "fetch_userslist";
        String url = Constants.API_FETCH_USERS_LIST;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response", response);
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<User> result = new UserListParser().getUsersList(jsonObject);
                    ((UsersListActivity)mContext).sendUserListRsponse(result);
                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, userId);
                params.put(FRIENDID, friendId);
                params.put(FLAG, flag);

                return params;
            }
        };
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);
    }

    public void getUserProfileTrailList(final String mUserId, final Fragment fragment) {
        String tag_fetch_geochat = "fetch_userslist";
        String url = Constants.API_FETCH_USER_TRAILS;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response", response);
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<Trail> result = new UserListParser().getTrailList(jsonObject);
                    ((UserTrailListFragment)fragment).sendTrailList(result);
                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, mUserId);

                return params;
            }
        };
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);
    }

    public void fetchWishListUsersList(final String userId, final String geoChatId) {
        String tag_fetch_geochat = "fetch_userslist";
        String url = Constants.API_FETCH_WISHLIST_USERS_LIST;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response", response);
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<User> result = new UserListParser().getUsersList(jsonObject);
                    ((UsersListActivity)mContext).sendUserListRsponse(result);
                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, userId);
                params.put(GEONOTEID, geoChatId);

                return params;
            }
        };
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);

    }
}
