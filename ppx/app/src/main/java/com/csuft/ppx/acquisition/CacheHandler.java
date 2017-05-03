package com.csuft.ppx.acquisition;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zf on 2017/5/2.
 */

public class CacheHandler {

    private static final String TAG = "CacheHandler";
    private static CacheHandler INSTANCE;
    private Timer timer;
    private TimerTask timerTask;
    private List<Beacon> lastPosition = new ArrayList<>();
    private int circle = 3;

    private CacheHandler() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handlingData();
            }
        };
    }
    public static CacheHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CacheHandler();
        }
        return INSTANCE;
    }

    //处理数据
    private void handlingData() {
        Map<String, List<Beacon>> cache = Cache.getInstance().copyCache();
        if (cache.size() > 3) {
            Log.w(TAG, "handlingData: cache is not enough...", new NoSuchElementException());
            //在这里回调，返回lastPosition
        } else {
            lastPosition.clear();//刷新
            for (List<Beacon> list : cache.values()) {
                //按rssi降序排列
                Collections.sort(list, new Comparator<Beacon>() {
                    @Override
                    public int compare(Beacon b1, Beacon b2) {
                        return b2.getRssi() - b1.getRssi();
                    }
                });
                //过滤数据，命中圈内数据
                if (list.size() > 5) {
                    //数据从6个起跳，剥除最大最小值
                    list.remove(0);
                    list.remove(list.size() - 1);
                    Beacon currentBeacon = list.get(0);
                    
                } else {

                }
            }
        }
    }

    private int hitCircle(List<Beacon> beaconList) {

        return -1;
    }
}
