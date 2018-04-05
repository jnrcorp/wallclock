package com.a3dx2.clock.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsService {

    private static final String CLOCK_PREFERENCES = "CLOCK_PREFERENCES";

    private final Context context;

    public SettingsService(Context context) {
        this.context = context;
    }

    public ClockSettings loadClockSettings() {
        SharedPreferences spref = context.getSharedPreferences(CLOCK_PREFERENCES, 0);
        ClockSettings clockSettings = new ClockSettings();
        String apiKey = spref.getString("API_KEY", "");
        String backgroundColor = spref.getString("BACKGROUND_COLOR", "#000000");
        String textColor = spref.getString("TEXT_COLOR", "#33b5e5");
        clockSettings.setApiKey(apiKey);
        clockSettings.setBackgroundColor(backgroundColor);
        clockSettings.setTextColor(textColor);
        return clockSettings;
    }

    public void saveClockSettings(ClockSettings clockSettings) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CLOCK_PREFERENCES, 0).edit();
        editor.putString("API_KEY", clockSettings.getApiKey());
        editor.putString("BACKGROUND_COLOR", clockSettings.getBackgroundColor());
        editor.putString("TEXT_COLOR", clockSettings.getTextColor());
        editor.commit();
    }

}
