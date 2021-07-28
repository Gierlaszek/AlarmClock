package com.pracainz.clock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//############################################################################################################
//Adding android.permission.FOREGROUND_SERVICE to Android manifest ->  is a normal permission,
// so the system automatically grants it to the requesting app, needed to startForeground
//Adding android.permission.RECEIVE_BOOT_COMPLETED to Android manifest -> auto run service after reboot device
//############################################################################################################


public class MyService extends Service {

    private static Timer timer = new Timer();
    private String currentTime;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private String savedTime;
    private NotificationManager manager;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //------------------------------------
        //data receive from shared preferences
        //------------------------------------
        SharedPreferences sharedPreferences = getSharedPreferences("Shared", MODE_PRIVATE);
        savedTime = sharedPreferences.getString("SavedTime", "00:00");


        //expanded notification because there was a error "Bad notification for startForeground"

        String ID = "com.pracainz.clock";
        String NAME = "Channel One";

        Intent intent = new Intent(MyService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notification; //Create service object

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID, NAME, manager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(MyService.this).setChannelId(ID);
        } else {
            notification = new NotificationCompat.Builder(MyService.this);
        }
        notification.setContentTitle("Alarm")
                .setContentText("Setting alarm at " + savedTime)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        Notification notification1 = notification.build();
        startForeground(1,notification1);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //---------------------
        //create new timer task
        //---------------------
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCurrentTime();
                if(currentTime.equals(savedTime)){
                    Log.i("Info from activity: ", "Alarm godzina: " + savedTime);
                    stopService(intent);  //clear notification and variable saved time
                    cancel(); //cancel timer task
                }
            }
        }, 0, 1000);

        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();

        return START_STICKY;  //START_STICKY needed to reboot, allow OS to recreate the service if it has enough memory
    }


    public void updateCurrentTime(){
        currentTime = simpleDateFormat.format(new Date());
    }

    @Override
    public void onDestroy() {
        savedTime = "";
        manager.cancelAll();
        super.onDestroy();
    }

}
