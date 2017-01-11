package net.bingyan.hustpass.scanner.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.bingyan.hustpass.scanner.R;
import net.bingyan.hustpass.scanner.Server;
import net.bingyan.hustpass.scanner.fragment.GetServerCallback;
import net.bingyan.hustpass.scanner.server.ServerService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginAndSignUpActivity extends AppCompatActivity implements GetServerCallback{

    private Server server;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_sign_up);

        bindService(ServerService.newIntent(), conn, BIND_AUTO_CREATE);
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


    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                getFragmentManager().popBackStack();
                return true;
        }
        return false;
    }

}

