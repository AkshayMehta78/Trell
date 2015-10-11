package app.geochat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by tasneem on 27/7/15.
 */
public class NetworkManager {
    /**
     * Check Connection
     *
     * @param ctx
     * @return
     */
    public static boolean isConnectedToInternet(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
