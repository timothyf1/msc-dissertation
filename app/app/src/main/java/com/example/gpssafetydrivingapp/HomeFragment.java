package com.example.gpssafetydrivingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.example.gpssafetydrivingapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    SharedPreferences sharedPreferences;

    private boolean active = false;

    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

//    public static HomeFragment newInstance() {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

//        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        binding.buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment_to_settingsFragment);
            }
        });

        binding.buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment_to_permissionsCheckFragment);
            }
        });

        binding.buttonActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Home Fragment", "Activate/Deactivate button pressed");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (binding.buttonActivate.getText().equals("Deactivate")) {
                    Log.d("Home Fragment", "Turn off alerts");
                    binding.textStatus.setText("Inactive");
                    binding.buttonActivate.setText("Activate");
//                    AlertCheckerService.stopAlertChecker(getContext());
                    editor.putBoolean("switch_alerts_enable", false);
                } else {
                    Log.d("Home Fragment", "Turn on alerts");
                    binding.textStatus.setText("Active");
                    binding.buttonActivate.setText("Deactivate");
                    editor.putBoolean("switch_alerts_enable", true);
                }
                editor.commit();
                Log.d("Home Fragment", "Settings updated");
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        int min_alert_speed = sharedPreferences.getInt("min_alert_speed", 0);
        binding.textViewTest.setText(String.valueOf(min_alert_speed));

        boolean active = sharedPreferences.getBoolean("switch_alerts_enable", false);
        if (active) {
            binding.textStatus.setText("Active");
            binding.buttonActivate.setText("Deactivate");
        } else {
            binding.textStatus.setText("Inactive");
            binding.buttonActivate.setText("Activate");
        }
    }
}
