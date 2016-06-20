package ru.qsolution.vodovoz.driver.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import ru.qsolution.vodovoz.driver.R;
import ru.qsolution.vodovoz.driver.TabbedOrderDetailedActivity;

public class ReceiveTransitionsIntentService extends IntentService {

    public static final String TRANSITION_INTENT_SERVICE = "TransitionsService";

    public ReceiveTransitionsIntentService() {
        super(TRANSITION_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TRANSITION_INTENT_SERVICE, "Location Services error: " + geofencingEvent.getErrorCode());
            return;
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();

        for (Geofence geofence : triggeredGeofences) {
            Log.d("GEO", "onHandle:" + geofence.getRequestId());
            processGeofence(geofence, transitionType);
        }
    }

    private void processGeofence(Geofence geofence, int transitionType) {
        int id = Integer.parseInt(geofence.getRequestId());
        Intent i = new Intent(this, TabbedOrderDetailedActivity.class);
        i.putExtra("OrderId",  String.valueOf(id));
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        PendingIntent openActivityIntetnt = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        notificationBuilder
                .setSmallIcon(R.drawable.small_icon)
                .setContentTitle("Прибытие в точку назначения")
                .setContentText("Заказ №" + id)
                .setVibrate(new long[]{500, 500})
                .setLargeIcon(largeIcon)
                .setContentIntent(openActivityIntetnt)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(transitionType * 10000 + id, notificationBuilder.build());
    }
}