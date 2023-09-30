package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class LinkCollectorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_collector);

        final String[] data = {"hello", "world", "my", "name", "is", "slim", "shady"};

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinkAdapter customAdapter = new LinkAdapter(data);
        recyclerView.setAdapter(customAdapter);
    }

}
