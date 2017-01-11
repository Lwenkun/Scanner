package net.bingyan.hustpass.scanner.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import net.bingyan.hustpass.scanner.App;

/**
 * Created by lwenkun on 2016/12/23.
 */

public class DimenUtils {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int px(float dp){
        Resources resources = App.getInstance().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static int dp(float px){
        Resources resources = App.getInstance().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp =(int) ( px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
