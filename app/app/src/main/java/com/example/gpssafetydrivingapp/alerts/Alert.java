package com.example.gpssafetydrivingapp.alerts;

public class Alert {

    private final int id;
    private final int alertType;
    private final int node;
    private final double latitude;
    private final double longitude;
    private final double bearing;

    public Alert(int id, int alertType, int node, double latitude, double longitude, double bearing) {
        this.id = id;
        this.alertType = alertType;
        this.node = node;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
    }

    public int getId() {
        return id;
    }

    public int getAlertType() {
        return alertType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getBearing() {
        return bearing;
    }
}
