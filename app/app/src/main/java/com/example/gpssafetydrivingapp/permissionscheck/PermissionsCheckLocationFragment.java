package com.example.gpssafetydrivingapp.permissionscheck;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gpssafetydrivingapp.databinding.FragmentPermissionsCheckLocationBinding;

public class PermissionsCheckLocationFragment extends Fragment {

    private FragmentPermissionsCheckLocationBinding binding;

    public PermissionsCheckLocationFragment() {
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
        binding = FragmentPermissionsCheckLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonOpenLocationSettings.setOnClickListener(v ->
                requireActivity().requestPermissions(new String[]{ACCESS_BACKGROUND_LOCATION}, 9));
    }
}
