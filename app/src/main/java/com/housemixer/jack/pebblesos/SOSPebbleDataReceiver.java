package com.housemixer.jack.pebblesos;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
    public static MediaPlayer ringPlayer = null;

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
        final int streamMusic = AudioManager.STREAM_MUSIC;
        final int currentAudioLevel = systemAudio.getStreamVolume(streamMusic);
        final int streamMaxVolume = systemAudio.getStreamMaxVolume(streamMusic);
        systemAudio.setStreamVolume(streamMusic, streamMaxVolume, 0); // notification doesn't work

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(ringPlayer == null) {
            ringPlayer = MediaPlayer.create(context, notification);
            ringPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    systemAudio.setStreamVolume(streamMusic, currentAudioLevel, 0);
                    ringPlayer.release();
                    ringPlayer = null;
                }
            });
        }
        else{
            ringPlayer.seekTo(0);
        }
        ringPlayer.start();

        if(FileInteractor.isVibrateEnabled(context)){
            // vibrate

        }
    }

}
