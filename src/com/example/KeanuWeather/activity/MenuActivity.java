package com.example.KeanuWeather.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.example.KeanuWeather.R;
import com.example.KeanuWeather.service.AutoUpdateService;

/**
 * Created by Administrator on 2014-11-15 .
 */
public class MenuActivity extends Activity {
    private CheckBox selfUpdateCheckBox;
    private LinearLayout updateDurLayout;
    private CheckBox oneCheckBox, threeCheckBox, fiveCheckBox, testCheckBox;
    private TextView isSelectedDurText;
    private SharedPreferences.Editor editor;
    private LinearLayout myCitiesLayout;
    private Button menuPageButton;
    private LinearLayout lastLine;
    private Intent intent;
    private ConnectivityManager connectivityManager;
    private CheckBox setWifiUpdateCheckbox;
    private LinearLayout aboutKeanu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        intent = new Intent(MenuActivity.this, AutoUpdateService.class);
        selfUpdateCheckBox = (CheckBox) findViewById(R.id.selfUpdateCheckBox);
        updateDurLayout = (LinearLayout) findViewById(R.id.updateDurLayout);
        isSelectedDurText = (TextView) findViewById(R.id.isSelectedDurText);
        myCitiesLayout = (LinearLayout) findViewById(R.id.myCitiesLayout);
        menuPageButton = (Button) findViewById(R.id.menuPageButton);
        lastLine = (LinearLayout) findViewById(R.id.lastLine);
        setWifiUpdateCheckbox = (CheckBox) findViewById(R.id.setWifiUpdateCheckbox);
        aboutKeanu = (LinearLayout) findViewById(R.id.aboutKeanu);
        editor = getSharedPreferences("duration", MODE_PRIVATE).edit();
        selfUpdate();
        selectDur();
        setMyCities();
        setBackButton();
        setUpdateOnWifi();
        setAboutKeanu();
    }
//自动更新
    private void selfUpdate() {
        selfUpdateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    updateDurLayout.setVisibility(View.VISIBLE);
                    lastLine.setVisibility(View.VISIBLE);
                    intent.putExtra("isShutBroad", false);
                    editor.putInt("service_duration",1*60*60*1000);
                    MenuActivity.this.startService(intent);
                    setWifiUpdateCheckbox.setVisibility(View.VISIBLE);
                } else {
                    MenuActivity.this.stopService(intent);//停止服务
                    updateDurLayout.setVisibility(View.INVISIBLE);
                    lastLine.setVisibility(View.INVISIBLE);
                    setWifiUpdateCheckbox.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //选择更新周期
    private void selectDur() {
        updateDurLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MenuActivity.this);
                dialog.setTitle("请选择更新周期");
                dialog.setIcon(R.drawable.ic_launcher);
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.duration_select, null);
                dialog.setView(linearLayout);
                oneCheckBox = (CheckBox) linearLayout.findViewById(R.id.oneCheckBox);
                threeCheckBox = (CheckBox) linearLayout.findViewById(R.id.threeCheckBox);
                fiveCheckBox = (CheckBox) linearLayout.findViewById(R.id.fiveCheckBox);
                testCheckBox = (CheckBox) linearLayout.findViewById(R.id.testCheckBox);
                oneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            isSelectedDurText.setText("每隔一小时");
                            editor.putInt("service_duration",1*60*60*1000);
                            editor.commit();
                            threeCheckBox.setChecked(false);
                            fiveCheckBox.setChecked(false);
                            testCheckBox.setChecked(false);
                        } else {
                            isSelectedDurText.setText("");
                            MenuActivity.this.stopService(intent);
                        }
                    }
                });
                threeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            isSelectedDurText.setText("每隔三小时");
                            editor.putInt("service_duration",3*60*60*1000);
                            editor.commit();
                            oneCheckBox.setChecked(false);
                            fiveCheckBox.setChecked(false);
                            testCheckBox.setChecked(false);
                        } else {
                            isSelectedDurText.setText("");
                            MenuActivity.this.stopService(intent);
                        }
                    }
                });
                fiveCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            isSelectedDurText.setText("每隔五小时");
                            editor.putInt("service_duration",5*60*60*1000);
                            editor.commit();
                            oneCheckBox.setChecked(false);
                            threeCheckBox.setChecked(false);
                            testCheckBox.setChecked(false);
                        } else {
                            isSelectedDurText.setText("");
                            MenuActivity.this.stopService(intent);
                        }
                    }
                });
                testCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            isSelectedDurText.setText("每隔十秒");
                            editor.putInt("service_duration",10000);
                            editor.commit();
                            oneCheckBox.setChecked(false);
                            threeCheckBox.setChecked(false);
                            fiveCheckBox.setChecked(false);
                        } else {
                            isSelectedDurText.setText("");
                            MenuActivity.this.stopService(intent);
                        }
                    }
                });
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.remove("service_duration");
                    }
                });
                dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MenuActivity.this.stopService(intent);
                        MenuActivity.this.startService(intent);
                    }
                });
                dialog.show();
            }
        });

    }
    //我的城市
    private void setMyCities(){
        myCitiesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,MyCitiesActivity.class);
                startActivity(intent);
            }
        });
    }

    //back按钮
    private void setBackButton(){
        menuPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * 判断网络是否是wifi
     */
    private boolean isWifi(){
        boolean flag = false;
        if (connectivityManager.getActiveNetworkInfo() == null) {
           Toast.makeText(this,"当前网络不可用",Toast.LENGTH_SHORT).show();
        }else {
            NetworkInfo.State gprs = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if(gprs == NetworkInfo.State.CONNECTED || gprs == NetworkInfo.State.CONNECTING){
                Toast.makeText(this, "当前网络不是wifi", Toast.LENGTH_SHORT).show();
               flag = false;
            }else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING){
                Toast.makeText(this, "当前网络是wifi", Toast.LENGTH_SHORT).show();
                flag = true;
            }
        }
        return flag;
    }
    /**
     * 设置是否在wifi下更新
     */

    private void setUpdateOnWifi(){
        setWifiUpdateCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    boolean flag = isWifi();
                    if (flag==false){
                        MenuActivity.this.stopService(intent);
                    }else {
                        MenuActivity.this.stopService(intent);
                        MenuActivity.this.startService(intent);
                    }
                }
            }
        });
    }
    /**
     * 关于基努
     */
    private void setAboutKeanu(){
        aboutKeanu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MenuActivity.this,AboutKeanuActivity.class);
                startActivity(intent1);
            }
        });
    }
}
