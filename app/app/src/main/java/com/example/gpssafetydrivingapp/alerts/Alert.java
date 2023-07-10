package com.example.gpssafetydrivingapp.alerts;

public class Alert {

    private final String id;
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
    }
}