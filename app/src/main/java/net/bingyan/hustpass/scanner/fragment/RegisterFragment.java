package net.bingyan.hustpass.scanner.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.bingyan.hustpass.scanner.R;
import net.bingyan.hustpass.scanner.Server;
import net.bingyan.hustpass.scanner.model.User;
import net.bingyan.hustpass.scanner.utils.MD5;
import net.bingyan.hustpass.scanner.utils.ToastUtil;

/**
 * Created by lwenkun on 2017/1/8.
 */

public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    private GetServerCallback callback;

    private TextView etEmail;
    private TextView etPassword;
    private TextView etPassword2;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (GetServerCallback) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initView(view);
        return view;
    }

    public static RegisterFragment newInstance() {

        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void initView(View rootView) {
        etEmail = (TextView) rootView.findViewById(R.id.et_email);
        etPassword = (TextView) rootView.findViewById(R.id.et_password);
        etPassword2 = (TextView) rootView.findViewById(R.id.et_password2);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setTitle("注册");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Button btnRegister = (Button) rootView.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String password2 = etPassword2.getText().toString();
                if (TextUtils.isEmpty(password) || ! TextUtils.equals(password, password2)) {
                    etPassword2.setError("输入的密码不一致");
                    return;
                }
                register(email, password);
                break;
        }
    }

    private void register(final String email, final String password) {

        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                showDialog("正在注册，请稍后...");
            }

            @Override
            protected Boolean doInBackground(String... params) {
                String email = params[0];
                String password = params[1];

                Server server = callback.getServer();
                try {
                    if (validate(email, password)) {
                        server.addUser(new User(email, MD5.string2MD5(password)));
                        return true;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                hideDialog();
                if (success) ToastUtil.show("注册成功");
                else ToastUtil.show("注册失败, 用户名可能已经存在");
            }
        }.execute(email, password);


    }

    private boolean validate(String email, String password) {
        if (!email.contains("@")) {
            etEmail.setError("邮箱的格式有误");
        } else if (password.length() < 6) {
            etPassword.setText("密码的长度太短");
        } else {
            return true;
        }
        return false;
    }


}
