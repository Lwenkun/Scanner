package net.bingyan.hustpass.scanner.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.bingyan.hustpass.scanner.R;
import net.bingyan.hustpass.scanner.Server;
import net.bingyan.hustpass.scanner.activity.MainActivity;
import net.bingyan.hustpass.scanner.server.ServerService;
import net.bingyan.hustpass.scanner.utils.MD5;
import net.bingyan.hustpass.scanner.utils.ToastUtil;

/**
 * Created by lwenkun on 2017/1/8.
 */

public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";

    private EditText etEmail;
    private EditText etPassword;
    private Server server;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().bindService(ServerService.newIntent(), conn, Context.BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_login, container, false);
        initView(contentView);
        return contentView;
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            server = Server.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initView(View contentView) {

        if (contentView == null) return;

        etEmail = (EditText) contentView.findViewById(R.id.et_email);
        etPassword = (EditText) contentView.findViewById(R.id.et_password);
        Button btnLogin = (Button) contentView.findViewById(R.id.btn_login);
        TextView tvRegister = (TextView) contentView.findViewById(R.id.tv_register);

        Toolbar toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle("物流助手");

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (checkBeforeLogin(email, password)) {
                    login(email, password);
                }
                break;
            case R.id.tv_register:
                register();
                break;
        }
    }

    private void register() {
        Log.d(TAG, "--> register() has been called");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, RegisterFragment.newInstance())
                .setCustomAnimations(R.anim.enter_login_fragment, R.anim.exit_login_fragment)
                .addToBackStack(null)
                .commit();
    }

    private boolean checkBeforeLogin(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            ToastUtil.show("邮箱不可为空");
        } else if (TextUtils.isEmpty(password)) {
            ToastUtil.show("密码不可为空");
        } else if (! isEmailValid(email)){
            ToastUtil.show("邮箱格式不正确");
        } else if (! isPasswordValid(password)) {
            ToastUtil.show("密码太短");
        } else {
            return true;
        }

        return false;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }


    private void login(String email, String password) {
        new LoginTask().execute(email, password);
    }

    class LoginTask extends AsyncTask<String, Void, Boolean> {

        private String email;
        private String pwdMd5;

        @Override
        protected void onPreExecute() {
            showDialog("登陆中，请稍后...");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            email = params[0];
            String password = params[1];

            pwdMd5 = MD5.string2MD5(password);
            Log.d(TAG, "pwdmd5 --> " + pwdMd5);

            if (server != null) {
                try {
                   return server.login(email, pwdMd5);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideDialog();

            if (success) {
                ToastUtil.show("登陆成功");

                SharedPreferences sp =
                        getActivity().getSharedPreferences("curr_user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("email", email);
                editor.putString("password", pwdMd5);
                editor.putBoolean("isLogin", true);
                editor.apply();

                setResult(Activity.RESULT_OK);
                finish();
            } else {
                ToastUtil.show("用户名或密码错误");
            }
        }
    }


}
