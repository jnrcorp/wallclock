package com.a3dx2.clock.service;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.a3dx2.clock.R;
import com.a3dx2.clock.service.model.ClockSettings;
import com.a3dx2.clock.view.ScrollAuto;
import com.a3dx2.clock.view.WeatherDayView;

import java.time.Clock;

public class ScrollingForecastService {

    private final Handler handler = new Handler();
    private final ScrollingRunner scrollingRunner = new ScrollingRunner();
    private final ScrollAuto scrollAuto = new ScrollAuto();

    private final Activity activity;
    private final ScrollView scrollView;

    public ScrollingForecastService(Activity activity) {
        this.activity = activity;
        this.scrollView = activity.findViewById(R.id.weather_status_scroll);
    }

    public void updateUI(ClockSettings clockSettings) {
        LinearLayout weatherStatuses = scrollView.findViewById(R.id.weather_status);
        int childCount = weatherStatuses.getChildCount();
        updateTextColor(clockSettings, weatherStatuses, childCount);
        updateFontSizeAndIcon(clockSettings, weatherStatuses, childCount);
    }

    private void updateTextColor(ClockSettings clockSettings, LinearLayout weatherStatuses, int childCount) {
        String textColor = clockSettings.getTextColor();
        Integer color = Color.parseColor(textColor);
        for (int i=0; i<childCount; i++) {
            View child = weatherStatuses.getChildAt(i);
            if (child instanceof WeatherDayView) {
                WeatherDayView day = (WeatherDayView) child;
                day.setTextColor(color);
            }
        }
    }

    private void updateFontSizeAndIcon(ClockSettings clockSettings, LinearLayout weatherStatuses, int childCount) {
        Integer fontSizeTemp = clockSettings.getFontSizeWeatherTemp();
        Integer fontSizeTime = clockSettings.getFontSizeWeatherTime();
        Double iconSizeMultiplier = clockSettings.getIconSizeMultiplier();
        for (int i=0; i<childCount; i++) {
            View child = weatherStatuses.getChildAt(i);
            if (child instanceof WeatherDayView) {
                WeatherDayView day = (WeatherDayView) child;
                day.setFontSize(fontSizeTemp, fontSizeTime);
                day.resizeWeatherIcon(iconSizeMultiplier);
            }
        }
    }

    public void alertKeyMissing() {
        LinearLayout weatherStatuses = scrollView.findViewById(R.id.weather_status);
        int childCount = weatherStatuses.getChildCount();
        for (int i=0; i<childCount; i++) {
            View child = weatherStatuses.getChildAt(i);
            if (child instanceof WeatherDayView) {
                WeatherDayView day = (WeatherDayView) child;
                day.setWeatherDayOfWeek("No Key");
            }
        }
    }

    public void updateDisplayTimeInterval(ClockSettings clockSettings) {
        Integer timeInterval = clockSettings.getWeatherTimeInterval();
        LinearLayout weatherStatuses = scrollView.findViewById(R.id.weather_status);
        int childCount = weatherStatuses.getChildCount();
        for (int i=0; i<childCount; i++) {
            View child = weatherStatuses.getChildAt(i);
            if (child instanceof WeatherDayView) {
                if (i % timeInterval != 0) {
                    child.setVisibility(View.GONE);
                } else {
                    child.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void activateScroll() {
        handler.removeCallbacks(scrollingRunner);
        handler.post(scrollingRunner);
    }

    private class ScrollingRunner implements Runnable {
        @Override
        public void run() {
            scrollView.scrollBy(0, scrollAuto.getIncrement());
            View lastView = scrollView.getChildAt(scrollView.getChildCount() - 1);
            int diff = lastView.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
            if (!scrollAuto.isFlippedLastCall()) {
                if (diff == 0) {
                    scrollAuto.flip();
                }
                if (scrollView.getScrollY() == 0) {
                    scrollAuto.flip();
                }
            } else {
                scrollAuto.nextCall();;
            }
            handler.postDelayed(this, scrollAuto.getHandlerDelay());
        }
    }

}
