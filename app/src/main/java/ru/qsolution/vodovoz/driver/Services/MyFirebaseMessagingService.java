package ru.qsolution.vodovoz.driver.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.qsolution.vodovoz.driver.ChatActivity;
import ru.qsolution.vodovoz.driver.R;


/**
 * Created by Andrei Vinogradov on 21.06.16.
 * (c) Quality Solution Ltd.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final int MESSAGE_NOTIFICATION = 0;
    public static List<INotificationObserver> notificationObserverList = new ArrayList<>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (data == null || data.size() == 0)
            return;
        String notificationType = data.get("notificationType");
        String sender;
        String message;
        switch (notificationType) {
            case "message":
            case "orderStatusChange":
                sender = data.get("sender");
                message = data.get("message");
                sendChatNotification(sender, message, notificationType);
            default:
                return;
        }
    }

    private void sendChatNotification(String sender, String message, String notificationType) {
        boolean observableNotified = false;
        if (notificationType.equals("message")) {
            for (INotificationObserver observer : notificationObserverList) {
                if (observer.NotificationType().equals("message") && observer.IsActive()) {
                    observer.HandleNotification();
                    observableNotified = true;
                }
            }
        } else if (notificationType.equals("orderStatusChange")) {
            for (INotificationObserver observer : notificationObserverList) {
                if ((observer.NotificationType().equals("message") || observer.NotificationType().equals("orderStatusChange")) && observer.IsActive()) {
                    observer.HandleNotification();
                    observableNotified = observer.NotificationType().equals("message");
                }
            }
        }

        if (observableNotified)
            return;
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.small_icon)
                .setContentTitle(sender)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        MediaPlayer mp= MediaPlayer.create(getApplicationContext(), R.raw.notification);
        mp.start();

        notificationManager.notify(MESSAGE_NOTIFICATION, notificationBuilder.build());
    }

    public static void AddObserver(INotificationObserver observer) {
        if (!notificationObserverList.contains(observer)) {
            notificationObserverList.add(observer);
        }
    }

    public static void RemoveObserver(INotificationObserver observer) {
        notificationObserverList.remove(observer);
    }

}
