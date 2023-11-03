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

    Thread locationThread;

    Handler locationHandler;

    String logTag = "@@LocationActivity";

    private boolean isThreadRunning;

    private int longitude;

    private int latitude;

    private int totalDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        View locationView = findViewById(R.id.location_view);

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
            if (isThreadRunning) {
                locationThread.interrupt();
                isThreadRunning = false;
            } else {
                Log.d(logTag, "tried to interrupt on dead thread");
            }
        });

        // restore saved state if exists
        if (savedInstanceState != null) {
            int longitude = savedInstanceState.getInt("longitude");
            int latitude = savedInstanceState.getInt("latitude");
            int totalDistance = savedInstanceState.getInt("totalDistance");
            boolean isThreadRunning = savedInstanceState.getBoolean("isThreadRunning");

            this.longitude = longitude;
            this.latitude = latitude;
            this.totalDistance = totalDistance;
            this.isThreadRunning = isThreadRunning;

            // restore the text view
            latitudeTextView.setText(getString(R.string.latitude, "" + latitude));
            longitudeTextView.setText(getString(R.string.longitude, "" + latitude));
            totalDistanceTextView.setText(getString(R.string.total_distance_travelled, "" + latitude));

            Log.d(logTag, String.format("restoring values: long: %d, lat: %d, tot: %d, isRun: %b",
                    longitude, latitude, totalDistance, isThreadRunning));

            // create new thread with saved valued
            locationThread = new Thread(new LocationRunner(this, locationHandler, latitudeTextView, longitudeTextView, longitude, latitude));

            // start thread if it was running before
            if (isThreadRunning) {
                locationThread.start();
            }
        }

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                builder.setMessage("Search is still running.\nStill Exit?");

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
        outState.putInt("longitude", longitude);
        outState.putInt("latitude", latitude);
        outState.putInt("totalDistance", totalDistance);
        outState.putBoolean("isThreadRunning", isThreadRunning);

        Log.d(logTag, String.format("saving values: long: %d, lat: %d, tot: %d, isThread: %b",
                longitude, latitude, totalDistance, isThreadRunning));
    }

    // for updating from the Thread class that was given this context (LocationDir class)
    public void updateLongitude(int longitude) {
        this.longitude = longitude;
    }

    public void updateLatitude(int latitude) {
        this.latitude = latitude;
    }

    public void updateTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    @Override
    public void onLocationUpdate(double latitude, double longitude, double totalDistance) {
        longitudeTextView.setText(getString(R.string.longitude, "" + longitude));
        latitudeTextView.setText(getString(R.string.latitude, "" + latitude));
        totalDistanceTextView.setText(getString(R.string.total_distance_travelled, "" + totalDistance));
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
