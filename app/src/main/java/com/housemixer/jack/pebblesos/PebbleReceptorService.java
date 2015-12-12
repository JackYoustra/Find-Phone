package com.housemixer.jack.pebblesos;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**

 * Created by jack on 12/11/2015.
 */
public class PebbleReceptorService extends IntentService{

    private BroadcastReceiver pebbleReceiver = null;
    private static boolean called = false;

    public PebbleReceptorService(){
        super("PebbleReceptorService");
    }

    public void startReception() {
        pebbleReceiver = PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(PebbleInfo.PEBBLE_APP_UUID) {

            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                // Log.i(getLocalClassName(), "Received value=" + data.getUnsignedIntegerAsLong(0) + " for key: 0");
                Log.i("PebbleReceptor", "Message Received");
                long ringcodeMessage = data.getUnsignedIntegerAsLong(PebbleInfo.PEBBLE_APP_RINGCODE_KEY);
                if (ringcodeMessage == PebbleInfo.PEBBLE_APP_RINGCODE_VALUE) {
                    ring(PebbleReceptorService.this);
                }
                PebbleKit.sendAckToPebble(context, transactionId);
            }

        });
    }

    public void ring(Context context){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(pebbleReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(called){
            // by destroying unregisterReceiver, it should guaruntee longevity
            Log.i("PebbleReceptor", "Already called, not creating");
            return;
        }
        called = true;
        // will stop when phone sleeps
        Log.i("PebbleReceptor", "Receptor Initialized");
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        Log.i("PebbleReceptor", "Scanning");
                        if (PebbleKit.isWatchConnected(PebbleReceptorService.this)) {
                            Log.i("PebbleReceptor", "Receptor Started");
                            startReception();
                            scheduler.shutdown();
                        }
                    }
                }, 0, 30, TimeUnit.SECONDS);

        try {
            Thread.sleep(1000*240, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
