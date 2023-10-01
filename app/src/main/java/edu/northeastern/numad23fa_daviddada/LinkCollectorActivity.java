package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

public class LinkCollectorActivity extends AppCompatActivity implements HyperlinkDialog.HyperlinkDialogListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_collector);

        final String[] data = {"hello", "world", "my", "name", "is", "slim", "shady"};

        // recycler view
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinkAdapter customAdapter = new LinkAdapter(data);
        recyclerView.setAdapter(customAdapter);

        // fab handle click
        // ImageButton FloatingActionButton
        final ImageButton newHyperlinkButton = findViewById(R.id.new_hyperlink_url_button);
        newHyperlinkButton.setOnClickListener(v -> showHyperlinkDialog());
    }

    public void showHyperlinkDialog() {
        // Create an instance of the dialog fragment and show it.
        DialogFragment dialog = new HyperlinkDialog();
        dialog.show(getSupportFragmentManager(), "hyperlink_dialog_tag");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String linkTitle, String linkUrl) {
        System.out.println(">>>>>>>>>>>>> LinkCollector Positive Callback");
        System.out.println(linkTitle);
        System.out.println(linkUrl);
    }
}
