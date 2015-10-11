package app.geochat.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by akshaymehta on 02/09/15.
 */
public class GeoChat implements Parcelable {
    String userId;
    String userAvatar;
    String checkInLocation;
    String geoChatImage;
    String createdByUserName;
    String createdDateTime;
    String geoChatId;
    String comments;
    String likes;

    public GeoChat(String userId, String userAvatar, String checkInLocation, String createdByUserName, String createdDateTime, String geoChatId, String comments, String likes,String geoChatImage) {
        this.userId = userId;
        this.userAvatar = userAvatar;
        this.checkInLocation = checkInLocation;
        this.createdByUserName = createdByUserName;
        this.createdDateTime = createdDateTime;
        this.geoChatId = geoChatId;
        this.comments = comments;
        this.likes = likes;
        this.geoChatImage = geoChatImage;
    }

    public GeoChat() {
    }

    public String getGeoChatId() {
        return geoChatId;
    }

    public void setGeoChatId(String geoChatId) {
        this.geoChatId = geoChatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getCreatedByUserName() {
        return createdByUserName;
    }

    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    public String getCheckInLocation() {
        return checkInLocation;
    }

    public void setCheckInLocation(String checkInLocation) {
        this.checkInLocation = checkInLocation;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }


    // Parcelling part
    public GeoChat(Parcel in) {
        String[] data = new String[8];

        in.readStringArray(data);
        this.userId = data[0];
        this.userAvatar = data[1];
        this.checkInLocation = data[2];
        this.createdByUserName = data[3];
        this.createdDateTime = data[4];
        this.geoChatId = data[5];
        this.comments = data[6];
        this.likes = data[7];
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }


    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.userId,
                this.userAvatar, this.checkInLocation, this.createdByUserName, this.createdDateTime, this.geoChatId, this.comments, this.likes,this.geoChatImage});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GeoChat createFromParcel(Parcel in) {
            return new GeoChat(in);
        }

        public GeoChat[] newArray(int size) {
            return new GeoChat[size];
        }
    };


    public String getGeoChatImage() {
        return geoChatImage;
    }

    public void setGeoChatImage(String geoChatImage) {
        this.geoChatImage = geoChatImage;
    }
}
