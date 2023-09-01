package com.example.gpssafetydrivingapp.alerts;

import com.google.gson.annotations.SerializedName;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class Alert {

    private final long id;
    @SerializedName("type")
    private final int alertType;
    private final long node;
    private final double latitude;
    private final double longitude;
    private final double bearing;

    public Alert(long id, int alertType, int node, double latitude, double longitude, double bearing) {
        this.id = id;
        this.alertType = alertType;
        this.node = node;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
    }

    public long getId() {
        return id;
    }

    public int getAlertType() {
        return alertType;
    }

    public double getBearing() {
        return bearing;
    }

    /**
     * Check to see if an alert is valid given a bearing
     * @param testBearing the bearing to check if an alert is valid
     * @param bearingAllowance the bearing allowance allowed when checking the bearing
     * @return boolean true if the alert is valid
     */
    public boolean checkBearing(double testBearing, int bearingAllowance) {
        return Math.abs(testBearing - bearing) < bearingAllowance;
    }

    /**
     * Calculation to find the distance from the alert point
     * @param testLocation the location to be checked
     * @return double the distance from the provided location to the alert point
     */
    public double distance(LatLng testLocation) {
        LatLng alertLocation = new LatLng(latitude, longitude);
        return LatLngTool.distance(testLocation, alertLocation, LengthUnit.METER);
    }
}
