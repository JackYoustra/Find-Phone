package com.housemixer.jack.pebblesos;

import java.util.UUID;

/**
 * Created by jack on 12/11/2015.
 */
public class PebbleInfo {
    public static final UUID PEBBLE_APP_UUID = UUID.fromString("16d01f80-dbf6-417b-80e4-83c4506a8339");
    public static final int PEBBLE_APP_RINGCODE_KEY = 0;
    public static final byte PEBBLE_APP_SINGLE_RING_VALUE = 0;
    public static final byte PEBBLE_APP_TOGGLE_RING_VALUE = 1;
    public static final byte PEBBLE_APP_FART_VALUE = 2;

    private PebbleInfo() {
    }
}
