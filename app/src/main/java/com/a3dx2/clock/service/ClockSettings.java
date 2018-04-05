package com.a3dx2.clock.service;

public class ClockSettings {

    private String apiKey;
    private String backgroundColor;
    private String textColor;

    public ClockSettings() {
        super();
    }

    public ClockSettings(String apiKey, String backgroundColor, String textColor) {
        this.apiKey = apiKey;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
