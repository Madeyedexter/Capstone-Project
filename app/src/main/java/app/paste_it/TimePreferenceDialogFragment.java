package app.paste_it;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import java.sql.Time;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Madeyedexter on 28-05-2017.
 */

public class TimePreferenceDialogFragment extends PreferenceDialogFragmentCompat {
    private static final String ARG_PREFERENCE_KEY = "key";


    private TimePicker timePicker;

    private TimePreference timePreference;

    public static TimePreferenceDialogFragment newInstance(String preferenceKey){
        TimePreferenceDialogFragment timePreferenceDialogFragment = new TimePreferenceDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PREFERENCE_KEY,preferenceKey);
        timePreferenceDialogFragment.setArguments(bundle);
        return timePreferenceDialogFragment;
    }

    private int getHour(String timeString){
        return Integer.parseInt(timeString.split(":")[0].trim());
    }

    private int getMinute(String timeString){
        return Integer.parseInt(timeString.split(":")[1].trim());
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    protected View onCreateDialogView(Context context) {
        View rootView = super.onCreateDialogView(context);
        timePicker = (TimePicker) rootView;

        DialogPreference.TargetFragment targetFragment = (DialogPreference.TargetFragment) getTargetFragment();

        String key = getArguments().getString(ARG_PREFERENCE_KEY);
        timePreference = (TimePreference) targetFragment.findPreference(key);
        String time = timePreference.getSummary().toString();

        timePicker.setCurrentHour(getHour(time));
        timePicker.setCurrentMinute(getMinute(time));
        timePicker.setSaveEnabled(true);
        timePicker.setIs24HourView(true);
        return rootView;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if(positiveResult){
            DialogPreference.TargetFragment targetFragment = (DialogPreference.TargetFragment) getTargetFragment();
            String key = getArguments().getString(ARG_PREFERENCE_KEY);
            timePreference = (TimePreference) targetFragment.findPreference(key);
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            timePreference.persistTime(getString(R.string.time_string,hour,minute));
        }
    }


}
