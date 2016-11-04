package com.easivend.view;

import com.easivend.common.ToolClass;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

public class MobileService extends Service implements WifiChangeBroadcastReceiver.BRInteraction
{
	TelephonyManager        Tel;
    MyPhoneStateListener    MyListener;
    
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
		
        
        //4g相关
        MyListener   = new MyPhoneStateListener();        
        Tel       = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE); 
        Tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //wifi相关
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        WifiChangeBroadcastReceiver dianLiangBR = new WifiChangeBroadcastReceiver();
        registerReceiver(dianLiangBR, intentFilter);
        dianLiangBR.setBRInteractionListener(this);
	}
	
	@Override
    public void setText(String content) {
        if (content != null) 
        {
        	ToolClass.Log(ToolClass.INFO,"EV_JNI","wifi信号:"+content,"jni.txt");
        	if(ToolClass.getNetType()==2)
        	{
        		ToolClass.setNetStr("wifi信号:"+content);
        	}
        }
    }
	
	private class MyPhoneStateListener extends PhoneStateListener
	{ 
      /* Get the Signal strength from the provider, each tiome there is an update  从得到的信号强度,每个tiome供应商有更新*/
 
      @Override 
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
          super.onSignalStrengthsChanged(signalStrength);
          ToolClass.Log(ToolClass.INFO,"EV_JNI","GSM 信号 = "
                  + String.valueOf(signalStrength.getGsmSignalStrength()),"jni.txt");
          if(ToolClass.getNetType()==3)
      	  {
	          ToolClass.setNetStr("GSM 信号 = "
	             + String.valueOf(signalStrength.getGsmSignalStrength()));
      	  }
      } 
    };/* End of private Class */

}
