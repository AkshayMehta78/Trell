package app.geochat.beans;

import com.google.android.gms.maps.model.LatLng;

import io.nlopez.clusterer.Clusterable;

/**
 * Created by akshaymehta on 10/11/15.
 */
public class ClusterGeoChat implements Clusterable{
    private LatLng locationLatLng;
    private String checkIn;
    private String description;

    public String getGeoChatImage() {
        return geoChatImage;
    }

    public void setGeoChatImage(String geoChatImage) {
        this.geoChatImage = geoChatImage;
    }

    private String geoChatImage;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public LatLng getLocationLatLng() {
        return locationLatLng;
    }

    public void setLocationLatLng(LatLng locationLatLng) {
        this.locationLatLng = locationLatLng;
    }

    @Override
    public LatLng getPosition() {
        return getLocationLatLng();
    }
}
