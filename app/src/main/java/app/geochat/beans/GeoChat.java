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
    String latitude,longitude;
    String descripton,tags,city,caption;
    String distance;
    String isFollowing,isWishListAdded;
    String commentCount,wishListCount;


    public String isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(String isFollowing) {
        this.isFollowing = isFollowing;
    }

    public String isWishListAdded() {
        return isWishListAdded;
    }

    public void setIsWishListAdded(String isWishListAdded) {
        this.isWishListAdded = isWishListAdded;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getWishListCount() {
        return wishListCount;
    }

    public void setWishListCount(String wishListCount) {
        this.wishListCount = wishListCount;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDescripton() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }



    public GeoChat(String userId, String userAvatar, String checkInLocation, String createdByUserName, String createdDateTime, String geoChatId, String comments, String likes,String geoChatImage,String latitude,String longitude, String descripton,String tags,String city,String caption,String distance,String isFollowing,String isWishListAdded,String wishListCount,String commentCount) {
        this.userId = userId;
        this.userAvatar = userAvatar;
        this.checkInLocation = checkInLocation;
        this.createdByUserName = createdByUserName;
        this.createdDateTime = createdDateTime;
        this.geoChatId = geoChatId;
        this.comments = comments;
        this.likes = likes;
        this.geoChatImage = geoChatImage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.descripton = descripton;
        this.tags = tags;
        this.city = city;
        this.caption = caption;
        this.distance = distance;
        this.isFollowing = isFollowing;
        this.isWishListAdded = isWishListAdded;
        this.wishListCount = wishListCount;
        this.commentCount = commentCount;
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
        String[] data = new String[20];

        in.readStringArray(data);
        this.userId = data[0];
        this.userAvatar = data[1];
        this.checkInLocation = data[2];
        this.createdByUserName = data[3];
        this.createdDateTime = data[4];
        this.geoChatId = data[5];
        this.comments = data[6];
        this.likes = data[7];
        this.geoChatImage = data[8];
        this.latitude = data[9];
        this.longitude = data[10];
        this.descripton = data[11];
        this.tags = data[12];
        this.city = data[13];
        this.caption = data[14];
        this.distance = data[15];

        this.isFollowing = data[16];
        this.isWishListAdded = data[17];
        this.wishListCount = data[18];
        this.commentCount = data[19];
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
                this.userAvatar, this.checkInLocation, this.createdByUserName, this.createdDateTime, this.geoChatId, this.comments, this.likes,this.geoChatImage,this.latitude,this.longitude,this.descripton,this.tags,this.city,this.caption,this.distance,this.isFollowing,this.isWishListAdded,this.wishListCount,this.commentCount});
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
