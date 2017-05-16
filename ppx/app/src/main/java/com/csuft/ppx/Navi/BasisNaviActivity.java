package com.csuft.ppx.Navi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
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
import com.autonavi.tbt.TrafficFacilityInfo;
import com.csuft.ppx.R;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;

public class BasisNaviActivity extends AppCompatActivity implements AMapNaviListener {

    //地图控件
    // MapView mMapView=null;
    //定位蓝点
    MyLocationStyle myLocationStyle=null;
    //地图类
    AMap aMap;
    //Marker 类的集合
    private List<Marker> markerList=new ArrayList<>();
    // 定义 Marker 点击事件监听
    AMap.OnMarkerClickListener markerClickListener=new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String title=marker.getTitle();
            //if(title.equals("1"))
            //将导航终点变为点击的marker点
            endLatlng.setLatitude(marker.getPosition().latitude);
            endLatlng.setLongitude(marker.getPosition().longitude);
            //规划路径,用来计算路径长度
            calculateDriveRote();

            textView_parkName.setText(marker.getTitle());
            textView_parkAddress.setText(marker.getSnippet());
            //textView_parkDistance.setText(AMapUtils.calculateArea(marker.getPosition(),startL)+"M");
            //根据marker查找当前车库的车库情况再设置
            Toast.makeText(BasisNaviActivity.this,"you clcik "+title+" marker",Toast.LENGTH_SHORT).show();
            return false;
        }
    };


    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
     AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //处理定位数据
            if(aMapLocation!=null){
                if(aMapLocation.getErrorCode()==0){
                    //将定位坐标赋值给导航起点坐标
                    if(startLatlng.getLatitude()==0){
                        //第一次定位后用来显示最近的路径
                        startLatlng.setLatitude(aMapLocation.getLatitude());
                        startLatlng.setLongitude(aMapLocation.getLongitude());
                        Message ms=new Message();
                        ms.arg1=1;
                        hander.sendMessage(ms);
                    }
                    startLatlng.setLatitude(aMapLocation.getLatitude());
                    startLatlng.setLongitude(aMapLocation.getLongitude());
                    Log.i("hwm......","当前坐标为("+aMapLocation.getLatitude()+","+aMapLocation.getLongitude()+")");

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

    //当前点坐标，也是导航起点坐标
    private NaviLatLng startLatlng = new NaviLatLng();
    //导航终点坐标
    private NaviLatLng endLatlng=new NaviLatLng();

    //当前点
    private LatLng startL;

    private TextView textView_parkName;
    private TextView textView_parkAddress;
    private TextView textView_parkChewei;
    private TextView textView_parkDistance;
    private CircleButton button_navi;

    /*
   导航对象定义
    */
    //导航对象类（单例）
    private AMapNavi mAMapNavi;
    private AMapNaviPath aMapNaviPath;
    //地图类
    private AMap mAMap;
    /*
     *开始坐标集合
     */
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();

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
    //handler处理数据
    Handler hander=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            if (msg.arg1==1)
                showClosePark();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_basis_navi);
        //获取地图控件引用
       // mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        //mMapView.onCreate(savedInstanceState);
        //获取地图对象

        textView_parkName=(TextView)findViewById(R.id.text_basis_parkname);
        textView_parkAddress=(TextView)findViewById(R.id.text_basis_parkaddress);
        textView_parkChewei=(TextView)findViewById(R.id.text_basis_chewei) ;
        textView_parkDistance=(TextView)findViewById(R.id.text_basis_distance) ;
        button_navi=(CircleButton) findViewById(R.id.navi_button) ;

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
            initialMap();
    }

    //初始化地图上的一些元素
    private void initialMap(){

        //初始化AMap对象
        if(aMap==null) {
            aMap = ((SupportMapFragment) this.getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        }

        //启动定位
        inititalLocation();
        //初始化导航
        initNavi();
        //控件监听器
        initialListenter();
        //设置定位蓝点
        setLocationPoint();
        //设置地图上markers
        setMarkers();

        //显示最近的那个停车场
       // showClosePark();



    }
    //设置定位蓝点
    private void setLocationPoint(){
        myLocationStyle=new MyLocationStyle();
        //定位一次，且将视角移动到地图中心点
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.strokeColor(Color.argb(0,0,0,0));
        myLocationStyle.radiusFillColor(Color.argb(0,0,0,0));
        //myLocationStyle.radiusFillColor(Color.WHITE);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
    }

    //初始化marker点,并设置监听器
    private void  setMarkers(){

        //地图上添加marker点
        markerList.add(aMap.addMarker(new MarkerOptions().position(new LatLng(28.133966471354167,112.99830539279515)).title("林科大二号车库").snippet("中南林业科技大学电子楼地下车库").icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.ic_marker))).setFlat(true)));
        markerList.add(aMap.addMarker(new MarkerOptions().position(new LatLng(28.13239284939236,113.001533203125)).title("林科大一号车库").snippet("中南林业科技大学行政楼地下车库").icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.ic_marker)))));
        //地图上添加marker监听器
        aMap.setOnMarkerClickListener(markerClickListener);

    }

    //初始化定位
    private void inititalLocation(){
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption=new AMapLocationClientOption();
        //设置为高精度定位模式，
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //根据当前位置，显示距离最近的那个停车场
    private void showClosePark(){
        //设置起点startL;
        startL=new LatLng(startLatlng.getLatitude(),startLatlng.getLatitude());
        //第一个距离
        float distance= AMapUtils.calculateLineDistance(startL,markerList.get(0).getPosition());
        float calcuDistance;
        Marker closeMarker=markerList.get(0);
        //遍历计算markList中的marker
        for(int i=0;i<markerList.size();i++){
            calcuDistance= AMapUtils.calculateLineDistance(startL,markerList.get(i).getPosition());
            if(calcuDistance<distance){
                distance=calcuDistance;
                closeMarker=markerList.get(i);
            }
        }
        //根据marker从服务器获取当前停车场的情况


        //设置初始导航终点
        endLatlng.setLatitude(closeMarker.getPosition().latitude);
        endLatlng.setLongitude(closeMarker.getPosition().longitude);


        //规划初始路径

        startList.add(startLatlng);
        endList.add(endLatlng);
        calculateDriveRote();
        //控件显示信息
        Log.i("hwm.........", "showClosePark: 初始化规划路径成功  startList："+startList.get(0).getLatitude()+"   endList:"+endList.get(0).getLatitude());
        textView_parkName.setText(closeMarker.getTitle());
        textView_parkAddress.setText(closeMarker.getSnippet());
        //textView_parkDistance.setText(aMapNaviPath.getAllLength()+"m");
    }

    //控件监听器
    private void initialListenter(){
        button_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始导航
                startNavi();
                Toast.makeText(BasisNaviActivity.this,"you clcik NAVI bUTTON",Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 开始导航
     */
    private void startNavi(){
        //initNavi();
        //将当前和终点坐标加到起点坐标集合中
        //startList.add(startLatlng);
        //endList.add(endLatlng);
        //计算路径
       // calculateDriveRote();

        //每次路径规划都在点击marker时规划过了，这里就不需要再次规划
        Intent gpsintent=new Intent(BasisNaviActivity.this,RouteNaviActivity.class);
        gpsintent.putExtra("gps",true);// gps 为true为真实导航，为false为模拟导航
        startActivity(gpsintent);
    }
    /**
     *
     * 导航初始化
     */
    private void initNavi(){

        //初始化对象，添加监听器
        mAMapNavi= AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);

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
                   initialMap();
                }
                else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    //路径计算成功
    @Override
    public void onCalculateRouteSuccess() {
        //计算路线成功回调
        //将路线显示再地图上
        aMapNaviPath=mAMapNavi.getNaviPath();
        //Log.i("hwm--------", "onCalculateRouteSuccess: "+aMapNaviPath.getAllLength());
        textView_parkDistance.setText(aMapNaviPath.getAllLength()+"M");

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
}
