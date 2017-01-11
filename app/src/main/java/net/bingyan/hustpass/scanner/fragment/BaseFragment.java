package net.bingyan.hustpass.scanner.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by lwenkun on 2017/1/8.
 */

public abstract class BaseFragment extends Fragment {

    private ProgressDialog dialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    protected void showDialog(String msg) {
        if (dialog == null) dialog = new ProgressDialog(getActivity());
        dialog.setMessage(msg);
        dialog.show();
    }

    protected void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected void setResult(int resultCode) {
        getActivity().setResult(resultCode);
    }

    protected void finish() {
        getActivity().finish();
    }

}
