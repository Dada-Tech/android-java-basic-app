package edu.northeastern.numad23fa_daviddada;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.List;


public class LocationService extends Service implements LocationListener {
    private LocationUpdateCallback locationUpdateCallback;
    private final IBinder binder = new LocalBinder();

    public interface LocationUpdateCallback {
        void onLocationUpdate(double latitude, double longitude);
    }

    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    private final String logTag = "@@LocationService";

    @Override
    public void onCreate() {
        super.onCreate();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(logTag, "exiting for permission reasons");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Broadcast location
        Intent intent = new Intent();
        intent.setAction("location-update");
        intent.setPackage("edu.northeastern.numad23fa_daviddada");
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        sendBroadcast(intent);
        Log.d(logTag, String.format("location changed %s %s", latitude, longitude));

        this.sendBroadcast(intent);
        if (locationUpdateCallback != null) {
            locationUpdateCallback.onLocationUpdate(latitude, longitude);
        } else {
            Log.d(logTag, "callback null!");
        }
    }

    public void setLocationUpdateCallback(LocationUpdateCallback callback) {
        this.locationUpdateCallback = callback;
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}

