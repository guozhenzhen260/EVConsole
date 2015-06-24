/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           DogService.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        Dog服务程序，用来判断当应用关闭时，就给他自动重启        
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.view;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.easivend.app.maintain.MaintainActivity;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DogService extends Service {

	Timer timer;
	private final IBinder binder=new LocalBinder();
	private int allopen=1;//1表示一直打开应用,0表示关闭后不打开应用
	public class LocalBinder extends Binder
	{
		public DogService getService()
		{
			return DogService.this;
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}
			
	public int getAllopen() {
		return allopen;
	}
	public void setAllopen(int allopen) {
		this.allopen = allopen;
		Log.i("EV_JNI","setAllopen="+allopen);
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		timer = new Timer(true);
		Log.i("EV_JNI","dog create");
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.i("EV_JNI","dog start");
		timer.schedule(new TimerTask() { 
	        @Override 
	        public void run() { 
	        	String str=null;
	        	if(allopen==1)
        		{
	        		//判断应用是否在运行 
	        		ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
	        		List<RunningTaskInfo> list = activityManager.getRunningTasks(100);
	        		String MY_PKG_NAME = "com.example.evconsole";
	        		int isopen=0;//1代表正在运行，0代表没在运行
	        		for (RunningTaskInfo info : list) 
	        		{	        			 
	        			 Log.i("EV_JNI","appName:"+info.topActivity.getClassName()+"-->pack:"+info.topActivity.getPackageName());
	        			 if (info.topActivity.getPackageName().equals(MY_PKG_NAME)||info.baseActivity.getPackageName().equals(MY_PKG_NAME)) 
	        			 {	        				  
	        				  isopen=1;
	        				  break;
        				 }
	        		}
	        		if(isopen==1)
	        		{
	        			Log.i("EV_JNI", "applicationrun");
	        		}
	        		else
	        		{
	        			Log.i("EV_JNI", "applicationstop");
		        		//1.开启应用程序
		        		Intent intent=new Intent(DogService.this,MaintainActivity.class);
		        		intent.setAction(Intent.ACTION_MAIN);  
		        		intent.addCategory(Intent.CATEGORY_LAUNCHER);  
		        		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		        		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);  
		        		DogService.this.startActivity(intent);	
					}
	        		
        		}
	        	else
	        	{
	        		Log.i("EV_JNI","unopen");
				}
	        } 
	    }, 15*1000, 15*1000);       // timeTask 
	}
	

}
