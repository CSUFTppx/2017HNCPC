package com.csuft.ppx.acquisition;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.csuft.ppx.application.ApplicationData;

/**
 * Created by zf on 2017/5/3.
 */

public class LeOperation {

    private static final String TAG = "LeOperation";
    private static LeOperation INSTANCE;
    private BluetoothManager manager;
    private BluetoothAdapter adapter;
    private BluetoothAdapter.LeScanCallback callback;
    private boolean state = false;

    private LeOperation() {
        manager = (BluetoothManager) ApplicationData.globalContext.getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = manager.getAdapter();
        callback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                Beacon beacon = Beacon.fromScanData2Beacon(bluetoothDevice, i, bytes);
                if (beacon == null) {
                    Log.w(TAG, "onLeScan: null", new NullPointerException("beacon is null"));
                } else {
                    Cache.getInstance().put(beacon);
                }
            }
        };
    }

    public static LeOperation getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LeOperation();
        }
        return INSTANCE;
    }

    public void start() {
        if (state == false) {
            adapter.startLeScan(callback);
            state = true;
            Log.i(TAG, "start: success!");
        } else {
            Log.i(TAG, "start: started");
        }
    }

    public void stop() {
        if (state = true) {
            adapter.stopLeScan(callback);
            state = false;
            Log.i(TAG, "stop: success!");
        } else {
            Log.i(TAG, "stop: stoped");
        }
    }
}
