package com.example.gpssafetydrivingapp.alerts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.gpssafetydrivingapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.security.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlertCheckerService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationListener locationListener;
    private LocationRequest locationRequest;
    private SharedPreferences sharedPreferences;

    private Alerts alerts;
    private Alert lastAlert;
    private long lastAlertTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationRequest = new LocationRequest.Builder(4000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        locationListener = this::checkLocationAlert;

        makeAlertActiveNotification();
        
        Log.d("AlertCheckerService", "Loading alert points");
        alerts = loadAlertPoints();
        Log.d("AlertCheckerService", alerts.getNumberOfAlerts() + " alert points loaded");
    }

    private void makeAlertActiveNotification() {
        // Make a channel if necessary
        CharSequence name = "Driving Alerts Active";
        String description = "Notification shown when alerts are active";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel =
                new NotificationChannel("ALERTS_ACTIVE", name, importance);
        channel.setDescription(description);

        // Add the channel
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        // Intent for stop button on notification
        Intent stopAlertIntent = new Intent(getApplicationContext(), AlertStopActionReceiver.class);
        stopAlertIntent.putExtra("action", "stopAlerts");
        PendingIntent stopAlertPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopAlertIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "ALERTS_ACTIVE")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Driving Alerts are active")
                .setContentText("")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .addAction(0, "Stop Alerts", stopAlertPendingIntent)
                .build();

        // Show the notification
        startForeground(1, notification);
    }

    private Alerts loadAlertPoints() {
        String myJson = inputStreamToString(getApplicationContext().getResources().openRawResource(R.raw.alerts_silchester));
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AlertCheckerService", "Starting AlertCheckerService");
        fusedLocationClient.requestLocationUpdates(locationRequest, locationListener, Looper.getMainLooper());
        Log.d("AlertCheckerService", "AlertCheckerService Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("AlertCheckerService", "Stopping AlertCheckerService");
        fusedLocationClient.removeLocationUpdates(locationListener);
        Log.d("AlertCheckerService", "AlertChecker Stopped");
    }

    public void checkLocationAlert (Location location) {
        Log.d("AlertCheckerService", "Location Received" + location.toString());

        Alert nearestAlert = alerts.findNearest(location.getLatitude(), location.getLongitude(), 40);

        // Check to see if there is a alert found
        if (nearestAlert == null) {
            Log.d("AlertCheckerService", "Could not alert point within 40 meters");
            return;
        }
        Log.d("AlertCheckerService", "Nearest Alert id: " + nearestAlert.getId());

        // Check to see if the alert type is active
        if (!checkAlertTypeSettings(nearestAlert.getAlertType())) {
            Log.d("AlertCheckerService", "Alert type " + nearestAlert.getAlertType() + " is currently disabled");
            return;
        }

        // Check the direction of travel to the direction of the alert
        if (!nearestAlert.checkBearing(location.getBearing())) {
            Log.d("AlertCheckerService", "Bearing is invalid for alert point");
            return;
        }

        // Check against last alert and its timing
        if (checkLastAlert(nearestAlert)) {
            Log.d("AlertCheckerService", "Nearest Alert is the last activated alert within 60 seconds. Skipping making alert");
            return;
        }

        lastAlertTime = System.currentTimeMillis();
        lastAlert = nearestAlert;
        createAlert(nearestAlert);
    }

    private boolean checkAlertTypeSettings(int alertType) {
        String alertTypeSetting = "switch_alert_type_" + alertType;
        boolean alertTypeActive = sharedPreferences.getBoolean(alertTypeSetting, false);
        boolean allAlertTypes = sharedPreferences.getBoolean("switch_all_alerts", true);

        return allAlertTypes || alertTypeActive;
    }

    private boolean checkLastAlert(Alert candidateAlert) {
        if (lastAlert == candidateAlert) {
            long timeDifference = System.currentTimeMillis() - lastAlertTime;
            return timeDifference < 60000;
        }
        return false;
    }

    private void createAlert(Alert alertPoint) {
        Log.d("AlertCheckerService", "Alert type: " + alertPoint.getAlertType());

        String alertText;
        String driveOn = "left";

        switch (alertPoint.getAlertType()) {
            case 10:
                alertText = "Caution, the road becomes wider ahead. Keep " + driveOn;
                break;
            case 20:
                alertText = "Caution, junction ahead with wider road. Keep " + driveOn;
                break;
            default:
                return;
        }

        Log.d("AlertCheckerService", "Alert text: " + alertText);

        Log.d("AlertCheckerService", "Creating alert notification");

        WorkerUtils.makeStatusNotification("Danger Ahead", alertText, getApplicationContext(), 667);
    }

    public static void startAlertChecker(Context context) {
        Log.d("AlertChecker", "Starting alert checker");
        Intent alertServiceIntent = new Intent(context, AlertCheckerService.class);
        context.startForegroundService(alertServiceIntent);
        Log.d("AlertChecker", "Alert checker started");
    }

    public static void stopAlertChecker(Context context) {
        Log.d("AlertChecker", "Stopping alert checker");
        Intent alertServiceIntent = new Intent(context, AlertCheckerService.class);
        context.stopService(alertServiceIntent);
        Log.d("AlertChecker", "Alert checker stopped");
    }
}
