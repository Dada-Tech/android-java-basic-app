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
    private int editingPos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_collector);

        // fab handle click using ImageButton or FloatingActionButton
        final ImageButton newHyperlinkButton = findViewById(R.id.new_hyperlink_url_button);
        newHyperlinkButton.setOnClickListener(v -> showHyperlinkDialog());

        // SimpleLink View
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // SimpleLink Adapter
        simpleLinkAdapter = new LinkAdapter(simpleLinksModels);
        recyclerView.setAdapter(simpleLinkAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Touch Helper for swipes
        new ItemTouchHelper(new SimpleLinkSwipeCallback()).attachToRecyclerView(recyclerView);

        // Edit callback
        simpleLinkAdapter.setEditClickListener(position ->
                showHyperlinkDialog(position, simpleLinksModels.get(position)));

        // restore saved state if exists
        if (savedInstanceState != null) {
            ArrayList<SimpleLink> savedLinks = savedInstanceState.getParcelableArrayList("simpleLinksModels");
            if (savedLinks != null) {
                simpleLinksModels.clear();
                simpleLinksModels.addAll(savedLinks);
            }
        }
    }

    public void showHyperlinkDialog(int position, SimpleLink link) {
        isEditMode = true;
        editingPos = position;
        DialogFragment dialog = new HyperlinkDialog(link);
        dialog.show(getSupportFragmentManager(), "hyperlink_dialog_tag");
    }

    public void showHyperlinkDialog() {
        isEditMode = false;
        editingPos = -1;
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
                    // always at size cause you always add to the end. You are passing in pos
                    simpleLinkAdapter.notifyItemRemoved(simpleLinksModels.size());
                }
            });
            snackbar.show();
        } else {
            SimpleLink oldChange = simpleLinksModels.get(editingPos);
            SimpleLink newChange = new SimpleLink(linkTitle, linkUrl);

            simpleLinksModels.set(editingPos, newChange);
            simpleLinkAdapter.notifyItemChanged(editingPos); // notify adaptor

            Snackbar snackbar = Snackbar.make(findViewById(R.id.simple_link_layout), "Link Edited", Snackbar.LENGTH_SHORT);

            snackbar.setAction("UNDO", (View v) -> {
                simpleLinksModels.set(editingPos, oldChange);
                // notify adapter about change because it doesn't infer automatically on edits
                simpleLinkAdapter.notifyItemChanged(editingPos);
            });
            snackbar.show();
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

    // Save only model, the rest can be recreated
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("simpleLinksModels", simpleLinksModels);
    }

}
