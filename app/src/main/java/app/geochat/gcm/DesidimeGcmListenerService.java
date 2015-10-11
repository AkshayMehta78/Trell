package app.geochat.gcm;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by tasneem on 23/7/15.
 */
public class DesidimeGcmListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }
}
