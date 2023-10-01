package edu.northeastern.numad23fa_daviddada;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.ViewHolder> {

    private final ArrayList<SimpleLink> simpleLinksData;

    // Grabbing views from recycler view row layout. Similar to "OnCreate" Methods
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewUrl;

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

    // Create new views (invoked by the layout manager).
    // This is where to inflate layout and giving the view to each of the items in the list
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.link_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Assigning values to each of the rows based on the position of the recycler view.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewTitle().setText(simpleLinksData.get(position).title);
        viewHolder.getTextViewUrl().setText(simpleLinksData.get(position).url);
    }

    // Return the size of your dataset (invoked by the layout manager)
    // Simple total items. Helps with binding process when updating the views
    @Override
    public int getItemCount() {
        return simpleLinksData.size();
    }
}

