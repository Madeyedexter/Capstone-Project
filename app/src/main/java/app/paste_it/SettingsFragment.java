package app.paste_it;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, PreferenceScreen.OnPreferenceChangeListener,
        DialogPreference.TargetFragment
{


    private static final int RC_SELECT_TIME = 1;
    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().setOnPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if(preference!=null && (preference instanceof ListPreference || preference instanceof EditTextPreference || preference instanceof DialogPreference)){
            preference.setSummary(sharedPreferences.getString(key,null));
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
    public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object newValue) {
        String value = newValue.toString();
        preference.setSummary(value);
        Log.d(TAG,"Preference Changed: "+value);
        return true;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        TimePreferenceDialogFragment fragment;
        if (preference instanceof TimePreference) {
            fragment = TimePreferenceDialogFragment.newInstance(preference.getKey());
            fragment.setTargetFragment(this, RC_SELECT_TIME);
            fragment.show(getFragmentManager(),
                    "android.support.v7.preference.PreferenceFragment.DIALOG");
        }
        else
        super.onDisplayPreferenceDialog(preference);
    }

}
