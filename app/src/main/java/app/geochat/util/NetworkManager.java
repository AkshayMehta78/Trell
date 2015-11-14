package app.geochat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import app.geochat.R;

/**
 * Created by akshay on 27/7/15.
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
            Utils.showToast(ctx,ctx.getString(R.string.no_internet));
            return false;
        }
    }
}
