package com.a3dx2.clock.service;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.a3dx2.clock.activity.BrightnessAwareActivity;
import com.a3dx2.clock.service.model.BrightnessContext;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrightnessService {

    private static final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.US);

    private final WeakReference<BrightnessAwareActivity> activity;
    private final Date created;

    public BrightnessService(BrightnessAwareActivity activity) {
        this.activity = new WeakReference<>(activity);
        this.created = new Date();
    }

    public void startUpdateBrightness(boolean manageBrightness) {
        LOGGER.log(Level.INFO, "Restarting BrightnessService created=" + created);
        brightnessHandler.removeCallbacks(updateBrightness);
        if (manageBrightness) {
            brightnessHandler.post(updateBrightness);
        }
    }

    public void shutdown() {
        LOGGER.log(Level.INFO, "Stop BrightnessSexrvice created=" + created);
        brightnessHandler.removeCallbacks(updateBrightness);
        brightnessHandler.removeCallbacksAndMessages(null);
    }

    private final Handler brightnessHandler = new Handler();
    private final Runnable updateBrightness = new Runnable() {
        @Override
        public void run() {
            try {
                LOGGER.log(Level.INFO, "Starting BrightnessService created=" + created);
                BrightnessContext brightnessContext = activity.get().getBrightnessContext();
                if (brightnessContext != null) {
                    int brightnessMode = Settings.System.getInt(activity.get().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
                    if (Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL == brightnessMode) {
                        boolean isNight = isNight(brightnessContext);
                        boolean isOnBatteryPower = isOnBatteryPower();
                        int brightnessLevel = isNight || isOnBatteryPower ? 100 : 255;
                        LOGGER.log(Level.INFO, "Brightness Adjusted: isNight=" + isNight + "; isBattery=" + isOnBatteryPower + ", brightnessLevel=" + brightnessLevel);
                        Settings.System.putInt(activity.get().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessLevel);
                    } else {
                        LOGGER.log(Level.INFO, "brightnessMode is not Manual");
                    }
                } else {
                    LOGGER.log(Level.INFO, "brightnessContext is null");
                }
            } catch (Settings.SettingNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "Cannot find screen brightness mode");
            } finally {
                int millisecondDelay = 60*1000;
                LOGGER.log(Level.INFO, "Running brightness leveling again in " + millisecondDelay + " milliseconds.");
                if (activity.get() != null) {
                    brightnessHandler.postDelayed(this, millisecondDelay);
                }
            }
        }

        private boolean isOnBatteryPower() {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = activity.get().getContext().registerReceiver(null, ifilter);
            assert batteryStatus != null;
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            return !isCharging;
        }

        private boolean isNight(BrightnessContext brightnessContext) {
            Date now = new Date();
            Date sunrise = addMinutesToDate(brightnessContext.getSunrise(), 0);
            Date sunset = addMinutesToDate(brightnessContext.getSunset(), 90);
            boolean isTomorrow = isTomorrow(sunset);
            LOGGER.log(Level.INFO, "isNight Data: now=" + now +", sunrise=" + sunrise + ", sunset=" + sunset + "; isTomorrow=" + isTomorrow);
            if (isTomorrow) {
                sunset = addDays(sunset, -1);
            }
            boolean nowAfterSunrise = now.compareTo(sunrise) >= 0;
            boolean nowBeforeSunset = now.compareTo(sunset) <= 0;
            boolean nowAfterSunset = now.compareTo(sunset) >= 0;
            boolean nowBeforeSunrise = now.compareTo(sunrise) <= 0;
            LOGGER.log(Level.INFO, "nowAfterSunrise=" + nowAfterSunrise + ", nowBeforeSunset=" + nowBeforeSunset + ", nowAfterSunset=" + nowAfterSunset + ", nowBeforeSunrise=" + nowBeforeSunrise);
            if (nowAfterSunrise && nowBeforeSunset) {
                return false;
            } else if (nowAfterSunset || nowBeforeSunrise) {
                return true;
            }
            boolean sunsetAfterSunrise = sunset.compareTo(sunrise) > 0;
            LOGGER.log(Level.SEVERE, "Unhandled Sunrise/Sunset situation. now=" + now + ", sunrise=" + sunrise + ", sunset=" + sunset + ", sunsetAfterSunrise=" + sunsetAfterSunrise);
            return sunsetAfterSunrise;
        }

        private boolean isTomorrow(Date date) {
            return !isSameDay(date) && isSameDay(addDays(date, -1));
        }

        private boolean isSameDay(Date date) {
            String dateDay = DAY_FORMAT.format(date);
            String nowDay = DAY_FORMAT.format(new Date());
            boolean isSameDay = dateDay.equals(nowDay);
            LOGGER.log(Level.INFO, "isSameDay Data: dateDay=" + dateDay + ", nowDay=" + nowDay + ", isSameDay=" + isSameDay);
            return isSameDay;
        }

        private Date addDays(Date date, int days) {
            Calendar result = Calendar.getInstance();
            result.setTime(date);
            result.add(Calendar.DATE, days);
            return result.getTime();
        }

        private Date addMinutesToDate(Date date, int minutes) {
            Calendar result = Calendar.getInstance();
            result.setTime(date);
            result.add(Calendar.MINUTE, minutes);
            return result.getTime();
        }

    };

}
