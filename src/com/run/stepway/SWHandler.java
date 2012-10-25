package com.run.stepway;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class SWHandler extends Handler{
	
	public static final int SET_SPEED = 100;
	
	public MainActivity mMainActivity = null;
	
	public void setMainActivity(MainActivity mainActivity){
		mMainActivity = mainActivity;
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		try {
			switch (msg.what) {
			case SET_SPEED:
				mMainActivity.refreshSpeed();
				break;
			
			case 2:
				
				break;
			default:
				break;
			}
		} catch (Exception ex) {

		}
	}

}
