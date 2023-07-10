package com.example.gpssafetydrivingapp.alerts;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.gpssafetydrivingapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AlertCheckerWorker extends Worker {

    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationManager locationManager;
    private final SharedPreferences sharedPreferences;
    private final Context context;
    private Alerts alerts;
    int count = 100;
    int nullLocationCount;

    public AlertCheckerWorker(Context appContext, WorkerParameters workerParams) {
        super(appContext, workerParams);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        context = appContext;
    }

    @NonNull
    @Override
    public Result doWork() {
//        Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_SHORT);
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return Result.failure();
        }

        nullLocationCount = 0;

        Log.d("AlertCheckerWorker", "Loading alert points");
        alerts = loadAlertPoints();
        Log.d("AlertCheckerWorker", alerts.getNumberOfAlerts() + " alert points loaded");

        while (sharedPreferences.getBoolean("switch_alerts_enable", false)) {

            // Code to check is location is enabled in if statement
            // This code was adapted from Stack Overflow post by Sunny 2019-09-26
            // accessed 2023-07-07
            // https://stackoverflow.com/a/58109400
            if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
                WorkerUtils.makeStatusNotification("Location is unavailable", "Alerts have been turned off", getApplicationContext(), 57);
                Log.e("AlertCheckerWorker", "Location is unavailable");
                return Result.failure();
            }
            Log.d("AlertCheckerWorker", "Location Enabled");

            // Run an alert check
            alertCheck();

            // Timer before next location check
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return Result.success();
    }

    private Alerts loadAlertPoints() {
        String myJson = inputStreamToString(context.getResources().openRawResource(R.raw.alerts_silchester));
        return new Gson().fromJson(myJson, Alerts.class);
    }

    // https://stackoverflow.com/a/45177069
    private String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    private boolean alertCheck() {

        LocationListener locationListener = location -> {};
        Looper looper = Looper.getMainLooper();

        // Force location update
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener, looper);

        Task<Location> taskLocation = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, null);

        taskLocation.addOnCompleteListener(this::onLocationAcquired);

        return true;
    }

    private void onLocationAcquired(@NonNull Task<Location> task) {
        Location location = task.getResult();
        Log.d("AlertCheckerWorker", "Running Location complete");

        if (location == null) {
            nullLocationCount++;
            Log.e("AlertCheckerWorker", "Location is null, current null location count " + nullLocationCount);
            if (nullLocationCount > 10) {
                AlertChecker.stopAlertChecker(getApplicationContext());
            }
            return;
        }
        Log.d("AlertCheckerWorker", count++ + " " + location.getTime() +
                " Latitude: " + location.getLatitude() +
                " Longitude: " + location.getLongitude() +
                " Bearing:" + location.getBearing() +
                " Speed: " + location.getSpeed());

        Alert nearestAlert = alerts.findNearest(location.getLatitude(), location.getLongitude(), 40);

        // Check to see if there is a alert found
        if (Objects.equals(nearestAlert.getId(), "-1")) {
            Log.d("AlertCheckerWorker", "Could not alert point within 40 meters");
            return;
        }
        Log.d("AlertCheckerWorker", "Nearest Alert id: " + nearestAlert.getId());

        // Check the direction of travel to the direction of the alert
        if (!nearestAlert.checkBearing(location.getBearing())) {
            Log.d("AlertCheckerWorker", "Bearing is invalid for alert point");
            return;
        }
        Log.d("AlertCheckerWorker", "Bearing is valid for alert point");


    }

}
