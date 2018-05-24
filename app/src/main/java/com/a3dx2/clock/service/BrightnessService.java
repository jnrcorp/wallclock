package com.a3dx2.clock.service;

import android.os.Handler;
import android.provider.Settings;

import com.a3dx2.clock.activity.BrightnessAwareActivity;
import com.a3dx2.clock.service.model.BrightnessContext;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrightnessService {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private final BrightnessAwareActivity activity;

    public BrightnessService(BrightnessAwareActivity activity) {
        this.activity = activity;
    }

    public void startUpdateBrightness(boolean manageBrightness) {
        brightnessHandler.removeCallbacks(updateBrightness);
        if (manageBrightness) {
            brightnessHandler.post(updateBrightness);
        }
    }
    private final Handler brightnessHandler = new Handler();
    private final Runnable updateBrightness = new Runnable() {
        @Override
        public void run() {
            try {
                BrightnessContext brightnessContext = activity.getBrightnessContext();
                if (brightnessContext != null) {
                    int brightnessMode = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
                    if (Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL == brightnessMode) {
                        int brightnessLevel = isNight(brightnessContext) ? 0 : 255;
                        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessLevel);
                    }
                }
            } catch (Settings.SettingNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "Cannot find screen brightness mode");
            } finally {
                brightnessHandler.postDelayed(this, 5*60*1000);
            }
        }

        private boolean isNight(BrightnessContext brightnessContext) {
            Date now = new Date();
            Date sunrise = addMinutesToDate(brightnessContext.getSunrise(), -90);
            Date sunset = addMinutesToDate(brightnessContext.getSunset(), 90);
            if (now.compareTo(sunrise) >= 0 && now.compareTo(sunset) <= 0) {
                return false;
            } else if (now.compareTo(sunset) >= 0 || now.compareTo(sunrise) <= 0) {
                return true;
            }
            LOGGER.log(Level.SEVERE, "Unhandled Sunrise/Sunset situation. now={}, sunrise={}, sunset={}", new Object[] { now, sunrise, sunset });
            return sunset.compareTo(sunrise) > 0;
        }

        private Date addMinutesToDate(Date date, int minutes) {
            Calendar result = Calendar.getInstance();
            result.setTime(date);
            result.add(Calendar.MINUTE, minutes);
            return result.getTime();
        }

    };

}
