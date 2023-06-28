package com.example.gpssafetydrivingapp.alerts;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import java.util.HashSet;

public class NearestAlert {

    private final HashSet<Alert> alertPoints;

    public NearestAlert(HashSet<Alert> alertPoints) {
        this.alertPoints = alertPoints;
    }

    public Alert findNearest(double lat, double lon) {
        double current_nearest_distance = Double.MAX_VALUE;
        Alert current_nearest_alert = new Alert(-1, 0, 0, 0, 0, 0);

        LatLng current_location = new LatLng(lat, lon);

        for (Alert alert: alertPoints ) {
            LatLng alert_location = new LatLng(alert.getLatitude(), alert.getLongitude());
            double distance = LatLngTool.distance(current_location, alert_location, LengthUnit.METER);

            if (distance < current_nearest_distance) {
                current_nearest_alert = alert;
                current_nearest_distance = distance;
            }
        }

        return current_nearest_alert;
    }
}
