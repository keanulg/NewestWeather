package com.example.KeanuWeather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.example.KeanuWeather.R;
import com.example.KeanuWeather.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Keanu on 2014-11-10 .
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(KeanuWeatherDB keanuWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    keanuWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(KeanuWeatherDB keanuWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    keanuWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountiesResponse(KeanuWeatherDB keanuWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    keanuWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据
     */
    public static void handleWeatherResponse(Context context, String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            String updateDate = jsonObject.optString("date");
            String status = jsonObject.optString("status");
            if (status.equals("success")) {
                JSONArray jsonArray = jsonObject.optJSONArray("results");
                JSONObject cityJSONObject = jsonArray.getJSONObject(0);
                String pm25 = cityJSONObject.optString("pm25");
                String currentCity = cityJSONObject.optString("currentCity");
                JSONArray weatherDataJsonArray = cityJSONObject.optJSONArray("weather_data");
                JSONObject todayWeatherData = weatherDataJsonArray.optJSONObject(0);
                String todayDate = todayWeatherData.optString("date");
                String todayDayPictureUrl = todayWeatherData.optString("dayPictureUrl");
                String todayNightPictureUrl = todayWeatherData.optString("nightPictureUrl");
                String todayWeather = todayWeatherData.optString("weather");
                String todayTemperature = todayWeatherData.optString("temperature");
                Log.d("todayTemperature=========", todayTemperature.toString());
                JSONObject tomorrowWeatherData = weatherDataJsonArray.optJSONObject(1);
                String tomorrowDate = tomorrowWeatherData.optString("date");
                String tomorrowDayPictureUrl = tomorrowWeatherData.optString("dayPictureUrl");
                String tomorrowNightPictureUrl = tomorrowWeatherData.optString("nightPictureUrl");
                String tomorrowWeather = tomorrowWeatherData.optString("weather");
                String tomorrowTemperature = tomorrowWeatherData.optString("temperature");
                JSONObject afterTomorrowWeatherData = weatherDataJsonArray.optJSONObject(2);
                String afterTomorrowDate = afterTomorrowWeatherData.optString("date");
                String afterTomorrowDayPictureUrl = afterTomorrowWeatherData.optString("dayPictureUrl");
                String afterTomorrowNightPictureUrl = afterTomorrowWeatherData.optString("nightPictureUrl");
                String afterTomorrowWeather = afterTomorrowWeatherData.optString("weather");
                String afterTomorrowTemperature = afterTomorrowWeatherData.optString("temperature");
                SimpleDateFormat formater = new SimpleDateFormat("M月d日 EEE HH:mm:ss", Locale.CHINA);
                Date date = new Date();
                String dateTime = formater.format(date);
                SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                String chineseDate = chineseDateFormat.format(date);
                Calendar calendar = Calendar.getInstance();
                String nongLi = "";
                try {
                    calendar.setTime(chineseDateFormat.parse(chineseDate));
                    NongLi nong = new NongLi(calendar);
                    nongLi = String.valueOf(nong);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //存到本地
                saveWeatherInfo(context,
                        pm25,
                        currentCity,
                        todayDate,
                        todayDayPictureUrl,
                        todayNightPictureUrl,
                        todayWeather,
                        todayTemperature,
                        tomorrowDate,
                        tomorrowDayPictureUrl,
                        tomorrowNightPictureUrl,
                        tomorrowWeather,
                        tomorrowTemperature,
                        afterTomorrowDate,
                        afterTomorrowDayPictureUrl,
                        afterTomorrowNightPictureUrl,
                        afterTomorrowWeather,
                        afterTomorrowTemperature,
                        updateDate,
                        dateTime,
                        nongLi);
            } else {
                Toast.makeText(context, "百度天气接口无法获取此地区天气", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将天气信息存到xml文件中
     */
    public static void saveWeatherInfo(Context context,
                                       String pm25,
                                       String currentCity,
                                       String todayDate,
                                       String todayDayPictureUrl,
                                       String todayNightPictureUrl,
                                       String todayWeather,
                                       String todayTemperature,
                                       String tomorrowDate,
                                       String tomorrowDayPictureUrl,
                                       String tomorrowNightPictureUrl,
                                       String tomorrowWeather,
                                       String tomorrowTemperature,
                                       String afterTomorrowDate,
                                       String afterTomorrowDayPictureUrl,
                                       String afterTomorrowNightPictureUrl,
                                       String afterTomorrowWeather,
                                       String afterTomorrowTemperature,
                                       String updateDate,
                                       String dateTime,
                                       String nongLi) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("update_date", updateDate);
        editor.putString("date_time", dateTime);
        editor.putString("nong_li", nongLi);
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", currentCity);
        editor.putString("pm25", pm25);
        editor.putString("today_date", todayDate);
        editor.putString("today_day_picture_url", todayDayPictureUrl);
        editor.putString("today_night_picture_url", todayNightPictureUrl);
        editor.putString("today_weather", todayWeather);
        editor.putString("today_temperature", todayTemperature);
        editor.putString("tomorrow_date", tomorrowDate);
        editor.putString("tomorrow_day_picture_url", tomorrowDayPictureUrl);
        editor.putString("tomorrow_night_picture_url", tomorrowNightPictureUrl);
        editor.putString("tomorrow_weather", tomorrowWeather);
        editor.putString("tomorrow_temperature", tomorrowTemperature);
        editor.putString("aftertomorrow_date", afterTomorrowDate);
        editor.putString("aftertomorrow_day_picture_url", afterTomorrowDayPictureUrl);
        editor.putString("aftertomorrow_night_picture_url", afterTomorrowNightPictureUrl);
        editor.putString("aftertomorrow_weather", afterTomorrowWeather);
        editor.putString("aftertomorrow_temperature", afterTomorrowTemperature);
        editor.commit();
    }

    /**
     * 处理城市收藏界面的数据
     */
    public static void handleMyCityWeatherResponse(Context context, String result, KeanuWeatherDB keanuWeatherDB) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String updateDate = jsonObject.optString("date");
            String status = jsonObject.optString("status");
            if (status.equals("success")) {
                JSONArray jsonArray = jsonObject.optJSONArray("results");
                JSONObject cityJSONObject = jsonArray.getJSONObject(0);
                String pm25 = cityJSONObject.optString("pm25");
                String currentCity = cityJSONObject.optString("currentCity");
                JSONArray weatherDataJsonArray = cityJSONObject.optJSONArray("weather_data");
                JSONObject todayWeatherData = weatherDataJsonArray.optJSONObject(0);
                String todayDate = todayWeatherData.optString("date");
                String todayDayPictureUrl = todayWeatherData.optString("dayPictureUrl");
                String todayNightPictureUrl = todayWeatherData.optString("nightPictureUrl");
                String todayWeather = todayWeatherData.optString("weather");
                String todayTemperature = todayWeatherData.optString("temperature");
                Log.d("todayTemperature=========", todayTemperature.toString());
                //存到数据库
                saveMyWeatherInfo(todayTemperature, todayWeather, currentCity, pm25, keanuWeatherDB);
            } else {
                Toast.makeText(context, "百度天气接口无法获取此地区天气", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将我的城市信息存到数据库
     */
    private static void saveMyWeatherInfo(String todayTemperature, String weatherInfo, String currentCity, String pm25Str, KeanuWeatherDB keanuWeatherDB) {
        String minTemPre = "";
        String maxTemPer = "";
        if (todayTemperature.contains("~")){
            String[] array = todayTemperature.split("~");
             minTemPre = array[1].trim();
             maxTemPer = array[0].trim() + "℃";
        }else {
            minTemPre = todayTemperature;
            maxTemPer = todayTemperature;
        }
        int pm25 = -1;
        if (!pm25Str.equals("")) {
            pm25 = Integer.parseInt(pm25Str);
        }
        if (pm25 >= 0 && pm25 <= 150) {
            pm25Str = "轻度污染";
        } else if (pm25 >= 151 && pm25 <= 200) {
            pm25Str = "中度污染";
        } else if (pm25 >= 201 && pm25 <= 300) {
            pm25Str = "重度污染";
        } else if (pm25 >= 301) {
            pm25Str = "严重污染";
        } else {
            pm25Str = "pm25为空";
        }
        int weahterImageId = 0;
        if (weatherInfo.equals("晴")) {
            weahterImageId = R.drawable.biz_plugin_weather_qing;
        } else if (weatherInfo.contains("多云")) {
            weahterImageId = R.drawable.biz_plugin_weather_duoyun;
        } else if (weatherInfo.contains("暴雪")) {
            weahterImageId = R.drawable.biz_plugin_weather_baoxue;
        } else if (weatherInfo.contains("暴雨")) {
            weahterImageId = R.drawable.biz_plugin_weather_baoyu;
        } else if (weatherInfo.contains("大雪")) {
            weahterImageId = R.drawable.biz_plugin_weather_daxue;
        } else if (weatherInfo.contains("大雨")) {
            weahterImageId = R.drawable.biz_plugin_weather_dayu;
        } else if (weatherInfo.contains("雷阵雨")) {
            weahterImageId = R.drawable.biz_plugin_weather_leizhenyu;
        } else if (weatherInfo.contains("雾")) {
            weahterImageId = R.drawable.biz_plugin_weather_wu;
        } else if (weatherInfo.contains("小雪")) {
            weahterImageId = R.drawable.biz_plugin_weather_xiaoxue;
        } else if (weatherInfo.contains("小雨")) {
            weahterImageId = R.drawable.biz_plugin_weather_xiaoyu;
        } else if (weatherInfo.contains("阴")) {
            weahterImageId = R.drawable.biz_plugin_weather_yin;
        } else if (weatherInfo.contains("阵雨")) {
            weahterImageId = R.drawable.biz_plugin_weather_zhenyu;
        } else if (weatherInfo.contains("中雨")) {
            weahterImageId = R.drawable.biz_plugin_weather_zhongyu;
        } else {
            weahterImageId = R.drawable.biz_plugin_weather_qing;
        }
        WeatherGridViewItem item = new WeatherGridViewItem(maxTemPer, minTemPre, weatherInfo, pm25Str, weahterImageId, currentCity);
        keanuWeatherDB.savaMyCityWeather(item);
    }
}
