package com.run.stepway;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.*;


public class MainActivity extends MapActivity {

	BMapManager mBMapMan = null;
	MapView mMapView = null;
	MapController mMapController = null;
	LocationListener mLocationListener = null;
	boolean mStartRun = false;
	
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
        mMapView.setBuiltInZoomControls(true);  //�����������õ����ſؼ�
         
        mMapController = mMapView.getController();  // �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
//        GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
//                (int) (116.404 * 1E6));  //�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
//        mMapController.setCenter(point);  //���õ�ͼ���ĵ�
//        mMapController.setZoom(15);    //���õ�ͼzoom����
        
        mLocationListener = new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				if(location != null){
//					String strLog = String.format("����ǰ��λ��:\r\n" +
//							"γ��:%f\r\n" +
//							"����:%f",
//							location.getLongitude(), location.getLatitude());
//
//					TextView mainText = (TextView)findViewById(R.id.textview);
//			        mainText.setText(strLog);
			        GeoPoint point = new GeoPoint((int) (location.getLongitude() * 1E6),
			                (int) (location.getLatitude() * 1E6));
			       // mMapController.setCenter(point);
				}
			}
        };
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
				// ע�ᶨλ�¼�
			    mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
			    mStartRun = true;
			}
			else{
				mBMapMan.getLocationManager().removeUpdates(mLocationListener);
				mStartRun = false;
			}	
    	}
    	break;  
    	case R.id.itemSatellite: { 
    		if(mMapView.isSatellite()){
    			mMapView.setSatellite(false); 
    			item.setTitle("����ͼ");
    		}else{
    			mMapView.setSatellite(true);
    			item.setTitle("��ͼ");
    		}
    	}
    	break;  
    	case R.id.itemAbout:{
    		Builder ad = new  AlertDialog.Builder(this);   
    		ad.setTitle("��; 1.0" );
    		ad.setMessage("make by hanchao" );
    		ad.setPositiveButton("ȷ��" ,  null );
    		ad.show();
    	}
    	break;
    	case R.id.itemExit:{
    		Builder ad = new  AlertDialog.Builder(this);   
    		ad.setTitle("��;" );
    		ad.setMessage("�Ƿ��˳�" );
    		ad.setPositiveButton("ȷ��", 
    			new DialogInterface.OnClickListener(){ 
	                public void onClick(DialogInterface dialoginterface, int i){ 
	                	finish();
	                }
    			}
    		);
    		
    		ad.setNegativeButton("ȡ��" ,  null );
    		ad.show(); 
    		
    	}
    	break;
	   }  
	   return true;  
	}  

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // ����Ƿ��ؼ�,ֱ�ӷ��ص�����
            if(keyCode == KeyEvent.KEYCODE_BACK){
        		Builder ad = new  AlertDialog.Builder(this);   
        		ad.setTitle("��;" );
        		ad.setMessage("�Ƿ��˳�" );
        		ad.setPositiveButton("ȷ��", 
        			new DialogInterface.OnClickListener(){ 
    	                public void onClick(DialogInterface dialoginterface, int i){ 
    	                	finish();
    	                }
        			}
        		);
        		
        		ad.setNegativeButton("ȡ��" ,  null );
        		ad.show();
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
    
}
