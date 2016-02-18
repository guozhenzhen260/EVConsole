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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.app.maintain.MaintainActivity;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.http.EVServerhttp;
import com.easivend.view.EVServerService.ActivityReceiver;

import android.R.integer;
import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class DogService extends Service {

	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	private int allopen=1;//1表示一直打开应用,0表示关闭后不打开应用
	private int logno=0;//表示计数
	ActivityReceiver receiver;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","dog bind","dog.txt");
		return null;
	}
	//8.创建activity的接收器广播，用来接收内容
	public class ActivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			setAllopen(bundle.getInt("isallopen"));	
		}

	}
			
	public int getAllopen() {
		return allopen;
	}
	public void setAllopen(int allopen) {
		this.allopen = allopen;
		ToolClass.Log(ToolClass.INFO,"EV_DOG","setAllopen="+allopen,"dog.txt");
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ToolClass.Log(ToolClass.INFO,"EV_DOG","dog create","dog.txt");
		//9.注册接收器
		receiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.dogserversend");
		this.registerReceiver(receiver,filter);	
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub		
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","dog destroy","dog.txt");
		//解除注册接收器
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ToolClass.Log(ToolClass.INFO,"EV_DOG","dog start","dog.txt");
		timer.scheduleWithFixedDelay(new Runnable() { 
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
	        			 ToolClass.Log(ToolClass.INFO,"EV_DOG","appName:"+info.topActivity.getClassName()+"-->pack:"+info.topActivity.getPackageName(),"dog.txt");
	        			 if (info.topActivity.getPackageName().equals(MY_PKG_NAME)||info.baseActivity.getPackageName().equals(MY_PKG_NAME)) 
	        			 {	        				  
	        				  isopen=1;
	        				  break;
        				 }
	        		}
	        		if(isopen==1)
	        		{
	        			ToolClass.Log(ToolClass.INFO,"EV_DOG","applicationrun","dog.txt");
	        		}
	        		else
	        		{
	        			ToolClass.Log(ToolClass.INFO,"EV_DOG","applicationstop","dog.txt");
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
	        		ToolClass.Log(ToolClass.INFO,"EV_DOG","unopen","dog.txt");
				}
	        	
	        	//整理日志文件用
	        	ToolClass.Log(ToolClass.INFO,"EV_DOG","logno["+Thread.currentThread().getId()+"]="+logno,"dog.txt");
	        	if(logno<5760)
	        	{
	        		logno++;
	        	}
	        	else 
	        	{
	        		logno=0;
	        		ToolClass.optLogFile(); 
				}	        	
	        } 
	    },15,15,TimeUnit.SECONDS);       // timeTask 
	}
	

}
