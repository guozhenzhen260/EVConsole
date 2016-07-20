package com.easivend.view;

import com.easivend.common.ToolClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_DOG","闹钟启动...","dog.txt");
		//启动服务
		Intent it=new Intent(context,AlarmService.class);
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(it);
	}

}
