package com.example.gpssafetydrivingapp.alerts;

import android.content.Context;
import android.widget.Toast;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AlertChecker extends Worker {

    public AlertChecker(Context appContext, WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @Override
    public Result doWork() {
//        Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_SHORT);
        WorkerUtils.makeStatusNotification("Hello", getApplicationContext());
        return Result.success();
    }
}

