package com.example.weatherapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.weatherapp.R;
import com.example.weatherapp.api.weather.WeatherApiManager;
import com.example.weatherapp.models.CurrentWeatherInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityPickerActivity extends AppCompatActivity {

    public static final String CITY_RESULT_KEY = "CITY_RESULT_KEY";

    private WeatherApiManager weatherApiManager;
    private EditText cityNameEditText;
    private ImageView searchImageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker);

        weatherApiManager = new WeatherApiManager();

        cityNameEditText = findViewById(R.id.city_name);
        searchImageView = findViewById(R.id.search_button);
        progressBar = findViewById(R.id.progress_bar);

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSearchForCity();
            }
        });
    }

    private void attemptSearchForCity() {
        showProgressBar();
        String currentText = cityNameEditText.getText().toString();
        if (!TextUtils.isEmpty(currentText)) {
            weatherApiManager
                    .getWeatherByCityName(currentText)
                    .enqueue(new Callback<CurrentWeatherInfo>() {
                        @Override
                        public void onResponse(Call<CurrentWeatherInfo> call, Response<CurrentWeatherInfo> response) {
                            if (response.isSuccessful()) {
                                CurrentWeatherInfo body = response.body();
                                Intent intent = new Intent();
                                intent.putExtra(CITY_RESULT_KEY, body);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            hideProgressBar();
                        }

                        @Override
                        public void onFailure(Call<CurrentWeatherInfo> call, Throwable t) {
                            hideProgressBar();
                        }
                    });
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
