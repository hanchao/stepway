package com.run.stepway;

import android.app.Application;

public class SWApplication extends Application {

	@Override
    public void onCreate() {
		SWMap.GetInstance().init(this);
		super.onCreate();
	}

	@Override
	//建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		// TODO Auto-generated method stub
		SWMap.GetInstance().destroy();
		super.onTerminate();
	}
}
