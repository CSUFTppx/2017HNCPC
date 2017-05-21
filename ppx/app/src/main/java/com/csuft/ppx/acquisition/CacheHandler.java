package com.csuft.ppx.acquisition;

import android.os.Message;
import android.util.Log;

import com.csuft.ppx.activity.ParkActivity;
import com.csuft.ppx.position.BeaconPoints;
import com.csuft.ppx.position.MoveUtils;
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
    private static Beacon closeBeacon = null;//距离最近的那个beacon，为缓存
    private static List<Beacon> ThreeBeacon = new ArrayList<>();//从所有接受到Beacon中赛选到的3个beacon

    private static String[] closeTwoBeaconMAC = new String[2];//最近的两个beacon的MAC

    //当前定位点
    private static Point point=null;
    //上一个定位点
    private static Point lastPoint=null;
    //运动方向
    private int moveDirection;
    //当前用于计算的3个beacon的坐标点集合
    private static List<Point> points=new ArrayList<>();

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
                    Log.i(TAG, "handlingData: " + currentBeacon.getMac());
                    currentBeacon.setRssi(hitCircle(list));
                    Log.i(TAG, "handlingData: distance" + currentBeacon.getDistance());
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


        //hwm................

        //对接收到beacon进行排序
        Collections.sort(lastPosition, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon o1, Beacon o2) {
                return (int) (o1.getDistance() * 100 - o2.getDistance() * 100);
            }
        });


        try {
        //获取最近的那个beacon
        Beacon mycloseBeacon = lastPosition.get(0);

        //如果和上一次数据的距离最近beacon一样，就无需再计算
        if (closeBeacon != null && mycloseBeacon.getMac().equals(closeBeacon.getMac())) {

        } else {
            //不一样就再次获取
            closeBeacon = mycloseBeacon;

            closeTwoBeaconMAC = BeaconPoints.getAroundBeacon(mycloseBeacon);
            //获取这3个beacond的坐标点

            points.clear();
            points.add(BeaconPoints.beaconPointModels.get(mycloseBeacon.getMac()));
            points.add(BeaconPoints.beaconPointModels.get(closeTwoBeaconMAC[0]));
            points.add(BeaconPoints.beaconPointModels.get(closeTwoBeaconMAC[1]));

        }

        //每次清除
        ThreeBeacon.clear();
        //把距离最近，信号最强的那个首先加入
        ThreeBeacon.add(mycloseBeacon);

        //再把其他两个也加入
        for (int i = 0; i < lastPosition.size(); i++) {
            if (lastPosition.get(i).getMac().equals(closeTwoBeaconMAC[0]) || lastPosition.get(i).getMac().equals(closeTwoBeaconMAC[1]))
                ThreeBeacon.add(lastPosition.get(i));
        }


        if (ThreeBeacon.size() == 3) {
            //获取到最近三组，较为精确的定位

            Log.i("hwm", "进行计算的3个beacon为:");
            for (Beacon b : ThreeBeacon) {
                Log.i("hwm", "MAC:  " + b.getMac());
            }
            //根据beacon实现定位
            try {
                //获取计算的坐标点
                point = PositionUtil.getIstance().Position(ThreeBeacon);
                //处理坐标点

                //获取这3个beacond的坐标点
                // points=BeaconPoints.getPointFromBeacon(ThreeBeacon);

                //获取他们的运动发向
                moveDirection = MoveUtils.moveDirection(points.get(0), points.get(1), points.get(2));

                //判断是不是在三角形内
                if (MoveUtils.isInTriangle(points, point)) {
                    //在三角形内

                    Log.i(TAG, "handlingData: 在三角形内");


                    if (moveDirection == 1)
                        point.setX(MoveUtils.mid(points, moveDirection));//直行处理X
                    else if (moveDirection == -1)
                        point.setY(MoveUtils.mid(points, moveDirection));//横行处理Y
                    //启动缓存
                    lastPoint = point;

                } else {
                    //不在三角形内,采用另一种方式来定位
                    Log.i(TAG, "handlingData: 不在三角形内，采用另一种方式来定位");
                    point = PositionUtil.getIstance().Position(lastPosition.subList(0, 3));
                    //判断是不是在三角形内
                    if (MoveUtils.isInTriangle(points, point)) {
                        if (moveDirection == 1) {
                            point.setX(MoveUtils.mid(points, moveDirection));//直行处理X
                        } else if (moveDirection == -1)
                            point.setY(MoveUtils.mid(points, moveDirection));//横行处理Y
                        lastPoint = point;
                    } else {
                        Log.i(TAG, "handlingData: 启动缓存点");
                        point = lastPoint;
                    }

                }

            } catch (Exception e) {
                Log.i(TAG, "handlingData: 此次定位失败，重新定位");
            }

        } else {
            //根据距离最近的三个beacon定位，精度有所欠缺
            Log.i("hwm", "精度欠缺定位。。。。。距离最近的三个beacon定位");
            point = PositionUtil.getIstance().Position(lastPosition.subList(0, 3));
            //
            if (moveDirection == 1 && lastPoint != null) {
                point.setX(lastPoint.getX());
            } else if (moveDirection == -1 && lastPoint != null) {
                point.setY(lastPoint.getY());
            }
        }
    }catch (Exception e){
            Log.i(TAG, "handlingData: 异常处理采用缓存");
        if(lastPoint!=null)
            point=lastPoint;
        else
            Log.i(TAG, "handlingData: 定位终极失败。。。。。。我也没办法了");
    }


        //检测不合理的数据
        if(point==null||Double.isNaN(point.getX())||Double.isNaN(point.getY())||point.getX()<0||point.getY()<0){
            Log.i(TAG, "handlingData: 定位出错，数字出现NAN或者出现负数,或者出现错误");
        }else{
            Log.i(TAG, "handlingData: ***************华丽的分割线****************");
            Log.i("hwm", "定位坐标为   (" + point.getX() + "," + point.getY() + ")");
            Log.i(TAG, "handlingData: ***************华丽的分割线****************");
             /*
        Message message = new Message();
        message.what = 1;
        message.arg1 = (int) (point.getX() * 17.5 * 100);
        message.arg2 = (int) (point.getY() * 13.3 * 100);
        ParkActivity.handler.sendMessage(message);
        */
        }
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
        Log.i(TAG, "hitCircle: " + hit);
        return hit;
    }

    public void start(int interval) {
        timer.schedule(timerTask, interval, interval);
    }

    public void stop() {
        timer.cancel();
    }
}
