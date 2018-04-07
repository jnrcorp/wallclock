package com.a3dx2.clock.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsService {

    private static final String SHARED_PREFERENCES_CLOCK = "CLOCK_PREFERENCES";

    public static final String API_KEY = "API_KEY";
    public static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    public static final String TEXT_COLOR = "TEXT_COLOR";

    private final Context context;

    public SettingsService(Context context) {
        this.context = context;
    }

    public ClockSettings loadClockSettings() {
        SharedPreferences spref = context.getSharedPreferences(SHARED_PREFERENCES_CLOCK, 0);
        ClockSettings clockSettings = new ClockSettings();
        String apiKey = spref.getString(API_KEY, "");
        String backgroundColor = spref.getString(BACKGROUND_COLOR, "#000000");
        String textColor = spref.getString(TEXT_COLOR, "#33b5e5");
        clockSettings.setApiKey(apiKey);
        clockSettings.setBackgroundColor(backgroundColor);
        clockSettings.setTextColor(textColor);
        return clockSettings;
    }

    public void saveClockSettings(ClockSettings clockSettings) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_CLOCK, 0).edit();
        editor.putString(API_KEY, clockSettings.getApiKey());
        editor.putString(BACKGROUND_COLOR, clockSettings.getBackgroundColor());
        editor.putString(TEXT_COLOR, clockSettings.getTextColor());
        editor.commit();
    }

}
