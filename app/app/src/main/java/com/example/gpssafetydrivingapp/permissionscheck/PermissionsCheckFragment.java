package com.example.gpssafetydrivingapp.permissionscheck;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gpssafetydrivingapp.R;
import com.example.gpssafetydrivingapp.databinding.FragmentPermissionsCheckBinding;

public class PermissionsCheckFragment extends Fragment {

    private FragmentPermissionsCheckBinding binding;

    public PermissionsCheckFragment() {
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
        binding = FragmentPermissionsCheckBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonGrantNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {POST_NOTIFICATIONS}, 7);
            }
        });

        binding.buttonGrantLocationPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PermissionsCheckFragment.this)
                        .navigate(R.id.action_permissionsCheckFragment_to_permissionsCheckLocationFragment2);
            }
        });

        binding.buttonFinshed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PermissionsCheckFragment.this)
                        .navigate(R.id.homeFragment);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getContext(), POST_NOTIFICATIONS) == PermissionChecker.PERMISSION_GRANTED) {
            Log.d("Permission Check", "Notifications already granted updating text and disable button");
            binding.textViewStatusNot.setText("Granted");
            binding.buttonGrantNotifications.setEnabled(false);
        }

        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_BACKGROUND_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            Log.d("Permission Check", "Background location already granted updating text and disable button");
            binding.textViewStatusLocation.setText("Granted");
            binding.buttonGrantLocationPage.setEnabled(false);
        }

    }
}
