package com.housemixer.jack.pebblesos;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

/**
 * Created by jack on 12/15/2015.
 */
class SOSPebbleDataReceiver extends PebbleKit.PebbleDataReceiver {

    public static SOSPebbleDataReceiver instance = null;

    public static SOSPebbleDataReceiver getInstance(){
        if(instance == null){
            instance = new SOSPebbleDataReceiver();
        }
        return instance;
    }

    private SOSPebbleDataReceiver() {
        super(PebbleInfo.PEBBLE_APP_UUID);
    }

    @Override
    public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
        // Log.i(getLocalClassName(), "Received value=" + data.getUnsignedIntegerAsLong(0) + " for key: 0");
        Log.i("PebbleReceptor", "Message Received");
        long ringcodeMessage = data.getUnsignedIntegerAsLong(PebbleInfo.PEBBLE_APP_RINGCODE_KEY);
        if (ringcodeMessage == PebbleInfo.PEBBLE_APP_RINGCODE_VALUE) {
            ring(context);
        }
        PebbleKit.sendAckToPebble(context, transactionId);
    }

    public void ring(Context context){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

}
