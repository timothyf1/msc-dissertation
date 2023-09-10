package com.example.gpssafetydrivingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.gpssafetydrivingapp.alerts.AlertCheckerService;
import com.example.gpssafetydrivingapp.databinding.ActivityMainBinding;
import com.google.android.material.color.DynamicColors;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DynamicColors.applyToActivityIfAvailable(this);

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
        if (id == R.id.action_check_permissions) {
            navController.navigate(R.id.permissionsCheckFragment);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 7:
                if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    Log.d("Permission Check Result", "Notifications Granted");
                } else {
                    Log.d("Permission Check Result", "Notifications Denied");
                }
                break;
            case 8:
                if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    Log.d("Permission Check Result", "Precise Location Granted");
                } else {
                    Log.d("Permission Check Result", "Precise Location Denied");
                }
                break;
            case 9:
                if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    Log.d("Permission Check Result", "Background Location Granted");
                    navController.popBackStack();
                } else {
                    Log.d("Permission Check Result", "Background Location Denied");
                }
                break;
        }
    }
}
