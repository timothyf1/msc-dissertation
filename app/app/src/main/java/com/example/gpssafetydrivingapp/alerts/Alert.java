package com.example.gpssafetydrivingapp.alerts;

import com.google.gson.annotations.SerializedName;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class Alert {

    private final String id;
    @SerializedName("type")
    private final String alertType;
    private final String node;
    private final String latitude;
    private final String longitude;
    private final String bearing;

    public Alert(String id, int alertType, int node, double latitude, double longitude, double bearing) {
        this.id = id;
        this.alertType = String.valueOf(alertType);
        this.node = String.valueOf(node);
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
        this.bearing = String.valueOf(bearing);
    }

    public String getId() {
        return id;
    }

    public int getAlertType() {
        return Integer.parseInt(alertType);
    }

    public double getLatitude() {
        return Double.parseDouble(latitude);
    }

    public double getLongitude() {
        return Double.parseDouble(longitude);
    }

    public double getBearing() {
        return Double.parseDouble(bearing);
    }

    public int getNode() {
        return Integer.parseInt(node);
    }

    /**
     * Check to see if an alert is valid given a bearing
     * @param testBearing the bearing to check if an alert is valid
     * @return boolean true if the alert is valid
     */
    public boolean checkBearing(double testBearing) {
        return Math.abs(testBearing - Double.parseDouble(bearing)) < 75;
    }

    /**
     * Calculation to find the distance from the alert point
     * @param testLocation the location to be checked
     * @return double the distance from the provided location to the alert point
     */
    public double distance(LatLng testLocation) {
        LatLng alertLocation = new LatLng(getLatitude(), getLongitude());
        return LatLngTool.distance(testLocation, alertLocation, LengthUnit.METER);
    }
}
