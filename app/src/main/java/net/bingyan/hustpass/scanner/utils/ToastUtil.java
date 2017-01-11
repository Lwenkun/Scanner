package net.bingyan.hustpass.scanner.utils;

import android.widget.Toast;

import net.bingyan.hustpass.scanner.App;

/**
 * Created by lwenkun on 2016/12/23.
 */

public class ToastUtil {
    /**
     * I create this method for convenience.
     * @param msg message you want to show
     */
    public static void show(String msg) {
        Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }
}
