package ru.qsolution.vodovoz.driver.Services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import ru.qsolution.vodovoz.driver.R;

/**
 * Created by Andrei Vinogradov on 10.08.16.
 * (c) Quality Solution Ltd.
 */
public class DriverNotificationService extends BroadcastReceiver {
    public static final int ONGOING_NOTIFICATION_ID = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String message;

        if (LocationService.RouteListId == null) {
            message = "Ведение трека не производится.";
        } else if (!LocationService.GpsEnabled()) {
            message = "GPS выключен!";
        } else if (networkInfo == null || !networkInfo.isConnected()) {
            message = "Отсутствует подключение к интернету!";
        } else {
            message = "Запись трека для МЛ №" + LocationService.RouteListId + ".";
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.small_icon)
                .setContentTitle("Трек")
                .setOngoing(true)
                .setContentText(message)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(ONGOING_NOTIFICATION_ID, notificationBuilder.build());
    }
}