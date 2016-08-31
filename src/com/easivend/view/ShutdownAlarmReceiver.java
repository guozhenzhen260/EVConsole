package com.easivend.view;

import com.easivend.common.ToolClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutdownAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","πÿª˙ƒ÷÷”∆Ù∂Ø...","server.txt");
	}

}
