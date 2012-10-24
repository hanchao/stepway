package com.run.stepway;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;

public class SWTrackOverlay extends MyLocationOverlay{

	public SWTrackOverlay(Context arg0, MapView arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when){
		boolean sec = super.draw(canvas,mapView,shadow,when);
		
//		 this.getLastFix();
//		 
//		 Location location = getLastFix();
//	        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
//	                (int) (location.getLongitude() * 1E6));
	       
		canvas.drawLine(0, 0, 100, 100, new Paint(Color.RED) );
		return sec;
		//return 
	}
}
