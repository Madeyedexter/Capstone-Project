package app.paste_it;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import app.paste_it.models.Tag;

public class ConfirmDialogFragment extends DialogFragment {

    public static ConfirmDialogFragment newInstance(Tag tag) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("TAG", tag);
        confirmDialogFragment.setArguments(args);
        return confirmDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Tag tag = getArguments().getParcelable("TAG");
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_dialog_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((YesNoListener) getActivity()).onYes(tag);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((YesNoListener) getActivity()).onNo();
                    }
                })
                .create();
        return alertDialog;
    }

    public interface YesNoListener {
        void onYes(Tag tag);

        void onNo();
    }
}