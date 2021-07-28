package com.pracainz.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class BootCompleteReceiver extends BroadcastReceiver {


    //this method auto run services after reboot devices
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, MyService.class);
        context.startForegroundService(service);
    }

}