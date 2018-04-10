package com.a3dx2.clock.service;

import android.graphics.Color;
import android.util.TypedValue;
import android.widget.TextClock;

import com.a3dx2.clock.R;
import com.a3dx2.clock.activity.ClockMain;
import com.a3dx2.clock.service.model.ClockSettings;

import java.time.Clock;

public class ClockUIService {

    private final ClockMain activity;
    private final TextClock clockTime;
    private final TextClock clockDate;

    public ClockUIService(ClockMain activity) {
        this.activity = activity;
        this.clockTime = activity.findViewById(R.id.fullscreen_clock_time);
        this.clockDate = activity.findViewById(R.id.fullscreen_clock_date);
    }

    public void updateFont(ClockSettings clockSettings) {
        Integer fontSizeTime = clockSettings.getFontSizeClockTime();
        clockTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeTime);
        Integer fontSizeDate = clockSettings.getFontSizeClockDate();
        clockDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeDate);
        String textColor = clockSettings.getTextColor();
        Integer color = Color.parseColor(textColor);
        clockTime.setTextColor(color);
        clockDate.setTextColor(color);
    }

}
