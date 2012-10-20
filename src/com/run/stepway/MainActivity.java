package com.run.stepway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.baidu.mapapi.*;
import com.weibo.sdk.android.*;
import com.weibo.sdk.android.api.*;
import com.weibo.sdk.android.net.RequestListener;


public class MainActivity extends MapActivity {

	BMapManager mBMapMan = null;
	MapView mMapView = null;
	MapController mMapController = null;
	LocationListener mLocationListener = null;
	MyLocationOverlay mMyloc = null;
	boolean mStartRun = false;
	ArrayList<GeoPoint> mTrackPoints = new ArrayList<GeoPoint>();
	
	Weibo mWeibo = null;
	static Oauth2AccessToken accessToken = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.activity_main);
        

        
        mBMapMan = new BMapManager(getApplication());
        mBMapMan.init("702F895FB6970D5DC38E3C9AC3C91A240800A7A9", null);
        mBMapMan.getLocationManager().setNotifyInternal(10, 5);
        
        mBMapMan.start();
        super.initMapActivity(mBMapMan);
         
        mMapView = (MapView) findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true);  //设置启用内置的缩放控件
         
        mMapController = mMapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
                (int) (116.404 * 1E6));  //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
//        mMapController.setCenter(point);  //设置地图中心点
//        mMapController.setZoom(15);    //设置地图zoom级别
        
        mLocationListener = new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				if(location != null){
//					String strLog = String.format("您当前的位置:\r\n" +
//							"纬度:%f\r\n" +
//							"经度:%f",
//							location.getLongitude(), location.getLatitude());
//
//					TextView mainText = (TextView)findViewById(R.id.textview);
//			        mainText.setText(strLog);
			        GeoPoint point = new GeoPoint((int) (location.getLongitude() * 1E6),
			                (int) (location.getLatitude() * 1E6));
			        mTrackPoints.add(point);
			       // mMapController.setCenter(point);
				}
			}
        };
        
        mMyloc = new MyLocationOverlay(this, mMapView);
        mMyloc.enableMyLocation(); // 启用定位
        mMyloc.enableCompass();    // 启用指南针
        mMapView.getOverlays().add(mMyloc);
        
        //mMapController.setCenter(myloc.getMyLocation());
        mWeibo = Weibo.getInstance("1398278549", "http://hanchao0123.diandian.com/");
    }
  

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override   
	public boolean onOptionsItemSelected(MenuItem item) {  

    	switch(item.getItemId()) {  
    	case R.id.itemRun: {
			if(!mStartRun){
				mMapController.setZoom(15);
				// 注册定位事件
			    mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
			    mStartRun = true;
			}
			else{
				mBMapMan.getLocationManager().removeUpdates(mLocationListener);
				mStartRun = false;
			}	
    	}
    	break;  
    	case R.id.itemPos: {
    		if(!mTrackPoints.isEmpty()){
    			GeoPoint point = mTrackPoints.get(mTrackPoints.size()-1);
    			mMapController.setCenter(point);
    		}  		
    	}
    	case R.id.itemShare: {
    		Bitmap screen = shot();
    		try {
    			saveMyBitmap(screen,"/sdcard/dcim/beijing.png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		mWeibo.authorize(this, new AuthDialogListener());
    	}
    	break; 
    	case R.id.itemSatellite: { 
    		if(mMapView.isSatellite()){
    			mMapView.setSatellite(false); 
    			item.setTitle("卫星图");
    		}else{
    			mMapView.setSatellite(true);
    			item.setTitle("地图");
    		}
    	}
    	break;  
    	case R.id.itemAbout:{
    		Builder ad = new  AlertDialog.Builder(this);   
    		ad.setTitle("步途 1.0" );
    		ad.setMessage("make by hanchao" );
    		ad.setPositiveButton("确定" ,  null );
    		ad.show();
    	}
    	break;
    	case R.id.itemExit:{
    		Builder ad = new  AlertDialog.Builder(this);   
    		ad.setTitle("步途" );
    		ad.setMessage("是否退出?" );
    		ad.setPositiveButton("确定", 
    			new DialogInterface.OnClickListener(){ 
	                public void onClick(DialogInterface dialoginterface, int i){ 
	                	finish();
	                }
    			}
    		);
    		
    		ad.setNegativeButton("取消" ,  null );
    		ad.show(); 
    		
    	}
    	break;
	   }  
	   return true;  
	}  
    
	/**
	 * 截屏方法
	 * @return
	 */
	private Bitmap shot() {
		View view = getWindow().getDecorView();
		Display display = this.getWindowManager().getDefaultDisplay();
		view.layout(0, 0, display.getWidth(), display.getHeight());
		view.setDrawingCacheEnabled(true);//允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap
		Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
		return bmp;
	}
	
	public void saveMyBitmap(Bitmap bitmap, String bitName) throws IOException {
		File f = new File(bitName);
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
		fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
		fOut.flush();
		} catch (IOException e) {
		e.printStackTrace();
		}
		try {
		fOut.close();
		} catch (IOException e) {
		e.printStackTrace();
		}
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    // 如果是返回键,直接返回到桌面
        if(keyCode == KeyEvent.KEYCODE_BACK){
    		Builder ad = new  AlertDialog.Builder(this);   
    		ad.setTitle("步途" );
    		ad.setMessage("是否退出?" );
    		ad.setPositiveButton("确定", 
    			new DialogInterface.OnClickListener(){ 
	                public void onClick(DialogInterface dialoginterface, int i){ 
	                	finish();
	                }
    			}
    		);
    		
    		ad.setNegativeButton("取消" ,  null );
    		ad.show();
    		return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onDestroy() {
	    if (mBMapMan != null) {
	        mBMapMan.destroy();
	        mBMapMan = null;
	    }
	    super.onDestroy();
	}
	@Override
	protected void onPause() {
	    if (mBMapMan != null) {
	        mBMapMan.stop();
	    }
	    super.onPause();
	}
	@Override
	protected void onResume() {
	    if (mBMapMan != null) {
	        mBMapMan.start();
	    }
	    super.onResume();
	}
	
	
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
			if (MainActivity.accessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(MainActivity.accessToken.getExpiresTime()));
				//mText.setText("认证成功: \r\n access_token: "+ token + "\r\n" + "expires_in: " + expires_in+"\r\n有效期："+date);
				try {
	                Class sso=Class.forName("com.weibo.sdk.android.api.WeiboAPI");//如果支持weiboapi的话，显示api功能演示入口按钮
	                //apiBtn.setVisibility(View.VISIBLE);
	            } catch (ClassNotFoundException e) {
//	                e.printStackTrace();
	                //Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");
	               
	            }
				//cancelBtn.setVisibility(View.VISIBLE);
				//AccessTokenKeeper.keepAccessToken(MainActivity.this, accessToken);
				//Toast.makeText(MainActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
				
				StatusesAPI api = new StatusesAPI(MainActivity.accessToken);
				//api.update("test from stepway", "90", "90", new RequestListener(){
				api.upload("test from stepway", "/sdcard/dcim/beijing.png", "90", "90", new RequestListener(){

					@Override
					public void onComplete(String arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onError(WeiboException arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onIOException(IOException arg0) {
						// TODO Auto-generated method stub
						
					}
				
				});
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			//Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(),
			//		Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			//Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			//Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(),
			//		Toast.LENGTH_LONG).show();
		}

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        /**
         * 下面两个注释掉的代码，仅当sdk支持sso时有效，
         */
//        if(mSsoHandler!=null){
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
    }

    
}


