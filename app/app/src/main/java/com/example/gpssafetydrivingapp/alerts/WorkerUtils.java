package com.example.gpssafetydrivingapp.alerts;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.gpssafetydrivingapp.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

final class WorkerUtils {

    static void makeStatusNotification(String message, Context context, int id) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "Driving Alerts";
            String description = "Show notification when alerts are active";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    new NotificationChannel("ALERTS", name, importance);
            channel.setDescription(description);

            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ALERTS")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Test location")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[0])
                .setShowWhen(false);

        // Show the notification
        NotificationManagerCompat.from(context).notify(id, builder.build());
    }

}
