package net.bingyan.hustpass.scanner.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.bingyan.hustpass.scanner.App;
import net.bingyan.hustpass.scanner.R;
import net.bingyan.hustpass.scanner.UserInfoManager;
import net.bingyan.hustpass.scanner.model.UserInfo;
import net.bingyan.hustpass.scanner.utils.ToastUtil;

/**
 * Created by lwenkun on 2016/12/16.
 */

public class AddUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AddUserInfoActivity";

    private UserInfoManager userInfoManager = App.getInstance().getUserInfoManager();

    private TextView etUserName;
    private TextView etAddress;
    private TextView etPhoneNum;
    private TextView etIdNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_info);

        initView();
    }

    private void initView() {

        etUserName = (TextView) findViewById(R.id.et_user_name);
        etAddress = (TextView) findViewById(R.id.et_address);
        etPhoneNum = (TextView) findViewById(R.id.et_phone_num);
        etIdNum = (TextView) findViewById(R.id.et_id_num);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
    }

    private UserInfoManager.Callback<Void> addUserInfoCallback = new UserInfoManager.Callback<Void>() {
        @Override
        public void onFinish(Void result) {
            dismissDialog();
            ToastUtil.show("添加成功");
        }

        @Override
        public void onStart() {
            showDialog("请稍后");
        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_add:
                final UserInfo info = new UserInfo();

                info.name = etUserName.getText().toString();
                info.address = etAddress.getText().toString();
                info.phoneNum = etPhoneNum.getText().toString();
                info.idNum = etIdNum.getText().toString();

                userInfoManager.addUserInfoAsync(info, addUserInfoCallback);
                break;
            default:
                break;
        }
    }

}
