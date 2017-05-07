package com.csuft.ppx.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.csuft.ppx.application.ApplicationData;

/**
 * Created by ling on 2017/5/5.
 */

public class BaseActivity extends AppCompatActivity{
    private static boolean BLE_PERMISSION = false;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    public BaseActivity() {
        ApplicationData.setBaseActivity(BaseActivity.this);
    }

    public static boolean isBlePermission() {
        return BLE_PERMISSION;
    }

    public static void setBlePermission(boolean blePermission) {
        BLE_PERMISSION = blePermission;
    }

    public static int getMyPermissionsRequestAccessCoarseLocation() {
        return MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION;
    }
    public void checkBlePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                BLE_PERMISSION = false;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                //具有权限
                BLE_PERMISSION = true;
            }
        }
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
