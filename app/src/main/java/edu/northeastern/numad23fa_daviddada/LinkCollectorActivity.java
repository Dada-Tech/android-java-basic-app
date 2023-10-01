package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

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
        if (linkTitle.length() == 0 || linkUrl.length() == 0) {
            Snackbar.make(findViewById(R.id.simple_link_layout), "Link Not Created", Snackbar.LENGTH_SHORT).show();
            return;
        }

        simpleLinksModels.add(new SimpleLink(linkTitle, linkUrl));
        Snackbar snackbar = Snackbar.make(findViewById(R.id.simple_link_layout), "Link Created", Snackbar.LENGTH_SHORT);
        snackbar.setAction("UNDO", new UndoListener());
        snackbar.show();
    }

    public static class UndoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            System.out.println("WILL UNDO");
        }
    }

}
