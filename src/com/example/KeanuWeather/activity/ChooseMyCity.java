package com.example.KeanuWeather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.example.KeanuWeather.R;
import com.example.KeanuWeather.model.City;
import com.example.KeanuWeather.model.County;
import com.example.KeanuWeather.model.KeanuWeatherDB;
import com.example.KeanuWeather.model.Province;
import com.example.KeanuWeather.util.Utility;
import com.example.KeanuWeather.util.HttpCallbackListener;
import com.example.KeanuWeather.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014-11-20 .
 */
public class ChooseMyCity extends Activity {
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private KeanuWeatherDB keanuWeatherDB;
    private List<String> datalist = new ArrayList<String>();
    private ListView chooseAreaListView;
    private ArrayAdapter<String> adapter;
    private TextView areaTitle;
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int currentLevel;
    private ProgressDialog progressDialog;
    private Province selectProvince;
    private City selectCity;
    private boolean isFromWeatherActivity;
    private Button areaPageBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_area);
        chooseAreaListView = (ListView) findViewById(R.id.chooseAreaListView);
        areaTitle = (TextView) findViewById(R.id.areaTitle);
        areaPageBack = (Button) findViewById(R.id.areaPageBack);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datalist);
        chooseAreaListView.setAdapter(adapter);
        keanuWeatherDB = KeanuWeatherDB.getInstence(this);
        chooseAreaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    Log.d("position", position + "");
                    selectProvince = provinceList.get(position);
                    Log.d("selectProvince",selectProvince.getProvinceName());
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectCity = cityList.get(position);
                    Log.d("selectCity",selectCity.getCityName());
                    queryCounties();
                }else if (currentLevel==LEVEL_COUNTY){
                    String countyName = countyList.get(position).getCountyName();
                    Intent intent = new Intent();
                    intent.putExtra("county_name_return",countyName);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        queryProvinces();
        setAreaPageBack();
    }

    /**
     * 查询全国所有的省
     */
    private void queryProvinces(){
        provinceList = keanuWeatherDB.loadProvinces();
        if (provinceList.size()>0){
            datalist.clear();
            for (Province province:provinceList){
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            chooseAreaListView.setSelection(0);
            areaTitle.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            //从服务器上查询
            queryFromServer(null,"province");
        }
    }
    /**
     * 查询省内所有的市
     */
    private void queryCities(){
        cityList = keanuWeatherDB.loadCities(selectProvince.getProvinceId());
        Log.d("getProvinceId",selectProvince.getProvinceId()+"");
        if (cityList.size()>0){
            datalist.clear();
            for (City city:cityList){
                Log.d("getCityName",city.getCityName());
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            chooseAreaListView.setSelection(0);
            areaTitle.setText(selectProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            Log.d("getProvinceCode",selectProvince.getProvinceCode());
            queryFromServer(selectProvince.getProvinceCode(),"city");
        }
    }
    /**
     * 查询市内所有的县
     */
    private void queryCounties(){
        countyList = keanuWeatherDB.loadcounties(selectCity.getCityId());
        if (countyList.size()>0){
            datalist.clear();
            for (County county:countyList){
                datalist.add(county.getCountyName());
                Log.d("getCountyName",county.getCountyName());
                Log.d("getCountyCode",county.getCountyCode());
                Log.d("getCountyId",county.getCountyId()+"");
            }
            adapter.notifyDataSetChanged();
            chooseAreaListView.setSelection(0);
            areaTitle.setText(selectCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectCity.getCityCode(),"county");
        }
    }
    /**
     * 从服务器上查询数据
     */
    private void queryFromServer(final String code,final String type){
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if (type.equals("province")) {
                    result = Utility.handleProvincesResponse(keanuWeatherDB, response);
                } else if (type.equals("city")) {
                    result = Utility.handleCitiesResponse(keanuWeatherDB, response, selectProvince.getProvinceId());
                } else if (type.equals("county")) {
                    result = Utility.handleCountiesResponse(keanuWeatherDB, response, selectCity.getCityId());
                }
                if (result) {
                    //返回主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals("province")) {
                                queryProvinces();
                            } else if (type.equals("city")) {
                                //查询city数据
                                queryCities();
                            } else if (type.equals("county")) {
                                //查询county数据
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //回到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseMyCity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    /**
     * 显示进度框
     */
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("数据加载中.....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度框
     */
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    /**
     * 重写back键
     */
    @Override
    public void onBackPressed() {
        if (currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
    //返回按钮
    private void setAreaPageBack(){
        areaPageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    queryProvinces();
                }else {
                    finish();
                }
            }
        });
    }
}
