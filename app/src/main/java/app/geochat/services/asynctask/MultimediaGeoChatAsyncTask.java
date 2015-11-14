package app.geochat.services.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.geochat.beans.SharedPreferences;
import app.geochat.ui.activities.SelectPhotoActivity;
import app.geochat.util.Constants;
import app.geochat.util.JSONParser;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 11/10/15.
 */
public class MultimediaGeoChatAsyncTask extends AsyncTask<String, Void, JSONObject> implements Constants.LOCATIONKEYS, Constants.JsonKeys {

    private Context mContext;
    SharedPreferences mSharedPreferences;

    public MultimediaGeoChatAsyncTask(Context context)
    {
        mContext = context;
        mSharedPreferences = new SharedPreferences(mContext);
    }
    @Override
    protected JSONObject doInBackground(String... param) {
        JSONParser jsonParser=new JSONParser();
        // Building Parameters
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair(USER_ID,mSharedPreferences.getUserId()));
        parameters.add(new BasicNameValuePair(CHECKIN,param[0]));
        parameters.add(new BasicNameValuePair(Constants.LOCATIONKEYS.LATITUDE,param[1]));
        parameters.add(new BasicNameValuePair(Constants.LOCATIONKEYS.LONGITUDE,param[2]));

        parameters.add(new BasicNameValuePair(Constants.GEOCHAT.DESCRIPTION,param[4]));
        parameters.add(new BasicNameValuePair(Constants.GEOCHAT.TAGS,param[5]));
        parameters.add(new BasicNameValuePair(Constants.GEOCHAT.CAPTION,param[6]));
        parameters.add(new BasicNameValuePair(Constants.GEOCHAT.CITY,param[7]));


        JSONObject json = jsonParser.getJSONWithImageUpload(Constants.API_CREATE_GEOCHAT,parameters, param[3]);
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject  result) {
        try {
            Utils.closeProgress();
            Log.e("result",result.toString());
            if(result.getString(Constants.JsonKeys.STATUS).equalsIgnoreCase(Constants.JsonKeys.SUCCESS)) {
                Utils.showToast(mContext, result.getString(Constants.JsonKeys.MESSAGE));
                Utils.startHomeActivity(mContext);
            }else
                Utils.showToast(mContext, result.getString(Constants.JsonKeys.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.startHomeActivity(mContext);
        }
    }

    @Override
    protected void onPreExecute() {
        Utils.showProgress(mContext);
    }

    @Override
    protected void onProgressUpdate(Void... values) {}

}