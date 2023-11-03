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
    TextView currentNumberTextView;
    TextView lastLocationTextView;

    Thread locationThread;

    Handler locationHandler;

    String logTag = "@@LocationActivity";

    private boolean isThreadRunning;

    private int currentNumber;

    private int lastLocationNumber;

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
        locationThread = new Thread(new LocationRunner(this, locationHandler, currentNumberTextView, lastLocationTextView));

        // current number text view
        currentNumberTextView = findViewById(R.id.current_location_text_view);
        currentNumberTextView.setText(getString(R.string.current_location_text, 0, 0));

        // last location text view
        lastLocationTextView = findViewById(R.id.total_distance_travelled_text_view);
        lastLocationTextView.setText(getString(R.string.total_distance_travelled, 0));

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
            int currentNumber = savedInstanceState.getInt("currentNumber");
            int lastLocationNumber = savedInstanceState.getInt("lastLocationNumber");
            boolean isThreadRunning = savedInstanceState.getBoolean("isThreadRunning");

            this.currentNumber = currentNumber;
            this.lastLocationNumber = lastLocationNumber;
            this.isThreadRunning = isThreadRunning;

            // restore the text view
            currentNumberTextView.setText(getString(R.string.current_location_text, currentNumber, currentNumber));
            lastLocationTextView.setText(getString(R.string.total_distance_travelled, lastLocationNumber));
            Log.d(logTag, String.format("restoring values: curr: %d prim: %d %b",
                    currentNumber, lastLocationNumber, isThreadRunning));

            // create new thread with saved valued
            locationThread = new Thread(new LocationRunner(this, locationHandler, currentNumberTextView, lastLocationTextView, currentNumber, lastLocationNumber));

            // start thread if it was running before
            if (isThreadRunning) {
                locationThread.start();
            }
        }


        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isThreadRunning) {
                    finish();
                    return;
                }

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
        outState.putInt("currentNumber", currentNumber);
        outState.putInt("lastLocationNumber", lastLocationNumber);
        outState.putBoolean("isThreadRunning", isThreadRunning);

        Log.d(logTag, String.format("saving values: curr: %d prim: %d %b",
                currentNumber, lastLocationNumber, isThreadRunning));
    }

    // for updating from the Thread class that was given this context (LocationDir class)
    public void updateCurrentNumber(int currentNumber) {
        this.currentNumber = currentNumber;
    }

    public void updateLastLocationNumber(int lastLocationNumber) {
        this.lastLocationNumber = lastLocationNumber;
    }

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

    @Override
    public void onLocationUpdate(double latitude, double longitude) {
        Log.d(logTag, String.format("Callback:\n%s\n%s", latitude, longitude));
    }
}
