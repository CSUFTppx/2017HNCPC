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
    private static Cache cache;//单例模式

    private Cache() {
        beaconCache = new HashMap<>();
    }

    public static Cache getCache() {
        if (cache == null) {
            cache = new Cache();
        }
        return cache;
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

}
