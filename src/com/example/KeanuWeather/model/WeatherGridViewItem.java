package com.example.KeanuWeather.model;

/**
 * Created by Administrator on 2014-11-20 .
 */
public class WeatherGridViewItem {
    private String maxTemPer;
    private String minTemPre;
    private String weatherInfo;
    private String pm25;
    private int weahterImageId;
    private String citysName;

    public WeatherGridViewItem(String maxTemPer, String minTemPre, String weatherInfo, String pm25, int weahterImageId, String citysName) {
        this.maxTemPer = maxTemPer;
        this.minTemPre = minTemPre;
        this.weatherInfo = weatherInfo;
        this.pm25 = pm25;
        this.weahterImageId = weahterImageId;
        this.citysName = citysName;
    }

    public String getMaxTemPer() {
        return maxTemPer;
    }

    public String getMinTemPre() {
        return minTemPre;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public String getPm25() {
        return pm25;
    }

    public int getWeahterImageId() {
        return weahterImageId;
    }

    public String getCitysName() {
        return citysName;
    }
}
