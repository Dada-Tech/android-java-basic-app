package edu.northeastern.numad23fa_daviddada;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

class LocationRunner implements Runnable {
    Handler handler;

    private final int n_skips;

    private int currentNumber;
    private int lastLocationNumber;

    private final TextView currentNumberView;
    private final TextView lastLocationView;

    private final Context context;

    private LocationActivity mainActivity;

    LocationRunner(Context context, Handler handler, TextView currentNumberView, TextView lastLocationView) {
        this(context, handler, currentNumberView, lastLocationView, 3, 3);
    }

    LocationRunner(Context context, Handler handler, TextView currentNumberView, TextView lastLocationView, int currentNumber, int lastLocationNumber) {
        this.handler = handler;
        this.currentNumber = currentNumber;
        this.lastLocationNumber = lastLocationNumber;
        n_skips = 20000;

        this.currentNumberView = currentNumberView;
        this.lastLocationView = lastLocationView;
        this.context = context;

        if (context instanceof LocationActivity) {
            this.mainActivity = ((LocationActivity) context);
        }
    }

    private boolean isLocation(int number) {
        return number % 2 == 0;
    }

    @Override
    public void run() {
        int skipCount = 0;

        while (!Thread.currentThread().isInterrupted()) {
            if (++skipCount % n_skips == 0) {
                skipCount = 0;
                currentNumber += 2;

                // if location, update
                if (isLocation(currentNumber)) {
                    lastLocationNumber = currentNumber;

                    // last location post in main handler
                    handler.post(() -> lastLocationView.setText(context.getString(R.string.total_distance_travelled, "" + lastLocationNumber)));
                    mainActivity.updateLatitude(lastLocationNumber);
                }

                // current number post in main handler
                handler.post(() -> currentNumberView.setText(context.getString(R.string.latitude, "" + currentNumber)));
                mainActivity.updateLongitude(currentNumber);

                if (Thread.currentThread().isInterrupted()) {
                    Log.d("LocationRunner", "Is Interrupted");
                    break;
                }
            }
        }
    }
}
