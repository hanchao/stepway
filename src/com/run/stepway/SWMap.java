package com.run.stepway;

import java.util.ArrayList;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;

public class SWMap {

	static BMapManager mBMapMan = null;
	MapView mMapView = null;
	MapController mMapController = null;
	LocationListener mLocationListener = null;
	MyLocationOverlay mMyloc = null;
	boolean mLocated = false;
	boolean mRuning = false;
	ArrayList<GeoPoint> mTrackPoints = new ArrayList<GeoPoint>();
	
	static SWMap mMap = null;
	
	static SWMap GetInstance(){
		if(mMap == null){
			mMap = new SWMap();
		}
		return mMap;
	}
	
	public SWMap(){
		
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
	
	public void onCreate(MapActivity mapActivity, MapView mapview){
		mBMapMan.start();
		mapActivity.initMapActivity(mBMapMan);
         
        mMapView = mapview;
        mMapView.setBuiltInZoomControls(true);  //设置启用内置的缩放控件
         
        mMapController = mMapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
                (int) (116.404 * 1E6));  //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
//        mMapController.setCenter(point);  //设置地图中心点
//        mMapController.setZoom(15);    //设置地图zoom级别
        
        mLocationListener = new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				if(location != null && location.getProvider() == "gps"){
					mLocated = true;
					if(isRuning()){
				        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
				                (int) (location.getLongitude() * 1E6));
				        mTrackPoints.add(point);
					}
			       // mMapController.setCenter(point);
				}
			}
        };
        
        mMyloc = new SWTrackOverlay(mapActivity, mMapView);
        mMyloc.enableMyLocation(); // 启用定位
        mMyloc.enableCompass();    // 启用指南针
        mMapView.getOverlays().add(mMyloc);
	}

	public void onDestroy() {
//	    if (mBMapMan != null) {
//	        mBMapMan.destroy();
//	        mBMapMan = null;
//	    }
	}

	public void onPause() {
	    if (mBMapMan != null) {
	        mBMapMan.stop();
	        closeGps();
	    }
	}

	public void onResume() {
	    if (mBMapMan != null) {
	        mBMapMan.start();
	        openGps();
	    }
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
	
	public void goCurPos(){
		Location location = mBMapMan.getLocationManager().getLocationInfo();
		if(location != null){
			GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
	                (int) (location.getLongitude() * 1E6));
			mMapController.setCenter(point);
		}
	}
	
	public boolean isRuning(){
		return mRuning;
	}
	
	public void startRun(){
		mTrackPoints.clear();
		mMapController.setZoom(15);
		goCurPos();
		mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		mRuning = true;
		
//		mTrackPoints.add(new GeoPoint((int) (39.915 * 1E6),
//      (int) (116.404 * 1E6)));
//
//		mTrackPoints.add(new GeoPoint((int) (39.935 * 1E6),
//      (int) (116.404 * 1E6)));
//
//		mTrackPoints.add(new GeoPoint((int) (39.915 * 1E6),
//      (int) (116.304 * 1E6)));
	}
	
	public void stopRun(){
		mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		mRuning = false;
	}
	
	public ArrayList<GeoPoint> getTrackPoints(){
		ArrayList<GeoPoint> trackPoints = new ArrayList<GeoPoint>();
		trackPoints.addAll(mTrackPoints);
		return trackPoints;
	}
}
