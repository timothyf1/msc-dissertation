package com.example.gpssafetydrivingapp.alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

public class AlertStopActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action=intent.getStringExtra("action");

        if (action.equals("stopAlerts")) {
            Log.d("AlertCheckerActionReceiver", "Stop action received from notification");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putBoolean("switch_alerts_enable", false);
            editor.commit();

            AlertCheckerService.stopAlertChecker(context);
        }
    }
}
