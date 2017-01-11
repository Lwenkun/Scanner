package net.bingyan.hustpass.scanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import net.bingyan.hustpass.scanner.cache.BitmapDiskCache;
import net.bingyan.hustpass.scanner.model.UserInfo;
import net.bingyan.hustpass.scanner.server.ServerService;
import net.bingyan.hustpass.scanner.utils.JsonUtils;
import net.bingyan.hustpass.scanner.utils.MD5;
import net.bingyan.hustpass.scanner.utils.QRCodeUtils;
import net.bingyan.hustpass.scanner.utils.ToastUtil;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by lwenkun on 2016/12/23.
 */

public class UserInfoManager {

    private static final String TAG = "UserInfoManager";
    private BitmapDiskCache QRCodeCache = App.getInstance().getQRCodeCache();

    private ThreadPoolManager tm = ThreadPoolManager.getInstance();
    private Server server;

    public interface Callback<T> {
        void onFinish(T result);
        void onStart();
    }

    public UserInfoManager(Context context) {
        context.bindService(ServerService.newIntent(), conn, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            server = Server.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public void addUserInfoAsync(final UserInfo u, final Callback<Void> c) {
        tm.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                addUserInfo(u);
                return null;
            }
        }, new OnTaskListener<Void>() {
            @Override
            public void onFinish(Void result) {
                if (c != null) c.onFinish(result);
            }

            @Override
            public void onPreTask() {
                if (c != null) c.onStart();
            }
        });
    }

    public void addUserInfoListAsync(final List<UserInfo> userInfoList, final Callback<Void> c) {
        tm.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (UserInfo u : userInfoList) {
                    addUserInfo(u);
                }
                return null;
            }
        }, new OnTaskListener<Void>() {
            @Override
            public void onFinish(Void result) {
                if (c != null) c.onFinish(result);
            }

            @Override
            public void onPreTask() {
                if (c != null) c.onStart();
            }
        });
    }

    public void queryUserInfoAsync(final String key, final Callback<List<UserInfo>> c) {
        tm.submit(new Callable<List<UserInfo>>() {
            @Override
            public List<UserInfo> call() throws Exception {
                return server.queryUserInfo(key);
            }
        }, new OnTaskListener<List<UserInfo>>() {
            @Override
            public void onFinish(List<UserInfo> result) {
                if (c != null) c.onFinish(result);
            }

            @Override
            public void onPreTask() {
                if (c != null)  c.onStart();
            }
        });
    }

    /**
     * generate a QRCode Bitmap
     * @param info
     * @return
     */
    public static Bitmap generateQRCode(UserInfo info) {
        String jsonInfo = JsonUtils.getInstance().bean2Json(info);
        return QRCodeUtils.string2QRCode(jsonInfo);
    }

    /**
     * please call this method at worker thread
     * @param u
     * @return
     */
    public int addUserInfo(UserInfo u) {
        if (!checkServerNotNull()) return -1;
        try {
            u.id = (int) server.addUserInfo(u);
            Bitmap QRCode = generateQRCode(u);
            String key = MD5.string2MD5(u.toString());
            QRCodeCache.put(key, QRCode);
            attachQRCode(u.id, key);
        } catch (RemoteException e) {
            Log.d(TAG, "remote invocation failed");
            e.printStackTrace();
        }
        return u.id;
    }

    /**
     * please call this method at worker thread
     * @param userId
     * @param key
     */
    private void attachQRCode(int userId, String key) {
        if (!checkServerNotNull()) return;
        try {
            server.attachQRCodeKey(userId, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkServerNotNull() {
        if (server == null) {
            ToastUtil.show("正在与服务端建立链接，请稍后");
            return false;
        }
        return true;
    }

    public void updateUserInfo(final UserInfo u) {
        if (! checkServerNotNull()) return;
        tm.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    server.updateUserInfo(u);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getAttachedQRCodeKey(final int userId, final Callback<String> c) {
        tm.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return server.getQRCodeKey(userId);
            }
        }, new OnTaskListener<String>() {
            @Override
            public void onFinish(String result) {
                if (c != null) c.onFinish(result);
            }

            @Override
            public void onPreTask() {
                if (c != null) c.onStart();
            }
        });
    }


}
