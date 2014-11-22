package com.example.KeanuWeather.model;

/**
 * Created by Keanu on 2014-11-10 .
 */
public class County {
    private int countyId;
    private String countyName;
    private String countyCode;
    private int cityId;

    public int getCountyId() {
        return countyId;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
