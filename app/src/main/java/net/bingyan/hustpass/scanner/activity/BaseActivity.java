package net.bingyan.hustpass.scanner.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by lwenkun on 2016/12/19.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    public void showDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        if (dialog.isShowing()) return;
        dialog.setMessage(message);
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
