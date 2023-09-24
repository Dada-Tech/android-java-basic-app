package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final TextView aboutTextView = findViewById(R.id.about_me_text_view);
        aboutTextView.setText(R.string.personal_info);
    }

}
