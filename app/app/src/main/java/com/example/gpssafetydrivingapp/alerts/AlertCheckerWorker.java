package com.example.gpssafetydrivingapp.alerts;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class AlertCheckerWorker extends Worker {

    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;
    int count = 100;

    public AlertCheckerWorker(Context appContext, WorkerParameters workerParams) {
        super(appContext, workerParams);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
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

        while (sharedPreferences.getBoolean("switch_alerts_enable", false)) {

            // Check to see if location has been turned off

            // Code to check is location is enabled in if statement
            // This code was adapted from Stack Overflow post by Sunny 2019-09-26
            // accessed 2023-07-07
            // https://stackoverflow.com/a/58109400
            if (! LocationManagerCompat.isLocationEnabled((LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE))) {
                WorkerUtils.makeStatusNotification("Location is unavailable", "Alerts have been turned off", getApplicationContext(), 57);
                AlertChecker.stopAlertChecker(getApplicationContext());
                return Result.failure();
            }

            Task<Location> taskLocation = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_PASSIVE, null);
            fusedLocationClient.flushLocations();

            taskLocation.addOnFailureListener((new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertChecker.stopAlertChecker(getApplicationContext());
                    WorkerUtils.makeStatusNotification("Location failed", "Alerts have been turned off", getApplicationContext(), 57);
                }
            }));

            taskLocation.addOnCompleteListener((new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    String out = count++ + "Latitude: " + location.getLatitude() +
                            " Longitude: " + location.getLongitude() +
                            " Bearing:" + location.getBearing() +
                            " Speed: " + location.getSpeed();

                    WorkerUtils.makeStatusNotification("Location", out, getApplicationContext(), 56);
                }
            }));

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return Result.success();
    }

}

