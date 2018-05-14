package com.example.weatherapp.models;

import java.io.Serializable;

/**
 * @author fouad
 */

public class Coordinates implements Serializable{

    private Double lat;
    private Double lon;

    public Double getLatitude() {
        return lat;
    }

    public Double getLongitude() {
        return lon;
    }
}
