package com.hamzalive.botton;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener

{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.preference_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.preference_units_key)));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String sValue = newValue.toString();
        if (preference instanceof ListPreference)
        {
            ListPreference listPref = (ListPreference) preference;
            int index = listPref.findIndexOfValue(sValue);
            if ( index >= 0 )
                preference.setSummary(listPref.getEntries()[index]);
        } else {
            preference.setSummary(sValue);
        }
        return true;
    }

    private void bindPreferenceSummaryToValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
