package com.example.gpssafetydrivingapp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference alertTypePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alertTypePreference = getPreferenceManager().findPreference("alert_types");

        assert alertTypePreference != null;
        alertTypePreference.setOnPreferenceClickListener(preference -> {
            Navigation.findNavController(view).navigate(R.id.settingsAlertTypeFragment);
            return false;
        });
    }
}
