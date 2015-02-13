package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/*
Simple dialog for change picture button.
Dialog has two options, use camera or gallery to change the photo
 */

public class ChangePicDialogFragment extends DialogFragment {

    // Different dialog IDs
    public static final int DIALOG_ID_PHOTO_PICKER = 1;

    private static final String DIALOG_ID_KEY = "dialog_id";

    public static ChangePicDialogFragment newInstance(int dialog_id) {
        ChangePicDialogFragment frag = new ChangePicDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

        final Activity parent = getActivity();

        switch (dialog_id) {
            case DIALOG_ID_PHOTO_PICKER:

                AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.dialog_camera);

                DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        ((ProfileActivity) parent).changePicDialogPressed(item);
                    }
                };
                // Set the item/s to display and create the dialog
                builder.setItems(R.array.profile_picture_selector, dlistener);

                return builder.create();
            default:
                return null;
        }
    }
}