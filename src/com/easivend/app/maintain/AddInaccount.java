/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           AddInaccount.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        纸币器硬币器测试页面          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.app.maintain;


import java.util.HashMap;
import java.util.Map;

import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddInaccount extends Activity
{
	private EditText edtpayout=null;
	private TextView txtbillin=null,txtcoinin=null,txtpaymoney=null;
	private Button btnpayout=null,btnbillexit=null;// 创建Button对象“退出”
	private Handler myhHandler=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addinaccount);// 设置布局文件
		//注册投币找零监听器
  	    EVprotocolAPI.setCallBack(new JNIInterface() 
		{
			
			@Override
			public void jniCallback(Map<String, Integer> allSet) {
				float amount=0;
				// TODO Auto-generated method stub	
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<payinout结果");
				Map<String, Integer> Set= allSet;
				int jnirst=Set.get("EV_TYPE");
				switch (jnirst)
				{
					case EVprotocolAPI.EV_PAYIN_RPT://接收子线程投币金额消息						
						amount=ToolClass.MoneyRec((Integer) allSet.get("amount"));
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<投币金额"+String.valueOf(amount));							
						txtbillin.setText(String.valueOf(amount));
						break;
					case EVprotocolAPI.EV_PAYOUT_RPT://接收子线程找零金额消息
						amount=ToolClass.MoneyRec((Integer) allSet.get("amount"));
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<找零金额"+String.valueOf(amount));							
						txtpaymoney.setText(String.valueOf(amount));
						break;	
				}				
			}
			
		});
		
		txtbillin = (TextView) findViewById(R.id.txtbillin);
		txtcoinin = (TextView) findViewById(R.id.txtcoinin);
		edtpayout = (EditText) findViewById(R.id.edtpayout);
		txtpaymoney = (TextView) findViewById(R.id.txtpaymoney);
		btnpayout = (Button) findViewById(R.id.btnpayout);
		btnpayout.setOnClickListener(new OnClickListener() {// 为退币按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	ToolClass.Log(ToolClass.INFO,"EV_JNI","[APPsend>>]"+edtpayout.getText().toString());
		    	EVprotocolAPI.payout(ToolClass.MoneySend(Float.parseFloat(edtpayout.getText().toString())));
		    	txtbillin.setText("0");
		    	txtcoinin.setText("0");
		    }
		});
		btnbillexit = (Button) findViewById(R.id.btnbillexit);
		btnbillexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		        finish();
		    }
		});
	}

}
