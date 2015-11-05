package app.geochat;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by akshay on 23/7/15.
 */
public class DesidimeApplication extends Application {

    public static final String TAG = DesidimeApplication.class.getSimpleName();
    private static final String PROPERTY_ID = "UA-46559859-1";
    private static Context mContext;


    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public DesidimeApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DesidimeApplication.mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return DesidimeApplication.mContext;
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : analytics.newTracker(PROPERTY_ID);// if trackerId == TrackerName.GLOBAL_TRACKER
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
