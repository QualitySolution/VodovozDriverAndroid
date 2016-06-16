package ru.qsolution.vodovoz.driver.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.AsyncTasks.AsyncTaskResult;
import ru.qsolution.vodovoz.driver.AsyncTasks.IAsyncTaskListener;
import ru.qsolution.vodovoz.driver.AsyncTasks.SendCoordinatesTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.StartTrackTask;
import ru.qsolution.vodovoz.driver.BuildConfig;
import ru.qsolution.vodovoz.driver.DTO.TrackPoint;
import ru.qsolution.vodovoz.driver.R;

public class LocationService extends Service implements IAsyncTaskListener<AsyncTaskResult<Integer>> {
    public static String RouteListId;

    private Context context;
    private LocationManager locationManager;
    private ArrayList<TrackPoint> trackPoints = new ArrayList<>();
    private AsyncTaskResult<Integer> trackIdResult;
    private String authKey;

    private LocationListener locationListener = new LocationListener() {
        class AsyncTaskListener implements IAsyncTaskListener<AsyncTaskResult<Boolean>> {
            @Override
            public void AsyncTaskCompleted(AsyncTaskResult<Boolean> result) {
                try {
                    if (result.getException() == null && result.getResult()) {
                        trackPoints = new ArrayList<>();
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
            Long time = location.getTime();
            trackPoints.add(new TrackPoint(latitude, longitude, time));
            Log.i("Location", "\nlat: " + latitude.toString() + "; lon: " + longitude.toString() + "; time: " + time.toString());
            if (trackPoints.size() > 10) {
                AsyncTaskListener listener = new AsyncTaskListener();
                SendCoordinatesTask sendTask = new SendCoordinatesTask();
                sendTask.addListener(listener);
                sendTask.execute(authKey, trackIdResult.getResult(), trackPoints);
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
            // TODO
            return 0;
        }
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
        locationManager.removeUpdates(locationListener);
        Log.i("Location", "stopping location updates for route list #" + RouteListId);
        super.onDestroy();
    }
}
