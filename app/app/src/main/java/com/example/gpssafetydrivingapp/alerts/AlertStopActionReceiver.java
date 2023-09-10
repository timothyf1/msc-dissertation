package com.example.gpssafetydrivingapp.alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlertStopActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action=intent.getStringExtra("action");

        if (action.equals("stopAlerts")) {
            Log.d("AlertCheckerActionReceiver", "Stop action received from notification");
            AlertCheckerService.stopAlertChecker(context);
        }
    }
}
