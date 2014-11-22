package com.example.KeanuWeather.model;

/**
 * Created by Keanu on 2014-11-10 .
 */
public class Province {
    private int provinceId;
    private String provinceName;
    private String provinceCode;

    public int getProvinceId() {
        return provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
