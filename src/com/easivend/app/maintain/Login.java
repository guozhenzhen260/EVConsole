/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           Login.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        登陆选择串口号页面          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/


package com.easivend.app.maintain;


import java.util.Map;

import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.app.business.Business;
import com.easivend.common.ToolClass;
import com.easivend.weixing.WeiConfigAPI;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class Login extends Activity 
{
	private EditText txtlogin,txtbent,txtserver;// 创建EditText对象
    private Button btnlogin, btnclose;// 创建两个Button对象
    private Switch switchallopen;
    String com =null;
    String bentcom =null;
    String server =null;
    int isallopen=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);// 设置布局文件
		switchallopen = (Switch)findViewById(R.id.switchallopen); //获取到控件  
        txtlogin = (EditText) findViewById(R.id.txtLogin);// 获取串口号文本框
        txtbent = (EditText) findViewById(R.id.txtbent);// 获取串口号文本框
        txtserver = (EditText) findViewById(R.id.txtserver);// 获取服务端地址文本框
        Map<String, String> list=ToolClass.ReadConfigFile();
        if(list!=null)
        {
	        com = list.get("com");
	        bentcom = list.get("bentcom");
	        server = list.get("server");
	        if(list.containsKey("isallopen"))
	        {
	        	isallopen=Integer.parseInt(list.get("isallopen"));
	        }
        }
        txtlogin.setText(com);
        txtbent.setText(bentcom);
        txtserver.setText(server);
        switchallopen.setChecked((isallopen==1)?true:false);
        btnlogin = (Button) findViewById(R.id.btnLogin);// 获取修改按钮
        btnclose = (Button) findViewById(R.id.btnClose);// 获取取消按钮
        btnclose.setOnClickListener(new OnClickListener() {// 为取消按钮设置监听事件
            @Override
            public void onClick(View arg0) {
                finish();// 退出当前程序
            }
        });
        btnlogin.setOnClickListener(new OnClickListener() {// 为登录按钮设置监听事件
            @Override
            public void onClick(View arg0)
            {
            	com = txtlogin.getText().toString();
    	        bentcom = txtbent.getText().toString(); 
    	        isallopen= (switchallopen.isChecked()==true)?1:0;
    	        server = txtserver.getText().toString(); 
            	ToolClass.WriteConfigFile(com, bentcom,server,String.valueOf(isallopen));            	
            	ToolClass.addOptLog(Login.this,1,"修改串口:");
	            // 弹出信息提示
	            Toast.makeText(Login.this, "〖修改串口〗成功！", Toast.LENGTH_SHORT).show();
	            //退出时，返回intent
	            Intent intent=new Intent();
	            setResult(MaintainActivity.RESULT_OK,intent);
            	finish();// 退出当前程序           
            }
        });        
	}
	
}
