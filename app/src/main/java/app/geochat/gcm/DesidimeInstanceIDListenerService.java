package app.geochat.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by akshay on 23/7/15.
 */
public class DesidimeInstanceIDListenerService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
