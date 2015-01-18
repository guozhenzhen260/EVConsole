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


package com.example.evconsole;


import com.example.business.Business;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity 
{
	private EditText txtlogin;// 创建EditText对象
    private Button btnlogin, btnclose,btnbusiness;// 创建两个Button对象
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);// 设置布局文件

        txtlogin = (EditText) findViewById(R.id.txtLogin);// 获取串口号文本框
        btnlogin = (Button) findViewById(R.id.btnLogin);// 获取登录按钮
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
                Intent intent = new Intent(Login.this, MaintainActivity.class);// 创建Intent对象
                intent.putExtra("comport", txtlogin.getText().toString());
                startActivity(intent);// 启动主Activity               
            }
        });
        btnbusiness = (Button) findViewById(R.id.btnBusiness);// 获取交易按钮
        btnbusiness.setOnClickListener(new OnClickListener() {// 为交易按钮设置监听事件
            @Override
            public void onClick(View arg0) 
            {
                Intent intent = new Intent(Login.this, Business.class);// 创建Intent对象
                intent.putExtra("comport", txtlogin.getText().toString());
                startActivity(intent);// 启动主Activity               
            }
        });
	}
	
}
