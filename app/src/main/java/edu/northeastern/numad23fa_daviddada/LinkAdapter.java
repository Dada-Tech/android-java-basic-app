package edu.northeastern.numad23fa_daviddada;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.ViewHolder> {

    private final ArrayList<SimpleLink> simpleLinksData;
    private EditClickListener editClickListener;

    interface EditClickListener {
        void onEditClick(int position);
    }

    public void setEditClickListener(EditClickListener listener) {
        this.editClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewUrl;

        // Grabbing views from recycler view row layout. Similar to "OnCreate" Methods
        public ViewHolder(View view) {
            super(view);
            // grab views and store them
            textViewTitle = view.findViewById(R.id.simple_link_textview_title);
            textViewUrl = view.findViewById(R.id.simple_link_textview_url);
        }

        // public getters for better practice than touching class variables directly
        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        // public getters for better practice than touching class variables directly
        public TextView getTextViewUrl() {
            return textViewUrl;
        }
    }

    // dataset init
    public LinkAdapter(ArrayList<SimpleLink> dataSet) {
        this.simpleLinksData = dataSet;
    }

    // This is where to inflate layout and giving the view to each of the items in the list
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // new view for each UI list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.link_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Assigning values to each of the rows based on the position of the recycler view.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewTitle().setText(simpleLinksData.get(position).title);
        viewHolder.getTextViewUrl().setText(simpleLinksData.get(position).url);
        View cardView = viewHolder.itemView;

        // regular click listener
        cardView.setOnClickListener(v -> {
            try {
                String url = simpleLinksData.get(viewHolder.getAdapterPosition()).url;
                String urlLink = url.startsWith("http://") || url.startsWith("https://") ?
                        url : "http://" + url;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink));
                v.getContext().startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make((View) cardView.getParent(), "Unable to Open URL; likely malformed", Snackbar.LENGTH_SHORT).show();
            }
        });

        // long click listener. Leveraging a custom callback so logic is implemented in the activity
        cardView.setOnLongClickListener(v -> {
            int updatedPosition = viewHolder.getAdapterPosition();
            editClickListener.onEditClick(updatedPosition);
            return true;
        });
    }

    // Simple total items. Helps with binding process when updating the views
    @Override
    public int getItemCount() {
        return simpleLinksData.size();
    }
}

