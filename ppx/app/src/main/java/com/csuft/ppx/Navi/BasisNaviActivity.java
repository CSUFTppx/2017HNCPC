package com.csuft.ppx.Navi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.csuft.ppx.R;

import java.util.ArrayList;
import java.util.List;

public class BasisNaviActivity extends AppCompatActivity implements AMapNaviListener, AMap.OnMapLoadedListener,View.OnClickListener{

    private Button startNaviButton;
    /*
    定位功能对象定义
     */
    //终点坐标，固定为停车场
    private NaviLatLng endLatlng = new NaviLatLng(28.133966471354167,112.99830539279515);
    //导航起点坐标，根据定位获取
    private NaviLatLng startLatlng = new NaviLatLng();
    //声明AMapLocationClient类对象
    public AMapLocationClient mapLocationClient=null;
    //声明定位回调监听器
    public AMapLocationListener mapLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                     //可在其中解析amapLocation获取相应内容。
                    //初始化导航起点的坐标
                    //Log.d("定位坐标","经度:"+aMapLocation.getLatitude()+"         纬度:"+aMapLocation.getLongitude());
                    startLatlng.setLatitude(aMapLocation.getLatitude());
                    startLatlng.setLongitude(aMapLocation.getLongitude());
                    System.out.println("已经定位,纬度:"+startLatlng.getLatitude());


                    initNavi();
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }

        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

   /*
   导航对象定义
    */
    //导航对象类（单例）
    private AMapNavi mAMapNavi;
    //地图类
    private AMap mAMap;
    /*
     *开始坐标集合
     */
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    //导航路线图层类
    private RouteOverLay mRouteOverlay;
    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    /*
        * strategyFlag转换出来的值都对应PathPlanningStrategy常量，用户也可以直接传入PathPlanningStrategy常量进行算路。
        * 如:mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList,PathPlanningStrategy.DRIVING_DEFAULT);
        */
    int strategyFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basis_navi);

        startNaviButton=(Button)findViewById(R.id.calculate_route_start_navi);
        startNaviButton.setOnClickListener(this);
        //获取相关权限
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(BasisNaviActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if(ContextCompat.checkSelfPermission(BasisNaviActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(ContextCompat.checkSelfPermission(BasisNaviActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        if(ContextCompat.checkSelfPermission(BasisNaviActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(ContextCompat.checkSelfPermission(BasisNaviActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(BasisNaviActivity.this,permissions,1);
        }else
            //开启定位获取当前坐标点
            startLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置地图
        setUpMapIfNeed();
        //路径规划
        //calculateDriveRote();
    }

    //开始定位
    private void startLocation(){
        //初始化定位
        mapLocationClient=new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mapLocationClient.setLocationListener(mapLocationListener);

        mLocationOption=new AMapLocationClientOption();

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
       // mLocationOption.setOnceLocationLatest(true);
        //给定位客户端对象设置定位参数
        mapLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mapLocationClient.startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0){
                    for (int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    startLocation();
                }
                else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    //设置地图
    private void setUpMapIfNeed(){
        if(mAMap==null){
            mAMap=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            UiSettings uiSettings=mAMap.getUiSettings();
            if(uiSettings!=null)
                uiSettings.setRotateGesturesEnabled(false);
            mAMap.setOnMapLoadedListener(this);
        }
    }

    /**
     * 驾车路径规划
     */
    private void calculateDriveRote(){
        try {
            strategyFlag = mAMapNavi.strategyConvert(true, false, false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //行车路线规划
        // mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag);
        //走路路线规划
        mAMapNavi.calculateWalkRoute(startLatlng,endLatlng);
    }

    /**
     *
     * 导航初始化
     */
    private void initNavi(){
        //将当前和终点坐标加到起点坐标集合中
        startList.add(startLatlng);
        endList.add(endLatlng);
        //初始化对象，添加监听器
        mAMapNavi=AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        calculateDriveRote();
    }

    //清除导航路线图层类
    private void cleanRoteOverlay(){
        if(mRouteOverlay!=null){
            mRouteOverlay.removeFromMap();
            mRouteOverlay.destroy();
        }
    }

    /**
     * 绘制路径规划结果，将导航的路线绘制在地图上
     *
     * @param path AMapNaviPath
     */
    private void  drawRoutes(AMapNaviPath path){
        mAMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        mRouteOverlay=new RouteOverLay(mAMap,path,this);
        mRouteOverlay.addToMap();
        mRouteOverlay.zoomToSpan();
    }

    /**
     * 开始导航
     */
    private void startNavi(){
        Intent gpsintent=new Intent(BasisNaviActivity.this,RouteNaviActivity.class);
        gpsintent.putExtra("gps",true);// gps 为true为真实导航，为false为模拟导航
        startActivity(gpsintent);
    }
    //点击事件
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.calculate_route_start_navi)
            startNavi();
    }

    /**
     * 导航监听器回调函数
     */
    @Override
    public void onCalculateRouteSuccess() {
        //计算路线成功回调
        //将路线显示再地图上
        cleanRoteOverlay();
        AMapNaviPath path=mAMapNavi.getNaviPath();
        if(path!=null)
            drawRoutes(path);
        startNaviButton.setVisibility(View.VISIBLE);
    }
    @Override
    public void onInitNaviFailure() {
        //初始化失败回调
        Toast.makeText(this,"init navi Faild",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitNaviSuccess() {
        //初始化成功回调
    }

    @Override
    public void onStartNavi(int i) {
        //开始导航回调
    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        //当前未知回调
    }

    @Override
    public void onGetNavigationText(int i, String s) {

        //播报类型和文字回到
    }

    @Override
    public void onEndEmulatorNavi() {

        //结束模拟导航
    }

    @Override
    public void onArriveDestination() {

        //到达目的地

    }



    @Override
    public void onCalculateRouteFailure(int i) {

        //路线计算失败回调
        Toast.makeText(this,"计算路径失败，请重新计算路径",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReCalculateRouteForYaw() {
        //偏航后重新计算路径回调

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        //拥堵后重新计算路径回调
    }

    @Override
    public void onArrivedWayPoint(int i) {
        //到达途经点回调
    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        //GPS开关状态回调
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {
             // 导航过程中的信息更新，请看NaviInfo的具体说明
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

        //显示转弯回调
    }

    @Override
    public void hideCross() {

        //隐藏转弯糊掉
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

        //显示道路信息
    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {
        if (i == 0) {
            Toast.makeText(this, "当前在主辅路过渡", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在主辅路过渡");
            return;
        }
        if (i == 1) {
            Toast.makeText(this, "当前在主路", Toast.LENGTH_SHORT).show();

            Log.d("wlx", "当前在主路");
            return;
        }
        if (i == 2) {
            Toast.makeText(this, "当前在辅路", Toast.LENGTH_SHORT).show();

            Log.d("wlx", "当前在辅路");
        }

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

    /**
     *
     * 路径加载函数类
     */
    @Override
    public void onMapLoaded() {

    }


}
