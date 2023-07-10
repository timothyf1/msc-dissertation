package com.example.gpssafetydrivingapp.alerts;

import java.util.HashSet;

public class Alerts {

    private String area;
    private HashSet<Alert> alerts;

    public HashSet<Alert> getAlerts() {
        return alerts;
    }
    public int getNumberOfAlerts() {
        return alerts.size();
    }
}