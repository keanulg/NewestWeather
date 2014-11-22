package com.example.KeanuWeather.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.KeanuWeather.R;
import com.example.KeanuWeather.model.KeanuWeatherDB;
import com.example.KeanuWeather.model.WeatherGridViewItem;

import java.util.List;

/**
 * Created by Administrator on 2014-11-20 .
 */
public class DeleteCityActivity extends Activity {
    private Button delCityBackButton;
    private ListView deleteCityList;
    private List<WeatherGridViewItem> list;
    private DelCityAdapter adapter;
    private KeanuWeatherDB keanuWeatherDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_city_delete);
        delCityBackButton = (Button) findViewById(R.id.deleteCityBackButton);
        deleteCityList = (ListView) findViewById(R.id.deleteCityList);
        keanuWeatherDB = KeanuWeatherDB.getInstence(this);
        setdelCityBackButton();
        list = keanuWeatherDB.loadMyCitiesWea();
        adapter = new DelCityAdapter(DeleteCityActivity.this,R.layout.city_delete_item,list);
        deleteCityList.setAdapter(adapter);
    }
    //返回按钮
    private void setdelCityBackButton(){
        delCityBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里需要传值,以便刷新上一页的数据
                Intent intent = new Intent();
                intent.putExtra("del_success","Done");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    //重写back键

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("del_success","Done");
        setResult(RESULT_OK,intent);
        finish();
    }

    class DelCityAdapter extends ArrayAdapter<WeatherGridViewItem>{
        int resourceId;

        DelCityAdapter(Context context, int resource, List<WeatherGridViewItem> objects) {
            super(context, resource,objects);
            this.resourceId = resource;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            final WeatherGridViewItem cityItem = getItem(position);
            TextView deleteCityName = (TextView) view.findViewById(R.id.deleteCityName);
            deleteCityName.setText(cityItem.getCitysName());
            Button deleteCityButton = (Button) view.findViewById(R.id.deleteCityButton);
            deleteCityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DeleteCityActivity.this);
                    dialog.setTitle("确定要删除？");
                    dialog.setIcon(R.drawable.ic_launcher);
                    dialog.setNegativeButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String cityName = cityItem.getCitysName();
                            Log.d("cityNamecityName",cityName);
                            keanuWeatherDB.delMyCity(cityName);
                            list = keanuWeatherDB.loadMyCitiesWea();
                            adapter = new DelCityAdapter(DeleteCityActivity.this,R.layout.city_delete_item,list);
                            deleteCityList.setAdapter(adapter);
                        }
                    });
                    dialog.setPositiveButton("取消",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();

                }
            });
            return view;
        }
    }
}
