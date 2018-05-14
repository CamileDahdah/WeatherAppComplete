package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author fouad
 */

public class ForecastWeatherInfo {

    @SerializedName("list")
    private List<ForecastWeatherItem> forecastWeatherItems;

    public List<ForecastWeatherItem> getForecastWeatherItems() {
        return forecastWeatherItems;
    }
}
