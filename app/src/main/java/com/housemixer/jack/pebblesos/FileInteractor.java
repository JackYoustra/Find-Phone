package com.housemixer.jack.pebblesos;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jack on 12/21/2015.
 */
public class FileInteractor {
    private static final String PREFS_FILENAME = "SettingsFile";
    private static final String VIBRATE_KEY = "vibrateMode";

    public static boolean isVibrateEnabled(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILENAME, 0);
        if(!settings.contains(VIBRATE_KEY)){
            changeVibrate(context, true);
            return true;
        }
        else {
            return settings.getBoolean(VIBRATE_KEY, false); // second parameter irrelevant b/c of initial check
        }
    }

    public static void changeVibrate(Context context, boolean vibrateOn){
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILENAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(VIBRATE_KEY, vibrateOn);
        editor.commit();
    }
}
