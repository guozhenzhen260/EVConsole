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

package com.example.evconsole;


import java.util.HashMap;
import java.util.Map;

import com.easivend.evprotocol.EVprotocolAPI;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddInaccount extends Activity
{
	private EditText edtbillin=null,edtcoinin=null,edtpayout=null,edtpaymoney=null;
	private Button btnpayout=null,btnbillexit=null;// 创建Button对象“退出”
	private Handler myhHandler=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addinaccount);// 设置布局文件
		myhHandler=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case EVprotocolAPI.EV_PAYIN_RPT://接收子线程消息
						Log.i("EV_JNI","投币金额");							
						edtbillin.setText(msg.obj.toString());
						break;
					case EVprotocolAPI.EV_PAYOUT_RPT://接收子线程消息
						Log.i("EV_JNI","找零金额");							
						edtbillin.setText(msg.obj.toString());
						break;	
				}				
			}
			
		};
		edtbillin = (EditText) findViewById(R.id.edtbillin);
		edtcoinin = (EditText) findViewById(R.id.edtcoinin);
		edtpayout = (EditText) findViewById(R.id.edtpayout);
		edtpaymoney = (EditText) findViewById(R.id.edtpaymoney);
		btnpayout = (Button) findViewById(R.id.btnpayout);
		btnpayout.setOnClickListener(new OnClickListener() {// 为退币按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	EVprotocolAPI.payout(Float.parseFloat(edtpayout.getText().toString()));
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
