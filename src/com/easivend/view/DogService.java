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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.easivend.app.maintain.MaintainActivity;
import com.easivend.common.ToolClass;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

public class DogService extends Service {

	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	private int allopen=1;//1表示一直打开应用,0表示关闭后不打开应用	
	ActivityReceiver receiver;
	//LocalBroadcastManager localBroadreceiver;
	AlarmManager alarm=null;//闹钟服务
	
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
		//localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.dogserversend");
		//localBroadreceiver.registerReceiver(receiver,filter);	
		registerReceiver(receiver,filter);	
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub		
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","dog destroy","dog.txt");
		//解除注册接收器
		//localBroadreceiver.unregisterReceiver(receiver);
		unregisterReceiver(receiver);
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
	        } 
	    },15,15,TimeUnit.SECONDS);       // timeTask 
		setalarm();//设置闹钟
	}
	//设置闹钟
	private void setalarm()
	{
		alarm=(AlarmManager)super.getSystemService(Context.ALARM_SERVICE);//取得闹钟服务
		
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd" + " "  
                + "HH:mm:ss"); //精确到毫秒 
    	//整理文件时间当天1点
    	Calendar todayStart = Calendar.getInstance();  
    	todayStart.set(Calendar.HOUR_OF_DAY, 1);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0); 
    	Date date = todayStart.getTime(); 
        String starttime=tempDate.format(date);
        ParsePosition posstart = new ParsePosition(0);  
//    	Date dstart = (Date) tempDate.parse(starttime, posstart);
    	ToolClass.Log(ToolClass.INFO,"EV_DOG","整理时间="+starttime+",="+todayStart.getTimeInMillis(),"dog.txt");
        //删除原闹钟
    	delalarm();
        //设置新闹钟
    	Intent intent=new Intent(DogService.this,AlarmReceiver.class);
    	intent.setAction("setalarm");
    	//一旦到时间之后，就跳转到由PendingIntent包装的Intent中，而这个Intent作用是
    	//跳转到一个广播之中
    	PendingIntent sender=PendingIntent.getBroadcast(DogService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	//设置闹钟服务
    	this.alarm.setRepeating(AlarmManager.RTC_WAKEUP, todayStart.getTimeInMillis(),1000*60*60*24, sender);
    	
	}
	//删除原闹钟
	private void delalarm()
    {
    	if(this.alarm!=null)
    	{
    		Intent intent=new Intent(DogService.this,AlarmReceiver.class);
	    	intent.setAction("setalarm");
	    	//一旦到时间之后，就跳转到由PendingIntent包装的Intent中，而这个Intent作用是
	    	//跳转到一个广播之中
	    	PendingIntent sender=PendingIntent.getBroadcast(DogService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    	//删除闹钟服务
	    	this.alarm.cancel(sender);	    	
    	}
    }

}
