/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           MaintainActivity.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        维护菜单主页面          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.app.maintain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.easivend.evprotocol.EVprotocol;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.view.DogService;
import com.easivend.view.DogService.LocalBinder;
import com.easivend.weixing.WeiConfigAPI;
import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.app.business.Business;
import com.easivend.common.PictureAdapter;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;


import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.R.string;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MaintainActivity extends Activity
{
	TextView txtcom=null,txtbentcom=null;
	private GridView gvInfo;// 创建GridView对象
	private ProgressBar barmaintain=null;
	// 定义字符串数组，存储系统功能
    private String[] titles = new String[] { "商品管理", "货道管理", "参数配置", "订单信息", "操作日志", "本机配置", "交易页面", "退出" };
    // 定义int数组，存储功能对应的图标
    private int[] images = new int[] { R.drawable.addoutaccount, R.drawable.addinaccount, R.drawable.outaccountinfo, R.drawable.showinfo,
            R.drawable.inaccountinfo, R.drawable.sysset, R.drawable.accountflag, R.drawable.exit };
    //EVprotocolAPI ev=null;
    int comopen=0,bentopen=0;//1串口已经打开，0串口没有打开    
    String com=null,bentcom=null;
    final static int REQUEST_CODE=1;
    Timer timer = new Timer();
    //Dog服务相关
    DogService localService;
	boolean bound=false;
	int isallopen=1;//是否保持持续一直打开,1一直打开,0关闭后不打开
	private final int SPLASH_DISPLAY_LENGHT = 3000; // 延迟3秒
	//绑定的接口
	private ServiceConnection conn=new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//Log.i("currenttime","service onBindSUC="+service.getInterfaceDescriptor());
			LocalBinder binder=(LocalBinder) service;
			localService = binder.getService();
			bound=true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			//Log.i("currenttime","service onBindFail");
			bound=false;
		}

		
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintain);	
		//注册串口监听器
		EVprotocolAPI.setCallBack(new JNIInterface() {
			
			@Override
			public void jniCallback(Map<String, Object> allSet) {
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<main监听到","log.txt");	
				Map<String, Object> Set= allSet;
				int jnirst=(Integer) Set.get("EV_TYPE");
				//txtcom.setText(String.valueOf(jnirst));
				switch (jnirst)
				{
					case EVprotocolAPI.EV_REGISTER://接收子线程消息
						//主柜初始化完成
						if(Set.get("port_com").equals(com))
						{
							ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<主柜初始化完成","log.txt");	
							txtcom.setText(com+"[主柜]初始化完成");		
							ToolClass.setCom_id((Integer)Set.get("port_id"));
						}
						else if(Set.get("port_com").equals(bentcom))
						{
							ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<格子柜初始化完成","log.txt");	
							txtbentcom.setText(bentcom+"[格子柜]初始化完成");		
							ToolClass.setBentcom_id((Integer)Set.get("port_id"));
						}
										
						break;
				}
			}
		}); 
		
		timer.schedule(new TimerTask() { 
	        @Override 
	        public void run() { 
	  
	            runOnUiThread(new Runnable() {      // UI thread 
	                @Override 
	                public void run() { 
	                	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<Maintain run","log.txt"); 
	                	ToolClass.optLogFile(); 
	                } 
	            }); 
	        } 
	    }, 24*60*60*1000, 24*60*60*1000);       // timeTask 
		//==========
		//Dog服务相关
		//==========
		//延时3s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
        		localService.setAllopen(isallopen);
            }

		}, SPLASH_DISPLAY_LENGHT);
		//启动服务
		startService(new Intent(this,DogService.class));
		//绑定服务
		Intent intent=new Intent(this,DogService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		bound=true;
		
		//================
		//串口配置和注册相关
		//================
		txtcom=(TextView)super.findViewById(R.id.txtcom);
		txtbentcom=(TextView)super.findViewById(R.id.txtbentcom);
		
		//从配置文件获取数据
		Map<String, String> list=ToolClass.ReadConfigFile();
		if(list!=null)
		{
	        com = list.get("com");
	        bentcom = list.get("bentcom");
	        AlipayConfigAPI.SetAliConfig(list);//设置阿里账号
	        WeiConfigAPI.SetWeiConfig(list);//设置微信账号
	        if(list.containsKey("isallopen"))
	        {
	        	isallopen=Integer.parseInt(list.get("isallopen"));	        	
	        }
			txtcom.setText(com+"主柜正在准备连接");	
			EVprotocolAPI.vmcEVStart();//开启监听
			//打开主柜串口		
			comopen = EVprotocolAPI.EV_portRegister(com);
			if(comopen == 1)
			{
				txtcom.setText(com+"[主柜]串口正在准备连接");			
			}
			else
			{
				txtcom.setText(com+"[主柜]串口打开失败");
			}			
			txtbentcom.setText(bentcom+"[格子柜]正在准备连接");	
			//打开格子柜
			bentopen = EVprotocolAPI.EV_portRegister(bentcom);
			if(bentopen == 1)
			{
				txtbentcom.setText(bentcom+"[格子柜]串口正在准备连接");			
			}
			else
			{
				txtbentcom.setText(bentcom+"[格子柜]串口打开失败");
			}
		}
				
		
		//================
		//九宫格相关
		//================		
		barmaintain= (ProgressBar) findViewById(R.id.barmaintain);
		gvInfo = (GridView) findViewById(R.id.gvInfo);// 获取布局文件中的gvInfo组件
        PictureAdapter adapter = new PictureAdapter(titles, images, this);// 创建pictureAdapter对象
        gvInfo.setAdapter(adapter);// 为GridView设置数据源
        gvInfo.setOnItemClickListener(new OnItemClickListener() {// 为GridView设置项单击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = null;// 创建Intent对象
                switch (arg2) {
                case 0:
                	barmaintain.setVisibility(View.VISIBLE);                	
                	intent = new Intent(MaintainActivity.this, GoodsManager.class);// 使用GoodsManager窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);// 打开GoodsManager
                    break;
                case 1:
                	barmaintain.setVisibility(View.VISIBLE); 
                    intent = new Intent(MaintainActivity.this, HuodaoTest.class);// 使用HuodaoTest窗口初始化Intent
                    startActivityForResult(intent,REQUEST_CODE);// 打开HuodaoTest
                    break;
                case 2:
                	barmaintain.setVisibility(View.VISIBLE);
                	intent = new Intent(MaintainActivity.this, ParamManager.class);// 使用ParamManager窗口初始化Intent
                    startActivityForResult(intent,REQUEST_CODE);// 打开ParamManager                    
                    break;    
                case 3:
                	intent = new Intent(MaintainActivity.this, Order.class);// 使用Accountflag窗口初始化Intent
                    startActivity(intent);// 打开Accountflag
                    break;                
                case 4:
                	intent = new Intent(MaintainActivity.this, LogOpt.class);// 使用Accountflag窗口初始化Intent
                    startActivity(intent);// 打开Accountflag
                    break;
                case 5:
                	intent = new Intent(MaintainActivity.this, Login.class);// 使用Accountflag窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
                    break;
                case 6:
                    intent = new Intent(MaintainActivity.this, Business.class);// 使用Accountflag窗口初始化Intent
                    startActivity(intent);// 打开Accountflag
                    break;
                case 7:
                    finish();// 关闭当前Activity
                }
            }
        });

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		if(requestCode==REQUEST_CODE)
		{
			if(resultCode==MaintainActivity.RESULT_CANCELED)
			{				
				barmaintain.setVisibility(View.GONE);
			}	
			else if(resultCode==MaintainActivity.RESULT_OK)
			{				
				//从配置文件获取数据
				Map<String, String> list=ToolClass.ReadConfigFile();
				if(list!=null)
				{
			        if(list.containsKey("isallopen"))
			        {
			        	isallopen=Integer.parseInt(list.get("isallopen"));	
			        	localService.setAllopen(isallopen);
			        }
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		EVprotocolAPI.vmcEVStop();//关闭监听
		//关闭串口
		if(comopen>0)	
			EVprotocolAPI.EV_portRelease(ToolClass.getCom_id());
		if(bentopen>0)
			EVprotocolAPI.EV_portRelease(ToolClass.getBentcom_id());
		EVprotocolAPI.vmcEVStop();//关闭监听
		// TODO Auto-generated method stub
		super.onDestroy();		
	}
}


