package com.csuft.ppx.acquisition;

import android.bluetooth.BluetoothDevice;

import com.csuft.ppx.utils.DataConvertUtils;

/**
 * Created by zf on 2017/5/2.
 */

public class Beacon {
    //设备地址
    private String mac = "EMPTY";
    //场强
    private int rssi = -1;
    //uuid
    private String uuid = "EMPTY";
    //测量功率
    private int measuredPower = -1;
    //发射功率
    private int txPower = -1;
    //距离
    private double distance = -1;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getMeasuredPower() {
        return measuredPower;
    }

    public void setMeasuredPower(int measuredPower) {
        this.measuredPower = measuredPower;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
        //同时更新距离
        setDistance(calculateAccuracy(this.measuredPower, this.rssi));
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public static Beacon fromScanData2Beacon(BluetoothDevice device, int rssi, byte[] data) {
        if (data == null) {
            return null;
        }
        int startByte = 2;
        boolean patternFound = false;
        int responseStartByte = 34;
        boolean responseFonud = false;
        while (startByte <= 5) {
            if (((int) data[startByte + 30] & 0xff) == 0x86 && ((int) data[startByte + 31] & 0xff) == 0x43) {
                responseStartByte = 34;
                responseFonud = true;
            }
            if (((int) data[startByte + 31] & 0xff) == 0x86 && ((int) data[startByte + 32] & 0xff) == 0x43) {
                responseStartByte = 35;
                responseFonud = true;
            }
            if (((int) data[startByte + 2] & 0xff) == 0x02 && ((int) data[startByte + 3] & 0xff) == 0x15) {
                // yes! This is an iBeacon
                patternFound = true;
                break;
            } else if (((int) data[startByte] & 0xff) == 0x2d && ((int) data[startByte + 1] & 0xff) == 0x24 && ((int) data[startByte + 2] & 0xff) == 0xbf && ((int) data[startByte + 3] & 0xff) == 0x16) {
                Beacon beacon = new Beacon();
                return beacon;
            } else if (((int) data[startByte] & 0xff) == 0xad && ((int) data[startByte + 1] & 0xff) == 0x77 && ((int) data[startByte + 2] & 0xff) == 0x00 && ((int) data[startByte + 3] & 0xff) == 0xc6) {
                Beacon beacon = new Beacon();
                return beacon;
            }
            startByte++;
        }

        if (patternFound == false) {
            // This is not an iBeacon
            return new Beacon();
        }

        Beacon beacon = new Beacon();
        if (responseFonud) {
            //解密数据
            byte[] decrypted = new byte[27];
            for(int i = 0; i< 27; i++){
                decrypted[i] = (byte) (data[responseStartByte+i] ^ data[responseStartByte+26]);
            }
            beacon.txPower = (int) decrypted[21];
        }
        beacon.mac = device.getAddress();
        beacon.rssi = rssi;
        beacon.measuredPower = (int) data[startByte + 24];

        // AirLocate:
        // 02 01 1a 1a ff 4c 00 02 15 # Apple's fixed iBeacon advertising prefix
        // e2 c5 6d b5 df fb 48 d2 b0 60 d0 f5 a7 10 96 e0 # iBeacon profile
        // uuid
        // 00 00 # major
        // 00 00 # minor
        // c5 # The 2's complement of the calibrated Tx Power
        byte[] proximityUuidBytes = new byte[16];
        System.arraycopy(data, startByte + 4, proximityUuidBytes, 0, 16);
        String hexString = DataConvertUtils.bytesToHexString(proximityUuidBytes);
        StringBuilder sb = new StringBuilder();
        sb.append(hexString.substring(0, 8));
        sb.append("-");
        sb.append(hexString.substring(8, 12));
        sb.append("-");
        sb.append(hexString.substring(12, 16));
        sb.append("-");
        sb.append(hexString.substring(16, 20));
        sb.append("-");
        sb.append(hexString.substring(20, 32));
        beacon.uuid = sb.toString();

        //计算距离
        beacon.distance = calculateAccuracy(beacon.measuredPower, beacon.rssi);

        return beacon;
    }

    private  static double calculateAccuracy(int measuredPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/measuredPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }
}
