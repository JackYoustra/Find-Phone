package com.housemixer.jack.pebblesos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jack on 12/12/2015.
 */
public class BluetoothStateChangeReceiver extends BroadcastReceiver{



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("PebbleReceptor", "Received bluetooth state change");

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            Log.i("PebbleReceptor", "Scanning");
            scanAndStart(context);

        }

    }

    static void scanAndStart(Context context) {
        // check if it's a pebble that's connected
        for(BluetoothDevice d : BluetoothAdapter.getDefaultAdapter().getBondedDevices()){
            String bluetoothName = d.getName();
            Log.i("PebbleReceptor", "Bluetooth name: " + bluetoothName);
            if(bluetoothName.toLowerCase().contains("pebble")){
                Intent serviceStarter = new Intent(context, ReceptorReceiverService.class);
                context.startService(serviceStarter);
                break;
            }
        }
    }
}
