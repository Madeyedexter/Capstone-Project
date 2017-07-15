package app.paste_it;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;

import app.paste_it.receivers.PasteItNotification;
import app.paste_it.service.NotificationService;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener,
        DialogPreference.TargetFragment
{


    private static final int RC_SELECT_TIME = 1;
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private static final String SHOW_NOTIFICATION_KEY="pref_key_show_notification";
    private static final String KEY_NOTIFICATION_DAY="pref_key_notification_day";
    private static final String KEY_NOTIFICATION_TIME="pref_key_notification_time";
    private static final String NOTIFICATION_JOB_TAG = "unique-tag";

    private Toast toast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);

        for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
            updateSummary(getPreferenceScreen().getPreference(i));
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        String dependencyKey = preference.getDependency()!=null?preference.getDependency():preference.getKey();
        if(sharedPreferences.getBoolean(dependencyKey,false) && dependencyKey.equals(SHOW_NOTIFICATION_KEY)){
            //TODO: show notificatin at specific time and day
            showToast("Notifications Enabled.");
        }
        else{
            //TODO: disable notification.
            showToast("Notifications Disabled.");
        }
        updateSummary(preference);
    }

    private void updateSummary(Preference preference){
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        if(preference instanceof PreferenceCategory){
            PreferenceCategory preferenceCategory = (PreferenceCategory)preference;
            for(int i=0;i<preferenceCategory.getPreferenceCount();i++){
                updateSummary(preferenceCategory.getPreference(i));
            }
        }
        if(preference!=null && (preference instanceof ListPreference || preference instanceof EditTextPreference || preference instanceof DialogPreference)){
            preference.setSummary(sharedPreferences.getString(preference.getKey(),null));
        }
        if(preference instanceof TimePreference){
            preference.setSummary(sharedPreferences.getString(preference.getKey(),"00:00"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        TimePreferenceDialogFragment fragment;
        if (preference instanceof TimePreference) {
            fragment = TimePreferenceDialogFragment.newInstance(preference.getKey());
            fragment.setTargetFragment(this, RC_SELECT_TIME);
            fragment.show(getChildFragmentManager(),"TAG");
        }
        else
        super.onDisplayPreferenceDialog(preference);
    }

    private void showToast(String message){
        if(toast!=null){
            toast.cancel();
        }
        toast = Toast.makeText(getContext(),message,Toast.LENGTH_SHORT);
        toast.show();
    }

}
