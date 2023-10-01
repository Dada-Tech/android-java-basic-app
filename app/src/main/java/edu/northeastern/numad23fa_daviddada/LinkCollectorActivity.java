package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LinkCollectorActivity extends AppCompatActivity implements HyperlinkDialog.HyperlinkDialogListener {

    private final ArrayList<SimpleLink> simpleLinksModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_collector);

        // SimpleLink View
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // SimpleLink Adapter
        LinkAdapter customAdapter = new LinkAdapter(simpleLinksModels);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // fab handle click using ImageButton or FloatingActionButton
        final ImageButton newHyperlinkButton = findViewById(R.id.new_hyperlink_url_button);
        newHyperlinkButton.setOnClickListener(v -> showHyperlinkDialog());
    }

    public void showHyperlinkDialog() {
        // Create an instance of the dialog fragment and show it.
        DialogFragment dialog = new HyperlinkDialog();
        dialog.show(getSupportFragmentManager(), "hyperlink_dialog_tag");
    }

    // add data to model on success
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String linkTitle, String linkUrl) {
        simpleLinksModels.add(new SimpleLink(linkTitle, linkUrl));
    }
}
