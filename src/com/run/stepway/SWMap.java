package com.run.stepway;

import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.weibo.sdk.android.Oauth2AccessToken;

public class SWMap {

	static BMapManager mBMapMan = null;
	
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI= 6.28318530712; // 2*PI
	static double DEF_PI180= 0.01745329252; // PI/180.0
	static double DEF_R =6370693.5; // radius of earth
	
	static final String PREFERENCES_NAME = "com_bmap_sdk_android";
	
	public static final int GPS_REFRESH = 100;
	
	MainActivity mMainActivity = null;
	MapView mMapView = null;
	MapController mMapController = null;
	LocationListener mLocationListener = null;
	MyLocationOverlay mMyloc = null;
	boolean mLocated = false;
	boolean mRuning = false;
	boolean mRunEnd = false;
	ArrayList<GeoPoint> mTrackPoints = new ArrayList<GeoPoint>();
	float mSpeed = 0.0f;
	float mMaxSpeed = 0.0f;
	float mDistance = 0.0f;

	Time mStartTime = new Time(); 
	Time mEndTime = new Time(); 
	
	int mMinLat = 0;
	int mMaxLat = 0;
	int mMinLon = 0;
	int mMaxLon = 0;
	
	Handler mHandler = null;
	
	Timer mTimer = null;

	TimerTask mTask = null;
	
	static SWMap mMap = null;
	
	static SWMap GetInstance(){
		if(mMap == null){
			mMap = new SWMap();
		}
		return mMap;
	}
	
	private SWMap(){
		
	}

	public void init(Application app){
        mBMapMan = new BMapManager(app);
        mBMapMan.init("702F895FB6970D5DC38E3C9AC3C91A240800A7A9", null);
        mBMapMan.getLocationManager().setNotifyInternal(1, 1);
	}
	
	public void destroy(){
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
	}
	
	public void onCreate(MainActivity mapActivity, MapView mapview){
		mBMapMan.start();
		openGps();
		mapActivity.initMapActivity(mBMapMan);
         
		mMainActivity = mapActivity;
        mMapView = mapview;
        mMapView.setBuiltInZoomControls(true);  //设置启用内置的缩放控件
         
        mMapController = mMapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放

        readLocation();
        mLocationListener = new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				if(location != null && location.getProvider().compareTo("gps") == 0){
					String str = location.getProvider();
					mLocated = true;
					if(isRuning()){
				        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
				                (int) (location.getLongitude() * 1E6));
				        if(!mTrackPoints.isEmpty()){
				        	GeoPoint pointLast = mTrackPoints.get(mTrackPoints.size()-1);
				        	mDistance += GetDistance(location.getLongitude(),location.getLatitude(),
				        			pointLast.getLongitudeE6()/1E6,pointLast.getLatitudeE6()/1E6);		
				        	
				        	mMinLat = mMinLat < point.getLatitudeE6()?mMinLat : point.getLatitudeE6();
				        	mMaxLat = mMaxLat > point.getLatitudeE6()?mMaxLat : point.getLatitudeE6();
				        	mMinLon = mMinLon < point.getLongitudeE6()?mMinLon : point.getLongitudeE6();
				        	mMaxLon = mMaxLon > point.getLongitudeE6()?mMaxLon : point.getLongitudeE6();
				        }else{
				        	mMinLat = point.getLatitudeE6();
				        	mMaxLat = point.getLatitudeE6();
				        	mMinLon = point.getLongitudeE6();
				        	mMaxLon = point.getLongitudeE6();
				        }
				        mTrackPoints.add(point);
				        
				        mSpeed = location.getSpeed();
				        if(mMaxSpeed<mSpeed){
				        	mMaxSpeed = mSpeed;
				        }
				        			    	
				        if(mHandler!=null){
							Message msg = new Message();
							msg.what = GPS_REFRESH;
							mHandler.sendMessage(msg);
				        }
					}
			       // mMapController.setCenter(point);
				}else{
					mLocated = false;
				}
			}
        };
        
        mMyloc = new SWTrackOverlay(mapActivity, mMapView);
        mMyloc.enableMyLocation(); // 启用定位
        //mMyloc.enableCompass();    // 启用指南针
        mMapView.getOverlays().add(mMyloc);
	}

	public void onDestroy() {
//	    if (mBMapMan != null) {
//	        mBMapMan.destroy();
//	        mBMapMan = null;
//	    }
		closeGps();
		keepLocation();
		mBMapMan.stop();
		
	}

	public void onPause() {
//	    if (mBMapMan != null) {
//	        mBMapMan.stop();
//	        closeGps();
//	    }
	}

	public void onResume() {
//	    if (mBMapMan != null) {
//	        mBMapMan.start();
//	        openGps();
//	    }
	}

	public boolean isSatellite(){
		return mMapView.isSatellite();
	}
	
	public void setSatellite(boolean bSatellite){
		mMapView.setSatellite(bSatellite); 
	}
	
	public boolean isLocated(){
		return mLocated;
	}
	
	public void openGps(){
		mBMapMan.getLocationManager().enableProvider(MKLocationManager.MK_GPS_PROVIDER);
	}

	public void closeGps(){
		mBMapMan.getLocationManager().disableProvider(MKLocationManager.MK_GPS_PROVIDER);
	}
	
	public Location getCurLocation(){
		return mBMapMan.getLocationManager().getLocationInfo();
	}
	
	public void goCurPos(){
		Location location = getCurLocation();
		if(location != null){
			GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
	                (int) (location.getLongitude() * 1E6));
			mMapController.animateTo(point);
		}
	}
	
	public void viewTrack(){
		if(mMaxLat - mMinLat >0 || mMaxLon - mMinLon >0){
			mMapController.setCenter(new GeoPoint((mMinLat + mMaxLat)/2,(mMinLon + mMaxLon)/2));
			mMapController.zoomToSpan(mMaxLat - mMinLat, mMaxLon - mMinLon);
		}
	}
	
	public boolean isRuning(){
		return mRuning;
	}
	
	public boolean isRunEnd(){
		return mRunEnd;
	}
	
	public void startRun(){
		mTrackPoints.clear();
		mSpeed = 0.0f;
		mMaxSpeed = 0.0f;
		mDistance = 0.0f;
		//mMapController.setZoom(15);
		goCurPos();
		mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		mStartTime.setToNow();
		mEndTime.setToNow();
		mTimer = new Timer();
		mTask = new TimerTask( ) {
				public void run ( ) {
					if(mHandler!=null){
						Message message = new Message( );
						message.what = GPS_REFRESH;
						mHandler.sendMessage(message);
					}
				}
			};
		mTimer.schedule(mTask,0,1000);
		mRuning = true;
		mRunEnd = false;
		
//		mTrackPoints.add(new GeoPoint((int) (39.915 * 1E6),
//      (int) (116.404 * 1E6)));
//
//		mTrackPoints.add(new GeoPoint((int) (39.935 * 1E6),
//      (int) (116.404 * 1E6)));
//
//		mTrackPoints.add(new GeoPoint((int) (39.915 * 1E6),
//      (int) (116.304 * 1E6)));
		
//    	mMinLat = (int)(39.915 * 1E6);
//    	mMaxLat = (int)(39.915 * 1E6);
//    	mMinLon = (int)(116.404 * 1E6);
//    	mMaxLon = (int)(116.404 * 1E6);
//    	
//    	mMinLat = mMinLat < (int)(39.935 * 1E6)?mMinLat : (int)(39.935 * 1E6);
//    	mMaxLat = mMaxLat > (int)(39.935 * 1E6)?mMaxLat : (int)(39.935 * 1E6);
//    	mMinLon = mMinLon < (int)(116.404 * 1E6)?mMinLon : (int)(116.404 * 1E6);
//    	mMaxLon = mMaxLon > (int)(116.404 * 1E6)?mMaxLon : (int)(116.404 * 1E6);
//    	
//    	mMinLat = mMinLat < (int)(39.915 * 1E6)?mMinLat : (int)(39.915 * 1E6);
//    	mMaxLat = mMaxLat > (int)(39.915 * 1E6)?mMaxLat : (int)(39.915 * 1E6);
//    	mMinLon = mMinLon < (int)(116.304 * 1E6)?mMinLon : (int)(116.304 * 1E6);
//    	mMaxLon = mMaxLon > (int)(116.304 * 1E6)?mMaxLon : (int)(116.304 * 1E6);
	}
	
	public void stopRun(){
		
		mRuning = false;
		mRunEnd = true;
		mTimer.cancel();
		mTimer.purge();
		mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		mEndTime.setToNow();

		viewTrack();
	}
	
	public ArrayList<GeoPoint> getTrackPoints(){
		ArrayList<GeoPoint> trackPoints = new ArrayList<GeoPoint>();
		trackPoints.addAll(mTrackPoints);
		return trackPoints;
	}
	
	public float getSpeed(){
		return mSpeed;
	}
	
	public float getMaxSpeed(){
		float averageSpeed = getAverageSpeed();
		if(mMaxSpeed<averageSpeed){//防止gps不准确导致最大速度小于平均速度的情况。
			mMaxSpeed = averageSpeed*1.2f;
		}
		return mMaxSpeed;
	}
	
	public float getDistance(){
		return mDistance;
	}
	
	public float getAverageSpeed(){
		Time time = getRunTime();
		return  mDistance / (time.hour * 3600 + time.minute * 60 + time.second);
	}
	
	public float getBurn(){
		float burn = 0.0f;
		float averageSpeed = getAverageSpeed()/0.2778f;
		Time time = getRunTime();
		float hours = time.hour + time.minute / 60.0f + time.second / 3600.0f;
		if(averageSpeed <= 1){//站着
			burn = 120.09f * hours;
		}else if(averageSpeed<=4){//随意走
			burn = 210.80f * hours;
		}else if(averageSpeed<=6){//慢走
			burn = 289.00f * hours;
		}else if(averageSpeed<=10){//跑步(8公里/小时)
			burn = 680.00f * hours;
		}else if(averageSpeed<=12){//跑步(12公里/小时)
			burn = 1020.00f * hours;
		}else if(averageSpeed<=18){//跑步(16公里/小时)
			burn = 1360.00f * hours;
		}else{//跑步(20公里/小时
			burn = 1700.00f * hours;
		}
		return burn;
	}
	
	public Time getRunTime(){
		Time runTime = new Time();
		if(isRuning()){
			Time curTime = new Time(); 
			curTime.setToNow();
			long millis = curTime.toMillis(false)-mStartTime.toMillis(false);
			runTime.set(millis-TimeZone.getDefault().getRawOffset());
		}else{
			long millis = mEndTime.toMillis(false)-mStartTime.toMillis(false);
			runTime.set(millis-TimeZone.getDefault().getRawOffset());
		}
		return runTime;
	}
	
	public void setHandler(Handler handler){
		mHandler = handler;
	}
	
	public double GetDistance(double lon1, double lat1, double lon2, double lat2)
	{
		double ew1, ns1, ew2, ns2;

		double distance;

		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;

		// 求大圆劣弧与球心所夹的角(弧度)
		distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);

		// 调整到[-1..1]范围内，避免溢出
		if (distance > 1.0)
			distance = 1.0;
		else if (distance < -1.0)
			 distance = -1.0;

		// 求大圆劣弧长度
		distance = DEF_R * Math.acos(distance);
		
		return distance;
	}
	
	public void keepLocation() {
		SharedPreferences pref = mMainActivity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		
		GeoPoint point = mMapView.getMapCenter();
		int zoom = mMapView.getZoomLevel();
		editor.putInt("Lat", point.getLatitudeE6());
		editor.putInt("lon", point.getLongitudeE6());
		editor.putInt("zoom", zoom);
		editor.commit();
	}
	
	public void clear(){
	    SharedPreferences pref = mMainActivity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}

	public void readLocation(){

		SharedPreferences pref = mMainActivity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		
		GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
                (int) (116.404 * 1E6));
		point.setLatitudeE6(pref.getInt("Lat", (int) (39.915 * 1E6)));
		point.setLongitudeE6(pref.getInt("lon", (int) (116.404 * 1E6)));
		int zoom = pref.getInt("zoom", (int) (15));
		
		mMapController.setCenter(point);  //设置地图中心点
		mMapController.setZoom(zoom);    //设置地图zoom级别
	}
	
	public Bitmap getBitmap() {

		mMapView.setBuiltInZoomControls(false);
		mMapView.setDrawingCacheEnabled(true);//允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap
		Bitmap bmp = Bitmap.createBitmap(mMapView.getDrawingCache());
		mMapView.setBuiltInZoomControls(true);
		return bmp;
	}
}
