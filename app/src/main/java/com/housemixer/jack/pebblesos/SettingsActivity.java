package com.housemixer.jack.pebblesos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ToggleButton vibrationButton = (ToggleButton) this.findViewById(R.id.UIVibrationToggleButton);
        final boolean vibrateEnabled = FileInteractor.isVibrateEnabled(this);
        vibrationButton.setChecked(vibrateEnabled);
        vibrationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FileInteractor.changeVibrate(SettingsActivity.this, isChecked);
            }
        });
    }
}