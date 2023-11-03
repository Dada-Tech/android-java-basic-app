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

    private double lastLatitude = 0;
    private double lastLongitude = 0;
    private double lastTotalDistance = 0;

    public interface LocationUpdateCallback {
        void onLocationUpdate(double latitude, double longitude, double totalDistance);
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
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

        if (lastLongitude == 0) {
            lastLatitude = latitude;
            lastLongitude = longitude;
        }

        lastTotalDistance = lastTotalDistance + distance(lastLatitude, latitude, lastLongitude, longitude, 1, 1);
        lastLatitude = latitude;
        lastLongitude = longitude;


        // Broadcast location
        Intent intent = new Intent();
        intent.setAction("location-update");
        intent.setPackage("edu.northeastern.numad23fa_daviddada");
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("totalDistance", lastTotalDistance);
        sendBroadcast(intent);
//        Log.d(logTag, String.format("location changed %s %s", latitude, longitude));

        this.sendBroadcast(intent);
        if (locationUpdateCallback != null) {
            locationUpdateCallback.onLocationUpdate(latitude, longitude, lastTotalDistance);
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

    /**
     * Reference: <a href="https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude/16794680#16794680">...</a>
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}

