package com.csuft.ppx;

import android.Manifest;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.csuft.ppx.XunFeiUtil.XunFeiSpeak;
import com.csuft.ppx.acquisition.CacheHandler;
import com.csuft.ppx.acquisition.LeOperation;
import com.csuft.ppx.activity.BaseActivity;
import com.csuft.ppx.application.ApplicationData;

public class MainActivity extends BaseActivity{

    private static boolean BLE_PERMISSION = false;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private Button button;

    private Button XunFeiButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApplicationData.setMainActivity(MainActivity.this);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LeOperation.getInstance().start();
                CacheHandler.getInstance().start(1500);
            }
        });
        XunFeiButton=(Button)findViewById(R.id.button1);
        XunFeiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XunFeiSpeak.getIance(MainActivity.this).Speak("黄伟民是个大傻逼");
            }
        });
        checkBlePermission();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                BLE_PERMISSION = true;
            } else {
                BLE_PERMISSION = false;
            }
        }
    }
}
