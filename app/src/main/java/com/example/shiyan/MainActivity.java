package com.example.shiyan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

import static java.lang.Thread.sleep;

public class MainActivity extends Activity implements View.OnClickListener {
    private final String apiEndpoint = "https://vi-power.cognitiveservices.azure.cn/face/v1.0/";
    private final String subscriptionKey = "a2f847cb765f44f592077fa31803f36d";
    private final FaceServiceClient faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);
    static int hz;
    String changJing;

    private EditText editText;
    static String userName;
    static String appName;
    static String scenarioNumber;

    MyReceiver myReceiver;
    LocalBroadcastManager localBroadcastManager;
    IntentFilter intentFilter;

    static String isUpload;
    static int weigh;
    static int heigh;
    static int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localBroadcastManager=LocalBroadcastManager.getInstance(this);

        Button btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(this);

        myReceiver=new MyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.shi.zhu.bin");
        registerReceiver(myReceiver, intentFilter);

        setSpinner();



    }





   // Intent intent = new Intent(this, MyService.class);
   Intent intent;
    String path;
    @Override
    public void onClick(View v) {
        intent = new Intent(this, MyService.class);
        Intent intent1 = new Intent();
        intent1.setAction(Intent.ACTION_MAIN);
        intent1.addCategory(Intent.CATEGORY_HOME);
        switch (v.getId()) {
            case R.id.button1:
//////////////
                path =getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+MainActivity.getWeigh()+"x"+MainActivity.getHeigh()+" "+MainActivity.getHz()+"s1次"; //所创建文件目录
                File f = new File(path);
                if(!f.exists()){
                    f.mkdirs(); //创建目录
                }

                try {
                    copyFileUsingFileStreams("/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state",path+"/测试前cpu0.txt");
                    copyFileUsingFileStreams("/sys/devices/system/cpu/cpu1/cpufreq/stats/time_in_state",path+"/测试前cpu1.txt");
                    copyFileUsingFileStreams("/sys/devices/system/cpu/cpu2/cpufreq/stats/time_in_state",path+"/测试前cpu2.txt");
                    copyFileUsingFileStreams("/sys/devices/system/cpu/cpu3/cpufreq/stats/time_in_state",path+"/测试前cpu3.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }



                startService(intent);
                stop(time);
                startActivity(intent1);


                break;
        }
    }

    void setSpinner()
    {
        Spinner mSpinner = (Spinner)findViewById(R.id.spinner);
        ArrayList<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        list.add("9");
        list.add("10");
        list.add("15");
        list.add("20");
        //为下拉列表定义一个适配器
        final ArrayAdapter<String> ad = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        //设置下拉菜单样式。
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //添加数据
        mSpinner.setAdapter(ad);
        mSpinner.setSelection(1);
        //点击响应事件
        mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String content=parent.getItemAtPosition(position).toString();
                hz=Integer.parseInt(content);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                hz=2;
            }
        });

        Spinner mSpinner2 = (Spinner)findViewById(R.id.spinner2);
        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("1920×1080");
        list2.add("1440×1080");
        list2.add("1280×720");
        list2.add("1088×1088");
        list2.add("1056×864");
        list2.add("960×720");
        list2.add("720×480");
        list2.add("640×480");
        list2.add("352×288");
        list2.add("320×240");
        list2.add("176×144");
        //为下拉列表定义一个适配器
        final ArrayAdapter<String> ad2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list2);
        //设置下拉菜单样式。
        ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //添加数据
        mSpinner2.setAdapter(ad2);
        mSpinner2.setSelection(8);
        //点击响应事件
        mSpinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changJing=parent.getItemAtPosition(position).toString();
                if(changJing.equals("1920×1080"))
                {
                    weigh=1920;
                    heigh=1080;
                }
                else if(changJing.equals("1440×1080"))
                {
                    weigh=1440;
                    heigh=1080;
                }
                else if(changJing.equals("1280×720"))
                {
                    weigh=1280;
                    heigh=720;
                }
                else if(changJing.equals("1088×1088"))
                {
                    weigh=1088;
                    heigh=1088;
                }
                else if(changJing.equals("1056×864"))
                {
                    weigh=1056;
                    heigh=864;
                }
                else if(changJing.equals("960×720"))
                {
                    weigh=960;
                    heigh=720;
                }
                else if(changJing.equals("720×480"))
                {
                    weigh=720;
                    heigh=480;
                }
                else if(changJing.equals("640×480"))
                {
                    weigh=640;
                    heigh=480;
                }
                else if(changJing.equals("352×288"))
                {
                    weigh=352;
                    heigh=288;
                }
                else if(changJing.equals("320×240"))
                {
                    weigh=320;
                    heigh=240;
                }
                else if(changJing.equals("176×144"))
                {
                    weigh=176;
                    heigh=144;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                changJing="352×288";
                weigh=352;
                heigh=288;
            }
        });

        Spinner mSpinner3 = (Spinner)findViewById(R.id.spinner3);
        ArrayList<String> list3 = new ArrayList<String>();
        list3.add("是");
        list3.add("否");
        //为下拉列表定义一个适配器
        final ArrayAdapter<String> ad3 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list3);
        //设置下拉菜单样式。
        ad3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //添加数据
        mSpinner3.setAdapter(ad3);
        //点击响应事件
        mSpinner3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isUpload=parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                isUpload="是";
            }
        });


        Spinner mSpinner4 = (Spinner)findViewById(R.id.spinner4);
        ArrayList<String> list4 = new ArrayList<String>();
        list4.add("10");
        list4.add("30");
        list4.add("60");
        list4.add("90");
        list4.add("120");
        //为下拉列表定义一个适配器
        final ArrayAdapter<String> ad4 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list4);
        //设置下拉菜单样式。
        ad4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //添加数据
        mSpinner4.setAdapter(ad4);
        mSpinner4.setSelection(2);
        //点击响应事件
        mSpinner4.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String content=parent.getItemAtPosition(position).toString();
                time=Integer.parseInt(content);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                time=60;
            }
        });
    }

    private void stop(final int time) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        stopService(intent);
                        try {
                            copyFileUsingFileStreams("/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state",path+"/测试后cpu0.txt");
                            copyFileUsingFileStreams("/sys/devices/system/cpu/cpu1/cpufreq/stats/time_in_state",path+"/测试后cpu1.txt");
                            copyFileUsingFileStreams("/sys/devices/system/cpu/cpu2/cpufreq/stats/time_in_state",path+"/测试后cpu2.txt");
                            copyFileUsingFileStreams("/sys/devices/system/cpu/cpu3/cpufreq/stats/time_in_state",path+"/测试后cpu3.txt");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this, "测试结束", Toast.LENGTH_LONG).show();
                    }
                },time*1000);
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    static String getAppName()
    {
        return appName;
    }

    static String getUserName()
    {
        return userName;
    }

    static String getScenarioNumber()
    {
        return scenarioNumber;
    }

    static int getWeigh()
    {
        return weigh;
    }

    static int getHeigh()
    {
        return heigh;
    }

    static int getHz()
    {
        return hz;
    }

    private static void copyFileUsingFileStreams(String source, String dest)
            throws IOException {
        File file1=new File(source);
        File file2=new File(dest);
        if(file1.exists()) {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(file1);
                output = new FileOutputStream(file2);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
            } finally {
                input.close();
                output.close();
            }
        }
    }
}