package com.example.weatherapp.api.weather;

import com.example.weatherapp.models.CurrentWeatherInfo;
import com.example.weatherapp.models.ForecastWeatherInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author fouad
 */

public class WeatherApiManager {

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private WeatherApi weatherApi;

    private final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/";

    public WeatherApiManager() {
        Gson gson = new GsonBuilder().create();
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new OpenWeatherInterceptor())
                .build();
        retrofit = new Retrofit
                .Builder()
                .client(okHttpClient)
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        weatherApi = retrofit.create(WeatherApi.class);
    }

    public Call<CurrentWeatherInfo> getWeatherAtCoordinates(double latitude, double longitude) {
        return weatherApi.getWeatherAtCoordinates(latitude, longitude);
    }

    public Call<ForecastWeatherInfo> getForecastWeatherInfo(double latitude, double longitude) {
        return weatherApi.getForecastWeatherInfo(latitude, longitude);
    }

    public Call<CurrentWeatherInfo> getWeatherByCityName(String cityName) {
        return weatherApi.getWeatherByCityName(cityName);
    }

    private static class OpenWeatherInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl originalUrl = request.url();
            HttpUrl modifiedUrl = originalUrl
                    .newBuilder()
                    .addQueryParameter("units", "metric")
                    .addQueryParameter("APPID", "33e206167fdb92a1ed1ef5d2b7d42995")
                    .build();
            Request modifiedRequest = request.newBuilder().url(modifiedUrl).build();
            return chain.proceed(modifiedRequest);
        }
    }
}
