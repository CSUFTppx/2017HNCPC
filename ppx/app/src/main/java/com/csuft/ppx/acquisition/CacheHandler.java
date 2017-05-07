package com.csuft.ppx.acquisition;

import android.os.Message;
import android.util.Log;

import com.csuft.ppx.activity.ParkActivity;
import com.csuft.ppx.position.BeaconPoints;
import com.csuft.ppx.position.Point;
import com.csuft.ppx.position.PositionUtil;

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
    private static Beacon closeBeacon=null;//距离最近的那个beacon，为缓存
    private static List<Beacon> ThreeBeacon=new ArrayList<>();//从所有接受到Beacon中赛选到的3个beacon
    private static String[] closeTwoBeaconMAC=new String[2];//最近的两个beacon的MAC

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
        if (cache.size() <= 3) {
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
                    Log.i(TAG, "handlingData: "+currentBeacon.getMac());
                    currentBeacon.setRssi(hitCircle(list));
                    Log.i(TAG, "handlingData: distance"+currentBeacon.getDistance());
                    lastPosition.add(currentBeacon);
                } else {
                    //数据较少，取最强的
                    Beacon currentBeacon = list.get(0);
                    lastPosition.add(currentBeacon);
                }
            }
            //在这里回调，返回lastPosition
            Log.i(TAG, "handlingData: ***************华丽的分割线****************");
        }
        
        //对接收到beacon进行排序
        Collections.sort(lastPosition, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon o1, Beacon o2) {
                return (int) (o1.getDistance()- o2.getDistance());
            }
        });
        //获取最近的那个beacon
        Beacon mycloseBeacon=lastPosition.get(0);
        //如果和上一次数据的beacon一样，就无需再计算

        if(closeBeacon!=null&&mycloseBeacon.getMac().equals(closeBeacon.getMac())){

        }else{
            //不一样就再次获取
            closeBeacon=mycloseBeacon;
            closeTwoBeaconMAC= BeaconPoints.getAroundBeacon(mycloseBeacon);
        }
        //每次清除
        ThreeBeacon.clear();
        //把距离最近，信号最强的那个首先加入
        ThreeBeacon.add(mycloseBeacon);
        //再把其他两个也加入
        for(int i=0;i<lastPosition.size();i++){
            if(lastPosition.get(i).getMac().equals(closeTwoBeaconMAC[0])||lastPosition.get(i).getMac().equals(closeTwoBeaconMAC[1]))
                ThreeBeacon.add(lastPosition.get(i));
        }
        Log.i("hwm","进行计算的3个beacon为:");
        for(Beacon b: ThreeBeacon){
           Log.i("hwm","MAC:  "+b.getMac());
        }
        //根据beacon实现定位
        if (ThreeBeacon.size()==3) {
            try{
                Point point = PositionUtil.getIstance().Position(ThreeBeacon);
                Log.i(TAG, "handlingData: ***************华丽的分割线****************");
                Log.i("hwm", "定位坐标为   (" + point.getX() + "," + point.getY() + ")");
                Log.i(TAG, "handlingData: ***************华丽的分割线****************");
                Message message = new Message();
                message.what = 1;
                message.arg1 = (int) (point.getX() * 100);
                message.arg2 = (int) (point.getY() * 100);
                ParkActivity.handler.sendMessage(message);
            }catch (Exception e){
                Log.i(TAG, "handlingData: 此次定位失败，重新定位");
            }
        }
        else
            Log.i(TAG, "handlingData: 此次定位失败，重新定位");

    }

    private int hitCircle(List<Beacon> beaconList) {
        int Max = 0;
        int hit = 0;
        for (int currentIndex = 0; currentIndex < beaconList.size(); currentIndex++) {
            int startIndex = currentIndex;
            int endIndex = currentIndex;
            //左移遍历
            for (int left = currentIndex; left > 0; left--) {
                if (Math.abs(beaconList.get(currentIndex).getRssi() - beaconList.get(left).getRssi()) <= circle) {
                    startIndex = left;
                } else {
                    break;
                }
            }
            //右移遍历
            for (int right = currentIndex; right < beaconList.size(); right++) {
                if (Math.abs(beaconList.get(currentIndex).getRssi() - beaconList.get(right).getRssi()) <= circle) {
                    endIndex = right;
                } else {
                    break;
                }
            }
            int length = endIndex - startIndex + 1;
            if (length > Max) {
                Max = length;
                //取中值
                if (length == 1) {
                    hit = beaconList.get(startIndex).getRssi();
                } else {
                    if (length % 2 == 0) {
                        hit = (beaconList.get(startIndex + length / 2).getRssi() + beaconList.get(startIndex + length / 2 - 1).getRssi()) / 2;
                    } else {
                        hit = beaconList.get(startIndex + length / 2).getRssi();
                    }
                }
            }
        }
        Log.i(TAG, "hitCircle: "+hit);
        return hit;
    }

    public void start(int interval) {
        timer.schedule(timerTask, interval, interval);
    }

    public void stop() {
        timer.cancel();
    }
}
