package com.a3dx2.clock.service.openweathermap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiveDayResult {

    private String country;
    private City city;
    private SingleDayResult[] list;

    public FiveDayResult() {
        super();
    }

    @Override
    public String toString() {
        return "FiveDayResult{" +
                "list=" + Arrays.toString(list) +
                '}';
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public SingleDayResult[] getList() {
        return list;
    }

    public void setList(SingleDayResult[] list) {
        this.list = list;
    }
}
