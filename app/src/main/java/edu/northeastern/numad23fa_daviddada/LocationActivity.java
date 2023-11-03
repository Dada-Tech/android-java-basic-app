package edu.northeastern.numad23fa_daviddada;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.snackbar.Snackbar;

public class LocationActivity extends AppCompatActivity implements LocationService.LocationUpdateCallback {
    TextView latitudeTextView;
    TextView longitudeTextView;
    TextView totalDistanceTextView;

    Handler locationHandler;

    String logTag = "@@LocationActivity";

    private boolean isThreadRunning;

    private double longitude;
    private double latitude;
    private double totalDistance;
    private double lastLatitude;
    private double lastLongitude;
    private double lastTotalDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        View locationView = findViewById(R.id.location_view);

        this.lastTotalDistance = 0;
        this.lastLatitude = 0;
        this.lastLongitude = 0;

        this.longitude = 0;
        this.latitude = 0;
        this.totalDistance = 0;

        // broadcast receive location-update intents
        LocationBroadCastReceiver locationBroadCastReceiver = new LocationBroadCastReceiver();
        ContextCompat.registerReceiver(this, locationBroadCastReceiver, new IntentFilter("location-changed"), ContextCompat.RECEIVER_NOT_EXPORTED);
        ContextCompat.registerReceiver(this, locationUpdateReceiver, new IntentFilter("location-changed"), ContextCompat.RECEIVER_NOT_EXPORTED);

        // main handler. Could also use method & runOnUiThread
        locationHandler = new Handler(Looper.getMainLooper());

        // Create and start a background thread
        // locationThread = new Thread(new LocationRunner(this, locationHandler, latitudeTextView, longitudeTextView));

        // current number text view
        latitudeTextView = findViewById(R.id.latitude_text_view);
        latitudeTextView.setText(getString(R.string.latitude, "" + 0));

        longitudeTextView = findViewById(R.id.longitude_text_view);
        longitudeTextView.setText(getString(R.string.longitude, "" + 0));

        // last location text view
        totalDistanceTextView = findViewById(R.id.total_distance_travelled_text_view);
        totalDistanceTextView.setText(getString(R.string.total_distance_travelled, "" + 0));

        // Location Request Definition
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {

                            // define permission
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);

                            // if either is allowed
                            if ((fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted)) {
                                Snackbar.make(locationView, "Location service granted", Snackbar.LENGTH_SHORT).show();
                                Log.d(logTag, "location services started from activity");

                                // Start LocationService using Intent
                                Intent serviceIntent = new Intent(this, LocationService.class);
                                startService(serviceIntent);
                                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

                            } else {
                                Snackbar.make(locationView, "Location service is required!", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                );

        // location request call
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        // reset total distance
        final Button resetTotalDistanceButton = findViewById(R.id.location_reset_total_distance_button);
        resetTotalDistanceButton.setOnClickListener(v -> {
            this.totalDistance = 0;
            this.lastTotalDistance = 0;
            totalDistanceTextView.setText(getString(R.string.total_distance_travelled, "" + 0));
        });

        // restore saved state if exists
        if (savedInstanceState != null) {
            double longitude = savedInstanceState.getDouble("longitude");
            double latitude = savedInstanceState.getDouble("latitude");
            double totalDistance = savedInstanceState.getDouble("totalDistance");

            double lastLongitude = savedInstanceState.getDouble("lastLongitude");
            double lastLatitude = savedInstanceState.getDouble("lastLatitude");
            double lastTotalDistance = savedInstanceState.getDouble("lastTotalDistance");

            boolean isThreadRunning = savedInstanceState.getBoolean("isThreadRunning");

            this.longitude = longitude;
            this.latitude = latitude;
            this.totalDistance = totalDistance;

            this.lastLongitude = lastLongitude;
            this.lastLatitude = lastLatitude;
            this.lastTotalDistance = lastTotalDistance;

            this.isThreadRunning = isThreadRunning;

            // restore the text view
            latitudeTextView.setText(getString(R.string.latitude, "" + latitude));
            longitudeTextView.setText(getString(R.string.longitude, "" + longitude));
            totalDistanceTextView.setText(getString(R.string.total_distance_travelled, "" + this.totalDistance));

            Log.d(logTag, String.format("restoring values: long: %.2f, lat: %.2f, tot: %.2f, last_tot: %.2f, isRun: %b",
                    longitude, latitude, totalDistance, lastTotalDistance, isThreadRunning));
        }

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                builder.setMessage("Are you sure you want to Exit?");

                builder.setPositiveButton("Exit", (dialog, which) -> {
                    finish(); // exit
                });
                builder.setNegativeButton("Dismiss", (dialog, which) -> {
                    dialog.dismiss(); // close
                });
                builder.show();
            }
        };

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            // The service is connected, and you can access its methods.
            LocationService.LocalBinder localBinder = (LocationService.LocalBinder) binder;
            LocationService locationService = localBinder.getService();
            locationService.setLocationUpdateCallback(LocationActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // The service is disconnected, you can perform cleanup here.
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        // broadcast receiver unregister
        Log.d(logTag, "destroying ref to location receiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdateReceiver);
    }

    // save: current number, last location number, isThread running
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("longitude", longitude);
        outState.putDouble("latitude", latitude);
        outState.putDouble("totalDistance", totalDistance);

        outState.putDouble("lastLongitude", lastLongitude);
        outState.putDouble("lastLatitude", lastLatitude);
        outState.putDouble("lastTotalDistance", lastTotalDistance);

        outState.putBoolean("isThreadRunning", isThreadRunning);

        Log.d(logTag, String.format("saving values: long: %.2f, lat: %.2f, tot: %.2f, last_tot: %.2f, isThread: %b",
                longitude, latitude, totalDistance, lastTotalDistance, isThreadRunning));
    }

    @Override
    public void onLocationUpdate(double latitude, double longitude) {

        // calculations
        if (lastLongitude == 0) {
            lastLatitude = latitude;
            lastLongitude = longitude;
        }

        this.lastTotalDistance = lastTotalDistance + distance(lastLatitude, latitude, lastLongitude, longitude, 1, 1);
        this.lastLatitude = latitude;
        this.lastLongitude = longitude;

        this.latitude = latitude;
        this.longitude = longitude;
        this.totalDistance = lastTotalDistance;

        // set of result
        longitudeTextView.setText(getString(R.string.longitude, "" + longitude));
        latitudeTextView.setText(getString(R.string.latitude, "" + latitude));
        totalDistanceTextView.setText(getString(R.string.total_distance_travelled, "" + lastTotalDistance));
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

    // doesn't work
    private final BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(logTag, "LOCATION RECEIVED");
            if ("location-update".equals(intent.getAction())) {
                double latitude = intent.getDoubleExtra("latitude", 0.0);
                double longitude = intent.getDoubleExtra("longitude", 0.0);
                Log.d(logTag, String.format("LOCATION RECEIVED:\n%s\n%s", latitude, longitude));
            } else {
                Log.d(logTag, "ignored Intent:" + intent.getAction());
            }
        }
    };
}
