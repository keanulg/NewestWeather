package com.example.KeanuWeather.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Keanu on 2014-11-10 .
 */
public class HttpUtil {
    //用于获取地址信息
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    if (listener!=null){
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener!=null){
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }
    //用于获取天气信息
    public static void sendHttpToBaiDu(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpGet request = new HttpGet(address);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                try {
                    HttpResponse response = httpClient.execute(request);
                    HttpEntity httpEntity = response.getEntity();
                    InputStream inputStream = httpEntity.getContent();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    String result = outputStream.toString();
                    if (listener!=null){
                        listener.onFinish(result);
                    }
                } catch (IOException e) {
                    if (listener!=null){
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }
}
