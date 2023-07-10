package com.example.gpssafetydrivingapp.alerts;

import com.javadocmd.simplelatlng.LatLng;

import java.util.HashSet;

public class Alerts {

    private String area;
    private HashSet<Alert> alerts;

    public int getNumberOfAlerts() {
        return alerts.size();
    }

    public Alert findNearest(double lat, double lon, int maxDistance) {
        double current_nearest_distance = maxDistance;
        Alert current_nearest_alert = new Alert("-1", 0, 0, 0, 0, 0);

        LatLng current_location = new LatLng(lat, lon);

        for (Alert alert: alerts ) {
            double distance = alert.distance(current_location);

            if (distance < current_nearest_distance) {
                current_nearest_alert = alert;
                current_nearest_distance = distance;
            }
        }

        return current_nearest_alert;
    }
}
