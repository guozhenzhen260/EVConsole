package com.easivend.view;

import com.easivend.common.ToolClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;  
import android.net.wifi.WifiManager; 

public class WifiChangeBroadcastReceiver extends BroadcastReceiver {
	private Context mContext;
	private BRInteraction brInteraction;
	 @Override  
     public void onReceive(Context context, Intent intent) {  
         mContext=context;  
         ToolClass.Log(ToolClass.INFO,"EV_JNI","Wifi发生变化","jni.txt");
         getWifiInfo();  
     }  
       
     private void getWifiInfo() {  
         WifiManager wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);  
         WifiInfo wifiInfo = wifiManager.getConnectionInfo();  
         if (wifiInfo.getBSSID() != null) {  
             //wifi名称  
             String ssid = wifiInfo.getSSID();  
             //wifi信号强度  
             int signalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);  
             //wifi速度  
             int speed = wifiInfo.getLinkSpeed();  
             //wifi速度单位  
             String units = WifiInfo.LINK_SPEED_UNITS;  
             ToolClass.Log(ToolClass.INFO,"EV_JNI","ssid="+ssid+",signalLevel="+signalLevel+",speed="+speed,"jni.txt");
             brInteraction.setText("ssid="+ssid+",signalLevel="+signalLevel+",speed="+speed);  
         }  
    } 
     public interface BRInteraction {
         public void setText(String content);
     }

     public void setBRInteractionListener(BRInteraction brInteraction) {
         this.brInteraction = brInteraction;
     } 

}
