package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.weatherapp.api.weather.WeatherApiManager;
import com.example.weatherapp.models.CurrentWeatherInfo;
import com.example.weatherapp.models.WeatherDescription;
import com.example.weatherapp.models.WeatherInfoManager;
import com.example.weatherapp.screens.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyWidgetActivity extends AppWidgetProvider {

    public static final int REQUEST_CODE = 0;
    public static final int FLAG = 0;

    private static WeatherApiManager weatherApiManager;
    private static Double latitude, longitude;
    private static LocationManager locationManager;
    private static WeatherInfoManager weatherInfoManager;
    static TextView currentTemperatureTextView, weatherDescriptionTextView;
    static ImageView currentConditionImageView;
    static Context context;
    static RemoteViews remoteViews;
    static int appWidgetId;
    static AppWidgetManager appWidgetManager;
    private static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            locationManager.removeUpdates(locationListener);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getWeatherAtCoordinates(latitude, longitude);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

     static void updateAppWidget(Context contextMe, AppWidgetManager appWidgetManager1,
                                int appWidgetIdd) {
         appWidgetId = appWidgetIdd;
         context = contextMe;
         appWidgetManager = appWidgetManager1;
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, FLAG);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget_activity);

        remoteViews = views;
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.widget_layout_button, pendingIntent);

        startLocationManager(context);

//        views.setTextViewText(R.id.widget_description_text, weatherInfoManager.getCurrentWeatherDescription());

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        this.context = context;
        weatherApiManager = new WeatherApiManager();
        weatherInfoManager = WeatherInfoManager.getInstance();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget_activity);

        remoteViews = views;

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            //MyWidgetActivityConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        this.context = context;
        weatherApiManager = new WeatherApiManager();
        weatherInfoManager = WeatherInfoManager.getInstance();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget_activity);

        remoteViews = views;

        startLocationManager(context);


    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    private static void getWeatherAtCoordinates(double latitude, double longitude) {
        weatherApiManager = new WeatherApiManager();
        weatherApiManager.getWeatherAtCoordinates(latitude, longitude).enqueue(new Callback<CurrentWeatherInfo>() {
            @Override
            public void onResponse(Call<CurrentWeatherInfo> call, Response<CurrentWeatherInfo> response) {
                if (response.isSuccessful()) {
                    CurrentWeatherInfo currentWeatherInfo = response.body();
                    if (currentWeatherInfo != null) {
                        showCurrentWeather(currentWeatherInfo);

                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherInfo> call, Throwable t) {

            }
        });
    }

    private static void showCurrentWeather(CurrentWeatherInfo info) {

        String currentTemp = String.valueOf(info.getTempInfo().getTemp());
        currentTemp = String.format(context.getString
                        (R.string.current_temp),
                currentTemp);
        //currentTemperatureTextView.setText(currentTemp);
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget_activity);
        remoteViews.setTextViewText(R.id.widget_temperature_text, currentTemp);
        //set temperature
        weatherInfoManager.setCurrentWeatherTemperature(currentTemp);

        if (info.getWeatherDescriptions() != null && !info.getWeatherDescriptions().isEmpty()) {
            final WeatherDescription currentWeatherDescription = info.getWeatherDescriptions().get(0);

            String weatherDescription = currentWeatherDescription.getDescription();

            remoteViews.setTextViewText(R.id.widget_description_text, weatherDescription);


            String iconUrl = "http://openweathermap.org/img/w/" + currentWeatherDescription.getIcon() + ".png";


            Picasso.with(context).load(iconUrl).into(new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(bitmap != null) {
                        remoteViews.setImageViewBitmap(R.id.widget_image, bitmap);
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });



            }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public void myBitMap(){


    }
    @SuppressLint("MissingPermission")
    private static void startLocationManager(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
}

