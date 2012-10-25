package com.run.stepway;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class SWHandler extends Handler{
	
	public static final int REFRESH = 100;
	public static final int WEIBO_AURHORIZE_SUCCESS = 101;
	public static final int WEIBO_AURHORIZE_ERROR = 102;
	public static final int WEIBO_SHARE_SUCCESS = 103;
	public static final int WEIBO_SHARE_ERROR = 104;
	
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
			case WEIBO_AURHORIZE_SUCCESS:
				mMainActivity.weiboAurhorizeSuccess();
				mMainActivity.share();
				break;
			case WEIBO_AURHORIZE_ERROR:
				mMainActivity.weiboAurhorizeError();
				break;
			case WEIBO_SHARE_SUCCESS:
				mMainActivity.weiboShareSuccess();
				break;
			case WEIBO_SHARE_ERROR:
				mMainActivity.weiboShareError();
				break;
			default:
				break;
			}
		} catch (Exception ex) {

		}
	}

}
