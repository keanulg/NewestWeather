package com.example.KeanuWeather.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.KeanuWeather.R;
import com.example.KeanuWeather.util.Utility;
import com.example.KeanuWeather.util.HttpCallbackListener;
import com.example.KeanuWeather.util.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keanu on 2014-11-11 .
 */
public class WeatherActivityaa extends Activity {
    private Button updateButton;
    private TextView updateText;
    private TextView updateTime;
    private String countyName;
    private TextView weatherCityName;
    private TextView todayTemp1,todayTemp2,wuranText,mingTemp1,mingTemp2,mingWeather;
    private TextView weatherDes,todayDate,weakDay,mingWeak,mingWuRan,houWuRan;
    private TextView houWeak,houTemp1,houTemp2,houWeather,nongTodaydDate;
    private RotateAnimation anim;
    private Button chooseCityButton,myWeatherButton;
    private ImageView todayWeatherImage,mingWeatherImg,houWeatherImg;
    private ConnectivityManager connectivityManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        houWeak = (TextView) findViewById(R.id.houWeak);
        houTemp1 = (TextView) findViewById(R.id.houTemp1);
        houTemp2 = (TextView) findViewById(R.id.houTemp2);
        houWeather = (TextView) findViewById(R.id.houWeather);
        updateButton = (Button) findViewById(R.id.updateButton);
        updateText = (TextView) findViewById(R.id.updateText);
        updateTime = (TextView) findViewById(R.id.updateTime);
        weatherCityName = (TextView) findViewById(R.id.weatherCityName);
        todayTemp1 = (TextView) findViewById(R.id.todayTemp1);
        todayTemp2 = (TextView) findViewById(R.id.todayTemp2);
        weatherDes = (TextView) findViewById(R.id.weatherDes);
        wuranText = (TextView) findViewById(R.id.wuranText);
        todayDate = (TextView) findViewById(R.id.todayDate);
        weakDay = (TextView) findViewById(R.id.weakDay);
        mingWeak = (TextView) findViewById(R.id.mingWeak);
        mingWuRan = (TextView) findViewById(R.id.mingWuRan);
        houWuRan = (TextView) findViewById(R.id.houWuRan);
        mingTemp1 = (TextView) findViewById(R.id.mingTemp1);
        mingTemp2 = (TextView) findViewById(R.id.mingTemp2);
        mingWeather = (TextView) findViewById(R.id.mingWeather);
        chooseCityButton = (Button) findViewById(R.id.chooseCityButton);
        todayWeatherImage = (ImageView) findViewById(R.id.todayWeatherImage);
        mingWeatherImg = (ImageView) findViewById(R.id.mingWeatherImg);
        houWeatherImg = (ImageView) findViewById(R.id.houWeatherImg);
        myWeatherButton = (Button) findViewById(R.id.myWeatherButton);
        nongTodaydDate = (TextView) findViewById(R.id.nongTodaydDate);
        countyName = getIntent().getStringExtra("county_name");
        if (!TextUtils.isEmpty(countyName)){
            anim = new RotateAnimation(360.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setFillAfter(true);
            anim.setDuration(10000);
            updateButton.startAnimation(anim);
            updateText.setText("更新中.....");
            updateTime.setVisibility(View.GONE);
            queryFromServer(countyName);
        }else {
            //直接获取本地天气信息
            showWeather();
        }
        chooseCity();
        refreshWeather();
        setMenuButton();
        checkNetworkState();
    }

   //从服务器上获取天气
    private void queryFromServer(String countyName){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("location", countyName));
        params.add(new BasicNameValuePair("output", "json"));
        params.add(new BasicNameValuePair("ak", "EDt2lNP1dSfsIXxCsACqiG79"));
        String paramStr = URLEncodedUtils.format(params, "UTF-8");
        String address = "http://api.map.baidu.com/telematics/v3/weather?" + paramStr;
        HttpUtil.sendHttpToBaiDu(address,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivityaa.this, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //更新UI
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    /**
     * 读取xml文件存储的天气信息
     */
    private void showWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String dateTime = preferences.getString("date_time", null);
        updateText.setText("更新于 ");
        updateTime.setText(dateTime);
        updateTime.setVisibility(View.VISIBLE);
        Log.d("date_time",preferences.getString("date_time",null));
        weatherCityName.setText(preferences.getString("city_name",null));
        String todayTemperature = preferences.getString("today_temperature",null);
        String nongli = preferences.getString("nong_li",null);
        nongTodaydDate.setText(nongli.substring(5,nongli.length()));
        //Log.d("todayTemperature",todayTemperature);
        if (todayTemperature.contains("~")){
            String[] array = todayTemperature.split("~");
//        for (int i = 0; i <array.length ; i++) {
//            Log.d("todayTemperature",array[i]);
//        }
            todayTemp1.setText(array[0].trim()+"℃");
            todayTemp2.setText(array[1].trim());
        }else {
            todayTemp1.setText(todayTemperature);
        }
        weatherDes.setText(preferences.getString("today_weather",null));
        String pm25Str = preferences.getString("pm25",null);
        int pm25 = -1;
        if (!pm25Str.equals("")){
            pm25 = Integer.parseInt(pm25Str);
        }

        if (pm25>=0&&pm25<=150){
            wuranText.setText("轻度污染");
            mingWuRan.setText("轻度污染");
            houWuRan.setText("轻度污染");
        }else if (pm25>=151&&pm25<=200){
            wuranText.setText("中度污染");
            mingWuRan.setText("中度污染");
            houWuRan.setText("中度污染");
        }else if (pm25>=201&&pm25<=300){
            wuranText.setText("重度污染");
            mingWuRan.setText("重度污染");
            houWuRan.setText("重度污染");
        }else if (pm25>=301){
            wuranText.setText("严重污染");
            mingWuRan.setText("严重污染");
            houWuRan.setText("严重污染");
        }else {
            wuranText.setText("PM2.5为空");
            mingWuRan.setText("PM2.5为空");
            houWuRan.setText("PM2.5为空");
        }
        String updateDate = preferences.getString("update_date",null);
        Log.d("updateDate",updateDate);
        String[] arrayDate = updateDate.split("-");
        todayDate.setText(arrayDate[0]+"年"+arrayDate[1]+"月"+arrayDate[2]+"日");
        String todayDate = preferences.getString("today_date",null);
        String[] arrayTodayDate = todayDate.split(" ");
//        for (int i = 0; i <arrayTodayDate.length ; i++) {
//            Log.d("arrayTodayDate",arrayTodayDate[i]);
//        }
        weakDay.setText("星期"+arrayTodayDate[0].trim().substring(arrayTodayDate[0].trim().length()-1,arrayTodayDate[0].trim().length()));
        String tomorrowDate = preferences.getString("tomorrow_date",null);
        mingWeak.setText("星期"+tomorrowDate.charAt(1));
        String tomorrowTemp = preferences.getString("tomorrow_temperature",null);
        String[] arrayTomorrowTemp = tomorrowTemp.split("~");
        mingTemp1.setText(arrayTomorrowTemp[0].trim()+"℃");
        mingTemp2.setText(arrayTomorrowTemp[1].trim());
        mingWeather.setText(preferences.getString("tomorrow_weather",null));

        String aftertomorrowDate = preferences.getString("aftertomorrow_date",null);
        houWeak.setText("星期"+aftertomorrowDate.charAt(1));
        String aftertomorrowTemp = preferences.getString("aftertomorrow_temperature",null);
        String[] arrayAftertomorrowTemp = aftertomorrowTemp.split("~");
        houTemp1.setText(arrayAftertomorrowTemp[0].trim()+"℃");
        houTemp2.setText(arrayAftertomorrowTemp[1].trim());
        houWeather.setText(preferences.getString("aftertomorrow_weather",null));
        updateWeatherImg(preferences.getString("today_weather",null),todayWeatherImage);
        updateWeatherImg(preferences.getString("tomorrow_weather",null),mingWeatherImg);
        updateWeatherImg(preferences.getString("aftertomorrow_weather",null),houWeatherImg);
    }
    //切换城市按钮
    private void chooseCity(){
        chooseCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivityaa.this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
            }
        });
    }
    private void updateWeatherImg(String weatherInfo,ImageView imageView){
        if (weatherInfo.equals("晴")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_qing);
        }else if (weatherInfo.contains("多云")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        }else if (weatherInfo.contains("暴雪")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        }else if (weatherInfo.contains("暴雨")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        }else if (weatherInfo.contains("大雪")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_daxue);
        }else if (weatherInfo.contains("大雨")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if (weatherInfo.contains("雷阵雨")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        }else if (weatherInfo.contains("雾")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_wu);
        }else if (weatherInfo.contains("小雪")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        }else if (weatherInfo.contains("小雨")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        }else if (weatherInfo.contains("阴")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_yin);
        }else if (weatherInfo.contains("阵雨")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        }else if (weatherInfo.contains("中雨")){
            imageView.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }else {
            imageView.setImageResource(R.drawable.biz_plugin_weather_qing);
        }
    }
    /**
     * 更新天气
     */
    private void refreshWeather(){
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("updateButton", "updateButton");
                anim = new RotateAnimation(360.0f, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setFillAfter(true);
                anim.setDuration(10000);
                updateButton.startAnimation(anim);
                updateText.setText("更新中.....");
                updateTime.setVisibility(View.GONE);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivityaa.this);
                String countyName2 = preferences.getString("city_name", null);
                Log.d("countyName2", countyName2);
                queryFromServer(countyName2);
            }
        });
    }
    //菜单按钮
    private void setMenuButton(){
        myWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivityaa.this,MenuActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * 检测网络是否连接
     */
    private boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (connectivityManager.getActiveNetworkInfo() != null) {
            flag = connectivityManager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            setNetwork();
        } else {
            Toast.makeText(this,"网络可用",Toast.LENGTH_SHORT).show();
        }

        return flag;
    }
    /**
     * 网络未连接时，调用设置方法
     */
    private void setNetwork(){
        Toast.makeText(this, "wifi is closed!", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("网络提示信息");
        builder.setMessage("网络不可用，如果继续，请先设置网络！");
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                startActivity(intent);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }
}
