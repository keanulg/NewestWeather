package com.example.KeanuWeather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.KeanuWeather.db.KeanuWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keanu on 2014-11-10 .
 */
public class KeanuWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "keanu_weather";
    /**
     * 数据库版本
     */
    public static final int VERSION = 2;
    private static KeanuWeatherDB keanuWeatherDB;
    private SQLiteDatabase db;
    /**
     * 将构造方法私有化
     */
    private KeanuWeatherDB(Context context){
        KeanuWeatherOpenHelper dbHelper = new KeanuWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }
    /**
     * 获取KeanuWeatherDB的实例
     */
    public synchronized static KeanuWeatherDB getInstence(Context context){
        if (keanuWeatherDB==null){
            keanuWeatherDB = new KeanuWeatherDB(context);
        }
        return keanuWeatherDB;
    }
    /**
     * 将province实例存储到数据库
     */
    public void saveProvince(Province province){
        if (province!=null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("provinces",null,values);
        }
    }
    /**
     * 从数据库读取全国所有的省份信息
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        String sql = "select * from  provinces";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst()){
            do {
                int provinceId = cursor.getInt(cursor.getColumnIndex("province_id"));
                String provinceName = cursor.getString(cursor.getColumnIndex("province_name"));
                String provinceCode = cursor.getString(cursor.getColumnIndex("province_code"));
                Province province = new Province();
                province.setProvinceId(provinceId);
                province.setProvinceCode(provinceCode);
                province.setProvinceName(provinceName);
                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /**
     * 将city实例存储到数据库
     */
    public void saveCity(City city){
        ContentValues values = new ContentValues();
        values.put("city_name",city.getCityName());
        values.put("city_code",city.getCityCode());
        values.put("province_id",city.getProvinceId());
        db.insert("cities",null,values);
    }
    /**
     * 将我的城市实例添加到数据库
     */
    public void savaMyCityWeather(WeatherGridViewItem item) {
        boolean flag = false;
        String citysName = "xx";
        String cityName = item.getCitysName();
        Log.d("cityName==",cityName.trim());
        String sql = "select * from myCityWeather";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                citysName = cursor.getString(cursor.getColumnIndex("citysName")).trim();
                Log.d("citysName",citysName);
                if (citysName.equals(cityName)) {
                    flag = true;
                }
            } while (cursor.moveToNext());
        }
        Log.d("flagflag",flag+"");
        if (flag == false) {
            ContentValues values = new ContentValues();
            values.put("maxTemPer", item.getMaxTemPer());
            values.put("minTemPre", item.getMinTemPre());
            values.put("weatherInfo", item.getWeatherInfo());
            values.put("pm25", item.getPm25());
            values.put("weahterImageId", item.getWeahterImageId());
            values.put("citysName", item.getCitysName());
            db.insert("myCityWeather", null, values);
        }
    }

    /**
     * 从数据库读取全国所有的城市信息
     */
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<City>();
        String sql = "select * from  cities where province_id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{provinceId+""});
        if (cursor.moveToFirst()){
            do {
                int cityId = cursor.getInt(cursor.getColumnIndex("city_id"));
                String cityName = cursor.getString(cursor.getColumnIndex("city_name"));
                String cityCode = cursor.getString(cursor.getColumnIndex("city_code"));
                City city = new City();
                city.setCityId(cityId);
                city.setCityName(cityName);
                city.setCityCode(cityCode);
                city.setProvinceId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /**
     * 将county实例存储到数据库
     */
    public void saveCounty(County county){
        ContentValues values = new ContentValues();
        values.put("county_name",county.getCountyName());
        values.put("county_code",county.getCountyCode());
        values.put("city_id",county.getCityId());
        db.insert("counties",null,values);
    }
    /**
     * 从数据库读取全国所有的县信息
     */
    public List<County> loadcounties(int cityId){
        List<County> list = new ArrayList<County>();
        String sql = "select * from  counties where city_id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{cityId+""});
        if (cursor.moveToFirst()){
            do {
                int countyId = cursor.getInt(cursor.getColumnIndex("county_id"));
                String countyName = cursor.getString(cursor.getColumnIndex("county_name"));
                String countyCode = cursor.getString(cursor.getColumnIndex("county_code"));
                County county = new County();
                county.setCountyId(countyId);
                county.setCountyName(countyName);
                county.setCountyCode(countyCode);
                county.setCityId(cityId);
                list.add(county);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /**
     * 从数据库读取我的城市天气信息
     */
    public List<WeatherGridViewItem> loadMyCitiesWea(){
        List<WeatherGridViewItem> list = new ArrayList<WeatherGridViewItem>();
        String sql = "select * from myCityWeather";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst()){
            do {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                String maxTemPer = cursor.getString(cursor.getColumnIndex("maxTemPer"));
                String minTemPre = cursor.getString(cursor.getColumnIndex("minTemPre"));
                String weatherInfo = cursor.getString(cursor.getColumnIndex("weatherInfo"));
                String pm25 = cursor.getString(cursor.getColumnIndex("pm25"));
                int weahterImageId = cursor.getInt(cursor.getColumnIndex("weahterImageId"));
                String citysName = cursor.getString(cursor.getColumnIndex("citysName"));
                WeatherGridViewItem item = new WeatherGridViewItem(maxTemPer,minTemPre,weatherInfo,pm25,weahterImageId,citysName);
                list.add(item);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /**
     * 从数据库中删除我的城市天气信息
     */
    public void delMyCity(String cityName){
        String sql = "delete from myCityWeather where citysName = ?";
        db.execSQL(sql,new String[]{cityName});
    }
}
