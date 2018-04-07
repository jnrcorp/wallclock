package com.a3dx2.clock.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextClock;

import com.a3dx2.clock.R;
import com.a3dx2.clock.service.openweathermap.WeatherSearchFiveDay;
import com.a3dx2.clock.view.WeatherDayView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ClockMain extends AppCompatActivity {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final Handler weatherUpdateHandler = new Handler();
    private final Runnable weatherUpdateRunnable = new Runnable() {
        @Override
        public void run() {
        Integer updateFrequency = PreferenceManager.getDefaultSharedPreferences(mThis).getInt(getString(R.string.pref_key_update_frequency), 30);
        try {
            getWeather();
        } catch (Exception ex) {
            LOGGER.log(Level.ALL, ex.getMessage(), ex);
        } finally {
            weatherUpdateHandler.postDelayed(this, updateFrequency*1000);
        }
        }
    };

    private final int PERMMISSIONS_REQUEST_ID = 9302;

    private ClockMain mThis = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clock_main);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSettings();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMMISSIONS_REQUEST_ID);
        } else {
            weatherUpdateHandler.post(weatherUpdateRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String backgroundColor = PreferenceManager.getDefaultSharedPreferences(mThis).getString(getString(R.string.pref_key_background_color), "#000000");
        String textColor = PreferenceManager.getDefaultSharedPreferences(mThis).getString(getString(R.string.pref_key_text_color), "#33b5e5");
        setBackgroundColor(backgroundColor);
        setTextColor(textColor);
        weatherUpdateHandler.removeCallbacks(weatherUpdateRunnable);
        weatherUpdateHandler.post(weatherUpdateRunnable);
    }

    public void setBackgroundColor(String backgroundColor) {
        getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(Color.parseColor(backgroundColor));
    }

    public void setTextColor(String textColor) {
        Integer color = Color.parseColor(textColor);
        TextClock clockTime = (TextClock) findViewById(R.id.fullscreen_clock_time);
        TextClock clockDate = (TextClock) findViewById(R.id.fullscreen_clock_date);
        WeatherDayView day1 = (WeatherDayView) findViewById(R.id.weather_day_1);
        WeatherDayView day2 = (WeatherDayView) findViewById(R.id.weather_day_2);
        WeatherDayView day3 = (WeatherDayView) findViewById(R.id.weather_day_3);
        WeatherDayView day4 = (WeatherDayView) findViewById(R.id.weather_day_4);
        WeatherDayView day5 = (WeatherDayView) findViewById(R.id.weather_day_5);
        clockTime.setTextColor(color);
        clockDate.setTextColor(color);
        day1.setWeatherTemperatureColor(color);
        day2.setWeatherTemperatureColor(color);
        day3.setWeatherTemperatureColor(color);
        day4.setWeatherTemperatureColor(color);
        day5.setWeatherTemperatureColor(color);
    }

    @Override
    protected void onPause() {
        super.onPause();
        weatherUpdateHandler.removeCallbacks(weatherUpdateRunnable);
    }

    public void changeWeatherUpdateFrequency() {
        weatherUpdateHandler.removeCallbacks(weatherUpdateRunnable);
        weatherUpdateHandler.post(weatherUpdateRunnable);
    }

    private void getWeather() {
        String openWeatherApiKey = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_api_key), "");
        LOGGER.log(Level.INFO, "About to load weather: apiKey={}", openWeatherApiKey);
        if (!openWeatherApiKey.trim().isEmpty()) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            WeatherSearchFiveDay fiveDay = new WeatherSearchFiveDay(this, location, openWeatherApiKey);
            fiveDay.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMMISSIONS_REQUEST_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    weatherUpdateHandler.post(weatherUpdateRunnable);
                }
                return;
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void loadSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
