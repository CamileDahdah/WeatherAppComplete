package com.example.weatherapp.api.weather;

import com.example.weatherapp.models.CurrentWeatherInfo;
import com.example.weatherapp.models.ForecastWeatherInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author fouad
 */

public interface WeatherApi {
    @GET("weather")
    Call<CurrentWeatherInfo> getWeatherAtCoordinates(@Query("lat") double latitude, @Query("lon") double longitude);


    @GET("forecast")
    Call<ForecastWeatherInfo> getForecastWeatherInfo(@Query("lat") double latitude, @Query("lon") double longitude);

    @GET("weather")
    Call<CurrentWeatherInfo> getWeatherByCityName(@Query("q") String cityName);

}
