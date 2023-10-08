package edu.northeastern.numad23fa_daviddada;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.math3.primes.Primes;

class PrimeRunner implements Runnable {
    Handler handler;

    private final int n_skips;

    private int currentNumber;
    private int lastPrimeNumber;

    private final TextView currentNumberView;
    private final TextView lastPrimeView;

    private final Context context;

    private PrimeDirectiveActivity mainActivity;

    PrimeRunner(Context context, Handler handler, TextView currentNumberView, TextView lastPrimeView) {
        this(context, handler, currentNumberView, lastPrimeView, 3, 3);
    }

    PrimeRunner(Context context, Handler handler, TextView currentNumberView, TextView lastPrimeView, int currentNumber, int lastPrimeNumber) {
        this.handler = handler;
        this.currentNumber = currentNumber;
        this.lastPrimeNumber = lastPrimeNumber;
        n_skips = 20000;

        this.currentNumberView = currentNumberView;
        this.lastPrimeView = lastPrimeView;
        this.context = context;

        if (context instanceof PrimeDirectiveActivity) {
            this.mainActivity = ((PrimeDirectiveActivity) context);
        }
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
                    handler.post(() -> lastPrimeView.setText(context.getString(R.string.last_prime_text, "" + lastPrimeNumber)));
                    mainActivity.updateLastPrimeNumber(lastPrimeNumber);
                }

                // current number post in main handler
                handler.post(() -> currentNumberView.setText(context.getString(R.string.current_number_prime_text, "" + currentNumber)));
                mainActivity.updateCurrentNumber(currentNumber);

                if (Thread.currentThread().isInterrupted()) {
                    Log.d("PrimeRunner", "Is Interrupted");
                    break;
                }
            }
        }
    }
}
