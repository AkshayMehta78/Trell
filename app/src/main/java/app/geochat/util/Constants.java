package app.geochat.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tasneem on 28/7/15.
 */
public interface Constants {
    /*
        API Constants
    */
    String BASE_URL = "http://expresso.netne.net/expresso/";
    //Auth token
    String API_AUTH_TOKEN = BASE_URL + "signup.php";
    String LOCATIONURL1="https://maps.googleapis.com/maps/api/place/search/json?";
    String LOCATIONURL2 ="&radius=1000&types=restaurant&sensor=false&key=AIzaSyAB-dwfPHYylEYEUn0Bg74lB1ogi-jCKBs" ;
    String API_CREATE_GEOCHAT = BASE_URL + "createGeoChat.php";
    String API_FETCH_GEOCHAT = BASE_URL + "displayCreatedGeoChat.php";
    String ANDROID = "android" ;
    String API_VERIFY_USER_GEOCHAT = BASE_URL+"checkIfUserIsInLocation.php";
    String API_FETCH_ALLCHATS = BASE_URL + "getAllChats.php";
    String API_SEND_CHAT =  BASE_URL + "sendGeoMessage.php";
    int MY_SOCKET_TIMEOUT_MS = 6000;
    int MAX_RETRIES = 2;

    interface LOCATIONKEYS {
        String LOCATION = "location";
        String CHECKIN="checkIn";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String GEOCHATIMAGE = "geoChatImage";
    }


    interface JsonKeys {
        String ERRORS = "errors";
        String MESSAGE = "message";
        String STATUS = "status";
        String SUCCESS = "Success";

        // Geo Chat Keys
        String RESULTARRAY = "resultArray";
        String USERID ="userid";
        String USER_ID ="userId";
        String GEOCHATID ="geoChatId";
        String USERAVATAR="userAvatar";
        String CHECKINLOCATION="checkInLocation";
        String CREATEDBYUSERNAME="createdByUserName";
        String DATE="date";
        String USERMESSAGE="userMessage";
        String GEO_CHAT_MESSAGE = "geoChatMessage";
        String CREATEDDATETIME = "createdDateTime";
        String RESULT = "result";
        String GEOCHATIMAGE = "geoChatImage";
    }

    public interface Config {

    }

    public interface Preferences{
         String PREFRENCES = "desi dime prefrences";

         String LOGGED_IN = "logged_in";
         String FB_ACCESS_TOKEN = "fb_access_token";
         String FACEBOOK_COVERPIC = "facebook_coverpic";
         String DD_ACCESS_TOKEN = "dd_access_token";
         String LOGIN_MODE = "login_mode";

         String GOOGLE_PLUS_ID = "google_plus_id";
         String GOOGLE_PLUS_NAME = "google_plus_name";
         String GOOGLE_EMAIL_ID = "google_email_id";
         String GOOGLE_PROFILE_PIC_URL = "google_profile_pic_url";
         String GOOGLE_PLUS_COVER_PIC_URL = "google_plus_cover_pic_url";
         String LEADER_BOARD_SITE = "leader_board_site";
         String LEADER_BOARD_EMAIL = "leader_board_email";
         String MILESTONE_SITE = "milestone_site";
         String MILESTONE_EMAIL = "milestone_email";
         String THREAD_ACTIVITY_SITE = "thread_activity_site";
         String THREAD_ACTIVITY_EMAIL = "thread_activity_email";
         String PROFILE_SITE = "profile_site";
         String PROFILE_EMAIL = "profile_email";
         String NOTIFICATION_UNREAD_COUNT = "notification_unread_count";
         String IS_SKIP_CLICKED = "is_skip_clicked";
        String GEOCHAT = "geo_chat";
    }

    public interface UserInfoKeys {
         String USER_ID = "user_id";
         String USER_NAME = "user_name";
         String ID = "id";
         String CURRENT_DIMES = "current_dimes";
         String RANK = "rank";
         String IMAGE = "image";
         String DOB = "date_of_birth";
         String GENDER = "gender";
         String OCCUPATION = "occupation";
         String INTERESTS = "interests";
         String WEBSITE = "website";
         String BIO = "bio";
         String UNREAD_MESSAGES_COUNT = "unread_messages_counter";
         String REFERRAL_LINK = "referral_link";
         String REFERRAL_CODE = "referral_code";
         String REFERRAL_COUNT = "referrals_count";

         String FACEBOOK_ID = "id";
         String FACEBOOK_EMAIL_ID = "email";
         String FACEBOOK_NAME = "name";
         String FACEBOOK_PROFILE_PIC_URL = "profilePicUrl";

         String IMAGE_LARGE = "image_large";

    }

    interface LoginKeys
    {
        String MODE_FACEBOOK = "facebook";
        String MODE_GOOGLE = "googleplus";

        String USEREMAIL = "userEmail";
        String SOCIALUID = "socialMediaId";
        String USERNAME = "userName";
        String DEVICE_NUMBER = "deviceId";
        String DEVICE_TOKEN = "deviceToken";
        String PLATFORM = "platform";
        String MODE ="mode";
        String PASSWORD = "password";
        String DATE_OF_BIRTH = "dob";
        String AVATAR = "avatar";
        String UNREAD_NOTIFICATION_COUNT = "unread_notifications_count";
        String LEADER_BOARD_PARMA_LINK = "leaderboard-notification-settings";
        String THREAD_ACTIVITY_PARMA_LINK = "thread-activity-settings";
        String PROFILE_SETTING_PARMA_LINK = "profile-settings";
        String MILESTONE_PARMA_LINK = "milestone-settings";
    }

    public interface FragmentTags {
        String FRAGMENT_GEOCHATLIST_TAG = "geo_chat_list";
    }
}