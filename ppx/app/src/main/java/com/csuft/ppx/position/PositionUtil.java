package com.csuft.ppx.position;

import com.csuft.ppx.acquisition.Beacon;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hwm on 2017/4/10.
 */

//定位工具类
public class PositionUtil {
    //private static Map<String,Point> BeaconPoints=new HashMap<>();
    //定义接受到的beacon转化为的圆的arraylist
    private List<Circular> circulars=new ArrayList<>();
    //定义当前的位置
    private Point point;

    private static Beacon closeBeacon=null;//距离最近的那个beacon，为缓存
    private static List<Beacon> ThreeBeacon=new ArrayList<>();//从所有接受到Beacon中赛选到的3个beacon
    private static String[] closeTwoBeaconMAC=new String[2];//最近的两个beacon的MAC

    private static volatile PositionUtil positionUtil;
    //初始化
    public static PositionUtil getIstance(){
        if(positionUtil==null){
            positionUtil=new PositionUtil();
        }
        return positionUtil;
    }

    private PositionUtil() {
        // TODO Auto-generated constructor stub
        //PointInital();
    }

    //根据传过来的beacon实现定位
    public Point Position(List<Beacon> beacons){
        //每次调用就清空arraylist
        circulars.clear();
        //把给出的beacon转化为圆加入到集合中
        for(Beacon b:beacons){
            circulars.add(toCircular(b));
        }
        //根据3个圆定位
        point=getPoint(circulars);
        return point;
    }
    /*
    //实现定位
    public Point Position(List<Beacon> beacons){
        //处理传过来的beacon
        handlerBeacon(beacons);
        //清除圆集合的缓存
        circulars.clear();
        //把给出的beacon转化为圆加入到集合中
        for(Beacon b:ThreeBeacon){
            circulars.add(toCircular(b));
        }
        //根据3个圆定位
        point=getPoint(circulars);
        return point;

    }

    //将接受的beacon处理，获得用于计算的3个beacon
    private void  handlerBeacon(List<Beacon> beacons){
        //将接收到的beacon进行排序
        Collections.sort(beacons, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon o1, Beacon o2) {
                return (int) (o1.getDistance()- o2.getDistance());
            }
        });
        //获取最近的那个beacon
        Beacon mycloseBeacon=beacons.get(0);

        //如果和上一次数据的beacon一样，就无需再计算距离最近的beacon的mac
        if(closeBeacon!=null&&mycloseBeacon.getMac().equals(closeBeacon.getMac())){

        }else{
            //不一样就再次获取
            closeBeacon=mycloseBeacon;
            //获取给出beacon的周围的最近的两个beacon的mac
            closeTwoBeaconMAC= BeaconPoints.getAroundBeacon(mycloseBeacon);
        }
        //每次清除
        ThreeBeacon.clear();
        //把距离最近，信号最强的那个首先加入
        ThreeBeacon.add(mycloseBeacon);
        //再把其他两个也加入
        for(int i=0;i<beacons.size();i++){
            if(beacons.get(i).getMac().equals(closeTwoBeaconMAC[0])||beacons.get(i).getMac().equals(closeTwoBeaconMAC[1]))
                ThreeBeacon.add(beacons.get(i));
        }
        Log.i("hwm","进行计算的3个beacon为:");
        for(Beacon b: ThreeBeacon){
            Log.i("hwm","MAC:  "+b.getMac());
        }

    }

*/
    //根据给定的Beacon，把他转化为相对应的圆
    private static Circular toCircular(Beacon beacon){
        Circular c=null;
        int measuredPower=beacon.getMeasuredPower();
        double rssi=beacon.getRssi();
        double X=-1,Y=-1;
        //获取半径
        double radius=calculateAccuracy(measuredPower, rssi);
        //根据，uuId来获取圆心
        String MAC=beacon.getMac();
        //根据MAC来获取当前Beacon的坐标的
        //X=BeaconPoints.get(MAC).getX();
        //Y=BeaconPoints.get(MAC).getY();
       // System.out.println("当前转化的Beacon MAC为:"+MAC);
        X= BeaconPoints.beaconPointModels.get(MAC).getX();
        Y= BeaconPoints.beaconPointModels.get(MAC).getY();
        if(X==-1||Y==-1){
            System.out.print("出错");
            return null;
        }
        //建立一个Beacon 的圆模型
        System.out.println("mac："+MAC+"     X："+X+"   Y:"+Y+"   txPower:"+measuredPower+"    rssi:"+rssi+"    R:"+radius);
        c=new Circular(radius, X, Y);
        return c;
    }


    //根据给出的三个圆来确定位置的大概位置
    private static Point getPoint(List<Circular> circulars){

        Point result=new Point();
        result.setX(-1);
        result.setY(-1);

        //根据三个圆得到的三个交点，人的位置就在这三个点围成的三角形内
        Point p1=intersect(circulars.get(0),circulars.get(1),circulars.get(2));
        Point p2=intersect(circulars.get(0),circulars.get(2),circulars.get(1));
        Point p3=intersect(circulars.get(1),circulars.get(2),circulars.get(0));

        /*
        //根据权值来合理计算出位置坐标点
        double sum=circulars.get(0).getR()+circulars.get(1).getR()+circulars.get(2).getR();

        //三个圆每个圆半径所占的比例
        double ps1=circulars.get(0).getR()/sum;
        double ps2=circulars.get(1).getR()/sum;
        double ps3=circulars.get(2).getR()/sum;

        //设置返回的结果坐标点
        result.setX((p1.getX()*ps1+p2.getX()*ps2+p3.getX()*ps3)/3);
        result.setY((p1.getY()*ps1+p2.getY()*ps2+p3.getY()*ps3)/3);
        if(result.getX()<0||result.getY()<0)
            System.out.print("位置定位失败！重新定位");
            */
        result.setX((p1.getX()+p2.getX()+p3.getX())/3);
        result.setY((p1.getY()+p2.getY()+p3.getY())/3);
        return result;
    }



    //求两个圆c1和c2的交点,并且这个交点在c3这个圆的范围内
    private  static Point intersect(Circular c1, Circular c2, Circular c3){
       System.out.println("半径为"+c1.getR()+"与 半径为"+c2.getR()+"的交点");
       // System.out.println();
        Point result=null;
        double x1=c1.getX(),y1=c1.getY(),r1=c1.getR();
        double x2=c2.getX(),y2=c2.getY(),r2=c2.getR();
        double x3=c3.getX(),y3=c3.getY();
        if(y1!=y2){
            //两个圆不一起在x轴上
            double M,N,A,B;
            //圆c1的方程表达式为  x*x+y*y-2*x1*x-2*y1*y+x1*x1+y1*y1-r1*r1=0,圆c2的方程表达式一样

            //两圆相交，可以得直线方程为  A-Bx=y,求得 A 和 B;
            A=Math.pow(x1, 2)-Math.pow(x2, 2)+Math.pow(y1, 2)-Math.pow(y2, 2)+Math.pow(r2, 2)-Math.pow(r1, 2);
            A=A/(2*y1-2*y2);

            B=(x1-x2)/(y1-y2);
            //System.out.println("A :"+A+"   B："+B);

            M=(x1+(A-y1)*B)/(1+Math.pow(B, 2));
            N=(Math.pow(x1, 2)+Math.pow(A-y1, 2)-Math.pow(r1, 2))/(1+Math.pow(B, 2));

            //System.out.println("M :"+M+"  N:"+N);

            //判断圆的交点有几个
            double index=Math.pow(2*M, 2)-4*N;

            if(index<0){
                //两个圆没有交点
                System.out.println("两个圆没有交点");
                //如果两个圆没有交点就取他们半径的中间值
                result=new Point();
                result.setX((c1.getX()+c2.getX())/2);
                result.setY((c1.getY()+c2.getY())/2);
                System.out.println("取半径中间值为 ("+(c1.getX()+c2.getX())/2+","+(c1.getY()+c2.getY())/2+")");
                //return result;
            }
            else{
                //两个圆有交点
                double xa=M+Math.sqrt(Math.pow(M, 2)-N);
                double ya=A-B*xa;
                //System.out.println("一组交点为 ("+xa+","+ya+")");
                double xb=M-Math.sqrt(Math.pow(M, 2)-N);
                double yb=A-B*xb;
                // System.out.println("另一组交点为 ("+xb+","+yb+")");

                if(xa==xb){
                    //只有一个交点
                    result=new Point(xa, ya);
                }else{
                    //两个交点，判断与第三个圆圆心距离更近的那个点
                    if((Math.pow(xa-x3, 2)+Math.pow(ya-y3, 2))<=(Math.pow(xb-x3, 2)+Math.pow(yb-y3, 2))){
                        result=new Point(xa, ya);
                    }else{
                        result=new Point(xb, yb);
                    }

                }
                //System.out.println("合理交点为:("+result.getX()+","+result.getY()+")");
                // return result;*/
            }

        }else{
            //两个圆相同的y坐标,不考虑两个相容的情况
            double yy1;
            //两个圆心的的距离
            double distan_l=Math.abs(x1-x2);


            //如果两圆心的距离大于半径之和，不存在交点
            if(distan_l>=(r1+r2)){
                result=new Point((x1+x2)/2, y1);
                System.out.println("两圆不存在交点，取他们中点为 ("+(x1+x2)/2+","+y1+")");
            }
            //存在交点
            else{
                //两个圆相交到中间
                if(r1<distan_l&&r2<distan_l){

                    double xx=(x1+x2)/2;
                    yy1=Math.sqrt(Math.pow(r1, 2)-Math.pow(xx-x1, 2));
                    System.out.println("一组交点为 ("+xx+","+(yy1+y1)+")   , 另一组交点为("+xx+","+(-yy1+y1)+")");

                    if(Math.pow((yy1+y1)-y3, 2)<=Math.pow((-yy1+y1)-y3,2)){
                        result=new Point(xx, yy1+y1);
                    }else{
                        result=new Point(xx, -yy1+y1);
                    }

                }
                //相交到右半边，计算大约值
                else if(r1>distan_l){
                    yy1=Math.sqrt(Math.pow(r1, 2)-Math.pow(distan_l, 2));
                    System.out.println("大约 一组交点为 ("+x2+","+(yy1+y1)+")   , 另一组交点为("+x2+","+(-yy1+y1)+")");

                    if(Math.pow((yy1+y1)-y3, 2)<=Math.pow((-yy1+y1)-y3,2)){
                        result=new Point(x2, yy1+y1);
                    }else{
                        result=new Point(x2, -yy1+y1);
                    }
                }
                //相交到左半边，计算大约值
                else if(r2>distan_l){

                    yy1=Math.sqrt(Math.pow(r2, 2)-Math.pow(distan_l, 2));
                    System.out.println("大约 一组交点为 ("+x1+","+(yy1+y1)+")   , 另一组交点为("+x1+","+(-yy1+y1)+")");

                    if(Math.pow((yy1+y1)-y3, 2)<=Math.pow((-yy1+y1)-y3,2)){
                        result=new Point(x1, yy1+y1);
                    }else{
                        result=new Point(x1, -yy1+y1);
                    }
                }
                else{
                    //
                    System.out.println("两圆不存在交点，取他们中点为 ("+(x1+x2)/2+","+y1+")");
                    result=new Point((x1+x2)/2, y1);
                }

            }

        }
        System.out.println("合理的点为("+result.getX()+","+result.getY()+")");
        return result;
    }

    //求一个数的平方
    private static double pf(double x){
        return x*x;
    }


    //根据txPower和Rssi来估算距离
    private  static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }


}
