package com.example.weatherapp.screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.weatherapp.R;
import com.example.weatherapp.api.weather.WeatherApiManager;
import com.example.weatherapp.models.CurrentWeatherInfo;
import com.example.weatherapp.models.ForecastWeatherInfo;
import com.example.weatherapp.models.ForecastWeatherItem;
import com.example.weatherapp.models.TempInfo;
import com.example.weatherapp.models.WeatherDescription;
import com.example.weatherapp.models.WeatherInfoManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public final int PERMISSION_REQUEST_CODE = 100;
    private final int CITY_ACTIVITY_REQUEST_CODE = 200;

    private WeatherApiManager weatherApiManager;
    private Double latitude, longitude;
    private LocationManager locationManager;
    private WeatherInfoManager weatherInfoManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            locationManager.removeUpdates(locationListener);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getWeatherAtCoordinates(latitude, longitude);
            getForecastAtCoordinates(latitude, longitude);
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

    private ImageView currentConditionImageView;
    private TextView currentTemperatureTextView, weatherDescriptionTextView;
    private LottieAnimationView loadingAnimationView;
    private RecyclerView forecastRecyclerView;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherApiManager = new WeatherApiManager();
        weatherInfoManager = WeatherInfoManager.getInstance();

        if (!hasLocationPermissions()) {
            requestFineLocationPermission();
        } else {
            startLocationManager();
        }
    }

    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, PERMISSION_REQUEST_CODE);
    }


    @SuppressLint("MissingPermission")
    private void startLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        currentConditionImageView = findViewById(R.id.current_condition);
        currentTemperatureTextView = findViewById(R.id.current_temperature);
        loadingAnimationView = findViewById(R.id.animation_view);
        weatherDescriptionTextView = findViewById(R.id.weather_description);
        forecastRecyclerView = findViewById(R.id.forecast_list);

        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationManager();
        }
    }

    private void getWeatherAtCoordinates(double latitude, double longitude) {
        weatherApiManager.getWeatherAtCoordinates(latitude, longitude).enqueue(new Callback<CurrentWeatherInfo>() {
            @Override
            public void onResponse(Call<CurrentWeatherInfo> call, Response<CurrentWeatherInfo> response) {
                if (response.isSuccessful()) {
                    CurrentWeatherInfo currentWeatherInfo = response.body();
                    if (currentWeatherInfo != null) {
                        showCurrentWeather(currentWeatherInfo);
                        hideLoadingAnimation();
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherInfo> call, Throwable t) {
                hideLoadingAnimation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.cities:
                gotoCitiesPickerScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CITY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CurrentWeatherInfo currentWeatherInfo = (CurrentWeatherInfo) data.getSerializableExtra(CityPickerActivity.CITY_RESULT_KEY);
            if (currentWeatherInfo != null) {
                latitude = currentWeatherInfo.getCoordinates().getLatitude();
                longitude = currentWeatherInfo.getCoordinates().getLongitude();
                setScreenTitle(currentWeatherInfo.getCityName());
                showCurrentWeather(currentWeatherInfo);
            }
        }
    }

    private void setScreenTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
    }

    private void gotoCitiesPickerScreen() {
        Intent intent = new Intent(this, CityPickerActivity.class);
        startActivityForResult(intent, CITY_ACTIVITY_REQUEST_CODE);
    }

    private void getForecastAtCoordinates(double latitude, double longitude) {
        weatherApiManager.getForecastWeatherInfo(latitude, longitude).enqueue(new Callback<ForecastWeatherInfo>() {
            @Override
            public void onResponse(Call<ForecastWeatherInfo> call, Response<ForecastWeatherInfo> response) {
                if (response.isSuccessful()) {
                    ForecastWeatherInfo forecastWeatherInfo = response.body();
                    if (forecastWeatherInfo != null) {
                        showForecastWeather(forecastWeatherInfo);
                        hideLoadingAnimation();
                    }
                }
            }

            @Override
            public void onFailure(Call<ForecastWeatherInfo> call, Throwable t) {
                hideLoadingAnimation();
            }
        });
    }

    private void showCurrentWeather(CurrentWeatherInfo info) {
        String currentTemp = String.valueOf(info.getTempInfo().getTemp());
        currentTemp = String.format(getString(R.string.current_temp), currentTemp);
        currentTemperatureTextView.setText(currentTemp);
        //set temperature
        weatherInfoManager.setCurrentWeatherTemperature(currentTemp);
        setScreenTitle(info.getCityName());

        if (info.getWeatherDescriptions() != null && !info.getWeatherDescriptions().isEmpty()) {
            WeatherDescription currentWeatherDescription = info.getWeatherDescriptions().get(0);

            String weatherDescription = currentWeatherDescription.getDescription();
            weatherDescriptionTextView.setText(weatherDescription);

            //set description
            weatherInfoManager.setCurrentWeatherDescription(weatherDescription);

            String iconUrl = "http://openweathermap.org/img/w/" + currentWeatherDescription.getIcon() + ".png";
            Picasso.with(this).load(iconUrl).into(currentConditionImageView);
        }
    }

    private void showForecastWeather(ForecastWeatherInfo info) {
        List<ForecastWeatherItem> items = info.getForecastWeatherItems();
        ForecastListAdapter adapter = new ForecastListAdapter(items);
        forecastRecyclerView.setAdapter(adapter);
    }

    private void hideLoadingAnimation() {
        loadingAnimationView.cancelAnimation();
        loadingAnimationView.setVisibility(View.GONE);
    }

    public static class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ViewHolder> {

        private List<ForecastWeatherItem> items;

        public ForecastListAdapter(List<ForecastWeatherItem> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Context context = holder.itemView.getContext();
            TempInfo tempInfo = items.get(position).getTempInfo();
            String currentTemp = String.valueOf(tempInfo.getTemp());
            currentTemp = String.format(context.getString(R.string.current_temp), currentTemp);
            holder.currentTempTextView.setText(currentTemp);


            List<WeatherDescription> descriptions = items.get(position).getWeatherDescriptions();
            if (descriptions != null && !descriptions.isEmpty()) {
                WeatherDescription description = descriptions.get(0);
                String desc = description.getDescription();
                holder.descriptionTextView.setText(desc);

                String iconUrl = "http://openweathermap.org/img/w/" + description.getIcon() + ".png";
                Picasso.with(context).load(iconUrl).into(holder.currentWeatherImageView);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView currentWeatherImageView;
            TextView currentTempTextView;
            TextView descriptionTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                currentWeatherImageView = itemView.findViewById(R.id.weather_condition);
                currentTempTextView = itemView.findViewById(R.id.current_temp);
                descriptionTextView = itemView.findViewById(R.id.weather_description);
            }
        }

    }
}
