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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.http.EVServerhttp;
import com.easivend.view.COMService;
import com.easivend.view.DogService;
import com.easivend.view.EVServerService;
import com.easivend.weixing.WeiConfigAPI;
import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.app.business.BusLand;
import com.easivend.app.business.BusPort;
import com.easivend.common.PictureAdapter;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MaintainActivity extends Activity
{
	private GridView gvInfo;// 创建GridView对象
	//跳转页面对话框
	private ProgressDialog barmaintain= null;
	//进度对话框
	ProgressDialog dialog= null;
	// 定义字符串数组，存储系统功能
    private String[] titles = new String[] { "商品管理", "货道管理", "参数配置", "订单信息", "操作日志", "本机配置", "交易页面", "退出" };
    // 定义int数组，存储功能对应的图标
    private int[] images = new int[] { R.drawable.addoutaccount, R.drawable.addinaccount, R.drawable.outaccountinfo, R.drawable.showinfo,
            R.drawable.inaccountinfo, R.drawable.sysset, R.drawable.accountflag, R.drawable.exit };
    String com=null,bentcom=null,columncom=null,server="";
    final static int REQUEST_CODE=1;   
    //获取货柜信息
   Map<String,Integer> huoSet=new HashMap<String,Integer>();
    //Dog服务相关
    int isallopen=1;//是否保持持续一直打开,1一直打开,0关闭后不打开
	private final int SPLASH_DISPLAY_LENGHT = 5000; // 延迟5秒
	//LocalBroadcastManager dogBroadreceiver;
	//Server服务相关
	LocalBroadcastManager localBroadreceiver;
	EVServerReceiver receiver;
	private boolean issale=false;//true是否已经自动打开过售卖页面了，如果打开过，就不再打开了
	Map<String, String> vmcmap;
	//COM服务相关
	LocalBroadcastManager comBroadreceiver;
	COMReceiver comreceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintain);	
		//取得屏幕的长和宽，进行比较设置横竖屏的变量
		Display display = getWindowManager().getDefaultDisplay();  
		int width = display.getWidth();  
		int height = display.getHeight();  
		if (width > height) {  
			ToolClass.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设为横屏
		} else { 
			ToolClass.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设为竖屏
			//ToolClass.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设为横屏
		}
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
						
		//==========
		//Dog服务相关
		//==========
		//启动服务
		startService(new Intent(this,DogService.class));
		//dogBroadreceiver.getInstance(this);
		//延时5s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	//发送指令广播给DogService
        		Intent intent=new Intent();
        		intent.putExtra("isallopen", isallopen);
        		intent.setAction("android.intent.action.dogserversend");//action与接收器相同
        		//dogBroadreceiver.sendBroadcast(intent); 
        		sendBroadcast(intent); 
            }

		}, SPLASH_DISPLAY_LENGHT);
		
		
		
		//=============
		//Server服务相关
		//=============
		//3.开启服务
		startService(new Intent(MaintainActivity.this,EVServerService.class));
		//4.注册接收器
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new EVServerReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserverrec");
		localBroadreceiver.registerReceiver(receiver,filter);
		vmcmap = ToolClass.getvmc_no(MaintainActivity.this);
		
		//=============
		//COM服务相关
		//=============
		//3.开启服务
		startService(new Intent(MaintainActivity.this,COMService.class));
		//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		//延时3s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	dialog= ProgressDialog.show(MaintainActivity.this,"同步服务器","请稍候...");
            	ToolClass.ResstartPort(1);
            	ToolClass.ResstartPort(2);
            	ToolClass.ResstartPort(3);
            	
            	
            	//7.发送指令广播给COMService
        		Intent intent=new Intent();
        		intent.putExtra("EVWhat", COMService.EV_CHECKALLCHILD);	
        		intent.setAction("android.intent.action.comsend");//action与接收器相同
        		comBroadreceiver.sendBroadcast(intent);
            }

		}, 1000);
				
		//================
		//串口配置和注册相关
		//================
		ToolClass.SetDir();	
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<log路径:"+ToolClass.getEV_DIR()+File.separator+"logs","log.txt");			
		//从配置文件获取数据
		Map<String, String> list=ToolClass.ReadConfigFile();
		if(list!=null)
		{
	        com = list.get("com");
	        bentcom = list.get("bentcom");
	        columncom = list.get("columncom");
	        server = list.get("server");//设置服务器路径
	        AlipayConfigAPI.SetAliConfig(list);//设置阿里账号
	        WeiConfigAPI.SetWeiConfig(list);//设置微信账号
	        if(list.containsKey("isallopen"))//设置是否一直打开程序
	        {
	        	isallopen=Integer.parseInt(list.get("isallopen"));		        	
	        }
	        ToolClass.setCom(com);
	        ToolClass.setBentcom(bentcom);
	        ToolClass.setColumncom(columncom);		        

		}
		else
		{
			dialog.dismiss();
		}
		
		//加载微信证书
		ToolClass.setWeiCertFile();
		//加载售罄水印图片		
		Bitmap mark = BitmapFactory.decodeResource(this.getResources(), R.drawable.ysq);  
		ToolClass.setMark(mark);
		//加载goc
		ToolClass.setGoc(MaintainActivity.this);	
		//================
		//九宫格相关
		//================		
		gvInfo = (GridView) findViewById(R.id.gvInfo);// 获取布局文件中的gvInfo组件
        PictureAdapter adapter = new PictureAdapter(titles, images, this);// 创建pictureAdapter对象
        gvInfo.setAdapter(adapter);// 为GridView设置数据源
        gvInfo.setOnItemClickListener(new OnItemClickListener() {// 为GridView设置项单击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = null;// 创建Intent对象
                switch (arg2) {
                case 0:
                	barmaintain= ProgressDialog.show(MaintainActivity.this,"打开商品管理","请稍候...");
                	intent = new Intent(MaintainActivity.this, GoodsManager.class);// 使用GoodsManager窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);// 打开GoodsManager
                    break;
                case 1:
                	barmaintain= ProgressDialog.show(MaintainActivity.this,"打开货道管理","请稍候...");
                    intent = new Intent(MaintainActivity.this, HuodaoTest.class);// 使用HuodaoTest窗口初始化Intent
                    startActivityForResult(intent,REQUEST_CODE);// 打开HuodaoTest
                    break;
                case 2:
                	barmaintain= ProgressDialog.show(MaintainActivity.this,"打开参数管理","请稍候...");
                	intent = new Intent(MaintainActivity.this, ParamManager.class);// 使用ParamManager窗口初始化Intent
                    startActivityForResult(intent,REQUEST_CODE);// 打开ParamManager                    
                    break;    
                case 3:
                	barmaintain= ProgressDialog.show(MaintainActivity.this,"打开订单日志查询","请稍候...");
                	intent = new Intent(MaintainActivity.this, Order.class);// 使用Accountflag窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);
                    break;                
                case 4:
                	barmaintain= ProgressDialog.show(MaintainActivity.this,"打开操作日志查询","请稍候...");
                	intent = new Intent(MaintainActivity.this, LogOpt.class);// 使用Accountflag窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);
                    break;
                case 5:
                	barmaintain= ProgressDialog.show(MaintainActivity.this,"打开本机配置","请稍候...");
                	intent = new Intent(MaintainActivity.this, Login.class);// 使用Accountflag窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
                    break;
                case 6:
                	barmaintain= ProgressDialog.show(MaintainActivity.this,"打开交易页面","请稍候...");
    				//横屏
    				if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    				{
    					intent = new Intent(MaintainActivity.this, BusLand.class);// 使用Accountflag窗口初始化Intent
    				}
    				//竖屏
    				else
    				{
    					intent = new Intent(MaintainActivity.this, BusPort.class);// 使用Accountflag窗口初始化Intent
    				}                	
                    startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
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
				barmaintain.dismiss();
			}	
			else if(resultCode==MaintainActivity.RESULT_OK)
			{	
				barmaintain.dismiss();
				//从配置文件获取数据
				Map<String, String> list=ToolClass.ReadConfigFile();
				if(list!=null)
				{
			        if(list.containsKey("isallopen"))
			        {
			        	isallopen=Integer.parseInt(list.get("isallopen"));	
			        	//发送指令广播给DogService
		        		Intent intent=new Intent();
		        		intent.putExtra("isallopen", isallopen);
		        		intent.setAction("android.intent.action.dogserversend");//action与接收器相同
		        		//dogBroadreceiver.sendBroadcast(intent);
		        		sendBroadcast(intent); 
			        }
				}
			}
		}	
	}
		
	//=============
	//Server服务相关
	//=============	
	//2.创建EVServerReceiver的接收器广播，用来接收服务器同步的内容
	public class EVServerReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			case EVServerhttp.SETMAIN:
				Log.i("EV_JNI","activity=签到成功");					
				dialog.dismiss();				
	    		break;
			case EVServerhttp.SETFAILMAIN:
				Log.i("EV_JNI","activity=失败，网络故障");
				if(dialog.isShowing())
					dialog.dismiss();
	    		break;	
			}
			if(issale==false)
			{
				issale=true;
				//签到完成，自动开启售货程序
				barmaintain= ProgressDialog.show(MaintainActivity.this,"打开交易页面","请稍候...");
				Intent intbus;
				//横屏
				if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
				{
					intbus = new Intent(MaintainActivity.this, BusLand.class);// 使用Accountflag窗口初始化Intent
				}
				//竖屏
				else
				{
					intbus = new Intent(MaintainActivity.this, BusPort.class);// 使用Accountflag窗口初始化Intent
				}
				startActivityForResult(intbus,REQUEST_CODE);// 打开Accountflag
			}
		}

	}
	
	//=============
	//COM服务相关
	//=============	
	//2.创建COMReceiver的接收器广播，用来接收服务器同步的内容
	public class COMReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			case COMService.EV_CHECKALLMAIN:
				//ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 货道查询全部","com.txt");
				SerializableMap serializableMap = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 货道查询全部="+Set,"com.txt");	
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<vmserversend","log.txt");
		    	//*******
				//服务器同步
				//*******
				Intent intent2=new Intent(); 
				intent2.putExtra("EVWhat", EVServerhttp.SETCHILD);
				intent2.putExtra("vmc_no", vmcmap.get("vmc_no"));
				intent2.putExtra("vmc_auth_code", vmcmap.get("vmc_auth_code"));
				//传递数据
		        final SerializableMap myMap=new SerializableMap();
		        myMap.setMap(Set);//将map数据添加到封装的myMap<span></span>中
		        Bundle bundle2=new Bundle();
		        bundle2.putSerializable("huoSet", myMap);
		        intent2.putExtras(bundle2);
				intent2.setAction("android.intent.action.vmserversend");//action与接收器相同
				localBroadreceiver.sendBroadcast(intent2);  
	    		break;				
			}			
		}

	}
	
	@Override
	protected void onDestroy() {		
		//=============
		//Server服务相关
		//=============
		//5.解除注册接收器
		localBroadreceiver.unregisterReceiver(receiver);
		//6.结束服务
		stopService(new Intent(MaintainActivity.this, EVServerService.class));
		//=============
		//COM服务相关
		//=============
		//5.解除注册接收器
		comBroadreceiver.unregisterReceiver(comreceiver);
		//6.结束服务
		stopService(new Intent(MaintainActivity.this, COMService.class));
		// TODO Auto-generated method stub
		super.onDestroy();		
	}
}


