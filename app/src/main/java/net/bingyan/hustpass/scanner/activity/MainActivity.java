package net.bingyan.hustpass.scanner.activity;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.bingyan.hustpass.scanner.App;
import net.bingyan.hustpass.scanner.DiffCallback;
import net.bingyan.hustpass.scanner.ExpandableFAB;
import net.bingyan.hustpass.scanner.R;
import net.bingyan.hustpass.scanner.UserInfoManager;
import net.bingyan.hustpass.scanner.model.UserInfo;
import net.bingyan.hustpass.scanner.utils.JsonUtils;
import net.bingyan.hustpass.scanner.utils.ToastUtil;
import net.bingyan.hustpass.scanner.widget.SearchResultAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static android.os.Build.VERSION.SDK_INT;
import static android.support.v7.util.DiffUtil.calculateDiff;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_LOGIN = 0x00;
    private final static int REQUEST_SCAN = 0x01;
    private final static int REQUEST_PERMISSION_CAMERA = 0x02;
    private final static int REQUEST_PERMISSION_CALL = 0x03;
    private final static int REQUEST_GET_INFO_FROM_FILE = 0x04;

    private UserInfoManager userInfoManager = App.getInstance().getUserInfoManager();
    private String phoneNum;

    private RecyclerView rvSearchResult;
    private SearchResultAdapter mSearchResultAdapter;
    private View searchBarLayout;
    private ExpandableFAB expandableFAB;
    private SearchView sv_search_user_info;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkLogin()) {
            Intent login = new Intent(this, LoginAndSignUpActivity.class);
            startActivityForResult(login, REQUEST_LOGIN);
        }

        initView();
    }


    /**
     * check if the user has logged in
     * @return
     */
    private boolean checkLogin() {
        SharedPreferences sp = getSharedPreferences("curr_user", MODE_PRIVATE);
        return sp.getBoolean("isLogin", false);
    }

    private final UserInfoManager.Callback<List<UserInfo>> queryCallback =
            new UserInfoManager.Callback<List<UserInfo>>() {
        @Override
        public void onFinish(List<UserInfo> result) {
            dismissDialog();
            if (result == null) {
                Log.d("MainActivity", " --> query result was null");
                return;
            }
            updateResult(result);
        }

        @Override
        public void onStart() {
            Log.d("MainActivity", "onPreTask was called");
            showDialog("正在查询，请稍后...");
        }
    };

    private void updateResult(List<UserInfo> newInfoList) {
        DiffUtil.DiffResult diffResult =
                calculateDiff(new DiffCallback(newInfoList,
                        mSearchResultAdapter.getData()));
        diffResult.dispatchUpdatesTo(mSearchResultAdapter);
        mSearchResultAdapter.updateData(newInfoList);
    }

    private final SearchResultAdapter.Callback callback = new SearchResultAdapter.Callback() {
        @Override
        public void onViewClick(View v, UserInfo info) {

        }

        @Override
        public boolean onPopupMenuItemClick(MenuItem item, UserInfo info) {
            switch (item.getItemId()) {
                case R.id.menu_call:
                    phoneNum = info.phoneNum;
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                REQUEST_PERMISSION_CALL);
                    } else {
                        call();
                    }
                    Log.d(TAG, "popup menu item has been click, associated user info --> " + info);
                    return true;
                default:
                    return false;
            }
        }
    };

    private void call() {
        if (TextUtils.isEmpty(phoneNum)) {
            Log.w(TAG, "phoneNum is null");
            return;
        }
        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + phoneNum));
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkSelfPermission(Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED)) {
                return;
            }
        }
        startActivity(call);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_overflow_24dp));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_logout:
                        SharedPreferences sp =
                                getSharedPreferences("curr_user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("email", null);
                        editor.putString("password", null);
                        editor.putBoolean("isLogin", false);
                        editor.apply();
                        startActivityForResult(new Intent(MainActivity.this, LoginAndSignUpActivity.class), REQUEST_LOGIN);
                        return true;
                    case R.id.menu_scan:
                        if (SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_PERMISSION_CAMERA);
                        } else {
                            scan();
                        }
                        return true;
                }
                return false;
            }
        });

        rvSearchResult = (RecyclerView) findViewById(R.id.rv_search_result);
        mSearchResultAdapter = new SearchResultAdapter(null, callback);
        rvSearchResult.setAdapter(mSearchResultAdapter);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        searchBarLayout = findViewById(R.id.cv_search_bar);
        searchBarLayout.setOnClickListener(this);

        sv_search_user_info = (SearchView) findViewById(R.id.sv_search_user_info);
        sv_search_user_info.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                userInfoManager.queryUserInfoAsync(query, queryCallback);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        expandableFAB = (ExpandableFAB) findViewById(R.id.id_expandable_fab);
        expandableFAB.setItems(Arrays.asList(
                new ExpandableFAB.Item(R.drawable.ic_clipboard_24dp, "从剪切版导入"),
                new ExpandableFAB.Item(R.drawable.ic_mode_edit_24dp, "手动添加"),
                new ExpandableFAB.Item(R.drawable.ic_file_24dp, "从文件批量导入")));
        expandableFAB.setOnItemClickListener(new ExpandableFAB.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                switch (position) {
                    case 0:
                        addFromClipBoard();
                        break;
                    case 1:
                        Intent scan = new Intent(MainActivity.this, AddUserInfoActivity.class);
                        startActivity(scan);
                        break;
                    case 2:
                        Intent openFile = new Intent(Intent.ACTION_GET_CONTENT);
                        openFile.setType("text/plain");
                        startActivityForResult(openFile, REQUEST_GET_INFO_FROM_FILE);
                        break;
                }
            }
        });
    }


    private void addFromClipBoard() {
        ClipboardManager clipboardManager = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);

        if (!clipboardManager.hasPrimaryClip()) return;

        String content = clipboardManager.getPrimaryClip()
                .getItemAt(0).getText().toString();

        UserInfoManager.Callback<Void> addUserInfoCallback = new UserInfoManager.Callback<Void>() {
            @Override
            public void onFinish(Void result) {
                ToastUtil.show("添加成功");
            }

            @Override
            public void onStart() {
            }
        };

        try {
            UserInfo userInfo = JsonUtils.getInstance()
                    .json2Bean(content, UserInfo.class);

            userInfoManager.addUserInfoAsync(userInfo, addUserInfoCallback);
        } catch (Exception e) {
            try {
                List<UserInfo> userInfoList = JsonUtils.getInstance()
                        .json2Bean(content, new TypeToken<List<UserInfo>>() {
                        }.getType());
                userInfoManager.addUserInfoListAsync(userInfoList, addUserInfoCallback);
            } catch (Exception ee) {
                ee.printStackTrace();
                ToastUtil.show("不支持的格式");
            }
        }

    }

    private void scan() {
        Intent scan = new Intent("com.google.zxing.client.android.SCAN");
        startActivityForResult(scan, REQUEST_SCAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scan();
                return;
            }
        } else if (requestCode == REQUEST_PERMISSION_CALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                call();
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_search_bar:
                sv_search_user_info.setIconified(false);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                ToastUtil.show("登陆成功");
            } else {
                finish();
            }
        } else if (requestCode == REQUEST_SCAN) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("SCAN_RESULT");
                Log.d(TAG, "result --> " + result);
                try {
                    UserInfo info = JsonUtils.getInstance().json2Bean(result, UserInfo.class);
                    info.received = true;
                    Log.d(TAG, "info --> " + info.toString());
                    updateResult(Arrays.asList(info));
                    userInfoManager.updateUserInfo(info);
                } catch (Exception e) {
                    ToastUtil.show("不支持的二维码");
                }
            }
        } else if (requestCode == REQUEST_GET_INFO_FROM_FILE) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(fileUri);
                    if (is == null) {
                        ToastUtil.show("解析失败");
                        return;
                    }
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        content.append(line);
                    }
                    try {
                        List<UserInfo> userInfoList = JsonUtils.getInstance().json2Bean(
                                content.toString(), new TypeToken<List<UserInfo>>(){}.getType());
                        userInfoManager.addUserInfoListAsync(userInfoList, null);
                        ToastUtil.show("导入成功");
                    } catch (Exception e) {
                        try {
                            UserInfo userInfo = JsonUtils.getInstance().json2Bean(
                                    content.toString(), UserInfo.class);
                            userInfoManager.addUserInfoAsync(userInfo, null);
                            ToastUtil.show("导入成功");
                        } catch (Exception ee) {
                            ToastUtil.show("非法的数据格式");
                            ee.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    ToastUtil.show("读取文件失败，请确保其是文本类型");
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
