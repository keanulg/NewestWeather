package com.example.KeanuWeather.model;

/**
 * Created by Administrator on 2014-11-20 .
 */
public class DelCityItem {
    private String cityName;
    private boolean isDeleted;

    public DelCityItem(String cityName, boolean isDeleted) {
        this.cityName = cityName;
        this.isDeleted = isDeleted;
    }

    public String getCityName() {
        return cityName;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
