package app.paste_it;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

/**
 * Created by Madeyedexter on 28-05-2017.
 */

public class TimePreferenceDialogFragment extends PreferenceDialogFragmentCompat {

    TimePicker timePicker;


    private static final String ARG_PREFERENCE_KEY = "ARG_PREFERENCE_KEY";

    public static TimePreferenceDialogFragment newInstance(String preferenceKey){
        TimePreferenceDialogFragment timePreferenceDialogFragment = new TimePreferenceDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PREFERENCE_KEY,preferenceKey);
        timePreferenceDialogFragment.setArguments(bundle);
        return timePreferenceDialogFragment;
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        timePicker = new TimePicker(getContext());

        String key = getArguments().getString(ARG_PREFERENCE_KEY);
        String time = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(key,null);

        timePicker.setCurrentHour(TimePreference.getHour(time));
        timePicker.setCurrentMinute(TimePreference.getMinute(time));
        return timePicker;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }
/*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.set_time)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        return alertDialog;
    }*/



}
