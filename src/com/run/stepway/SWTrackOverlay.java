package com.run.stepway;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Shader;
import android.location.Location;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.Projection;

public class SWTrackOverlay extends MyLocationOverlay{

	Paint mPaint = null;
	Paint mBackPaint = null;
	public SWTrackOverlay(Context arg0, MapView arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.rgb(0x04, 0x9F, 0xF1));
		mPaint.setStrokeWidth(8); 
		mPaint.setStyle(Paint.Style.STROKE); 
		mPaint.setPathEffect(new CornerPathEffect(8));
		mPaint.setShadowLayer(5, 3, 3, 0xFF4C4C4C);  
		
		mBackPaint = new Paint();
		mBackPaint.setAntiAlias(true);
		mBackPaint.setColor(Color.rgb(0xEA, 0xE0, 0xEF));
		mBackPaint.setStrokeWidth(14); 
		mBackPaint.setStyle(Paint.Style.STROKE); 
		mBackPaint.setPathEffect(new CornerPathEffect(14));
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when){
		
	//	GeoPoint geopoint2 = this.getMyLocation();

//	        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
//	                (int) (location.getLongitude() * 1E6));
		ArrayList<GeoPoint> trackPoints = SWMap.GetInstance().getTrackPoints();
		if(!trackPoints.isEmpty()){
			Projection projection = mapView.getProjection();
			Iterator<GeoPoint> iterator = trackPoints.iterator();
			Path path = new Path();
			while(iterator.hasNext()){
				GeoPoint geoPoint = iterator.next();
				Point point = new Point();
				projection.toPixels(geoPoint, point);
				if(path.isEmpty()){
					path.moveTo(point.x,point.y);
				}else{
					
				}
				path.lineTo(point.x,point.y);
			}
			
			//canvas.drawPath(path, mBackPaint);
			canvas.drawPath(path, mPaint);
		}
		return super.draw(canvas,mapView,shadow,when);
		//return 
	}
}
