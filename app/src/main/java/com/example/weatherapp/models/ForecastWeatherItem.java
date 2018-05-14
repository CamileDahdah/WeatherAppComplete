package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author fouad
 */

public class ForecastWeatherItem {

    @SerializedName("dt")
    private Integer unixTimeStamp;

    @SerializedName("main")
    private TempInfo tempInfo;

    @SerializedName("weather")
    private List<WeatherDescription> weatherDescriptions;

    public Integer getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public TempInfo getTempInfo() {
        return tempInfo;
    }

    public List<WeatherDescription> getWeatherDescriptions() {
        return weatherDescriptions;
    }
}
