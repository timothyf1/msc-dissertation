package com.example.gpssafetydrivingapp.alerts;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.gpssafetydrivingapp.R;

public class AlertChecker {

    private static void makeAlertActiveNotification(Context context, PendingIntent stopAlertPendingIntent) {

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
                .setOngoing(true)
                .addAction(0, "Stop Alerts", stopAlertPendingIntent);

        // Show the notification
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(context).notify(1, builder.build());
    }

    public static void startAlertChecker(Context context) {

        Log.d("AlertChecker", "Starting alert checker");
        Intent stopAlertIntent = new Intent(context, AlertStopActionReceiver.class);
        stopAlertIntent.putExtra("action","stopAlerts");

        PendingIntent stopAlertPendingIntent = PendingIntent.getBroadcast(context, 0, stopAlertIntent, PendingIntent.FLAG_IMMUTABLE);

        makeAlertActiveNotification(context, stopAlertPendingIntent);

        WorkManager.getInstance(context).enqueue(OneTimeWorkRequest.from(AlertCheckerWorker.class));
        Log.d("AlertChecker", "Alert checker started");
    }

    public static void stopAlertChecker(Context context) {
        Log.d("AlertChecker", "Stopping alert checker");
        NotificationManagerCompat.from(context).cancel(1);
        WorkManager.getInstance(context).cancelAllWork();

        // Update preferences
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("switch_alerts_enable", false);
        editor.commit();

        Log.d("AlertChecker", "Alert checker stopped");
    }

}