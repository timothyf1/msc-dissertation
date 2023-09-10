package com.example.gpssafetydrivingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.example.gpssafetydrivingapp.alerts.AlertCheckerService;
import com.example.gpssafetydrivingapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    NavController navController;
    SharedPreferences sharedPreferences;

    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

//        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.requireActivity());
        navController = Navigation.findNavController(view);

        binding.buttonSettings.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_homeFragment_to_settingsFragment));

        binding.buttonCheckPermissions.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_homeFragment_to_permissionsCheckFragment));

        binding.buttonActivate.setOnClickListener(v -> {
            Log.d("Home Fragment", "Activate/Deactivate button pressed");

            if (binding.buttonActivate.getText().equals("Deactivate")) {
                Log.d("Home Fragment", "Turn off alerts");
                binding.textStatus.setText(R.string.inactive);
                binding.buttonActivate.setText(R.string.activate);
                AlertCheckerService.stopAlertChecker(requireContext());
            } else {
                Log.d("Home Fragment", "Turn on alerts");
                binding.textStatus.setText(R.string.active);
                binding.buttonActivate.setText(R.string.deactivate);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("switch_alerts_enable", true);
                editor.commit();
                AlertCheckerService.startAlertChecker(requireContext());
            }
            Log.d("Home Fragment", "Settings updated");
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        boolean active = sharedPreferences.getBoolean("switch_alerts_enable", false);
        if (active) {
            binding.textStatus.setText(R.string.active);
            binding.buttonActivate.setText(R.string.deactivate);
        } else {
            binding.textStatus.setText(R.string.inactive);
            binding.buttonActivate.setText(R.string.activate);
        }
    }
}
