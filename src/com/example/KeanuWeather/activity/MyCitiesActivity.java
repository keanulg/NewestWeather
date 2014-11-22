package com.example.KeanuWeather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.example.KeanuWeather.R;
import com.example.KeanuWeather.model.KeanuWeatherDB;
import com.example.KeanuWeather.model.WeatherGridViewItem;
import com.example.KeanuWeather.util.Utility;
import com.example.KeanuWeather.util.HttpCallbackListener;
import com.example.KeanuWeather.util.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014-11-17 .
 */
public class MyCitiesActivity extends Activity {
    private GridView myCitiesGridView;
    private List<WeatherGridViewItem> list;
    private String  countyName;
    private MycitiesWeatherAdapter adapter;
    private KeanuWeatherDB keanuWeatherDB;
    private Button myCityBackButton;
    private Button lajiButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mycities);
        keanuWeatherDB = KeanuWeatherDB.getInstence(this);
        myCitiesGridView = (GridView) findViewById(R.id.myCitiesGridView);
        myCityBackButton = (Button) findViewById(R.id.myCityBackButton);
        lajiButton = (Button) findViewById(R.id.lajiButton);
        list = keanuWeatherDB.loadMyCitiesWea();
        adapter = new MycitiesWeatherAdapter(this,R.layout.mycities_gridview_item,list);
        myCitiesGridView.setAdapter(adapter);
        setMyCitiesGridView();
        setBackButton();
        setLajiButton();
    }
    //适配器
    class MycitiesWeatherAdapter extends ArrayAdapter<WeatherGridViewItem>{
        int resourceId;

        public MycitiesWeatherAdapter(Context context, int resource, List<WeatherGridViewItem> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public int getCount() {
            return list.size()+1;
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(MyCitiesActivity.this).inflate(resourceId, null);
            Log.d("getViewposition",position+"");
            if (list.size()==position){
                view.setBackgroundResource(R.drawable.bg_add_city_weather);
                view.setLayoutParams(new GridView.LayoutParams(200,300));
            }else{
                WeatherGridViewItem gridViewItem = getItem(position);
                view.setBackgroundResource(R.drawable.weather_city_bg);
                view.setLayoutParams(new GridView.LayoutParams(200,300));

                TextView maxTemPreTextView = (TextView) view.findViewById(R.id.max_Tempreture_TextView);
                maxTemPreTextView.setText(gridViewItem.getMaxTemPer());

                TextView minTemPreTextView = (TextView) view.findViewById(R.id.citysMinTem_TextView);
                minTemPreTextView.setText(gridViewItem.getMinTemPre());

                TextView citysWeatherInfoTextView = (TextView) view.findViewById(R.id.citysWeather_TextView);
                citysWeatherInfoTextView.setText(gridViewItem.getWeatherInfo());

                TextView citysPm25TextView = (TextView) view.findViewById(R.id.citysPm_TextView);
                citysPm25TextView.setText(gridViewItem.getPm25());

                TextView citysNameTextView = (TextView) view.findViewById(R.id.citysName_TextView);
                citysNameTextView.setText(gridViewItem.getCitysName());

                ImageView weatherImageView = (ImageView) view.findViewById(R.id.citys_ImageView);
                weatherImageView.setImageResource(gridViewItem.getWeahterImageId());

            }
            return view;
        }
    }
    //GridView点击事件
    private void setMyCitiesGridView(){
        myCitiesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==list.size()){
                    Intent intent = new Intent(MyCitiesActivity.this,ChooseMyCity.class);
                    startActivityForResult(intent,1);
                }else {
                    //跳到天气主界面
                    WeatherGridViewItem item = list.get(position);
                    String cityName = item.getCitysName();
                    Intent intent = new Intent(MyCitiesActivity.this,WeatherActivityaa.class);
                    intent.putExtra("county_name",cityName);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK){
                    countyName = data.getStringExtra("county_name_return");
                    if (!TextUtils.isEmpty(countyName)){
                        queryFromServer(countyName);
                    }
                }
                break;
            case 2:
                list = keanuWeatherDB.loadMyCitiesWea();
                adapter = new MycitiesWeatherAdapter(MyCitiesActivity.this,R.layout.mycities_gridview_item,list);
                myCitiesGridView.setAdapter(adapter);
                break;
            default:
        }
    }

    /**
     *
     * 从服务器上查询天气
     */

    private void queryFromServer(String countyName){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("location", countyName));
        params.add(new BasicNameValuePair("output", "json"));
        params.add(new BasicNameValuePair("ak", "EDt2lNP1dSfsIXxCsACqiG79"));
        String paramStr = URLEncodedUtils.format(params, "UTF-8");
        String address = "http://api.map.baidu.com/telematics/v3/weather?" + paramStr;
        HttpUtil.sendHttpToBaiDu(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleMyCityWeatherResponse(MyCitiesActivity.this, response, keanuWeatherDB);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list = keanuWeatherDB.loadMyCitiesWea();
                        adapter = new MycitiesWeatherAdapter(MyCitiesActivity.this,R.layout.mycities_gridview_item,list);
                        myCitiesGridView.setAdapter(adapter);
                    }
                });

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    /**
     * 返回按钮
     */
    private void setBackButton(){
        myCityBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * 垃圾按钮
     */
    private void setLajiButton(){
        lajiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCitiesActivity.this,DeleteCityActivity.class);
                startActivityForResult(intent,2);
            }
        });
    }

}
