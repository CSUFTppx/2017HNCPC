package com.csuft.ppx.position;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by hwm on 2017/5/17.
 */

public class MoveUtils {
    private static final double ABS_DOUBLE_0 = 0.0001;

    //根据三个点获取这个三角形的面积
    private static double getTriangleArea(Point p0, Point p1, Point p2) {
        Point ab, bc;
        ab = new Point(p1.getX() - p0.getX(), p1.getY() - p0.getY());
        bc = new Point(p2.getX() - p1.getX(), p2.getY() - p1.getY());
        return Math.abs((ab.getX() * bc.getY() - ab.getY() * bc.getX()) / 2.0);
    }

    //判断一个点是不是在一个三角形范围内
    //判断点d是不是在points构成的三角形内,注意points的长度只能为3
    public static boolean isInTriangle(List<Point> points, Point d) {
        if(points.size()==3){
        double sabc, sadb, sbdc, sadc;
        sabc = getTriangleArea(points.get(0),points.get(1),points.get(2));
        sadb = getTriangleArea(points.get(0), d, points.get(1));
        sbdc = getTriangleArea(points.get(0), d, points.get(2));
        sadc = getTriangleArea(points.get(1), d, points.get(2));

        double sumSuqar = sadb + sbdc + sadc;

        if (-ABS_DOUBLE_0 < (sabc - sumSuqar) && (sabc - sumSuqar) < ABS_DOUBLE_0) {
            return true;
        } else {
            return false;
        }
        }
        return false;
    }

    //根据点的布局情况，只会出现如下三种情况
    //判断用于定位的三个点的情况，如果其中两个点的x轴坐标相同,根据点的布局，那么为直行，返回1.如果为y轴相同那么为横行，
    //返回-1，如果三个点构成一个直角三角形，那么处于起点或终点(根据实际点的布局情况处理),返回0
    public static int moveDirection(Point a, Point b, Point c){
        if(a.getX()==b.getX()||a.getX()==c.getX()||b.getX()==c.getX()){
            if(a.getY()==b.getY()||a.getY()==c.getY()||b.getY()==c.getY())
                return 0;
            return 1;
        }

        return -1;
    }

    //根据传入的三个点来计算他们的中间值，moveDirection只能为1或-1，为1为直行，为-1为横行
    public static double mid(List<Point> threePoint, int moveDirection){
        Set values=new HashSet();
        double result=0;
        if(moveDirection==1){
            //为直行的时候就算x轴的中点
            for(Point p:threePoint){
                values.add(p.getX());
            }
        }else{
            for(Point p:threePoint){
                values.add(p.getY());
            }
        }
        for(Iterator<Double> it = values.iterator(); it.hasNext(); )
        {
            result+=it.next();
        }
        return result/2;
    }
}
