package com.usbdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import sjj.alog.Config;

public class MainActivity extends BaseUSBActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        Config config = new Config();
        config.hold = true;
        config.holdLev = Config.ERROR;
        config.holdMultiple = false;
        Config.init(config);
    }
}
