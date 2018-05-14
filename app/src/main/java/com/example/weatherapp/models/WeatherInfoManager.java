package com.example.weatherapp.models;

/**
 * Created by camilledahdah on 5/14/18.
 */

public class WeatherInfoManager {

    private static WeatherInfoManager weatherInfoManager = null;
    private String currentWeatherTemperature = "", currentWeatherDescription = "";



    private WeatherInfoManager(){

    }


    public static WeatherInfoManager getInstance(){

        if(weatherInfoManager == null){
            weatherInfoManager = new WeatherInfoManager();
        }

        return weatherInfoManager;

    }


    public String getCurrentWeatherTemperature() {
        return currentWeatherTemperature;
    }

    public void setCurrentWeatherTemperature(String currentWeatherTemperature) {
        this.currentWeatherTemperature = currentWeatherTemperature;
    }

    public String getCurrentWeatherDescription() {
        return currentWeatherDescription;
    }

    public void setCurrentWeatherDescription(String currentWeatherDescription) {
        this.currentWeatherDescription = currentWeatherDescription;
    }


}
