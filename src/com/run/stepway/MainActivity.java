package com.run.stepway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.mapapi.*;
import com.weibo.sdk.android.*;
import com.weibo.sdk.android.api.*;
import com.weibo.sdk.android.net.RequestListener;


public class MainActivity extends MapActivity {

	RelativeLayout mRelativeLayoutBar = null;
	RelativeLayout mRelativeLayoutSpeed = null;
	TextView mTextViewTime = null;
	TextView mTextViewDistance = null;
	TextView mTextViewSpeed = null;
	MenuItem mItemDetail = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.activity_main);
        mRelativeLayoutBar = (RelativeLayout)findViewById(R.id.relativeLayoutBar);
        mRelativeLayoutSpeed = (RelativeLayout)findViewById(R.id.relativeLayoutSpeed);
        mTextViewTime = (TextView)findViewById(R.id.textViewTime);
        mTextViewDistance = (TextView)findViewById(R.id.textViewDistance);
        mTextViewSpeed = (TextView)findViewById(R.id.textViewSpeed);
        mRelativeLayoutBar.setVisibility(View.INVISIBLE);
        mRelativeLayoutSpeed.setVisibility(View.INVISIBLE);
        mItemDetail = (MenuItem)findViewById(R.id.itemDetail);
        if(!isGpsEnable()){
        	enableGPS();
        }
        
        MapView mapView = (MapView)findViewById(R.id.bmapsView);
        
        SWMap.GetInstance().onCreate(this, mapView);
        
        SWMap.GetInstance().setHandler(new SWMainHandler());
        	
        SWWeibo.GetInstance().setActivity(this);
        SWWeibo.GetInstance().setHandler(new SWMainHandler());
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
		        mRelativeLayoutBar.setVisibility(View.VISIBLE);
		        mRelativeLayoutSpeed.setVisibility(View.VISIBLE);
				item.setTitle("结束跑步");
    		}else{
    			SWMap.GetInstance().stopRun();
    	        mRelativeLayoutBar.setVisibility(View.INVISIBLE);
    	        mRelativeLayoutSpeed.setVisibility(View.INVISIBLE);
    			item.setTitle("开始跑步");    		
    		}


    	}
    	break;  
    	case R.id.itemPos: {
    		SWMap.GetInstance().goCurPos();
    	}
    	break;
    	case R.id.itemDetail: {
    		if(!SWMap.GetInstance().isRunEnd()){
	    		Builder ad = new  AlertDialog.Builder(this);   
	    		ad.setTitle("步途" );
	    		ad.setMessage("跑步还未完成" );
	    		ad.setPositiveButton("确定" ,  null );
	    		ad.show();
    		}else{
	            Intent intent = new Intent(MainActivity.this,ResultActivity.class);
	            startActivity(intent);	
    		}
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
		String info = String.format("%.1f", speed/0.2778);
		mTextViewSpeed.setText(info);
    }
    
    public void refreshBar(){	
		Time time = SWMap.GetInstance().getRunTime();
		String infoTime = String.format("%02d:%02d:%02d", 
				time.hour,time.minute,time.second);
		mTextViewTime.setText(infoTime);
		float distance = SWMap.GetInstance().getDistance();
		String infoDistance = String.format("%.2fkm",distance/1000);
		mTextViewDistance.setText(infoDistance);
    }
    
    public boolean isGpsEnable(){
    	//GPS是否开启  
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GPS_status = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);//
        return GPS_status;
    }
    
 // 启用GPS
	private void enableGPS() {
		
		Builder ad = new  AlertDialog.Builder(this);   
		ad.setTitle("步途" );
		ad.setMessage("GPS未开启，是否需要开启?" );
		ad.setPositiveButton("确定", 
			new DialogInterface.OnClickListener(){ 
                public void onClick(DialogInterface dialoginterface, int i){ 
					Intent intent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 0);
                }
			}
		);
		
		ad.setNegativeButton("取消" ,  null );
		ad.show(); 
	}
    
    public void share(){	
    	SWWeibo.GetInstance().Share("轨迹", "/sdcard/dcim/beijing.png", "", "");
    }
    
    public void weiboAurhorizeSuccess(){	
    	Toast.makeText(this, "验证成功！", Toast.LENGTH_SHORT)
		.show();
    }
    public void weiboAurhorizeError(){	
    	Toast.makeText(this, "验证失败！", Toast.LENGTH_SHORT)
		.show();
    }
    public void weiboShareSuccess(){	
    	Toast.makeText(this, "分享成功！", Toast.LENGTH_SHORT)
		.show();
    }
    public void weiboShareError(){	
    	Toast.makeText(this, "分享失败！", Toast.LENGTH_SHORT)
		.show();
    }
    
	/**
	 * 截屏方法
	 * @return
	 */
	private Bitmap shot() {
		MapView view = (MapView)findViewById(R.id.bmapsView);
//		View view = getWindow().getDecorView();
//		Display display = this.getWindowManager().getDefaultDisplay();
//		view.layout(0, 0, display.getWidth(), display.getHeight());
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
        SWWeibo.GetInstance().setActivity(this);
        SWWeibo.GetInstance().setHandler(new SWMainHandler());
	    super.onResume();
	}
	

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SWWeibo.GetInstance().onActivityResult(requestCode, resultCode, data);
    }

    class SWMainHandler extends Handler{

    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		try {
    			switch (msg.what) {
    			case SWMap.GPS_REFRESH:
    				refreshSpeed();
    				refreshBar();
    				break;			
    			case SWWeibo.AURHORIZE_SUCCESS:
    				weiboAurhorizeSuccess();
    				share();
    				break;
    			case SWWeibo.AURHORIZE_ERROR:
    				weiboAurhorizeError();
    				break;
    			case SWWeibo.SHARE_SUCCESS:
    				weiboShareSuccess();
    				break;
    			case SWWeibo.SHARE_ERROR:
    				weiboShareError();
    				break;
    			default:
    				break;
    			}
    		} catch (Exception ex) {

    		}
    	}
    }
}


