package com.csuft.ppx.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.csuft.ppx.R;
import com.csuft.ppx.bean.FindPath;
import com.csuft.ppx.bean.Method;
import com.csuft.ppx.bean.Place;

import java.util.ArrayList;
import java.util.List;

public class ParkActivity extends Activity implements OnClickListener {

//    private static TextView tv;
    private TextView ppx;
    private static ImageView car;
    //停车位
    private ImageView carport1;
    private ImageView carport2;
    private ImageView carport3;
    private ImageView carport4;
    private ImageView carport5;
    private ImageView carport6;
    private List<ImageView> carportList = new ArrayList<ImageView>();
    //选择车位
    private TextView selectPack;
    private TextView stop;
    static float density;  // 屏幕密度（0.75 / 1.0 / 1.5）

    public List<Place> placeList;
    //寻路
    List<FindPath> findPathList = new ArrayList<FindPath>();
    private Method method;
//    public static Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case 1:
//                    move(250,(int)((float)((130-msg.arg2*7.0)*density)));
//                    break;
//            }
//        }
//    };
    //定时器
    final Handler handler = new Handler();
    final int delay = 50;
    int step = 30;
    PathRunnable runnable = null;

    //最大权值
    final int max_val = Integer.MAX_VALUE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_park);

        //屏幕分辨率
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        density = metric.density;

        ppx = (TextView) findViewById(R.id.ppx);
        ppx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        BeaconOperation.getInstance().init(ParkActivity.this);
//                        BeaconOperation.getInstance().startScanLe(100);
                    }
                }).start();

            }
        });


        car = (ImageView)findViewById(R.id.car);
        carport1 = (ImageView)findViewById(R.id.carport1);
        carport1.setOnClickListener(this);
        carport2 = (ImageView)findViewById(R.id.carport2);
        carport2.setOnClickListener(this);
        carport3 = (ImageView)findViewById(R.id.carport3);
        carport3.setOnClickListener(this);
        carport4 = (ImageView)findViewById(R.id.carport4);
        carport4.setOnClickListener(this);
        carport5 = (ImageView)findViewById(R.id.carport5);
        carport5.setOnClickListener(this);
        carport6 = (ImageView)findViewById(R.id.carport6);
        carport6.setOnClickListener(this);
        carportList.add(carport1);
        carportList.add(carport2);
        carportList.add(carport3);
        carportList.add(carport4);
        carportList.add(carport5);
        carportList.add(carport6);
        selectPack = (TextView) findViewById(R.id.select_pack);
        selectPack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                for(ImageView carPort:carportList)
                    carPort.setVisibility(View.VISIBLE);
            }
        });


        placeList = new ArrayList<Place>();
        method = new Method();
        placeList = method.getPlaceList();


    }

    @Override
    public void onClick(View view) {
        int carPort = 0;
        switch(view.getId()){
            case R.id.carport1:
                carPort = 7;
                break;
            case R.id.carport2:
                carPort = 6;
                break;
            case R.id.carport3:
                carPort = 9;
                break;
            case R.id.carport4:
                carPort = 8;
                break;
            case R.id.carport5:
                carPort = 12;
                break;
            case R.id.carport6:
                carPort = 14;
                break;
        }
        for(ImageView cp:carportList)
            cp.setVisibility(View.GONE);
        findPathList = method.getShortPath(carPort,car,density);

        if(runnable == null) {
            runnable = new PathRunnable();
        }
        runnable.setSpaceSize();
        handler.postDelayed(runnable, delay);
    }

    int currentPath = 0;


    class PathRunnable implements Runnable {
        Point startPoint;

        int runPath = 0;
        FindPath pathInfo;
        //是否结束
        boolean isEnd = false;

        //绘图坐标
        float xx=0,yy=0;

        public PathRunnable(){
            // this.spaceSize = spaceSize;
            //运行的地点
            this.runPath = currentPath;
            pathInfo = null;
            isEnd = false;

        }
        public void setSpaceSize(){
            // this.spaceSize = spaceSize;
            this.runPath = currentPath;
            pathInfo = null;

            isEnd = false;
        }
        @Override
        public void run() {
            int left = car.getLeft();
            int top = car.getTop();

            if(pathInfo==null&&findPathList.size()>0){
                pathInfo = findPathList.get(findPathList.size()-1);
                findPathList.remove(pathInfo);

            }


            if(pathInfo!=null) {
                if(runPath==pathInfo.endPath&&findPathList.size()>0){
                    pathInfo = findPathList.get(findPathList.size()-1);
                    findPathList.remove(pathInfo);

                }else if(runPath==pathInfo.endPath&&findPathList.size()==0){
                    isEnd = true;

                }
                switch (pathInfo.direction) {
                    case "left":
                        xx -= step;
                        move(left-step,top);
                        if (left - step <= pathInfo.endLocation * density)
                            runPath = pathInfo.endPath;
                        break;
                    case "top":
                        yy -= step;
                        move(left,top-step);
                        if (top - step <= pathInfo.endLocation * density)
                            runPath = pathInfo.endPath;
                        break;
                    case "right":
                        xx += step;
                        move(left+step,top);
                        if (left + step >= pathInfo.endLocation * density)
                            runPath = pathInfo.endPath;
                        break;
                    case "button":
                        yy += step;
                        move(left,top+step);
                        if (top + step >= pathInfo.endLocation * density)
                            runPath = pathInfo.endPath;
                        break;
                }
                if (!isEnd) {
                    handler.postDelayed(this, delay);
                }
            }
        }
    }
    public static void move(int x,int y){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)car.getLayoutParams();
        params.setMargins(x, y, 0, 0);
        car.requestLayout();
    }
}
