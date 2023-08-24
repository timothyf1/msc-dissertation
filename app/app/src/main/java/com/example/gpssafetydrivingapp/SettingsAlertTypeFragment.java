package com.example.gpssafetydrivingapp;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsAlertTypeFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.alert_type_preferences, rootKey);
    }
}
