package com.usbdemo.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Inflater;

import sjj.alog.Log;

/**
 * Created by sjj on 2017/7/11.
 */

public class ZipUtils {
    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                Log.e("decompress", e);
            }
        }
        decompresser.end();
        return output;
    }
}
