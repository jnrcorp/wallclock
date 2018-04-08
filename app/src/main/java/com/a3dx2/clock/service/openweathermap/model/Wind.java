package com.a3dx2.clock.service.openweathermap.model;

public class Wind {

    private Double speed;
    private Integer deg;
    private Integer gust;

    public Wind() {
        super();
    }

    @Override
    public String toString() {
        return "Wind{" +
                "speed=" + speed +
                ", deg=" + deg +
                ", gust=" + gust +
                '}';
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getDeg() {
        return deg;
    }

    public void setDeg(Integer deg) {
        this.deg = deg;
    }

    public Integer getGust() {
        return gust;
    }

    public void setGust(Integer gust) {
        this.gust = gust;
    }
}
