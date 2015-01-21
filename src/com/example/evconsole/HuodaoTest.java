/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           HuodaoTest.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        货道测试页面          
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
import com.easivend.evprotocol.ToolClass;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class HuodaoTest extends Activity 
{
	private int device=0;//出货柜号		
	private int status=0;//出货结果
	private int hdid=0;//货道id
	private int hdtype=0;//出货类型
	private float cost=0;//扣钱
	private float totalvalue=0;//剩余金额
	private int huodao=0;//剩余存货数量
	private TextView txthuorst=null;
	private Button btnhuochu=null;// 创建Button对象“出货”
	private Button btnhuocancel=null;// 创建Button对象“重置”
	private Button btnhuoexit=null;// 创建Button对象“退出”
	private EditText edtcolumn=null,edtprice=null;
	private RadioGroup cabinet=null, type=null;
	private RadioButton mainhuodao=null,fuhuodao=null,moneychu=null,pcchu=null;
	private int cabinetvar=0,typevar=0;
	private Handler myhHandler=null;
	//EVprotocolAPI ev=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.huodao);// 设置布局文件
		myhHandler=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case EVprotocolAPI.EV_TRADE_RPT://接收子线程消息
						Map<String,Integer> allSet = new HashMap<String,Integer>();
						allSet=(Map<String, Integer>) msg.obj;
						device=allSet.get("device");//出货柜号
						status=allSet.get("status");//出货结果
						hdid=allSet.get("hdid");//货道id
						hdtype=allSet.get("type");//出货类型
						cost=ToolClass.MoneyRec(allSet.get("cost"));//扣钱
						totalvalue=ToolClass.MoneyRec(allSet.get("totalvalue"));//剩余金额
						huodao=allSet.get("huodao");//剩余存货数量
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<出货结果"+"device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
								+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");	
						
						txthuorst.setText("device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
								+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");
						break;
				}				
			}
			
		};
		EVprotocolAPI.setHandler(myhHandler);
		txthuorst=(TextView)findViewById(R.id.txthuorst);
		btnhuochu = (Button) findViewById(R.id.btnhuochu);
		btnhuocancel = (Button) findViewById(R.id.btnhuocancel);
		btnhuoexit = (Button) findViewById(R.id.btnhuoexit);
		edtcolumn = (EditText) findViewById(R.id.edtcolumn);
		edtprice = (EditText) findViewById(R.id.edtprice);
		//得到那个柜出货
		this.cabinet = (RadioGroup) super.findViewById(R.id.cabinet);
	    this.mainhuodao = (RadioButton) super.findViewById(R.id.mainhuodao);
	    this.fuhuodao = (RadioButton) super.findViewById(R.id.fuhuodao);		
	    this.cabinet.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 主柜出货
				if(mainhuodao.getId()==checkedId)
				{
					cabinetvar=1;
				}
				//副柜出货
				else if(fuhuodao.getId()==checkedId)
				{
					cabinetvar=2;
				}
			}
		});
	    //得到出货方式
  		this.type = (RadioGroup) super.findViewById(R.id.type);
  	    this.moneychu = (RadioButton) super.findViewById(R.id.moneychu);
  	    this.pcchu = (RadioButton) super.findViewById(R.id.pcchu);		
  	    this.type.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
  			@Override
  			public void onCheckedChanged(RadioGroup group, int checkedId) {
  				// 现金出货
  				if(moneychu.getId()==checkedId)
  				{
  					typevar=0;
  				}
  				//PC出货
  				else if(pcchu.getId()==checkedId)
  				{
  					typevar=2;
  				}
  			}
  		});
		
		btnhuochu.setOnClickListener(new OnClickListener() {// 为出货按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {		    	  
		    	ToolClass.Log(ToolClass.INFO,"EV_JNI",
		    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
		    	+" column="+String.valueOf(Integer.parseInt(edtcolumn.getText().toString()))
		    	+" type="+String.valueOf(typevar)
		    	+" price="+String.valueOf(Float.parseFloat(edtprice.getText().toString())*100)
		    	);
		    	EVprotocolAPI.trade(cabinetvar,Integer.parseInt(edtcolumn.getText().toString()),typevar,
		    			ToolClass.MoneySend(Float.parseFloat(edtprice.getText().toString())));	    	
		    	
		    }
		});
		btnhuocancel.setOnClickListener(new OnClickListener() {// 为重置按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	edtcolumn.setText("");// 设置金额文本框为空
		    	edtprice.setText("");// 设置时间文本框为空		        
		    }
		});
		btnhuoexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		        finish();
		    }
		});
	}
	
}
