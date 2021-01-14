package com.lang.fridainject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lang.fridainject.handler.JingdongHandler;
import com.lang.fridainject.util.Command;
import com.lang.fridainject.util.LogUtils;
import com.lang.fridainject.util.Utils;
import com.lang.sekiro.netty.client.SekiroClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private static ScrollView scrollView;
    private static TextView console;
    private static EditText cmd;

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            String txt = msg.getData().getString("log");
            if (console != null) {
                if (console.getText() != null) {
                    if (console.getText().toString().length() > 7500) {
                        console.setText("日志定时清理完成..." + "\n\n" + txt);
                    } else {
                        console.setText(console.getText().toString() + "\n\n" + txt);
                    }
                } else {
                    console.setText(txt);
                }
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
            super.handleMessage(msg);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.scrollview);
        console = findViewById(R.id.console);
        cmd = findViewById(R.id.cmd);

        // 权限开启
        requestPermission();

        // 拷贝文件到缓冲
        // frida 脚本
        Utils.copyAssetAndWrite(getApplicationContext(),"jingdong_frida_rpc_http.js");
        // frida-inject
        Utils.copyAssetAndWrite(getApplicationContext(),"frida-inject-12.8.2-android-arm64");
        // 启动sekiro
        startSekiro();
        // 启动后台监听，定时执行脚本， 具体脚本请自行修改 到AlarmReceiver
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        IntentFilter alarmIntentFilter = new IntentFilter();
        alarmIntentFilter.addAction(DaemonService.NOTIFY_ACTION);
        registerReceiver(alarmReceiver, alarmIntentFilter);
        Intent intentService = new Intent(this, DaemonService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intentService);
        } else {
            startService(intentService);
        }
    }

    public void server(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Command.execRootCmd("nohup /data/local/tmp/flang -l 0.0.0.0:12345 > /data/local/tmp/fridalog 2>&1 &");
            }
        }).start();
    }

    public void inject(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utils.copyAssetAndWrite(getApplicationContext(), "jingdong_frida_rpc_http.js");
                String agentJsPath = getCacheDir() + "/jingdong_frida_rpc_http.js";
                Command.execRootCmdNotExit("/data/local/tmp/fridainject -n com.jingdong.app.mall -s "+agentJsPath+" --runtime=v8 -e");
            }
        }).start();

    }

    public void appStart(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Command.execRootCmd("am start -n com.jingdong.app.mall/com.jingdong.app.mall.main.MainActivity");
            }
        }).start();
    }

    public void serverCheck(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Command.execRootCmdNotExit("ps /data/local/tmp/flang");
            }
        }).start();

    }
    public void injectCheck(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Command.execRootCmdNotExit("ps /data/local/tmp/fridainject");
            }
        }).start();

    }
    public void execute(View view){
        String cmdStr = MainActivity.cmd.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Command.execRootCmdNotExit(cmdStr);
            }
        }).start();
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 10001);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, 10002);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 10003);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10004);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 10005);
        LogUtils.e("申请权限");
    }

    public static void sendmsg(String txt) {
        Message msg = new Message();
        msg.what = 1;
        Bundle data = new Bundle();
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = dateFormat.format(date);
        data.putString("log", d + ":" + " " + txt);
        msg.setData(data);
        try {
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSekiro() {
        String uuid = Build.SERIAL;
        LogUtils.i("tyc start sekiro...... ");
        final SekiroClient sekiroClient = SekiroClient.start("XXX",11000,uuid,"fridainject");
        sekiroClient.registerHandler("jingdong", new JingdongHandler());
        LogUtils.i("tyc start sekiro!!!! ");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startFridaHook() throws IOException, InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String agentJsPath = "file:///android_asset/jingdong_frida_rpc_http.js";
                Command.execRootCmd("nohup /data/local/tmp/flang -l 0.0.0.0:12345 > /data/local/tmp/fridalog 2>&1 &");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Utils.copyAssetAndWrite(getApplicationContext(),"jingdong_frida_rpc_http.js");
                agentJsPath = getCacheDir() + "/jingdong_frida_rpc_http.js";
//                Command.execRootCmd(getApplicationContext(),"am start -n com.jingdong.app.mall/com.jingdong.app.mall.main.MainActivity");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                Command.execRootCmdNotExit("/data/local/tmp/fridainject -n com.jingdong.app.mall -s "+agentJsPath+" --runtime=v8 -e");
            }
        }).start();
    }


}
