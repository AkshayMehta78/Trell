package app.geochat.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Akshay on 18/6/15.
 */
public class GcmIntentService extends GcmListenerService {
    private Handler handler;
    public GcmIntentService() {
        handler=new Handler();
    }

    protected void onHandleIntent(Intent intent) {
        Bundle bundle=intent.getExtras();
        GoogleCloudMessaging gcm=GoogleCloudMessaging.getInstance(this);
        final String messageType = gcm.getMessageType(intent);
        handler.post(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(GcmIntentService.this, messageType, Toast.LENGTH_LONG);
                toast.show();
            }
        });

        if (!bundle.isEmpty())
        {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            }
        }
     /*   generateLoginNotification(this, "Expresso App","Hi");*/

    }

    public void onMessageReceived(String from, Bundle data) {
        sendNotification("Received: " + data.toString());
    }

    private void sendNotification(String msg) {
        Log.e("msg",msg.toString());
    }




    /*private void generateLoginNotification(Context context, String type, String message) {
        Intent notificationIntent = null;
        String title;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if(type.equalsIgnoreCase(context.getResources().getString(R.string.no_alarm_set_type))) {
            title="No Alarm Set";
            notificationIntent = new Intent(context, LandingScreen.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            title="Not LoggedIn";
            notificationIntent = new Intent(context, LandingScreen.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        PendingIntent pendingIntent = null;
        pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentText(message);
        builder.setContentIntent(pendingIntent);

        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.ic_launcher);

        Notification notification = builder.build();
        notification.when = when;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0,notification);

    }*/

}
