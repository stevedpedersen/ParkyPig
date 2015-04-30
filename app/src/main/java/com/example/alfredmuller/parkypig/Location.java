package com.example.alfredmuller.parkypig;


public class Location {
    private int _id;
    private double _lat;
    private double _lng;

    public Location() {

    }

    public Location(int id, double lat, double lng) {
        this._id = id;
        this._lat = lat;
        this._lng = lng;
    }

    public Location(double lat, double lng) {
        this._lat = lat;
        this._lng = lng;
    }

    public void setID(int id) {
        this._id = id;
    }

    public int getID() {
        return this._id;
    }

    public void setLatitude(double lat) {
        this._lat = lat;
    }

    public double getLatitude() {
        return this._lat;
    }

    public void setLongitude(double lng) {
        this._lng = lng;
    }

    public double getLongitude() {
        return this._lng;
    }
}
