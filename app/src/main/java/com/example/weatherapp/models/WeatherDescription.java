package com.example.weatherapp.models;

import java.io.Serializable;

/**
 * @author fouad
 */

public class WeatherDescription implements Serializable {

    private String description;
    private String icon;

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
