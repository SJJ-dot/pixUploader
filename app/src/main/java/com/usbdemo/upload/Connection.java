package com.usbdemo.upload;

/**
 * Created by sjj on 2017/7/11.
 */

public interface Connection {
    int read(byte[] buff);

    void send(byte[] buff);

    void open();

    void close();
}
