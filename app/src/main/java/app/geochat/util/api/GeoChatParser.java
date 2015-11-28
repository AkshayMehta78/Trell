package app.geochat.util.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.geochat.beans.GeoChat;
import app.geochat.beans.Trail;
import app.geochat.beans.UserChats;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 02/09/15.
 */
public class GeoChatParser implements Constants.JsonKeys,Constants.GEOCHAT{



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
                    item.setLatitude(geoChatObject.getString(LATITUDE));
                    item.setLongitude(geoChatObject.getString(LONGITUDE));

                    item.setDescripton(geoChatObject.getString(DESCRIPTION));
                    item.setTags(geoChatObject.getString(TAGS));
                    item.setCity(geoChatObject.getString(CITY));
                    item.setCaption(geoChatObject.getString(CAPTION));
                    item.setDistance(" ");

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

    public ArrayList<GeoChat> getGeoChatsInList(JSONObject json, String latitude, String longitude) {
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
                    item.setLatitude(geoChatObject.getString(LATITUDE));
                    item.setLongitude(geoChatObject.getString(LONGITUDE));

                    item.setDescripton(geoChatObject.getString(DESCRIPTION));
                    item.setTags(geoChatObject.getString(TAGS));
                    item.setCity(geoChatObject.getString(CITY));
                    item.setCaption(geoChatObject.getString(CAPTION));

                    JSONObject userStatusObject = geoChatObject.getJSONObject(USERSTATUS);
                    JSONObject countObject = geoChatObject.getJSONObject(COUNTS);

                    item.setIsFollowing(userStatusObject.getString(ISFOLLOWING));
                    item.setIsWishListAdded(userStatusObject.getString(ISWISHLISTADDED));

                    item.setWishListCount(countObject.getString(WISHLISTCOUNT));
                    item.setCommentCount(countObject.getString(COMMENTSCOUNT));

                    int distance = Utils.getLocationDistance(geoChatObject.getString(LATITUDE),geoChatObject.getString(LONGITUDE),latitude,longitude);
                    item.setDistance(distance+"");

                    item.setUserTrails(geoChatObject.getJSONObject(TRAILS).toString());

                    result.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Trail> parseUserTrails(JSONObject json) {
        ArrayList<Trail> result = new ArrayList<Trail>();
        try {
            if (json != null) {
                JSONArray trailArray = json.getJSONArray(TRAILLIST);
                for (int i = 0; i < trailArray.length(); i++) {
                    JSONObject jsonObject = trailArray.getJSONObject(i);
                    Trail item = new Trail();
                    item.setTrailId(jsonObject.getString(TRAILID));
                    item.setName(jsonObject.getString(NAME));
                    item.setDateTime(jsonObject.getString(DATETIME));
                    item.setIsAdded(jsonObject.getBoolean(ISADDED));
                    result.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }
}
