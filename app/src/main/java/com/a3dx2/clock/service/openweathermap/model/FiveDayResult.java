package com.a3dx2.clock.service.openweathermap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiveDayResult {

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

    public SingleDayResult[] getList() {
        return list;
    }

    public void setList(SingleDayResult[] list) {
        this.list = list;
    }
}
