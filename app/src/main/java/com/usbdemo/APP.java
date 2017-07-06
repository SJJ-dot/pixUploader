package com.usbdemo;

import android.app.Application;

import sjj.alog.Config;
import sjj.alog.Log;

/**
 * Created by sjj on 2017/7/6.
 */

public class APP extends Application {
    private static APP app;

    public static APP getApp() {
        return app;
    }

    public APP() {
        final Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(t.toString(), e);
                handler.uncaughtException(t, e);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Config config = new Config();
        config.hold = true;
        config.holdLev = Config.ERROR;
        config.holdMultiple = false;
        Config.init(config);
    }
}
