package com.example.gpssafetydrivingapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.gpssafetydrivingapp.alerts.AlertChecker;
import com.example.gpssafetydrivingapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private SharedPreferences sharedPreferences;
    private WorkManager mWorkManager;
//    private FusedLocationProviderClient fusedLocationClient;

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

    private static boolean makeAlertActiveNotification(Context context) {

        // Make a channel if necessary
        CharSequence name = "Driving Alerts Active";
        String description = "Notification shown when alerts are active";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel =
                new NotificationChannel("ALERTS_ACTIVE", name, importance);
        channel.setDescription(description);

        // Add the channel
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ALERTS_ACTIVE")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Driving Alerts are active")
                .setContentText("")
                .setOngoing(true);

        // Show the notification
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        NotificationManagerCompat.from(context).notify(1, builder.build());
//        NotificationManagerCompat.from(context).
        return true;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {

        switch (key) {
            case "switch_alerts_enable":
                boolean active = sharedPreferences.getBoolean("switch_alerts_enable", false);

                if (active) {
                    makeAlertActiveNotification(getApplicationContext());
                    mWorkManager.enqueue(OneTimeWorkRequest.from(AlertChecker.class));
                } else {
                    NotificationManagerCompat.from(getApplicationContext()).cancel(1);
                    Toast.makeText(this.getApplicationContext(), "Alerts are inactive", Toast.LENGTH_SHORT).show();
                }
        }

    }
}