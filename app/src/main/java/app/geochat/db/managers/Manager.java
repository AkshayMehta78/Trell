package app.geochat.db.managers;

/**
 * Created by akshaymehta on 30/07/15.
 */

import app.geochat.util.Constants;
import app.geochat.util.api.ResponseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Manager implements Constants.JsonKeys {


    public String getErrorCode(String response) throws JSONException, ResponseException {
//        JSONObject responseJson = new JSONObject(response);
//
//        String errorCode = responseJson.getString(ERRORCODE);
//        return errorCode;
        return "";
    }

    public JSONArray getResultJsonArray(String response) throws JSONException {

        JSONObject responseJson = null;
        JSONArray messagesJsonArray = null;

        responseJson = new JSONObject(response);

     //   messagesJsonArray = responseJson.getJSONArray(RESULT);
        return messagesJsonArray;
    }

//   public int getResponseStatusCode() {
//        return mApiClient.getResponseStatusCode();
//    }

    public String   getErrorMessage(JSONObject response) throws JSONException {

        String errorMessage = response.getString(ERRORS);

        return errorMessage;
    }

    public String   getErrorMessage(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        String errorMessage = jsonResponse.getString(ERRORS);
        return errorMessage;
    }


    //  {"error":"invalid_resource_owner",
    // "error_description":"The provided resource owner credentials are not valid, or resource owner cannot be found"}

    public String getErrorDescription(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);
        String errorMessage = jsonObject.getString("error_description");

        return errorMessage;
    }

}