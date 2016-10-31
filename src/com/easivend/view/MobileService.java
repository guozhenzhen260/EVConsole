package com.easivend.view;

import com.easivend.common.ToolClass;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.IBinder;

public class MobileService extends Service 
{
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) 
	{
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //有线
        NetworkInfo networkInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if(networkInfo.isConnected())
        {
        	ToolClass.setNetType(1);
        }
        //wifi
        else
        {
        	State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        	if(wifi.toString().equals("CONNECTED"))
        	{
        		ToolClass.setNetType(2);
        	}
        	//mobile
        	else
        	{
        		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        		if(mobile.toString().equals("CONNECTED"))
            	{
            		ToolClass.setNetType(3);
            	}
        		else
        		{
            		ToolClass.setNetType(4);
            	}	
        	}
        }
        ToolClass.Log(ToolClass.INFO,"EV_JNI","启动网络监测服务="+ToolClass.getNetType(),"jni.txt");
		
	}
	
	

}
