package com.usbdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.skylin.mavlink.connection.usb.UsbConnection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import sjj.alog.Config;

public class MainActivity extends BaseUSBActivity implements View.OnClickListener {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> rebootFuture;
    private Future<?> uploadFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.restart).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancel();
    }
    private Runnable reboot = new Runnable() {
        @Override
        public void run() {
            UsbConnection usbConnection = new UsbConnection(MainActivity.this, 57600);

        }
    };
    private Runnable upload = new Runnable() {
        @Override
        public void run() {
            Toasts.show("START");
        }
    };

    @Override
    protected void onUsbHost() {
        super.onUsbHost();
        uploadFuture = executorService.submit(upload);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart:
                cancel();
                rebootFuture = executorService.submit(reboot);
                break;
            case R.id.stop:
                cancel();
                break;
        }
    }

    private void cancel() {
        if (rebootFuture != null) {
            rebootFuture.cancel(true);
        }
        if (uploadFuture != null) {
            uploadFuture.cancel(true);
        }
    }
}
