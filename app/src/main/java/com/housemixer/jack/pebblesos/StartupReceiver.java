package com.housemixer.jack.pebblesos;

/**
 * Created by jack on 12/11/2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent receivingService = new Intent("com.houemixer.jack.pebblesos.PebbleReceptorService");
        receivingService.setClass(context, PebbleReceptorService.class);
        context.startService(receivingService);
    }
}