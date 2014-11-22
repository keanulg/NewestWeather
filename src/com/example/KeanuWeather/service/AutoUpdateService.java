package com.example.KeanuWeather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import com.example.KeanuWeather.receiver.AutoUpdateReceiver;
import com.example.KeanuWeather.util.Utility;
import com.example.KeanuWeather.util.HttpCallbackListener;
import com.example.KeanuWeather.util.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014-11-11 .
 */
public class AutoUpdateService extends Service {
    AlarmManager manager;
    PendingIntent pi;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        SharedPreferences preferences = getSharedPreferences("duration",MODE_PRIVATE);
        int duration = preferences.getInt("service_duration",0);
        Log.d("duration==",duration+"");
        if (duration==0){
            onDestroy();
        }
        long triggerTime = SystemClock.elapsedRealtime()+duration;
        Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
        pi = PendingIntent.getBroadcast(this,0,intent1,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        manager.cancel(pi);
        super.onDestroy();
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = preferences.getString("city_name","");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("location", cityName));
        params.add(new BasicNameValuePair("output", "json"));
        params.add(new BasicNameValuePair("ak", "EDt2lNP1dSfsIXxCsACqiG79"));
        String paramStr = URLEncodedUtils.format(params, "UTF-8");
        String address = "http://api.map.baidu.com/telematics/v3/weather?" + paramStr;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
