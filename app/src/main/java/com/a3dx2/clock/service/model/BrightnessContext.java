package com.a3dx2.clock.service.model;

import java.util.Date;

public class BrightnessContext {

    private final Date sunrise;
    private final Date sunset;

    public BrightnessContext(Date sunrise, Date sunset) {
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public Date getSunrise() {
        return sunrise;
    }

    public Date getSunset() {
        return sunset;
    }

}
