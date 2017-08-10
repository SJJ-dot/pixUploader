package com.usbdemo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.SparseArray;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.usbdemo", appContext.getPackageName());
    }
    @Test
    public void testSparseArray() throws Exception{
        SparseArray<Integer> sparseArray = new SparseArray<>();
        sparseArray.put(1,1);
        sparseArray.put(3,1);
        sparseArray.put(2,1);
        sparseArray.put(4,1);
        sparseArray.put(5,1);
        println(sparseArray);
    }
    private void println(Object s) {
        System.out.println(s);
    }
}
