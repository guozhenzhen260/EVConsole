package com.easivend.view;

import com.easivend.common.ToolClass;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AlarmService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ToolClass.Log(ToolClass.INFO,"EV_DOG","闹钟整理文件...","dog.txt");
		//整理日志文件用
		ToolClass.optLogFile(); 
	}
	
	

}
