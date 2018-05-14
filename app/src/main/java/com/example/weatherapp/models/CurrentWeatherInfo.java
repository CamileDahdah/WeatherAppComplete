package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author fouad
 */

public class CurrentWeatherInfo implements Serializable {

    @SerializedName("name")
    private String cityName;

    @SerializedName("coord")
    private Coordinates coordinates;

    @SerializedName("main")
    private TempInfo tempInfo;

    @SerializedName("weather")
    private List<WeatherDescription> weatherDescriptions;

    public TempInfo getTempInfo() {
        return tempInfo;
    }

    public List<WeatherDescription> getWeatherDescriptions() {
        return weatherDescriptions;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getCityName() {
        return cityName;
    }

}
