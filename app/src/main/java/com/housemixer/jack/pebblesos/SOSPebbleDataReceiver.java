package com.housemixer.jack.pebblesos;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        final AudioManager systemAudio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        final int maxVolume = systemAudio.getStreamMaxVolume(AudioManager.STREAM_RING);
        final int currentVolume = systemAudio.getStreamVolume(AudioManager.STREAM_RING);
        systemAudio.setStreamVolume(AudioManager.STREAM_RING, maxVolume, 0); // notification doesn't work

        // http://stackoverflow.com/questions/4441334/how-to-play-an-android-notification-sound
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone r = RingtoneManager.getRingtone(context, notification); // should be the same, don't need to worry about getting a different instnace each time
        r.play();

        if (maxVolume != currentVolume) {
            // not max before, need to change back when done, do this by brute-force play-checking
            final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if(!r.isPlaying()){
                        // reset and clean up
                        systemAudio.setStreamVolume(AudioManager.STREAM_RING, currentVolume, 0);
                        scheduler.shutdown();
                    }
                }
            }, 1000, 750, TimeUnit.MILLISECONDS);
            // no communication needed between worker and parent so using thread instead of handler
        }
    }

}
