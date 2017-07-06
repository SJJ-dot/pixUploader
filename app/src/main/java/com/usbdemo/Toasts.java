package com.usbdemo;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by SJJ on 2017/2/24.
 * toast
 */

public class Toasts {
    private static Toast toast;
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void show(final String string) {
        if (!TextUtils.isEmpty(string))
            if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
                show_(string);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        show_(string);
                    }
                });
            }
    }
    @UiThread
    private static void show_(String string) {
        initialize();
        toast.setText(string);
        toast.show();
    }

    @SuppressLint("ShowToast")
    private static void initialize() {
        if (toast == null)
        toast = Toast.makeText(APP.getApp(), "", Toast.LENGTH_SHORT);
    }
}
