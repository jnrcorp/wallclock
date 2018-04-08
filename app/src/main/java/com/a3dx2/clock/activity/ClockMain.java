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
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;

import com.a3dx2.clock.R;
import com.a3dx2.clock.service.openweathermap.WeatherSearchCurrent;
import com.a3dx2.clock.service.openweathermap.WeatherSearchFiveDay;
import com.a3dx2.clock.view.ScrollAuto;
import com.a3dx2.clock.view.WeatherDayView;
import com.a3dx2.clock.weather.WeatherUtil;

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

    private Date lastWeatherUpdate;

    private final Handler weatherUpdateHandler = new Handler();
    private final Runnable weatherUpdateRunnable = new Runnable() {
        @Override
        public void run() {
        Integer updateFrequency = PreferenceManager.getDefaultSharedPreferences(mThis).getInt(getString(R.string.pref_key_update_frequency), 30);
        try {
            getWeather(updateFrequency);
        } catch (Exception ex) {
            LOGGER.log(Level.ALL, ex.getMessage(), ex);
        } finally {
            weatherUpdateHandler.postDelayed(this, updateFrequency*60*1000);
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
        setFontSizeTime();
        setFontSizeDate();
        setFontSizeWeather();
        sendScroll();
        weatherUpdateHandler.removeCallbacks(weatherUpdateRunnable);
        weatherUpdateHandler.post(weatherUpdateRunnable);
    }

    private void sendScroll() {
        final ScrollView scrollView = findViewById(R.id.weather_status_scroll);
        final Handler handler = new Handler();
        final ScrollAuto scrollAuto = new ScrollAuto();
        handler.post(new Runnable() {
            @Override
            public void run() {
                boolean flipped = false;
                scrollView.scrollBy(0, scrollAuto.getIncrement());
                View lastView = (View) scrollView.getChildAt(scrollView.getChildCount()-1);
                int diff = (lastView.getBottom()-(scrollView.getHeight()+scrollView.getScrollY()));
                if (!scrollAuto.isFlippedLastCall()) {
                    if (diff == 0) {
                        scrollAuto.flip();
                        flipped = true;
                    }
                    if (scrollView.getScrollY() == 0) {
                        scrollAuto.flip();
                        flipped = true;
                    }
                } else {
                    scrollAuto.nextCall();;
                }
                handler.postDelayed(this, flipped ? 3000 : 15);
            }
        });
    }

    public void setBackgroundColor(String backgroundColor) {
        getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(Color.parseColor(backgroundColor));
    }

    public void setFontSizeWeather() {
        String fontSizeTempPref = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_font_size_temp), "20");
        String fontSizeTimePref = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_font_size_weather_time), "20");
        Integer fontSizeTemp = Integer.valueOf(fontSizeTempPref);
        Integer fontSizeTime = Integer.valueOf(fontSizeTimePref);
        String iconSizePref = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_icon_size), "1");
        Double iconSizeMultiplier = Double.valueOf(iconSizePref);
        LinearLayout weatherStatuses = findViewById(R.id.weather_status);
        int childCount = weatherStatuses.getChildCount();
        for (int i=0; i<childCount; i++) {
            View child = weatherStatuses.getChildAt(i);
            if (child instanceof WeatherDayView) {
                WeatherDayView day = (WeatherDayView) child;
                day.setFontSize(fontSizeTemp, fontSizeTime);
                day.resizeWeatherIcon(iconSizeMultiplier);
            }
        }
        TextView textView = findViewById(R.id.current_weather_temp);
        textView.setTextSize(fontSizeTemp);
        ImageView currentTempIcon = findViewById(R.id.current_weather_image);
        WeatherUtil.resizeIcon(currentTempIcon, iconSizeMultiplier);
    }

    public void setFontSizeTime() {
        String fontSizePref = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_font_size_time), "50");
        Integer fontSize = Integer.valueOf(fontSizePref);
        TextClock clockTime = (TextClock) findViewById(R.id.fullscreen_clock_time);
        clockTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    public void setFontSizeDate() {
        String fontSizePref = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_font_size_date), "30");
        Integer fontSize = Integer.valueOf(fontSizePref);
        TextClock clockDate = (TextClock) findViewById(R.id.fullscreen_clock_date);
        clockDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    public void setTextColor(String textColor) {
        Integer color = Color.parseColor(textColor);
        TextClock clockTime = (TextClock) findViewById(R.id.fullscreen_clock_time);
        TextClock clockDate = (TextClock) findViewById(R.id.fullscreen_clock_date);
        clockTime.setTextColor(color);
        clockDate.setTextColor(color);

        LinearLayout weatherStatuses = (LinearLayout) findViewById(R.id.weather_status);
        int childCount = weatherStatuses.getChildCount();
        for (int i=0; i<childCount; i++) {
            View child = weatherStatuses.getChildAt(i);
            if (child instanceof WeatherDayView) {
                WeatherDayView day = (WeatherDayView) child;
                day.setTextColor(color);
            }
        }
        TextView textView = findViewById(R.id.current_weather_temp);
        textView.setTextColor(color);
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

    private void getWeather(Integer updateFrequency) {
        String openWeatherApiKey = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_api_key), "");
        LOGGER.log(Level.INFO, "About to load weather: apiKey={}", openWeatherApiKey);
        if (!openWeatherApiKey.trim().isEmpty() && isWeatherUpdateDue(updateFrequency)) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            WeatherSearchFiveDay fiveDay = new WeatherSearchFiveDay(this, location, openWeatherApiKey);
            fiveDay.execute();
            WeatherSearchCurrent currentWeather = new WeatherSearchCurrent(this, location, openWeatherApiKey);
            currentWeather.execute();
        } else if (openWeatherApiKey.trim().isEmpty()) {
            alertKeyMissing();
            setKeyForDeveloper();
        }
    }

    private void setKeyForDeveloper() {
        String openWeatherApiKey = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_api_key), "");
        String apiKey = ""; // If you are a developer, you can put a key here, but do not commit it to the repo.
        if (openWeatherApiKey.trim().isEmpty() && !apiKey.trim().isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(getString(R.string.pref_key_api_key), apiKey).commit();
            changeWeatherUpdateFrequency();
        }
    }

    private void alertKeyMissing() {
        LinearLayout weatherStatuses = (LinearLayout) findViewById(R.id.weather_status);
        int childCount = weatherStatuses.getChildCount();
        for (int i=0; i<childCount; i++) {
            View child = weatherStatuses.getChildAt(i);
            if (child instanceof WeatherDayView) {
                WeatherDayView day = (WeatherDayView) child;
                day.setWeatherDayOfWeek("No Key");
            }
        }
    }

    public void setLastWeatherUpdate() {
        this.lastWeatherUpdate = new Date();
    }

    private boolean isWeatherUpdateDue(Integer updateFrequency) {
        if (lastWeatherUpdate == null) {
            return true;
        }
        Calendar nextUpdate = Calendar.getInstance();
        nextUpdate.setTime(lastWeatherUpdate);
        nextUpdate.add(Calendar.MINUTE, updateFrequency);
        Date now = new Date();
        if (now.compareTo(nextUpdate.getTime()) > 0) {
            return true;
        }
        return false;
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
