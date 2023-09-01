package com.example.gpssafetydrivingapp.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.gpssafetydrivingapp.R;

public class SettingsAdvanceAlertOptionsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.advance_alert_options, rootKey);
    }
}
