package com.example.KeanuWeather.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.example.KeanuWeather.R;
import com.example.KeanuWeather.activity.WeatherActivityaa;
import com.example.KeanuWeather.service.AutoUpdateService;

/**
 * Created by Administrator on 2014-11-11 .
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String cityName = preferences.getString("city_name","");
        String temp = preferences.getString("today_temperature","");
        String weatherDesp = preferences.getString("today_weather","");
        Intent intent2 = new Intent(context, WeatherActivityaa.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,intent2,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.myicon,"基努天气播报",System.currentTimeMillis());
        notification.setLatestEventInfo(context,cityName,temp+"\n"+weatherDesp,pi);
        manager.notify(1,notification);
        Intent intent1 = new Intent(context, AutoUpdateService.class);
        context.startService(intent1);
    }
}
