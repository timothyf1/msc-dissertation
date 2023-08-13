package com.example.gpssafetydrivingapp.permissionscheck;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPermissionsCheckLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonGrantLocation.setOnClickListener(v -> {
            String[] permissionRequest = {"ACCESS_BACKGROUND_LOCATION"};
            ActivityCompat.requestPermissions(getActivity(), permissionRequest, 6);
        });
    }
}
