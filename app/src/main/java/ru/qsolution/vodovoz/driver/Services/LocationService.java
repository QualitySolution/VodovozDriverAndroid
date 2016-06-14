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

import ru.qsolution.vodovoz.driver.AsyncTasks.SendCoordinatesTask;

public class LocationService extends Service {
    private LocationManager locationManager;
    public static String RouteListId;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            Float accuracy = location.getAccuracy();
            Long time = location.getTime();
            location.getTime();
            Log.i("Location", "\nlat: " + latitude.toString() + "; lon: " + longitude.toString() + "; acc: " + accuracy.toString() + "; time: " + time.toString());
            //FIXME: Auth key
            new SendCoordinatesTask().execute("authKey", latitude.toString(), longitude.toString());
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
