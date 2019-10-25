package com.example.jlccustomer.Utils;

public class Event {
    private int key;
    public int colorValue;
    double lattitude, longitude;
    private String value, ride_id, driverID, driverImage, message, vehi_number, driverName;

    public Event(int key, String value) {
        this.key = key;
        this.value = value;
    }


    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getMessage() {
        return message;
    }

    public String getVehi_number() {
        return vehi_number;
    }

    public void setVehi_number(String vehi_number) {
        this.vehi_number = vehi_number;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDriverImage() {
        return driverImage;
    }

    public void setDriverImage(String driverImage) {
        this.driverImage = driverImage;
    }

    public Event(int key, int colorValue) {
        this.key = key;
        this.colorValue = colorValue;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getValue() {
        return value;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Event(int key, double lattitude, double longitude) {
        this.key = key;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public int getColorValue() {
        return colorValue;
    }

    public void setColorValue(int colorValue) {
        this.colorValue = colorValue;
    }

    public Event(int key, String message, String driverID, String driverImage, String ride_id, String vehi_number, String driverName) {
        this.key = key;
        this.ride_id = ride_id;
        this.message = message;
        this.driverID = driverID;
        this.driverImage = driverImage;
        this.vehi_number = vehi_number;
        this.driverName = driverName;
    }

    public Event(int key, String ride_id, String driverName) {
        this.key = key;
        this.ride_id = ride_id;
        this.driverName = driverName;

    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}