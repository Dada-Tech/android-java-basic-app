package edu.northeastern.numad23fa_daviddada;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class HyperlinkDialog extends DialogFragment {

    private final String dialogTitle;
    private final String dialogLinkTitle;
    private final String dialogLinkUrl;
    HyperlinkDialogListener listener;

    // The activity that creates an instance of this dialog fragment must
    // implement this interface to receive event callbacks. Each method passes
    // the DialogFragment in case the host needs to query it.
    public interface HyperlinkDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String linkTitle, String linkUrl);
    }

    // overload for editing links
    HyperlinkDialog(SimpleLink link) {
        dialogTitle = "Edit";
        dialogLinkTitle = link.title;
        dialogLinkUrl = link.url;
    }

    // for new links
    HyperlinkDialog() {
        dialogTitle = "Create";
        dialogLinkTitle = "";
        dialogLinkUrl = "";
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate the custom layout for the dialog
        View dialogView = inflater.inflate(R.layout.create_link_dialog, null);

        EditText linkNameEditText = dialogView.findViewById(R.id.create_link_field_title);
        EditText linkUrlEditText = dialogView.findViewById(R.id.create_link_field_url);
        TextView dialogTitleText = dialogView.findViewById(R.id.create_link_dialog_title);

        // set view based on dynamic properties
        dialogTitleText.setText(dialogTitle);
        linkNameEditText.setText(dialogLinkTitle);
        linkUrlEditText.setText(dialogLinkUrl);

        // Inflate and set the view and buttons
        builder.setView(dialogView)
                .setPositiveButton("Create", (dialog, id) -> listener.onDialogPositiveClick(HyperlinkDialog.this, linkNameEditText.getText().toString(), linkUrlEditText.getText().toString()))
                .setNegativeButton("Cancel", (dialog, id) -> System.out.println("Cancel pressed"));

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
