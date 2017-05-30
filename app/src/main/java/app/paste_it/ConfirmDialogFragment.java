package app.paste_it;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import app.paste_it.models.ConfirmDialogMessage;
import app.paste_it.models.ImageModel;
import app.paste_it.models.Tag;
import app.paste_it.service.ImageImportService;

public class ConfirmDialogFragment extends DialogFragment {

    private static final String TAG = ConfirmDialogFragment.class.getSimpleName();

    private static final String ARG_MESSAGE ="ARG_MESSAGE";
    private static final String ARG_DATA ="ARG_DATA";

    public static ConfirmDialogFragment newInstance(@Nullable Parcelable data, ConfirmDialogMessage message) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        if(data!=null)
            args.putParcelable(ARG_DATA, data);
        args.putParcelable(ARG_MESSAGE,message);
        confirmDialogFragment.setArguments(args);
        return confirmDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Parcelable data = getArguments().getParcelable(ARG_DATA);
        ConfirmDialogMessage message = getArguments().getParcelable(ARG_MESSAGE);
        AlertDialog alertDialog =null;
        if(data instanceof Tag){
            final Tag tag = (Tag)data;
            alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(message.getTitle())
                    .setMessage(message.getMessage())
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

        }
        if(data instanceof ImageModel){
            final ImageModel imageModel = (ImageModel) data;
            alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(message.getTitle())
                    .setMessage(message.getMessage())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ImageImportService.startActionDelete(getContext(),imageModel);
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((YesNoListener) getActivity()).onNo();
                        }
                    })
                    .create();

        }
        return alertDialog;
    }

    public interface YesNoListener {
        void onYes(Parcelable data);
        void onNo();
    }
}