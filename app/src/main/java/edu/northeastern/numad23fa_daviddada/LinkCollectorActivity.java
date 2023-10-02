package edu.northeastern.numad23fa_daviddada;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class LinkCollectorActivity extends AppCompatActivity implements HyperlinkDialog.HyperlinkDialogListener {

    private final ArrayList<SimpleLink> simpleLinksModels = new ArrayList<>();
    private LinkAdapter simpleLinkAdapter;
    private boolean isEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_collector);

        // fab handle click using ImageButton or FloatingActionButton
        final ImageButton newHyperlinkButton = findViewById(R.id.new_hyperlink_url_button);
        newHyperlinkButton.setOnClickListener(v -> showHyperlinkDialog());

        // SimpleLink View
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // Temporary test data
        simpleLinksModels.add(new SimpleLink("test 1", "google.com"));
        simpleLinksModels.add(new SimpleLink("test 2", "facebook.com"));
        simpleLinksModels.add(new SimpleLink("test 3", "daviddada.com"));
        simpleLinksModels.add(new SimpleLink("test 4", "http://wydget.ca"));
        simpleLinksModels.add(new SimpleLink("test 5", "https://runescape.com"));

        // SimpleLink Adapter
        simpleLinkAdapter = new LinkAdapter(simpleLinksModels);
        recyclerView.setAdapter(simpleLinkAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Touch Helper for swipes
        new ItemTouchHelper(new SimpleLinkSwipeCallback()).attachToRecyclerView(recyclerView);

        // Edit callback
        simpleLinkAdapter.setEditClickListener(position -> {
            showHyperlinkDialog(simpleLinksModels.get(position));
        });
    }

    public void showHyperlinkDialog(SimpleLink link) {
        isEditMode = true;
        DialogFragment dialog = new HyperlinkDialog(link);
        dialog.show(getSupportFragmentManager(), "hyperlink_dialog_tag");
    }

    public void showHyperlinkDialog() {
        isEditMode = false;
        DialogFragment dialog = new HyperlinkDialog();
        dialog.show(getSupportFragmentManager(), "hyperlink_dialog_tag");
    }

    // add data to model on success
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String linkTitle, String linkUrl) {
        if (linkTitle.length() == 0 || linkUrl.length() == 0) {
            Snackbar.make(findViewById(R.id.simple_link_layout), isEditMode ? "Cannot update fields to be blank" : "Link Not Created", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!isEditMode) {
            simpleLinksModels.add(new SimpleLink(linkTitle, linkUrl));
            Snackbar snackbar = Snackbar.make(findViewById(R.id.simple_link_layout), "Link Created", Snackbar.LENGTH_SHORT);

            snackbar.setAction("UNDO", (View v) -> {
                if (!simpleLinksModels.isEmpty()) {
                    simpleLinksModels.remove(simpleLinksModels.size() - 1);

                    // notify adapter about change because it doesn't infer automatically
                    simpleLinkAdapter.notifyItemRemoved(simpleLinksModels.size());
                }
            });
            snackbar.show();
        } else {

        }
    }

    // Simple Link ItemTouchHelper
    public class SimpleLinkSwipeCallback extends ItemTouchHelper.SimpleCallback {

        // Swipe left trigger swipe callback, which deletes
        public SimpleLinkSwipeCallback() {
            super(0, ItemTouchHelper.LEFT);
        }

        // ignore onMove for RTL/LTR swipes
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            simpleLinksModels.remove(position);
            simpleLinkAdapter.notifyItemRemoved(position);
        }
    }

}
