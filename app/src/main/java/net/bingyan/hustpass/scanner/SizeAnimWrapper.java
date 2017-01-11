package net.bingyan.hustpass.scanner;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lwenkun on 2016/12/24.
 */

public class SizeAnimWrapper {

    private View v;
    private ViewGroup.LayoutParams lp;

    public SizeAnimWrapper(View v) {
        if (v == null) throw new NullPointerException("v == null");
        this.v = v;
        this.lp = v.getLayoutParams();
    }

    void setWidth(int width) {
        if (lp != null) {
            lp.width = width;
            v.setLayoutParams(lp);
        }
    }

    void setHeight(int height) {
        if (lp != null) {
            lp.height = height;
            v.setLayoutParams(lp);
        }
    }
}
