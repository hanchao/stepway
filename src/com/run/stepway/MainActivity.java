package com.run.stepway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.mapapi.*;
import com.weibo.sdk.android.*;
import com.weibo.sdk.android.api.*;
import com.weibo.sdk.android.net.RequestListener;


public class MainActivity extends MapActivity {

	
	public SWHandler mHandler = null;
	
	public ImageView mImageViewBar = null;
	public ImageView mImageViewSpeed = null;
	public TextView mTextViewBar = null;
	public TextView mTextViewSpeed = null;
	
	Weibo mWeibo = null;
	static Oauth2AccessToken accessToken = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.activity_main);
        mImageViewBar = (ImageView)findViewById(R.id.imageViewBar);
        mImageViewSpeed = (ImageView)findViewById(R.id.imageViewSpeed);
        mTextViewBar = (TextView)findViewById(R.id.textViewBar);
        mTextViewSpeed = (TextView)findViewById(R.id.textViewSpeed);
        
        MapView mapView = (MapView)findViewById(R.id.bmapsView);
        
        SWMap.GetInstance().onCreate(this, mapView);
        
        mHandler = new SWHandler();
        mHandler.setMainActivity(this);
        SWMap.GetInstance().setHandler(mHandler);
        	
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
    		if(!SWMap.GetInstance().isRuning()){
				SWMap.GetInstance().startRun();	
		        refreshSpeed();
		        refreshBar();
		        mImageViewBar.setVisibility(View.VISIBLE);
		        mImageViewSpeed.setVisibility(View.VISIBLE);
		        mTextViewBar.setVisibility(View.VISIBLE);
		        mTextViewSpeed.setVisibility(View.VISIBLE);
				item.setTitle("结束跑步");
    		}else{
    			SWMap.GetInstance().stopRun();
		        mImageViewBar.setVisibility(View.INVISIBLE);
		        mImageViewSpeed.setVisibility(View.INVISIBLE);
    			mTextViewBar.setVisibility(View.INVISIBLE);
    			mTextViewSpeed.setVisibility(View.INVISIBLE);
    			item.setTitle("开始跑步");
    		}


    	}
    	break;  
    	case R.id.itemPos: {
    		SWMap.GetInstance().goCurPos();
    	}
    	break;
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
    		if(SWMap.GetInstance().isSatellite()){
    			SWMap.GetInstance().setSatellite(false); 
    			item.setTitle("卫星图");
    		}else{
    			SWMap.GetInstance().setSatellite(true);
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
    
    public void refreshSpeed(){
		float speed = SWMap.GetInstance().getSpeed();
		String info = String.format("%.2f", speed/0.2778);
		mTextViewSpeed.setText(info);
    }
    
    public void refreshBar(){
		float distance = SWMap.GetInstance().getDistance();
		Time time = SWMap.GetInstance().getRunTime();
		String info = String.format("时长%2d:%2d:%2d  距离%.2fkm", 
				time.hour,time.minute,time.second,distance/1000);
		mTextViewBar.setText(info);
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
		SWMap.GetInstance().onDestroy();
	    super.onDestroy();
	}
	@Override
	protected void onPause() {
		SWMap.GetInstance().onPause();
	    super.onPause();
	}
	@Override
	protected void onResume() {
	    SWMap.GetInstance().onResume();
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


