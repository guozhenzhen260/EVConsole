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
import android.widget.Toast;

public class Login extends Activity 
{
	private EditText txtlogin;// 创建EditText对象
    private Button btnlogin, btnclose;// 创建两个Button对象
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);// 设置布局文件

        txtlogin = (EditText) findViewById(R.id.txtLogin);// 获取串口号文本框
        Map<String, String> list=ToolClass.ReadConfigFile();
        final String com = list.get("com");
        final String bentcom = list.get("bentcom");
        txtlogin.setText(com);
        AlipayConfigAPI.SetAliConfig(list);//设置阿里账号
        WeiConfigAPI.SetWeiConfig(list);//设置微信账号
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
                intent.putExtra("com", com);
                intent.putExtra("bentcom", bentcom);
                startActivity(intent);// 启动主Activity               
            }
        });        
	}
	
}
