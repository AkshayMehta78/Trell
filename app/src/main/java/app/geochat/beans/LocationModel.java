package app.geochat.beans;

/**
 * Created by akshaymehta on 30/08/15.
 */
public class LocationModel {
    String locationName;
    String locationAddress;
    String latitude;
    String longitude;

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

    public LocationModel(String locationName, String locationAddress, String latitude, String longitude)
    {
        this.locationName=locationName;
        this.locationAddress=locationAddress;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public String getLocationAddress() {
        return locationAddress;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationAddress(String locationAaddress) {
        this.locationAddress = locationAaddress;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

}