package com.example.gpssafetydrivingapp.alerts;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class AlertCheckerWorker extends Worker {

    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;

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
            Task<Location> taskLocation = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, null);

            taskLocation.addOnCompleteListener((new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    String out = "Latitude: " + location.getLatitude() +
                            " Longitude" + location.getLongitude() +
                            " Bearing" + location.getBearing() +
                            " Speed" + location.getSpeed();

                    WorkerUtils.makeStatusNotification(out, getApplicationContext(), 56);
                }
            }));

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return Result.success();
    }

}

