package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ButtonGridExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button aboutMeButton = findViewById(R.id.button_about_me);
        aboutMeButton.setOnClickListener(v -> Toast.makeText(v.getContext(), "David Dada\ndada.d@northeastern.edu", Toast.LENGTH_SHORT).show());
    }

}
