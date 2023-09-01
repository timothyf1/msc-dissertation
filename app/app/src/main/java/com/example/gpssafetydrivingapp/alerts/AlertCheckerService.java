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
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;

public class AlertCheckerService extends Service {

    private final int DISTANCE_FROM_ALERT = 40;
    private final int TIME_BETWEEN_LOCATION_REQUESTS = 4000;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationListener locationListener;
    private LocationRequest locationRequest;
    private SharedPreferences sharedPreferences;
    private TextToSpeech textToSpeech;

    private Alerts alerts;
    private long[] lastAlertTimes;

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
        locationRequest = new LocationRequest.Builder(TIME_BETWEEN_LOCATION_REQUESTS)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        locationListener = this::checkLocationAlert;
        textToSpeech = new TextToSpeech(getApplicationContext(), i -> {
            // if No error is found then only it will run
            if(i!=TextToSpeech.ERROR){
                // To Choose language of speech
                textToSpeech.setLanguage(Locale.UK);
            }
        });

        // Create and show the notification for the service
        makeAlertActiveNotification();
        
        // Load alert points from JSON file
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
        PendingIntent stopAlertPendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                0,
                stopAlertIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

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

    /**
     * Method to read JSON files
     * Adapted from stackoverflow post by faraz khonsari 2020-11-04
     * https://stackoverflow.com/a/45177069
     * Accessed 2023-07-10
     * @return Alerts object containing the alerts
     */
    private Alerts loadAlertPoints() {
        String myJson = inputStreamToString(
                getApplicationContext().getResources().openRawResource(R.raw.alerts_silchester)
        );
        return new Gson().fromJson(myJson, Alerts.class);
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }
    /** End of referenced code */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AlertCheckerService", "Starting AlertCheckerService");
        // Note permissions checks are done before the service starts
        fusedLocationClient.requestLocationUpdates(locationRequest, locationListener, Looper.getMainLooper());
        Log.d("AlertCheckerService", "Location updates requested");

        lastAlertTimes = new long[100];
        return START_STICKY;
    }

    // Runs when stopping the service to remove the location updates
    @Override
    public void onDestroy() {
        Log.d("AlertCheckerService", "Stopping AlertCheckerService");
        fusedLocationClient.removeLocationUpdates(locationListener);
        Log.d("AlertCheckerService", "Location updates removed");
    }

    /**
     * Method to check if there is a valid alert for the given location.
     * @param location the location to be checked for an alert
     */
    public void checkLocationAlert (Location location) {
        Log.d("AlertCheckerService", "Location Received" + location.toString());

        Alert nearestAlert = alerts.findNearest(
                location.getLatitude(),
                location.getLongitude(),
                DISTANCE_FROM_ALERT
        );

        // Check to see if there is a alert found
        if (nearestAlert == null) {
            Log.d(
                    "AlertCheckerService",
                    "Could not alert point within " + DISTANCE_FROM_ALERT + " meters");
            return;
        }
        Log.d("AlertCheckerService", "Nearest Alert id: " + nearestAlert.getId());

        // Check to see if the alert type is active
        if (!checkAlertTypeSettings(nearestAlert.getAlertType())) {
            Log.d(
                    "AlertCheckerService",
                    "Alert type " + nearestAlert.getAlertType() + " is currently disabled"
            );
            return;
        }

        // Check the direction of travel to the direction of the alert
        if (!nearestAlert.checkBearing(location.getBearing())) {
            Log.d("AlertCheckerService", "Bearing is invalid for alert point");
            return;
        }

        // Get speed setting and convert to mph
        int speedMin = sharedPreferences.getInt("min_alert_speed", 0);
        // Check speed
        if (location.getSpeed() < (speedMin * 0.44707)) {
            Log.d(
                    "AlertCheckerService",
                    "Speed below minimal speed of " + speedMin + " mph."
            );
            return;
        }

        // Check against last alert and its timing
        int timeSeconds = sharedPreferences.getInt("min_time_between_alerts", 40);
        if (checkLastAlertTime(nearestAlert, timeSeconds)) {
            Log.d(
                    "AlertCheckerService",
                    "Alert type last activated within "
                            + timeSeconds + " seconds. Skipping making alert"
            );
            return;
        }

        // New valid alert updating variables related to last found alert
        lastAlertTimes[nearestAlert.getAlertType()] = System.currentTimeMillis();

        createAlert(nearestAlert);
    }

    /**
     * Method to check if an alert type is active
     * @param alertType An integer define the alert type which is to be checked
     * @return boolean true if the alert type is currently active
     */
    private boolean checkAlertTypeSettings(int alertType) {
        String alertTypeSetting = "switch_alert_type_" + alertType;
        boolean alertTypeActive = sharedPreferences.getBoolean(alertTypeSetting, false);
        boolean allAlertTypes = sharedPreferences.getBoolean("switch_all_alerts", true);

        return allAlertTypes || alertTypeActive;
    }

    /**
     * Method to check if the type of the found alert point, has been produced within the time
     * period the user has set in the applications settings
     * @param candidateAlert The candidate alert point found
     * @param timeSeconds The minimal time in seconds allowed between alerts
     * @return boolean ture if the alert type the last activated alert within the time period
     */
    private boolean checkLastAlertTime(Alert candidateAlert, int timeSeconds) {
        long timeDifference = System.currentTimeMillis()
                - lastAlertTimes[candidateAlert.getAlertType()];
        return timeDifference < (timeSeconds * 1000L);
    }

    /**
     * Method to create an audible alert given an alert point
     * @param alertPoint the alert details we are making the alert for
     */
    private void createAlert(Alert alertPoint) {
        Log.d("AlertCheckerService", "Alert type: " + alertPoint.getAlertType());

        String alertText;

        // Finding the correct alert message
        // First switch on the alert type
        // Then check for left or right side driving on the road
        switch (alertPoint.getAlertType()) {
            case 10:
                alertText = alerts.getDrivingLeft()
                        ? getResources().getString(R.string.alert_type10_left)
                        : getResources().getString(R.string.alert_type10_right);
                break;
            case 20:
                alertText = alerts.getDrivingLeft()
                        ? getResources().getString(R.string.alert_type20_left)
                        : getResources().getString(R.string.alert_type20_right);
                break;
            default:
                return;
        }

        Log.d("AlertCheckerService", "Alert text: " + alertText);

        Log.d("AlertCheckerService", "Creating alert notification");

        textToSpeech.speak(alertText, TextToSpeech.QUEUE_FLUSH, null, null);
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
