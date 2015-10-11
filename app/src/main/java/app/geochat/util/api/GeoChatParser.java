package app.geochat.util.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.geochat.beans.GeoChat;
import app.geochat.beans.UserChats;
import app.geochat.util.Constants;

/**
 * Created by akshaymehta on 02/09/15.
 */
public class GeoChatParser implements Constants.JsonKeys{



    public ArrayList<GeoChat> getGeoChatsInList(JSONObject json) {
        ArrayList<GeoChat> result =new ArrayList<GeoChat>();
        try {
            if(json.getString(STATUS).equalsIgnoreCase(SUCCESS))
            {
                JSONArray resultArray = json.getJSONArray(RESULTARRAY);
                for(int i=0;i<resultArray.length();i++)
                {
                    JSONObject geoChatObject = resultArray.getJSONObject(i);
                    GeoChat item = new GeoChat();
                    item.setUserId(geoChatObject.getString(USER_ID));
                    item.setCreatedByUserName(geoChatObject.getString(CREATEDBYUSERNAME));
                    item.setUserAvatar(geoChatObject.getString(USERAVATAR));
                    item.setCheckInLocation(geoChatObject.getString(CHECKINLOCATION));
                    item.setGeoChatId(geoChatObject.getString(GEOCHATID));
                    item.setCreatedDateTime(geoChatObject.getString(CREATEDDATETIME));
                    item.setGeoChatImage(geoChatObject.getString(GEOCHATIMAGE));

                    result.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<UserChats> getAllChatsInList(JSONObject json) {
        ArrayList<UserChats> result =new ArrayList<UserChats>();
        try {
            if(json.getString(STATUS).equalsIgnoreCase(SUCCESS))
            {
                JSONArray resultArray = json.getJSONArray(RESULTARRAY);
                for(int i=0;i<resultArray.length();i++)
                {
                    JSONObject geoChatObject = resultArray.getJSONObject(i);
                    UserChats item = new UserChats();
                    item.setUserId(geoChatObject.getString(USER_ID));
                    item.setUserName(geoChatObject.getString(CREATEDBYUSERNAME));
                    item.setUserAvatar(geoChatObject.getString(USERAVATAR));
                    item.setUserMessage(geoChatObject.getString(USERMESSAGE));
                    item.setGeoChatId(geoChatObject.getString(GEOCHATID));
                    item.setDate(geoChatObject.getString(DATE));
                    result.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
