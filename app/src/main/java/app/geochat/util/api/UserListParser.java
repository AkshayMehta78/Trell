package app.geochat.util.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.geochat.beans.GeoChat;
import app.geochat.beans.User;
import app.geochat.util.Constants;

/**
 * Created by akshaymehta on 10/11/15.
 */
public class UserListParser implements Constants.JsonKeys,Constants.USER,Constants.UserInfoKeys{

    public ArrayList<User> getUsersList(JSONObject jsonObject) {
        ArrayList<User> result =new ArrayList<User>();
        try {
            if(jsonObject.getString(Constants.USER.STATUS).equalsIgnoreCase(SUCCESS))
            {
                JSONArray resultArray = jsonObject.getJSONArray(USERS);
                for(int i=0;i<resultArray.length();i++)
                {
                    JSONObject geoChatObject = resultArray.getJSONObject(i);
                    User item = new User();
                    item.setUserId(geoChatObject.getString(Constants.USER.USERID));
                    item.setUserName(geoChatObject.getString(Constants.USER.USERNAME));
                    item.setUserAvatar(geoChatObject.getString(Constants.USER.USERAVATAR));
                    item.setUserStatus(geoChatObject.getString(Constants.USER.STATUS));
                    result.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
