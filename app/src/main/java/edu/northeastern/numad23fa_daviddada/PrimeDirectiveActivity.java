package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.math3.primes.Primes;

public class PrimeDirectiveActivity extends AppCompatActivity {
    TextView currentNumberTextView;
    TextView lastPrimeTextView;

    Thread primeThread;

    Handler primeHandler;

    String logTag = "--------- PrimeDirective ---------";

    boolean isThreadRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime_directive);

        primeHandler = new Handler(Looper.getMainLooper());

        // Create and start a background thread
        primeThread = new Thread(new PrimeRunner(primeHandler));

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
                primeThread = new Thread(new PrimeRunner(primeHandler));
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
    }

    class PrimeRunner implements Runnable {
        Handler handler;

        private final int n_skips;

        private int currentNumber;
        private int lastPrimeNumber;

        PrimeRunner(Handler handler) {
            this.handler = handler;
            currentNumber = 3;
            lastPrimeNumber = 3;
            n_skips = 2000;
        }

        private boolean isPrime(int number) {
            return Primes.isPrime(number);
        }

        @Override
        public void run() {
            int skipCount = 0;

            while (!Thread.currentThread().isInterrupted()) {
                if (++skipCount % n_skips == 0) {
                    skipCount = 0;
                    currentNumber += 2;

                    // if prime, update
                    if (isPrime(currentNumber)) {
                        lastPrimeNumber = currentNumber;

                        // last prime post in main handler
                        handler.post(() -> lastPrimeTextView.setText(getString(R.string.last_prime_text, "" + lastPrimeNumber)));
                    }

                    // current number post in main handler
                    handler.post(() -> currentNumberTextView.setText(getString(R.string.current_number_prime_text, "" + currentNumber)));

                    if (Thread.currentThread().isInterrupted()) {
                        Log.d(logTag, "Is Interrupted");
                        break;
                    }
                }
            }
        }
    }
}
