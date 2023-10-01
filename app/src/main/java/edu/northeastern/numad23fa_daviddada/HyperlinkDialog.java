package edu.northeastern.numad23fa_daviddada;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class HyperlinkDialog extends DialogFragment {

    // The activity that creates an instance of this dialog fragment must
    // implement this interface to receive event callbacks. Each method passes
    // the DialogFragment in case the host needs to query it.
    public interface HyperlinkDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String linkTitle, String linkUrl);
    }

    // Use this instance of the interface to deliver action events.
    HyperlinkDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater.
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate the custom layout for the dialog.
        View dialogView = inflater.inflate(R.layout.create_link_dialogue, null);

        // dialog text fields
        EditText linkNameEditText = dialogView.findViewById(R.id.create_link_field_title);
        EditText linkUrlEditText = dialogView.findViewById(R.id.create_link_field_url);

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because it's going in the dialog layout.
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Create", (dialog, id) -> {
                    listener.onDialogPositiveClick(HyperlinkDialog.this, linkNameEditText.getText().toString(), linkUrlEditText.getText().toString());
                })
                .setNegativeButton("Cancel",
                        (dialog, id) -> {
                            System.out.println("Cancel pressed");
                        }
                );
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the HyperlinkDialogListener.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface.
        try {
            // Instantiate the HyperlinkDialogListener so you can send events to
            // the host.
            listener = (HyperlinkDialogListener) context;

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface. Throw exception.
            throw new ClassCastException(String.format("%s must implement HyperlinkDialogListener", super.getActivity()));
        }
    }
}
