package com.marcusjacobsson.vault.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.marcusjacobsson.vault.R;

/**
 * Created by Marcus Jacobsson on 2015-08-02.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference nbrOfAttemptsPref = (ListPreference) findPreference(getString(R.string.pref_break_in_nbr_of_attempts_key));
        // Set summary to be the user-description for the selected value
        nbrOfAttemptsPref.setSummary(nbrOfAttemptsPref.getEntry());

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(getString(R.string.pref_break_in_nbr_of_attempts_key))) {
            ListPreference nbrOfAttemptsPref = (ListPreference) findPreference(key);
            // Set summary to be the user-description for the selected value
            nbrOfAttemptsPref.setSummary(nbrOfAttemptsPref.getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
