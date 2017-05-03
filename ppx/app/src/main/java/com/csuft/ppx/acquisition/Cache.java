package com.csuft.ppx.acquisition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zf on 2017/5/2.
 * 用于存放扫描到的数据
 */

public class Cache {

    private Map<String, List<Beacon>> beaconCache;
    private static Cache INSTANCE;//单例模式

    private Cache() {
        beaconCache = new HashMap<>();
    }

    public static Cache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Cache();
        }
        return INSTANCE;
    }

    public void put(Beacon beacon) {
        String key = beacon.getMac();
        if (beaconCache.containsKey(key)) {
            beaconCache.get(key).add(beacon);
        } else {
            List<Beacon> list = new ArrayList<>();
            list.add(beacon);
            beaconCache.put(key, list);
        }
    }

    public synchronized Map<String, List<Beacon>> copyCache() {
        Map<String, List<Beacon>> copy = beaconCache;
        beaconCache = new HashMap<>();
        return copy;
    }

    public boolean isCacheEnough() {
        if (beaconCache.size() >= 4) {
            return true;
        }
        return false;
    }
}
