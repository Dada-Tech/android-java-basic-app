package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// TODO: 1. continue the search through rotation using onSaveInstanceState().
// TODO: 2. back button prompts user to verify termination only if searching. Closing the current activity should terminate/reset the search.

public class PrimeDirectiveActivity extends AppCompatActivity {
    TextView currentNumberTextView;
    TextView lastPrimeTextView;

    Thread primeThread;

    Handler primeHandler;

    String logTag = "--------- PrimeDirective ---------";

    private boolean isThreadRunning;

    private int currentNumber;

    private int lastPrimeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime_directive);

        // main handler. Could also use method & runOnUiThread
        primeHandler = new Handler(Looper.getMainLooper());

        // Create and start a background thread
        primeThread = new Thread(new PrimeRunner(this, primeHandler, currentNumberTextView, lastPrimeTextView));

        // current number text view
        currentNumberTextView = findViewById(R.id.prime_dir_current_number_text_view);
        currentNumberTextView.setText(getString(R.string.current_number_prime_text, " "));

        // last prime text view
        lastPrimeTextView = findViewById(R.id.prime_dir_last_prime_text_view);
        lastPrimeTextView.setText(getString(R.string.last_prime_text, " "));

        // find primes button
        final Button findPrimesButton = findViewById(R.id.prime_dir_find_primes_button);
        findPrimesButton.setOnClickListener(v -> {
            if (!isThreadRunning) {
                primeThread = new Thread(new PrimeRunner(this, primeHandler, currentNumberTextView, lastPrimeTextView));
                primeThread.start();
                isThreadRunning = true;
            } else {
                Log.d(logTag, "ALREADY RUNNING!");
            }
        });

        // terminate search button
        final Button terminateSearchButton = findViewById(R.id.prime_dir_terminate_search_button);
        terminateSearchButton.setOnClickListener(v -> {
            if (isThreadRunning) {
                primeThread.interrupt();
                isThreadRunning = false;
            } else {
                Log.d(logTag, "tried to interrupt on dead thread");
            }
        });

        // pacifier switch checkbox
        final CheckBox pacifierSwitchCheckbox = findViewById(R.id.prime_dir_pacifier_switch_checkbox);
        pacifierSwitchCheckbox.setOnClickListener(v -> pacifierSwitchCheckbox.setActivated(!pacifierSwitchCheckbox.isActivated()));

        // restore saved state if exists
        if (savedInstanceState != null) {
            int currentNumber = savedInstanceState.getInt("currentNumber");
            int lastPrimeNumber = savedInstanceState.getInt("lastPrimeNumber");
            boolean isThreadRunning = savedInstanceState.getBoolean("isThreadRunning");

            this.currentNumber = currentNumber;
            this.lastPrimeNumber = lastPrimeNumber;
            this.isThreadRunning = isThreadRunning;

            // restore the text view
            currentNumberTextView.setText(getString(R.string.current_number_prime_text, "" + currentNumber));
            lastPrimeTextView.setText(getString(R.string.last_prime_text, "" + lastPrimeNumber));
            Log.d(logTag, String.format("restoring values: curr: %d prim: %d %b",
                    currentNumber, lastPrimeNumber, isThreadRunning));

            // create new thread with saved valued
            primeThread = new Thread(new PrimeRunner(this, primeHandler, currentNumberTextView, lastPrimeTextView, currentNumber, lastPrimeNumber));

            // start thread if it was running before
            if(isThreadRunning) {
                primeThread.start();
            }
        }
    }

    // save: current number, last prime number, isThread running
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentNumber", currentNumber);
        outState.putInt("lastPrimeNumber", lastPrimeNumber);
        outState.putBoolean("isThreadRunning", isThreadRunning);

        Log.d(logTag, String.format("saving values: curr: %d prim: %d %b",
                currentNumber, lastPrimeNumber, isThreadRunning));
    }

    // for updating from the Thread class that was given this context (PrimeDir class)
    public void updateCurrentNumber(int currentNumber) {
        this.currentNumber = currentNumber;
    }

    public void updateLastPrimeNumber(int lastPrimeNumber) {
        this.lastPrimeNumber = lastPrimeNumber;
    }
}
