package com.csuft.ppx.Navi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.csuft.ppx.MainActivity;
import com.csuft.ppx.R;
import com.csuft.ppx.XunFeiUtil.TTSController;
import com.csuft.ppx.XunFeiUtil.XunFeiSpeak;
import com.csuft.ppx.acquisition.Cache;
import com.csuft.ppx.acquisition.CacheHandler;
import com.csuft.ppx.acquisition.LeOperation;
import com.csuft.ppx.activity.ParkActivity;
import com.iflytek.cloud.thirdparty.V;


public class RouteNaviActivity extends FragmentActivity implements AMapNaviListener{

    //导航类（单例）
    AMapNavi mAMapNavi;
    Fragment mNaviFragemnt;
    TTSController mTtsManager;

    private static  RouteNaviActivity context;
    private  Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (((String) msg.obj).equals("enough")) {
                //取消进度条
               // Toast.makeText(RouteNaviActivity.this,"接受到足够的beacon",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                //跳转到室内地图
                Intent intent=new Intent(RouteNaviActivity.this, ParkActivity.class);
                startActivity(intent);
            }
        }
    };

    //关闭高德导航页面
    public static void cancel(){
        BasisNaviActivity.context.finish();
        RouteNaviActivity.context.finish();
    }

    //进度对话框
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_route_navi);

        context=this;
        //语音初始化
        mTtsManager = TTSController.getInstance(getApplicationContext());
        mTtsManager.init();

        mAMapNavi=AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
       mAMapNavi.addAMapNaviListener(mTtsManager);
        boolean gps=getIntent().getBooleanExtra("gps",false);
        if(gps)
            mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
        else
            mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode);

        setUpMapIfNeeded();
    }
    private void setUpMapIfNeeded() {
        if (mNaviFragemnt== null) {
            mNaviFragemnt = getSupportFragmentManager().findFragmentById(R.id.navi_fragment);
        }
    }

    @Override
    protected void onResume() {
        setUpMapIfNeeded();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTtsManager.stopSpeaking();
        //
        //        停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
        //        mAMapNavi.stopNavi();
        mAMapNavi.pauseNavi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNavi.stopNavi();
        mAMapNavi.removeAMapNaviListener(mTtsManager);
       mTtsManager.destroy();
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {
        //到达目的地后
        //显示进度条

        XunFeiSpeak.getIance(RouteNaviActivity.this).Speak("请将车缓慢开进车库");

        progressDialog=ProgressDialog.show(RouteNaviActivity.this,"请稍后","正在为你切换车库地图",true);


       // Intent intent=new Intent(RouteNaviActivity.this, ParkActivity.class);
       // startActivity(intent);

       new Thread(new Runnable() {
            @Override
            public void run() {
                /*
                LeOperation.getInstance().start();//开始扫描
                while (true) {
                   if (Cache.getInstance().isCacheEnough()) {
                        //数据充足，跳出循环

                       //取消进度条，进入地图
                      // progressDialog.dismiss();
                       Message msg = new Message();
                       msg.obj = "enough";
                       handler.sendMessage(msg);
                        break;

                    } else {
                        continue;
                    }
                }*/
                //CacheHandler.getInstance().start(1500);//开始处理数据，间隔1500ms
                try {
                    //线程睡两秒
                    Thread.sleep(4000);
                    Message msg = new Message();
                    msg.obj = "enough";
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
       }).start();
    }

    @Override
    public void onCalculateRouteSuccess() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }
}
