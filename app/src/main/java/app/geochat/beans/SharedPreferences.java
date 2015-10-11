package app.geochat.beans;

import android.content.Context;

import app.geochat.util.Constants;

/**
 * Created by tasneem on 28/7/15.
 */
public class SharedPreferences implements Constants.UserInfoKeys,Constants.Preferences,Constants.LoginKeys{
    private static android.content.SharedPreferences mSharedPreferences;


    public SharedPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFRENCES,
                Context.MODE_PRIVATE);
    }


    //Google shared prefs.

    public void setGooglePlusId(String id) {
        mSharedPreferences.edit().putString(GOOGLE_PLUS_ID, id).commit();
    }

    public String getGooglePlusId() {
        return mSharedPreferences.getString(GOOGLE_PLUS_ID, "");
    }

    public void setGooglePlusName(String userName) {
        mSharedPreferences.edit().putString(GOOGLE_PLUS_NAME, userName).commit();
    }

    public String getGooglePlusName() {
        return mSharedPreferences.getString(GOOGLE_PLUS_NAME, "");
    }


    public void setGooglePlusEmailId(String emailId) {
        mSharedPreferences.edit().putString(GOOGLE_EMAIL_ID, emailId).commit();
    }

    public String getGooglePlusEmailId() {
        return mSharedPreferences.getString(GOOGLE_EMAIL_ID, "");
    }


    public void setGooglePlusProfilePicUrl(String profilePicUrl) {
        mSharedPreferences.edit().putString(GOOGLE_PROFILE_PIC_URL, profilePicUrl).commit();
    }

    public String getGooglePlusProfilePicUrl() {
        return mSharedPreferences.getString(GOOGLE_PROFILE_PIC_URL, "");
    }

    public void setGooglePlusCoverPicUrl(String coverPicUrl) {
        mSharedPreferences.edit().putString(GOOGLE_PLUS_COVER_PIC_URL, coverPicUrl).commit();
    }

    public String getGooglePlusCoverPicUrl() {
        return mSharedPreferences.getString(GOOGLE_PLUS_COVER_PIC_URL, "");
    }


    // Login preferences
    public void setLoggedIn(boolean loggedIn) {
        mSharedPreferences.edit().putBoolean(LOGGED_IN, loggedIn).commit();
    }

    public boolean getLoggedIn() {
        return mSharedPreferences.getBoolean(LOGGED_IN, false);
    }

    public void setFBAccessToken(String accessToken) {
        mSharedPreferences.edit().putString(FB_ACCESS_TOKEN, accessToken)
                .commit();
    }


    public void setUserId(String userId) {
        mSharedPreferences.edit().putString(USER_ID, userId).commit();
    }
    public String getUserId() {
        return mSharedPreferences.getString(USER_ID, "");
    }

    public void setUserName(String userName) {
        mSharedPreferences.edit().putString(USER_NAME, userName).commit();
    }
    public String getUserName() {
        return mSharedPreferences.getString(USER_NAME, "");
    }


    public void setCurrentDimes(String current_dimes) {
        mSharedPreferences.edit().putString(CURRENT_DIMES, current_dimes)
                .commit();
    }

    public void setRank(String rank) {
        mSharedPreferences.edit().putString(RANK, rank).commit();
    }

    public void setProfileImage(String image_path) {
        mSharedPreferences.edit().putString(IMAGE, image_path).commit();
    }
    public String getProfileImage() {
        return mSharedPreferences.getString(IMAGE, "");
    }
    public void setReferralCode(String referral_code) {
        mSharedPreferences.edit().putString(REFERRAL_CODE, referral_code).commit();
    }
    public void setDob(String date_of_birth) {
        mSharedPreferences.edit().putString(DOB, date_of_birth).commit();
    }

    public void setGender(String gender) {
        mSharedPreferences.edit().putString(GENDER, gender).commit();
    }

    public void setOccupation(String occupation) {
        mSharedPreferences.edit().putString(OCCUPATION, occupation).commit();
    }

    public void setInterests(String interests) {
        mSharedPreferences.edit().putString(INTERESTS, interests).commit();
    }

    public void setWebsite(String website) {
        mSharedPreferences.edit().putString(WEBSITE, website).commit();
    }

    public void setBio(String bio) {
        mSharedPreferences.edit().putString(BIO, bio).commit();
    }

    public void setUnreadMessageCount(int unread_messages_counter) {
        mSharedPreferences.edit()
                .putInt(UNREAD_MESSAGES_COUNT, unread_messages_counter)
                .commit();
    }

    public void setReferralLink(String referral_link) {
        mSharedPreferences.edit().putString(REFERRAL_LINK, referral_link)
                .commit();
    }

    public void setReferralCount(int referral_count) {
        mSharedPreferences.edit().putInt(REFERRAL_COUNT, referral_count)
                .commit();
    }

    public void setLoginMode(String loginMode) {
        mSharedPreferences.edit().putString(LOGIN_MODE, loginMode).commit();
    }

    public String getLoginMode() {
        return mSharedPreferences.getString(LOGIN_MODE, "");
    }

    /*
        facebook preferences
    */
    public void setFacebookId(String id) {
        mSharedPreferences.edit().putString(FACEBOOK_ID, id).commit();
    }

    public String getFacebookId() {
        return mSharedPreferences.getString(FACEBOOK_ID, "");
    }

    public void setFacebookProfilePicture(String profilePicUrl) {
        mSharedPreferences.edit().putString(FACEBOOK_PROFILE_PIC_URL, profilePicUrl).commit();
    }

    public String getFacebookProfilePicture() {
        return mSharedPreferences.getString(FACEBOOK_PROFILE_PIC_URL, "");
    }

    public void setFacebookEmailId(String emailId) {
        mSharedPreferences.edit().putString(FACEBOOK_EMAIL_ID, emailId).commit();
    }

    public String getFacebookEmailId() {
        return mSharedPreferences.getString(FACEBOOK_EMAIL_ID, "");
    }

    public void setFacebookName(String emailId) {
        mSharedPreferences.edit().putString(FACEBOOK_NAME, emailId).commit();
    }

    public String getFacebookName() {
        return mSharedPreferences.getString(FACEBOOK_NAME, "");
    }

    public String getFacebookCoverPic() {
        return mSharedPreferences.getString(FACEBOOK_COVERPIC, "");

    }


    public void setLeaderBoardEmail (boolean value){
        mSharedPreferences.edit().putBoolean(LEADER_BOARD_EMAIL, value).commit();
    }

    public void setLeaderBoardSite ( boolean value){
        mSharedPreferences.edit().putBoolean(LEADER_BOARD_SITE, value).commit();
    }

    public void setProfileEmail(boolean value){
        mSharedPreferences.edit().putBoolean(PROFILE_EMAIL,value).commit();
    }

    public void setProfileSite(boolean value){
        mSharedPreferences.edit().putBoolean(PROFILE_SITE,value).commit();
    }

    public void setThreadActivityEmail(boolean value){
        mSharedPreferences.edit().putBoolean(THREAD_ACTIVITY_EMAIL,value).commit();
    }

    public void setThreadActivitySite(boolean value){
        mSharedPreferences.edit().putBoolean(THREAD_ACTIVITY_SITE, value).commit();
    }

    public void setMilestoneEmail (boolean value){
        mSharedPreferences.edit().putBoolean(MILESTONE_EMAIL, value).commit();
    }

    public void setMilestoneSite(boolean value){
        mSharedPreferences.edit().putBoolean(MILESTONE_SITE, value).commit();
    }

    public void setWebNotificationCount(int value){
        mSharedPreferences.edit().putInt(NOTIFICATION_UNREAD_COUNT, value).commit();
    }

    public String getDeviceToken() {
        return mSharedPreferences.getString(DEVICE_TOKEN, "");
    }

    public void setDeviceToken(String regId) {
        mSharedPreferences.edit().putString(DEVICE_TOKEN, regId).commit();
    }



}
