package com.housemixer.jack.pebblesos;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jack on 12/15/2015.
 */
class SOSPebbleDataReceiver extends PebbleKit.PebbleDataReceiver {

    public static SOSPebbleDataReceiver instance = null;
    public static MediaPlayer ringPlayer = null;
    public static MediaPlayer fartPlayer = null;
    public static boolean isRepeating = false;
    public static final ScheduledExecutorService repeatingExecutor =  Executors.newSingleThreadScheduledExecutor();
    public static Future<?> musicRunnableFuture = null;

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
        byte ringcodeMessage = data.getBytes(PebbleInfo.PEBBLE_APP_RINGCODE_KEY)[0];
        switch (ringcodeMessage){
            case PebbleInfo.PEBBLE_APP_SINGLE_RING_VALUE:
                ring(context);
                break;
            case PebbleInfo.PEBBLE_APP_TOGGLE_RING_VALUE:
                toggleRepeatedRing(context);
                break;
            case PebbleInfo.PEBBLE_APP_FART_VALUE:
                fart(context);
        }
        PebbleKit.sendAckToPebble(context, transactionId);
    }

    public void ring(Context context) {
        final AudioManager systemAudio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int streamMusic = AudioManager.STREAM_MUSIC;
        final int currentAudioLevel = systemAudio.getStreamVolume(streamMusic);
        final int streamMaxVolume = systemAudio.getStreamMaxVolume(streamMusic);
        systemAudio.setStreamVolume(streamMusic, streamMaxVolume, 0); // notification doesn't work

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (ringPlayer == null) {
            ringPlayer = MediaPlayer.create(context, notification);
            ringPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    systemAudio.setStreamVolume(streamMusic, currentAudioLevel, 0);
                    ringPlayer.release();
                    ringPlayer = null;
                }
            });
        } else {
            ringPlayer.seekTo(0);
        }
        ringPlayer.start();
        // automatically vibrate
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        int vibrationDuration = ringPlayer.getDuration();
        if (vibrationDuration == -1) vibrationDuration = 2;
        vibrator.vibrate(vibrationDuration);
    }

    public void toggleRepeatedRing(final Context context){
        if(!isRepeating){
            musicRunnableFuture = repeatingExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    ring(context);
                }
            }, 0, 2, TimeUnit.SECONDS);
            isRepeating = true;
        }
        else{
            musicRunnableFuture.cancel(false); // ring seems like a pretty atomically important function
            isRepeating = false;
        }
    }

    // probably some way to make this besides copy/paste, really don't feel like making it orthogonal right now
    public void fart(Context context) {
        final AudioManager systemAudio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int streamMusic = AudioManager.STREAM_MUSIC;
        final int currentAudioLevel = systemAudio.getStreamVolume(streamMusic);
        final int streamMaxVolume = systemAudio.getStreamMaxVolume(streamMusic);
        systemAudio.setStreamVolume(streamMusic, streamMaxVolume, 0); // notification doesn't work

        if (fartPlayer == null) {
            fartPlayer = MediaPlayer.create(context, R.raw.fart);
            fartPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    systemAudio.setStreamVolume(streamMusic, currentAudioLevel, 0);
                    fartPlayer.release();
                    fartPlayer = null;
                }
            });
        } else {
            fartPlayer.seekTo(0);
        }
        fartPlayer.start();
        // no vibration for fart, can change if necessary
        /*
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        int vibrationDuration = fartPlayer.getDuration();
        if (vibrationDuration == -1) vibrationDuration = 2;
        vibrator.vibrate(vibrationDuration);
        */
    }
}
