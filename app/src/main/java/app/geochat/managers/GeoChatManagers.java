package app.geochat.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import app.geochat.beans.UserChats;
import app.geochat.db.managers.LoginManager;
import app.geochat.ui.activities.ChatActivity;
import app.geochat.ui.activities.SearchActivity;
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

    public void createGeoChat(final String location, final String latitude, final String longitude, final String description, final String tags, final String captionText) {
        Utils.showProgress(mContext);
        // Tag used to cancel the request
        String tag_create_geochat = "create_geochat";

        String url = Constants.API_CREATE_GEOCHAT;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("response",response);
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
                params.put(Constants.LOCATIONKEYS.LATITUDE, latitude);
                params.put(Constants.LOCATIONKEYS.LONGITUDE, longitude);
                params.put(Constants.GEOCHAT.DESCRIPTION, description);
                params.put(Constants.GEOCHAT.TAGS, tags);
                params.put(Constants.GEOCHAT.CAPTION, captionText);
                params.put(Constants.GEOCHAT.CITY, "Mumbai");
                return params;
            }
        };

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_create_geochat);

    }

    public void fetchAllChats(final Activity activity, final GeoChat mChatItem) {
        // Tag used to cancel the request
        String tag_fetch_geochat = "fetch_chat";

        String url = Constants.API_FETCH_ALLCHATS;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                try {
                    JSONObject json = new JSONObject(response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    if (status.equalsIgnoreCase(SUCCESS)) {
                        ArrayList<UserChats> result = new GeoChatParser().getAllChatsInList(json);
                        ((ChatActivity) activity).renderChatListView(result);
                    } else
                        ((ChatActivity) activity).failResult(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(GEOCHATID, mChatItem.getGeoChatId());
                return params;
            }
        };

        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);
    }

    public void sendUserMessage(final Activity activity, final GeoChat mChatItem, final String userMessage, final String userId) {
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
                    Log.e("response", response);
                    JSONObject json = new JSONObject(response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    if (status.equalsIgnoreCase(SUCCESS)) {
                        ((ChatActivity) activity).updateResult();

                    } else
                        Utils.showToast(activity, message);

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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_send_msg);
    }


    public boolean checkIfUserIsInLocation(double[] userLocation, GeoChat item) {
        if (userLocation != null) {
            Location source = new Location("");
            source.setLatitude(Double.parseDouble(item.getLatitude()));
            source.setLongitude(Double.parseDouble(item.getLongitude()));

            Location destination = new Location("");
            destination.setLatitude(userLocation[0]);
            destination.setLongitude(userLocation[1]);

            float distance = source.distanceTo(destination);

            if (distance <= Constants.RADIUS) {
                return true;
            } else
                return false;
        }
        return false;
    }

    public void addUserToGeoChat(final GeoChat item, final Activity activity, final String status) {
        // Tag used to cancel the request
        String tag_send_msg = "set_user_status";
        Utils.showProgress(activity);
        String url = Constants.API_SET_GEOCHAT_USER_STATUS;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Utils.closeProgress();
                    Log.e("response", response);
                    String status = LoginManager.getAPIStatus(response);
                    String message = LoginManager.getAPIMessage(response);
                    if (status.equalsIgnoreCase(SUCCESS)) {
                        Intent chatIntent = new Intent(activity,ChatActivity.class);
                        chatIntent.putExtra(Constants.Preferences.GEOCHAT,item);
                        activity.startActivity(chatIntent);
                    } else
                        Utils.showToast(activity, message);

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
                params.put(GEOCHATID, item.getGeoChatId());
                params.put(USER_ID, mSharedPreferences.getUserId());
                params.put(USER_STATUS, status);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_send_msg);
    }

    public void fetchAllGeoChats(final FragmentActivity activity, final String latitude, final String longitude, final String status) {
        // Tag used to cancel the request
        String tag_fetch_geochat = "fetch_geochat";
        final GeoChatListFragment fragment = (GeoChatListFragment) activity.getSupportFragmentManager().findFragmentByTag(Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG);

        String url = Constants.API_FETCH_GEOCHAT;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Log.d("json", json.toString());
                    ArrayList<GeoChat> result = new GeoChatParser().getGeoChatsInList(json,latitude,longitude);
                    fragment.renderGeoChatListView(result, status);

                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null) {
                    Log.e("error", error.networkResponse.statusCode + "");
                    Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
                    fragment.failResult();
                }else {
                    fragment.failResult();
                    Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, mSharedPreferences.getUserId());
                return params;
            }
        };
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);
    }


    public void addToWishList(FragmentActivity activity, final String geoChatId, final String Wishlist, final int position) {
        // Tag used to cancel the request
        String tag_fetch_geochat = "update_wishlist";
        final GeoChatListFragment fragment = (GeoChatListFragment) activity.getSupportFragmentManager().findFragmentByTag(Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG);
        Utils.showProgress(activity);
        String url = Constants.API_WISHLIST;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                try {
                    fragment.updateWishList(position);
                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeProgress();
                Log.e("error", error.networkResponse.statusCode + "");
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, mSharedPreferences.getUserId());
                params.put(GEONOTEID, geoChatId);
                params.put(STATUS, Wishlist);

                return params;
            }
        };
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);
    }

    public void removeGeoNoteAPI(FragmentActivity activity, final String geoChatId, final int position) {
        // Tag used to cancel the request
        String tag_fetch_geochat = "update_geochat";
        String url = Constants.API_REMOVE_GEOCHAT;
        final GeoChatListFragment fragment = (GeoChatListFragment) activity.getSupportFragmentManager().findFragmentByTag(Constants.FragmentTags.FRAGMENT_GEOCHATLIST_TAG);
        Utils.showProgress(activity);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.closeProgress();
                try {
                    fragment.updateGeoNoteList(position);
                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeProgress();
                Log.e("error", error.networkResponse.statusCode + "");
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, mSharedPreferences.getUserId());
                params.put(GEONOTEID, geoChatId);

                return params;
            }
        };
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);
    }

    public void fetchSearchResult(final Context context, final String latitude, final String longitude, final String searchType, final String searchKey) {
        // Tag used to cancel the request
        String tag_fetch_geochat = "fetch_geochat";

        String url = Constants.API_SEARCH_GEOCHAT;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Log.d("json", json.toString());
                    ArrayList<GeoChat> result = new GeoChatParser().getGeoChatsInList(json,latitude,longitude);
                    ((SearchActivity)context).renderGeoChatListView(result);
                } catch (Exception e) {
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.networkResponse.statusCode+"");
                Utils.showToast(mContext, mContext.getResources().getString(R.string.something_went_wrong));
                ((SearchActivity)context).failResult();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, mSharedPreferences.getUserId());
                params.put(SEARCHTYPE,searchType);
                params.put(SEARCHKEY, searchKey);
                return params;
            }
        };
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, Constants.MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance().addToRequestQueue(jsonObjReq, tag_fetch_geochat);

    }
}
