package ru.qsolution.vodovoz.driver.Workers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import ru.qsolution.vodovoz.driver.Services.LocationService;

/**
 * Created by Andrei on 14.06.16.
 */
public class ServiceWorker {
    private static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void StartLocationService (Context context, String routeListId) {
        if (!ServiceWorker.isServiceRunning(context, LocationService.class)) {
            Intent i = new Intent(context, LocationService.class);
            i.putExtra("routeListId", routeListId);
            context.startService(i);
        }
    }

    public static void StopLocationService (Context context) {
            context.stopService(new Intent(context, LocationService.class));
    }
}
