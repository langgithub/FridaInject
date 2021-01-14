package com.lang.fridainject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 *  

* @ClassName: DaemonService

* @Description: TODO(这里用一句话描述这个类的作用)

* @author SuXiaoliang

* @date 2018年6月23日 下午1:26:14

*
 */
public class DaemonService extends Service {
    public static String NOTIFY_ACTION = "com.lang.daemon.action";
    public static final int NOTICE_ID = 100;  
   
    @Nullable
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
  
  
    @Override  
    public void onCreate() {  
        // 通知栏
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("CHANNEL_ID_STRING", "frida inject 守护进程", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), "CHANNEL_ID_STRING").build();
            startForeground(1, notification);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            Notification.Builder builder = new Notification.Builder(this);  
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            builder.setContentTitle("frida-inject 后台监控");
            builder.setContentText("frida-inject 守护进程正在运行中...");
            builder.setAutoCancel(false);
            builder.setOngoing(true);
            startForeground(NOTICE_ID,builder.build());  
        }else{  
            startForeground(NOTICE_ID,new Notification());  
        }

        // 定时器
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int triggerTime = 1 * 10 * 1000;
        Intent intent = new Intent(NOTIFY_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setRepeating(AlarmManager.RTC_WAKEUP , System.currentTimeMillis(), triggerTime, pi);
    }

    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        // 如果Service被终止  
        // 当资源允许情况下，重启service  
        return START_STICKY;  
    }  
  
  
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        // 如果Service被杀死，干掉通知  
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){  
            NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);  
            mManager.cancel(NOTICE_ID);  
        }
        // 重启自己  
        Intent intent = new Intent(getApplicationContext(),DaemonService.class);  
        startService(intent);  
    }  
}  