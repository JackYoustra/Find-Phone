package com.housemixer.jack.pebblesos;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;

/**
 * Created by jack on 12/16/2015.
 */
public class ReceptorReceiverService extends Service{

    private static BroadcastReceiver pebbleReceiver;

    public ReceptorReceiverService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("PebbleReceptor", "Receptor Started if not already");
        if(pebbleReceiver == null) {
            pebbleReceiver = PebbleKit.registerReceivedDataHandler(this, SOSPebbleDataReceiver.getInstance()); // update to latest one
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(pebbleReceiver);
        super.onDestroy();
    }
}
