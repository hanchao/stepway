package com.run.stepway;

import android.app.Application;

public class SWApplication extends Application {

	@Override
    public void onCreate() {
		SWMap.GetInstance().init(this);
		super.onCreate();
	}

	@Override
	//��������app���˳�֮ǰ����mapadpi��destroy()�����������ظ���ʼ��������ʱ������
	public void onTerminate() {
		// TODO Auto-generated method stub
		SWMap.GetInstance().destroy();
		super.onTerminate();
	}
}
