package com.a3dx2.clock.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.a3dx2.clock.R;
import com.a3dx2.clock.service.ClockUIService;
import com.a3dx2.clock.service.CurrentWeatherUIService;
import com.a3dx2.clock.service.ScrollingForecastUIService;
import com.a3dx2.clock.service.WeatherUpdateService;
import com.a3dx2.clock.service.model.ClockSettings;
import com.a3dx2.clock.service.openweathermap.model.CurrentLocationResult;
import com.a3dx2.clock.service.openweathermap.model.FiveDayResult;

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

    private final Handler brightnessHandler = new Handler();
    private final Runnable updateBrightness = new Runnable() {
        @Override
        public void run() {
            try {
                if (weatherCurrent != null) {
                    int brightnessMode = Settings.System.getInt(mThis.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
                    if (Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL == brightnessMode) {
                        int brightnessLevel = isNight() ? 0 : 255;
                        Settings.System.putInt(mThis.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessLevel);
                    }
                }
            } catch (Settings.SettingNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "Cannot find screen brightness mode");
            } finally {
                brightnessHandler.postDelayed(this, 60*1000);
            }
        }

        private boolean isNight() {
            Long sunsetTime = Long.valueOf(weatherCurrent.getSys().getSunset());
            Long sunriseTime = Long.valueOf(weatherCurrent.getSys().getSunrise());
            Date sunset = new Date(sunsetTime * 1000);
            Date sunrise = new Date(sunriseTime * 1000);
            return sunset.compareTo(sunrise) > 0;
        }

    };

    private final int PERMMISSIONS_LOCATION_REQUEST_ID = 9302;
    private final int PERMMISSIONS_WRITE_SETTINGS_REQUEST_ID = 9303;

    private ClockMain mThis = this;
    private ClockSettings clockSettings;
    private ClockUIService clockUIService;
    private ScrollingForecastUIService scrollingForecastUIService;
    private WeatherUpdateService weatherUpdateService;
    private CurrentWeatherUIService currentWeatherUIService;

    private CurrentLocationResult weatherCurrent;
    private FiveDayResult weatherForecast;

    public CurrentLocationResult getWeatherCurrent() {
        return weatherCurrent;
    }

    public void setWeatherCurrent(CurrentLocationResult weatherCurrent) {
        this.weatherCurrent = weatherCurrent;
    }

    public FiveDayResult getWeatherForecast() {
        return weatherForecast;
    }

    public void setWeatherForecast(FiveDayResult weatherForecast) {
        this.weatherForecast = weatherForecast;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clock_main);

        clockSettings = new ClockSettings(this);
        clockUIService = new ClockUIService(this);
        scrollingForecastUIService = new ScrollingForecastUIService(this);
        weatherUpdateService = new WeatherUpdateService(this);
        currentWeatherUIService = new CurrentWeatherUIService(this);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        if (ActivityCompat.checkSelfPermission(mThis, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mThis, new String[]{Manifest.permission.WRITE_SETTINGS}, PERMMISSIONS_WRITE_SETTINGS_REQUEST_ID);
        } else {
            restartBrightnessChecker();
        }

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMMISSIONS_LOCATION_REQUEST_ID);
        } else {
            weatherUpdateService.startWeatherUpdate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clockSettings = new ClockSettings(this);
        setBackgroundColor();
        clockUIService.updateFont(clockSettings);
        currentWeatherUIService.updateText(clockSettings);
        scrollingForecastUIService.updateDisplayTimeInterval(clockSettings);
        scrollingForecastUIService.updateUI(clockSettings);
        scrollingForecastUIService.activateScroll();
        weatherUpdateService.startWeatherUpdate();
        weatherUpdateService.updateLastTimeUI(clockSettings);
        restartBrightnessChecker();
    }

    public void restartBrightnessChecker() {
        String manageBrightnessPref = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_manage_brightness), "true");
        boolean manageBrightness = Boolean.valueOf(manageBrightnessPref);
        brightnessHandler.removeCallbacks(updateBrightness);
        if (manageBrightness) {
            brightnessHandler.post(updateBrightness);
        }
    }

    public void processNoApiKey() {
        scrollingForecastUIService.alertKeyMissing();
        setKeyForDeveloper();
    }

    public void processNoLocation() {
        scrollingForecastUIService.alertNoLocation();
    }

    public void setBackgroundColor() {
        String backgroundColor = clockSettings.getBackgroundColor();
        getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(Color.parseColor(backgroundColor));
    }

    public ClockSettings getClockSettings() {
        return clockSettings;
    }

    @Override
    protected void onPause() {
        super.onPause();
        weatherUpdateService.stopWeatherUpdate();
    }

    private void setKeyForDeveloper() {
        String openWeatherApiKey = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_api_key), "");
        String apiKey = ""; // If you are a developer, you can put a key here, but do not commit it to the repo.
        if (openWeatherApiKey.trim().isEmpty() && !apiKey.trim().isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(getString(R.string.pref_key_api_key), apiKey).commit();
            clockSettings = new ClockSettings(this);
            weatherUpdateService.startWeatherUpdate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMMISSIONS_LOCATION_REQUEST_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    weatherUpdateService.startWeatherUpdate();
                }
                break;
            }
            case PERMMISSIONS_WRITE_SETTINGS_REQUEST_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    restartBrightnessChecker();
                }
                break;
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
