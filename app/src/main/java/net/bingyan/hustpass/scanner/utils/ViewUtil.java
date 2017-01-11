package net.bingyan.hustpass.scanner.utils;

import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lwenkun on 2016/12/22.
 */

public class ViewUtil {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * generate a random view id that's not conflict with existing ids. This is copied from source
     * code in order to support lower api
     * @return a random id
     */
    private static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * set a random id to a view.
     * @param v the view to which you want to set a id
     */
    public static void setRandomId(View v) {
        v.setId(generateViewId());
    }
}
