package com.run.stepway;

import android.os.Bundle;
import android.app.Activity;
import android.text.format.Time;
import android.view.Menu;
import android.widget.TextView;

public class ResultActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        TextView textViewBurn = (TextView)findViewById(R.id.textViewBurn);
        TextView textViewDistance = (TextView)findViewById(R.id.textViewDistance);
        TextView textViewTime = (TextView)findViewById(R.id.textViewTime);
        TextView textViewAverageSpeed = (TextView)findViewById(R.id.textViewAverageSpeed);
        TextView textViewMostSpeed = (TextView)findViewById(R.id.textViewMostSpeed);
        
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
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }
}
