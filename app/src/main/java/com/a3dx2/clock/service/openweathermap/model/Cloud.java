package com.a3dx2.clock.service.openweathermap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cloud {

    private Integer all;

    public Cloud() {
        super();
    }

    @Override
    public String toString() {
        return "Cloud{" +
                "all=" + all +
                '}';
    }

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }
}
