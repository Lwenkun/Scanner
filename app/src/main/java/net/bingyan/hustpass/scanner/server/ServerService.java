package net.bingyan.hustpass.scanner.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import net.bingyan.hustpass.scanner.App;

/**
 * I use this class to simulate Internet server
 * Created by lwenkun on 2016/12/17.
 */

public class ServerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Proxy(this, 1).asBinder();
    }

    public static Intent newIntent() {
        return new Intent(App.getInstance(), ServerService.class);
    }
}
