package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ButtonGridExampleActivity extends AppCompatActivity {
    String defaultPressedText = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_buttons);

        // default pressed text
        setPressedText(defaultPressedText);

        // button logic
        final Button clickyButtonA = findViewById(R.id.clicky_A);
        clickyButtonA.setOnClickListener(v -> setPressedText("A"));

        final Button clickyButtonB = findViewById(R.id.clicky_B);
        clickyButtonB.setOnClickListener(v -> setPressedText("B"));

        final Button clickyButtonC = findViewById(R.id.clicky_C);
        clickyButtonC.setOnClickListener(v -> setPressedText("C"));

        final Button clickyButtonD = findViewById(R.id.clicky_D);
        clickyButtonD.setOnClickListener(v -> setPressedText("D"));

        final Button clickyButtonE = findViewById(R.id.clicky_E);
        clickyButtonE.setOnClickListener(v -> setPressedText("E"));

        final Button clickyButtonF = findViewById(R.id.clicky_F);
        clickyButtonF.setOnClickListener(v -> setPressedText("F"));
    }

    // Set text of Pressed Text View
    void setPressedText(String text) {
        String newTextValue = getText(R.string.pressed) + " " + text + " ";
        final TextView pressedTextView = (TextView) findViewById(R.id.pressed_text);
        pressedTextView.setText(newTextValue);
    }

}
