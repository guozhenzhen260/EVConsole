/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           BootBroadcastReceiver.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        开机自启动广播服务      
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.view;

import com.easivend.app.maintain.MaintainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	static final String action_boot="android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(action_boot)){
            Intent ootStartIntent=new Intent(context,MaintainActivity.class);
            ootStartIntent.setAction(Intent.ACTION_MAIN);  
            ootStartIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); 
            context.startActivity(ootStartIntent);
        }
	}

}
