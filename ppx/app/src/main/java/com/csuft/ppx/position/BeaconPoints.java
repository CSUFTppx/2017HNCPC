package com.csuft.ppx.position;

import com.csuft.ppx.acquisition.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hwm on 2017/4/19.
 */

//该类是Beacon的坐标映射类
public class BeaconPoints {
    //坐标映射集合
    public static Map<String,Point> beaconPointModels=new HashMap<>();
    private static List<Point> points=new ArrayList<>();

    static {
        beaconPointModels.put("19:18:FC:03:B6:AD",new Point(0,8));
        //beaconPointModels.put("19:18:FC:03:B6:A0",new Point(0,0));
       // beaconPointModels.put("19:18:FC:03:B6:AA",new Point(0,0));
        //beaconPointModels.put("19:18:FC:03:B6:AB",new Point(0,16));
        beaconPointModels.put("19:18:FC:03:B6:C2",new Point(2.3,4));
        beaconPointModels.put("19:18:FC:03:B6:BA",new Point(0,0));
       beaconPointModels.put("19:18:FC:03:07:D2",new Point(0,16));
       beaconPointModels.put("19:18:FC:03:07:AD",new Point(2.3,12));
        // beaconPointModels.put("19:18:FC:03:07:A4",new Point(5,5));
    }

    //根据给的Beacon,返回距离最近的两个Beacon的MAC
    public static String[] getAroundBeacon(Beacon beacon){
        double d;
        double firstmin=100000000; //最小的距离
        double secondmin=100000000;//第二小的距离
        String firstMinMAC="";//最小距离beacon的MAC
        String secondMinMAC="";//第二小的距离beacon的MAC
        String[] result=new String[2];
        //得到目标beacon的坐标
        Point bPoint=beaconPointModels.get(beacon.getMac());
        //遍历map,把距离目标点最短的两个MAC拿出来
        for(Map.Entry<String,Point> entry:beaconPointModels.entrySet()){
            if(!entry.getKey().equals(beacon.getMac())){
                d=pointsDistance(entry.getValue(),bPoint);
                if(d<=firstmin){
                    secondmin=firstmin;
                    secondMinMAC=firstMinMAC;
                    firstmin=d;
                    firstMinMAC=entry.getKey();
                }else if(d>firstmin && d<=secondmin){
                    secondmin=d;
                    secondMinMAC=entry.getKey();
                }
            }
        }

        result[0]=firstMinMAC;
        result[1]=secondMinMAC;
        return result;
    }

    //计算两个点之间的距离
    private static Double pointsDistance(Point a, Point b){
        return Math.pow(a.getX()-b.getX(),2)+Math.pow(a.getY()-b.getY(),2);
    }

    //根据bacon返回对应的坐标点
    public static List<Point> getPointFromBeacon(List<Beacon> beacons){
        points.clear();
        for(Beacon b:beacons){
            points.add(beaconPointModels.get(b.getMac()));
        }
        return points;

    }


}
