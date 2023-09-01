package com.example.gpssafetydrivingapp.settings;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.gpssafetydrivingapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Preference alertTypePreference = getPreferenceManager().findPreference("alert_types");

        assert alertTypePreference != null;
        alertTypePreference.setOnPreferenceClickListener(preference -> {
            Navigation.findNavController(view).navigate(R.id.settingsAlertTypeFragment);
            return false;
        });

        Preference advancePreference = getPreferenceManager().findPreference("advance_options");

        assert advancePreference != null;
        advancePreference.setOnPreferenceClickListener(preference -> {
            Navigation.findNavController(view).navigate(R.id.settingsAdvanceAlertOptionsFragment);
            return false;
        });

    }
}
