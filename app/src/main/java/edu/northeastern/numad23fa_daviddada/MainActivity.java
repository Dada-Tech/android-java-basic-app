package edu.northeastern.numad23fa_daviddada;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button aboutMeButton = findViewById(R.id.button_about_me);
        aboutMeButton.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));

        final Button clickyButton = findViewById(R.id.button_clicky);
        clickyButton.setOnClickListener(v -> startActivity(new Intent(this, ButtonGridExampleActivity.class)));

        final Button linkCollectorButton = findViewById(R.id.button_link_collector);
        linkCollectorButton.setOnClickListener(v -> startActivity(new Intent(this, LinkCollectorActivity.class)));

        findViewById(R.id.button_prime_directive).setOnClickListener(v -> startActivity(new Intent(this, PrimeDirectiveActivity.class)));

        findViewById(R.id.button_location_activity).setOnClickListener(v -> startActivity(new Intent(this, LocationActivity.class)));
    }

}