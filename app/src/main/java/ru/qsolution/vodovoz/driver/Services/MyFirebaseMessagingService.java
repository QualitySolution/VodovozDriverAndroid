package ru.qsolution.vodovoz.driver.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
    public static final int STATUS_NOTIFICATION = 1;
    public static final int SCHEDULE_NOTIFICATION = 2;
    public static List<INotificationObserver> notificationObserverList = new ArrayList<>();


    public static int GetNotificationCode (String notificationType) {
        switch (notificationType) {
            case "message": return MESSAGE_NOTIFICATION;
            case "orderStatusChange": return STATUS_NOTIFICATION;
            case "orderDeliveryScheduleChange": return SCHEDULE_NOTIFICATION;
            default: return -1;
        }
    }
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
            case "orderDeliveryScheduleChange":
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
        } else if (notificationType.equals("orderDeliveryScheduleChange")) {
            for (INotificationObserver observer : notificationObserverList) {
                if ((observer.NotificationType().equals("message") || observer.NotificationType().equals("orderDeliveryScheduleChange")) && observer.IsActive()) {
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
                .setDefaults(Notification.DEFAULT_ALL);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(GetNotificationCode(notificationType), notificationBuilder.build());
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
