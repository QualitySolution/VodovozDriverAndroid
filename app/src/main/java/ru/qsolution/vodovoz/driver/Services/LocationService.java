package ru.qsolution.vodovoz.driver.Services;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import ru.qsolution.vodovoz.driver.AsyncTasks.AsyncTaskResult;
import ru.qsolution.vodovoz.driver.AsyncTasks.IAsyncTaskListener;
import ru.qsolution.vodovoz.driver.AsyncTasks.SendCoordinatesTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.StartTrackTask;
import ru.qsolution.vodovoz.driver.BuildConfig;
import ru.qsolution.vodovoz.driver.DTO.TrackPoint;
import ru.qsolution.vodovoz.driver.R;

public class LocationService extends Service implements IAsyncTaskListener<AsyncTaskResult<Integer>> {
    public static String RouteListId;

    private static Context context;
    private static LocationManager locationManager;
    private static ArrayList<TrackPoint> trackPoints = new ArrayList<>();
    private static AsyncTaskResult<Integer> trackIdResult;
    private static SendCoordinatesTask sendTask;
    private static ArrayList<TrackPoint> sendingPoints;
    private static String authKey;

    public static Boolean GpsEnabled () {
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private final LocationListener locationListener = new LocationListener() {
        class AsyncTaskListener implements IAsyncTaskListener<AsyncTaskResult<Boolean>> {
            @Override
            public void AsyncTaskCompleted(AsyncTaskResult<Boolean> result) {
                try {
                    if (result.getException() == null && result.getResult()) {
                        Log.i("Location", "Send " + sendingPoints.size() +" coordinates completed");
                        trackPoints.removeAll(sendingPoints);
                    } else if (result.getException() != null) {
                        throw result.getException();
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG)
                        e.printStackTrace();
                }
            }
        }

        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            Long time;
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
            if (sharedPref.getBoolean("UseGPSTime", true)) {
                Log.i("Location", "Using gps time");
                time = location.getTime();
            } else {
                Log.i("Location", "Using system time");
                time = Calendar.getInstance().getTimeInMillis();
            }
            trackPoints.add(new TrackPoint(latitude, longitude, time));
            Log.i("Location", "\nlat: " + latitude.toString() + "; lon: " + longitude.toString() + "; time: " + time.toString() + "; " + trackPoints.size());
            if (trackPoints.size() > 10 && (sendTask == null || sendTask.getStatus() == AsyncTask.Status.FINISHED)) {
                Log.i("Location", "Sending " + trackPoints.size() + " points started");
                sendingPoints = new ArrayList<>(trackPoints);
                AsyncTaskListener listener = new AsyncTaskListener();
                sendTask = new SendCoordinatesTask();
                sendTask.addListener(listener);
                sendTask.execute(authKey, trackIdResult.getResult(), sendingPoints);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                RouteListId = extras.getString("routeListId");
                context = this.getApplicationContext();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
                authKey = sharedPref.getString("Authkey", "");

                StartTrackTask task = new StartTrackTask();
                task.addListener(this);
                task.execute(authKey, RouteListId);
            }
            return Service.START_REDELIVER_INTENT;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void AsyncTaskCompleted(AsyncTaskResult<Integer> result) {
        try {
            if (result.getException() == null && result.getResult() != null) {
                trackIdResult = result;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
                Log.i("Location", "requesting location updates for route list #" + RouteListId);
            } else if (trackIdResult.getException() == null && trackIdResult.getResult() == null) {
                Toast toast = Toast.makeText(context, "Не удалось запустить сервис геолокации.", Toast.LENGTH_LONG);
                toast.show();
            } else {
                throw trackIdResult.getException();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(DriverNotificationService.ONGOING_NOTIFICATION_ID);

        locationManager.removeUpdates(locationListener);
        RouteListId = null;
        if (trackPoints.size() > 0) {
            new SendCoordinatesTask().execute(authKey, trackIdResult.getResult(), trackPoints);
        }
        Log.i("Location", "stopping location updates for route list #" + RouteListId);
        super.onDestroy();
    }
}
