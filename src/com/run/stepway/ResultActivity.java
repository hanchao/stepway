package com.run.stepway;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //ImageView imageViewMedal = (ImageView)findViewById(R.id.imageViewMedal);
        TextView textViewBurn = (TextView)findViewById(R.id.textViewBurn);
        TextView textViewDistance = (TextView)findViewById(R.id.textViewDistance);
        TextView textViewTime = (TextView)findViewById(R.id.textViewTime);
        TextView textViewAverageSpeed = (TextView)findViewById(R.id.textViewAverageSpeed);
        TextView textViewMostSpeed = (TextView)findViewById(R.id.textViewMostSpeed);
        
//        Bitmap bmTrack = SWMap.GetInstance().getBitmap();
//        imageViewMedal.setImageBitmap(bmTrack);
		float burn = SWMap.GetInstance().getBurn();
		String infoBurn = String.format("%.1f´ó¿¨", burn);
		textViewBurn.setText(infoBurn);
		
		float distance = SWMap.GetInstance().getDistance();
		String infoDistance = String.format("%.2fkm",distance/1000);
		textViewDistance.setText(infoDistance);
		
		Time time = SWMap.GetInstance().getRunTime();
		String infoTime = String.format("%02d:%02d:%02d", 
				time.hour,time.minute,time.second);
		textViewTime.setText(infoTime);

		float speed = SWMap.GetInstance().getAverageSpeed();
		String infoSpeed = String.format("%.1fkm/h", speed/0.2778);
		textViewAverageSpeed.setText(infoSpeed);
		
		float mostSpeed = SWMap.GetInstance().getMaxSpeed();
		String info = String.format("%.1fkm/h", mostSpeed/0.2778);
		textViewMostSpeed.setText(info);
		
		ImageButton imageButtonWeibo = (ImageButton)findViewById(R.id.imageButtonWeibo);
		imageButtonWeibo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                Intent intent = new Intent(ResultActivity.this,ShareActivity.class);
                startActivity(intent);
			}
			
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }
}
