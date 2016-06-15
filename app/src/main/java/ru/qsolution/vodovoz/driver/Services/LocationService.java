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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ru.qsolution.vodovoz.driver.AsyncTasks.SendCoordinatesTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.StartTrackTask;
import ru.qsolution.vodovoz.driver.DTO.TrackPoint;
import ru.qsolution.vodovoz.driver.R;

public class LocationService extends Service {
    public static String RouteListId;

    private LocationManager locationManager;
    private ArrayList<TrackPoint> trackPoints = new ArrayList<>();
    private Integer trackId;
    private String authKey;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            Long time = location.getTime();
            trackPoints.add(new TrackPoint(latitude, longitude, time));
            Log.i("Location", "\nlat: " + latitude.toString() + "; lon: " + longitude.toString() + "; time: " + time.toString());
            if (trackPoints.size() > 0) {
                try {
                    if (new SendCoordinatesTask().execute(authKey, trackId, trackPoints).get()) {
                        trackPoints = new ArrayList<>();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
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
            Context context = this.getApplicationContext();
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
            authKey = sharedPref.getString("Authkey", "");
            try {
                trackId = new StartTrackTask().execute(authKey, RouteListId).get();
                if (trackId != null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
                    Log.i("Location", "requesting location updates for route list #" + RouteListId);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        locationManager.removeUpdates(locationListener);
        Log.i("Location", "stopping location updates for route list #" + RouteListId);
        super.onDestroy();
    }
}
