package edu.northeastern.numad23fa_daviddada;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LocationActivity extends AppCompatActivity {
    TextView currentNumberTextView;
    TextView lastLocationTextView;

    Thread locationThread;

    Handler locationHandler;

    String logTag = "@@Location Activity";

    private boolean isThreadRunning;

    private int currentNumber;

    private int lastLocationNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

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

        // find locations button
//        final Button findLocationsButton = findViewById(R.id.location_dir_find_locations_button);
//        findLocationsButton.setOnClickListener(v -> {
//            if (!isThreadRunning) {
//                locationThread = new Thread(new LocationRunner(this, locationHandler, currentNumberTextView, lastLocationTextView));
//                locationThread.start();
//                isThreadRunning = true;
//            } else {
//                Log.d(logTag, "ALREADY RUNNING!");
//            }
//        });

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
}
