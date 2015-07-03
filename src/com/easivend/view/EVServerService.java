package com.easivend.view;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.ToolClass;
import com.easivend.http.EVServerhttp;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class EVServerService extends Service {
	private final int SPLASH_DISPLAY_LENGHT = 3000; // 延迟3秒
	Timer timer; 
	private Thread thread=null;
    private Handler mainhand=null,childhand=null;   
    EVServerhttp serverhttp=null;
    boolean isev=false;
    ActivityReceiver receiver;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service bind","server.txt");
		return null;
	}
	//8.创建activity的接收器广播，用来接收内容
	public class ActivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			case EVServerhttp.SETCHILD:
				String vmc_no=bundle.getString("vmc_no");
				String vmc_auth_code=bundle.getString("vmc_auth_code");
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","receiver:vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code,"server.txt");
				//处理接收到的内容,发送签到命令到子线程中
				//初始化一:发送签到指令
	        	childhand=serverhttp.obtainHandler();
	    		Message childmsg=childhand.obtainMessage();
	    		childmsg.what=EVServerhttp.SETCHILD;
	    		JSONObject ev=null;
	    		try {
	    			ev=new JSONObject();
	    			ev.put("vmc_no", vmc_no);
	    			ev.put("vmc_auth_code", vmc_auth_code);
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev.toString(),"server.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		childmsg.obj=ev;
	    		childhand.sendMessage(childmsg);
	    		break;
			}			
		}

	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service create","server.txt");
		super.onCreate();
		timer = new Timer();
		//9.注册接收器
		receiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserversend");
		this.registerReceiver(receiver,filter);						
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub		
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service destroy","server.txt");
		isev=false;//即使service销毁线程也不会停止，所以这里通过设置isev来停止线程
		//解除注册接收器
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service start","server.txt");
		//***********************
		//线程进行vmserver操作
		//***********************
		mainhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				Intent intent;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case EVServerhttp.SETMAIN://子线程接收主线程消息签到完成
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 签到成功","server.txt");
						isev=true;
						//返回给activity广播
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
						sendBroadcast(intent);
						//初始化二:获取商品分类信息
						childhand=serverhttp.obtainHandler();
		        		Message childmsg=childhand.obtainMessage();
		        		childmsg.what=EVServerhttp.SETCLASSCHILD;
		        		childhand.sendMessage(childmsg);
						break;
					case EVServerhttp.SETFAILMAIN://子线程接收主线程消息签到失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 失败，网络故障","server.txt");
						//返回给activity广播
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
						sendBroadcast(intent);	
						break;
					case EVServerhttp.SETERRFAILMAIN://子线程接收主线程消息签到失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 签到失败，原因="+msg.obj.toString(),"server.txt");
						//返回给activity广播
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
						sendBroadcast(intent);	
						break;	
				}				
			}
			
		};
		//启动用户自己定义的类，启动线程
  		serverhttp=new EVServerhttp(mainhand);
  		thread=new Thread(serverhttp,"serverhttp Thread");
  		thread.start();
		
	    //每隔一段时间，心跳同步一次
	    timer.schedule(new TimerTask() { 
	        @Override 
	        public void run() { 
	        	if(isev)
	        	{
		        	//发送心跳命令到子线程中
	            	childhand=serverhttp.obtainHandler();
	        		Message childmsg=childhand.obtainMessage();
	        		childmsg.what=EVServerhttp.SETHEARTCHILD;
	        		childhand.sendMessage(childmsg);	
	        	}
	        } 
	    }, 15*1000, 3*60*1000);       // timeTask 

	}	

}
