package com.example.gpssafetydrivingapp.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.gpssafetydrivingapp.R;
import com.example.gpssafetydrivingapp.alerts.AlertCheckerService;

public class SettingsFragment extends PreferenceFragmentCompat {

    NavController navController;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        navController = Navigation.findNavController(view);

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

        SwitchPreference alertSwitch = getPreferenceManager().findPreference("switch_alerts_enable");
        assert alertSwitch != null;
        alertSwitch.setOnPreferenceClickListener(preference -> {
            Log.d("Settings", "alert switch clicked");
            if (sharedPreferences.getBoolean("switch_alerts_enable", false)) {
                AlertCheckerService.startAlertChecker(requireContext());
            } else {
                AlertCheckerService.stopAlertChecker(requireContext());
            }
            return false;
        });
    }
}
