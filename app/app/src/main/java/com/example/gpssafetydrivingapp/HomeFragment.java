package com.example.gpssafetydrivingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.example.gpssafetydrivingapp.databinding.FragmentHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SharedPreferences sharedPreferences;

    private boolean active = false;

    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                        .navigate(R.id.action_homeFragment_to_alertHistoryFragment);
            }
        });

        binding.buttonActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (active) {
                    binding.textStatus.setText("Inactive");
                    binding.buttonActivate.setText("Activate");
                    active = false;
                    editor.putBoolean("switch_alerts_enable", false);
//                    Toast.makeText(getActivity().getApplicationContext(), "Alerts are now inactive", Toast.LENGTH_SHORT).show();
                } else {
                    binding.textStatus.setText("Active");
                    binding.buttonActivate.setText("Deactivate");
                    active = true;
                    editor.putBoolean("switch_alerts_enable", true);
//                    Toast.makeText(getActivity().getApplicationContext(), "Alerts are now active", Toast.LENGTH_SHORT).show();
                }
                editor.commit();
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