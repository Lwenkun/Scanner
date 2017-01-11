package net.bingyan.hustpass.scanner;

import android.app.Application;

import net.bingyan.hustpass.scanner.cache.BitmapDiskCache;

/**
 * Created by lwenkun on 2016/12/21.
 */

public class App extends Application {

    //Singleton instance
    private static App INSTANCE;
    private BitmapDiskCache QRCodeCache;
    private UserInfoManager userInfoManager;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;


    }

    public static App getInstance() {
        return  INSTANCE;
    }

    public BitmapDiskCache getQRCodeCache() {
        if (QRCodeCache == null)
            // I don't want delete any cache item because I use this cache as a permanent storage.
            QRCodeCache = new BitmapDiskCache(this, "qr_code_cache", Long.MAX_VALUE);
        return QRCodeCache;
    }


    public UserInfoManager getUserInfoManager() {
        if (userInfoManager == null)
            userInfoManager = new UserInfoManager(this);
        return userInfoManager;
    }

}
