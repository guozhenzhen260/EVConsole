package com.easivend.view;

import java.io.IOException;

import com.easivend.common.ToolClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutdownAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","关机闹钟启动...","server.txt");
		try {
				//以下两种方法都可以实现
				//String cmd = "su -c reboot";
		        //Runtime.getRuntime().exec(cmd);
				Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"}); 
		  } catch (IOException e) {
		         // TODO Auto-generated catch block
		        // new AlertDialog.Builder(this).setTitle("Error").setMessage(e.getMessage()).setPositiveButton("OK", null).show();
		  }
	}

}
