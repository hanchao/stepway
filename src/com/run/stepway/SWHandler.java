package com.run.stepway;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class SWHandler extends Handler{
	
	public static final int REFRESH = 100;
	
	public MainActivity mMainActivity = null;
	
	public void setMainActivity(MainActivity mainActivity){
		mMainActivity = mainActivity;
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		try {
			switch (msg.what) {
			case REFRESH:
				mMainActivity.refreshSpeed();
				mMainActivity.refreshBar();
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
