package com.example.gpssafetydrivingapp.alerts;

import com.google.gson.annotations.SerializedName;
import com.javadocmd.simplelatlng.LatLng;

import java.util.HashSet;

public class Alerts {

    private String area;
    @SerializedName("driving_left")
    private boolean drivingLeft;
    private HashSet<Alert> alerts;

    public int getNumberOfAlerts() {
        return alerts.size();
    }

    public boolean getDrivingLeft() { return drivingLeft; }

    /**
     * Find the nearest alert to a given location within a maximum distance
     * @param lat the latitude of the location to check
     * @param lon the longitude of the location to check
     * @param maxDistance the maximum distance an alert can be from the provided location
     * @return The nearest Alert if there is one within the maximum distance
     */
    public Alert findNearest(double lat, double lon, int maxDistance) {
        double current_nearest_distance = maxDistance;
        Alert current_nearest_alert = null;

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
