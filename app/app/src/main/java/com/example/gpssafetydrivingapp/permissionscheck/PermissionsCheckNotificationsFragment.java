package com.example.gpssafetydrivingapp.permissionscheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.gpssafetydrivingapp.databinding.FragmentPermissionsCheckNotificationsBinding;

public class PermissionsCheckNotificationsFragment extends Fragment {

    private FragmentPermissionsCheckNotificationsBinding binding;

    public PermissionsCheckNotificationsFragment() {
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
        binding = FragmentPermissionsCheckNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonGrantNotification.setOnClickListener(v -> {
            String[] permissionRequest = {"POST_NOTIFICATIONS"};
            ActivityCompat.requestPermissions(getActivity(), permissionRequest, 5);
        });
    }
}
