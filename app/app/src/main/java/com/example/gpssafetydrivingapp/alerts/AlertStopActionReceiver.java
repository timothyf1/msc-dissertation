package com.example.gpssafetydrivingapp.alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlertStopActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action=intent.getStringExtra("action");

        if (action.equals("stopAlerts")) {
            AlertChecker.stopAlertChecker(context);
        }
    }
}
