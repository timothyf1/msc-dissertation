package com.example.gpssafetydrivingapp;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.gpssafetydrivingapp.alerts.AlertCheckerService;
import com.example.gpssafetydrivingapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        // Start alert checker if enabled in preferences
        if (sharedPreferences.getBoolean("switch_alerts_enable", false)) {
            // Check for missing permissions
            if (checkMissingPermissions()) {
                Log.e("AlertChecker", "Missing permissions, not starting alert checker");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("switch_alerts_enable", false);
                editor.commit();
                return;
            }

            AlertCheckerService.startAlertChecker(getApplicationContext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            navController.navigate(R.id.settingsFragment);
            return true;
        }
        if (id == R.id.action_about) {
            navController.navigate(R.id.aboutFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    The
    @Override
    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Code to register and unregister an event listener on preference change
    // This code was adapted from Stack Overflow post by thumbmunkeys 2010-09-26
    // accessed 2023-06-28
    // https://stackoverflow.com/a/3799894
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this::onSharedPreferenceChanged);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this::onSharedPreferenceChanged);
    }
    // End of referenced code
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {

        switch (key) {
            case "switch_alerts_enable":
                boolean active = sharedPreferences.getBoolean("switch_alerts_enable", false);
                Log.d("AlertChecker", "Alerts setting change to " + active);

                if (active) {
                    Log.d("AlertChecker", "Alerts turned on, checking permissions");

                    // Check for missing permissions
                    if (checkMissingPermissions()) {
                        Log.e("AlertChecker", "Missing permissions");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("switch_alerts_enable", false);
                        editor.commit();
                        navController.navigate(R.id.permissionsCheckFragment);
                        return;
                    }

                    Log.d("AlertChecker", "Permissions are granted");

                    AlertCheckerService.startAlertChecker(getApplicationContext());
                } else {
                    AlertCheckerService.stopAlertChecker(getApplicationContext());
                }
        }

    }

    public boolean checkMissingPermissions() {
        ArrayList<String> missingPermissionsAL = new ArrayList<String>();

        // Check for notifications
        if (ContextCompat.checkSelfPermission(getApplicationContext(), POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            Log.d("AlertChecker", "Missing notification permission");
            return true;
        }

        // Check for background location
        if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Log.d("AlertChecker", "Missing background location permission");
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 7:
                if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    Log.d("Permission Check", "Notifications Granted");
                    navController.clearBackStack(R.id.permissionsCheckFragment);
                } else {
                    Log.d("Permission Check", "Notifications Denied");
                }
                break;
            case 8:
                if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    Log.d("Permission Check", "Precise Location Granted");
                    navController.clearBackStack(R.id.permissionsCheckFragment);
                } else {
                    Log.d("Permission Check", "Precise Location Denied");
                }
                break;
            case 9:
                if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    Log.d("Permission Check", "Background Location Granted");
                    navController.popBackStack();
                } else {
                    Log.d("Permission Check", "Background Location Denied");
                }
                break;
        }
    }
}
