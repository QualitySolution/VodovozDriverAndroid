package ru.qsolution.vodovoz.driver.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import ru.qsolution.vodovoz.driver.DTO.TrackPoint;

public class LocationService extends Service {
    public static String RouteListId;

    private LocationManager locationManager;
    private ArrayList<TrackPoint> trackPoints = new ArrayList<>();

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            Float accuracy = location.getAccuracy();
            Long time = location.getTime();
            trackPoints.add(new TrackPoint(latitude, longitude, time));
            Log.i("Location", "\nlat: " + latitude.toString() + "; lon: " + longitude.toString() + "; acc: " + accuracy.toString() + "; time: " + time.toString());
            //FIXME: Auth key
            if (trackPoints.size() > 2) {
                try {
                    if (new SendCoordinatesTask().execute("authKey", trackPoints).get()) {
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
            LocationService.RouteListId = extras.getString("routeListId");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 3, locationListener);
            Log.i("Location", "requesting location updates for route list #" + LocationService.RouteListId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        locationManager.removeUpdates(locationListener);
        Log.i("Location", "stopping location updates for route list #" + LocationService.RouteListId);
        super.onDestroy();
    }
}
