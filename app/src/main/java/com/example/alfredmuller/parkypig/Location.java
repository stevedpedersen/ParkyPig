package com.example.alfredmuller.parkypig;

/**
 *  This class is used in MyDBHandler.java and GPSTracker.java
 *  Primarily used to retrieve/store data from/to SQLite , and retrieve last known location of device
 */
public class Location {
    private int _id;
    private double _lat;
    private double _lng;
    private String name;
    private String address;
    private String date;

    /**
     *
     *  Does nothing
     *
     */
    public Location() {

    }

    /**
     *  3 parameter constructor, with ID and latitude/longitude.
     *
     * @param id id of "this" particular location
     * @param lat Latitude
     * @param lng Longitude
     */
    public Location(int id, double lat, double lng) {
        this._id = id;
        this._lat = lat;
        this._lng = lng;
    }

    /**
     *  Only if you want to initialize longitude/latitude upon creation
     *
     * @param lat Latitude
     * @param lng Longitude
     */
    public Location(double lat, double lng) {
        this._lat = lat;
        this._lng = lng;
    }

    /**
     *
     * @param lat Latitude
     * @param lng Longitude
     * @param date Date
     */
    public Location(double lat, double lng, String date){
        this._lat = lat;
        this._lng = lng;
        this.date = date;
    }

    /**
     * constructor to generate all the SQLite parameters for a new location
     *
     * @param id
     * @param lat - longitude
     * @param lng latitude
     * @param name Name
     * @param address
     * @param date
     */
    public Location(int id, double lat, double lng,
                    String name, String address, String date) {
        this._id = id;
        this._lat = lat;
        this._lng = lng;
        this.name = name;
        this.address = address;
        this.date = date;
    }

    // constructor to generate all the SQLite parameters for a new location

    /**
     *
     * @param lat Latitude
     * @param lng Longitude
     * @param name
     * @param address
     * @param date
     */
    public Location(double lat, double lng,
                    String name, String address, String date) {
        this._lat = lat;
        this._lng = lng;
        this.name = name;
        this.address = address;
        this.date = date;
    }

    /**
     *
     * @param id Takes an integer
     */
    public void setID(int id) {
        this._id = id;
    }

    /**
     *
     * @return returns integer ID of this object
     */

    public int getID() {
        return this._id;
    }

    /**
     *
     * @param lat Latitude
     */
    public void setLatitude(double lat) {
        this._lat = lat;
    }

    /**
     *
     * @return returns latitude associated with this object.
     */
    public double getLatitude() {
        return this._lat;
    }

    /**
     *
     * @param lng Longitude
     */
    public void setLongitude(double lng) {
        this._lng = lng;
    }

    /**
     *
     * @return This will return longitude associated with this object.
     */
    public double getLongitude() {
        return this._lng;
    }

    /**
     *
     * @param name Type string
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return returns a string specifying a name
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param address Takes a string as an address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return returns a string representing an address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     *
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return returns a string specifying the date
     */
    public String getDate() {
        return this.date;
    }
}