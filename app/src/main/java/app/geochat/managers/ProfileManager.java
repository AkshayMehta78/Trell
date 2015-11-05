package app.geochat.managers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import app.geochat.R;
import app.geochat.beans.SharedPreferences;
import app.geochat.db.managers.LoginManager;
import app.geochat.ui.activities.ChatActivity;
import app.geochat.util.Constants;
import app.geochat.util.Utils;
import app.geochat.util.VolleyController;

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
}