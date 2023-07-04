package com.example.gpssafetydrivingapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.gpssafetydrivingapp.alerts.AlertChecker;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.gpssafetydrivingapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private SharedPreferences sharedPreferences;
    private WorkManager mWorkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        mWorkManager = WorkManager.getInstance(getApplicationContext());
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    The
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
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

                if (active) {
                    mWorkManager.enqueue(OneTimeWorkRequest.from(AlertChecker.class));
                } else {
                    Toast.makeText(this.getApplicationContext(), "Alerts are inactive", Toast.LENGTH_SHORT).show();
                }
        }

    }
}