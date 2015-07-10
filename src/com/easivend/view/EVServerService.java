package com.easivend.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.app.maintain.GoodsProSet;
import com.easivend.app.maintain.HuodaoSet;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.model.Tb_vmc_class;
import com.easivend.model.Tb_vmc_column;
import com.easivend.model.Tb_vmc_product;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class EVServerService extends Service {
	private final int SPLASH_DISPLAY_LENGHT = 3000; // 延迟3秒
	Timer timer; 
	private Thread thread=null;
    private Handler mainhand=null,childhand=null;   
    EVServerhttp serverhttp=null;
    boolean isev=false;
    ActivityReceiver receiver;
    Map<String,Integer> huoSet=null;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service bind","server.txt");
		return null;
	}
	//8.创建activity的接收器广播，用来接收内容
	public class ActivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			//签到
			case EVServerhttp.SETCHILD:
				String vmc_no=bundle.getString("vmc_no");
				String vmc_auth_code=bundle.getString("vmc_auth_code");
				SerializableMap serializableMap = (SerializableMap) bundle.get("huoSet");
				huoSet=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","receiver:vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code
						+"huoSet="+huoSet.toString(),"server.txt");
				
				//处理接收到的内容,发送签到命令到子线程中
				//初始化一:发送签到指令
	        	childhand=serverhttp.obtainHandler();
	    		Message childmsg=childhand.obtainMessage();
	    		childmsg.what=EVServerhttp.SETCHILD;
	    		JSONObject ev=null;
	    		try {
	    			ev=new JSONObject();
	    			ev.put("vmc_no", vmc_no);
	    			ev.put("vmc_auth_code", vmc_auth_code);
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev.toString(),"server.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		childmsg.obj=ev;
	    		childhand.sendMessage(childmsg);
	    		break;
	    	//单货道状态上报	
			case EVServerhttp.SETHUODAOSTATUONECHILD:
				// 创建InaccountDAO对象
    			vmc_columnDAO columnDAO = new vmc_columnDAO(EVServerService.this);
    			Tb_vmc_column tb_vmc_column = columnDAO.find(bundle.getString("CABINET_NO"),bundle.getString("PATH_NO"));// 添加商品信息 
    			String CABINET_NO=bundle.getString("CABINET_NO");
    			String PATH_NO=bundle.getString("PATH_NO");
    			String PATH_STATUS=String.valueOf(tb_vmc_column.getColumnStatus());
    			String PATH_COUNT=String.valueOf(tb_vmc_column.getPathCount());
    			String PATH_REMAINING=String.valueOf(tb_vmc_column.getPathRemain());
    			String PRODUCT_NO=tb_vmc_column.getProductID();
    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 上报货道CABINET_NO="+CABINET_NO
						+" PATH_NO="+PATH_NO+" PATH_STATUS="+PATH_STATUS+" PATH_COUNT="+PATH_COUNT
						+" PATH_REMAINING="+PATH_REMAINING+" PRODUCT_NO="+PRODUCT_NO,"server.txt");				
    			//
	        	childhand=serverhttp.obtainHandler();
	    		Message childmsg2=childhand.obtainMessage();
	    		childmsg2.what=EVServerhttp.SETHUODAOSTATUONECHILD;
	    		JSONObject ev2=null;
	    		try {
	    			ev2=new JSONObject();
	    			ev2.put("CABINET_NO", CABINET_NO);
	    			ev2.put("PATH_NO", PATH_NO);
	    			ev2.put("PATH_STATUS", PATH_STATUS);
	    			ev2.put("PATH_COUNT", PATH_COUNT);
	    			ev2.put("PATH_REMAINING", PATH_REMAINING);
	    			ev2.put("PRODUCT_NO", PRODUCT_NO);	    			
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev2.toString(),"server.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		childmsg2.obj=ev2;
	    		childhand.sendMessage(childmsg2);
    			break;
    		//设备状态上报	
			case EVServerhttp.SETDEVSTATUCHILD:
				int bill_err=bundle.getInt("bill_err");
				int coin_err=bundle.getInt("coin_err");
    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 上报设备bill_err="+bill_err
						+" coin_err="+coin_err,"server.txt");				
    			//
	        	childhand=serverhttp.obtainHandler();
	    		Message childmsg3=childhand.obtainMessage();
	    		childmsg3.what=EVServerhttp.SETDEVSTATUCHILD;
	    		JSONObject ev3=null;
	    		try {
	    			ev3=new JSONObject();
	    			ev3.put("bill_err", bill_err);
	    			ev3.put("coin_err", coin_err);	    			  			
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev3.toString(),"server.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		childmsg3.obj=ev3;
	    		childhand.sendMessage(childmsg3);
    			break;	
			}			
		}

	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service create","server.txt");
		super.onCreate();
		timer = new Timer();
		//9.注册接收器
		receiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserversend");
		this.registerReceiver(receiver,filter);						
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub		
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service destroy","server.txt");
		isev=false;//即使service销毁线程也不会停止，所以这里通过设置isev来停止线程
		//解除注册接收器
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service start","server.txt");		
		//***********************
		//线程进行vmserver操作
		//***********************
		mainhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				Intent intent;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					//签到
					case EVServerhttp.SETMAIN://子线程接收主线程消息签到完成
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 签到成功","server.txt");
						//返回给activity广播
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
						sendBroadcast(intent);
						//初始化二:获取商品分类信息
						childhand=serverhttp.obtainHandler();
		        		Message childmsg=childhand.obtainMessage();
		        		childmsg.what=EVServerhttp.SETCLASSCHILD;
		        		childhand.sendMessage(childmsg);
						break;
					case EVServerhttp.SETERRFAILMAIN://子线程接收主线程消息签到失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 签到失败，原因="+msg.obj.toString(),"server.txt");
						//返回给activity广播
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
						sendBroadcast(intent);	
						break;
					//获取商品分类信息	
					case EVServerhttp.SETERRFAILCLASSMAIN://子线程接收主线程消息获取商品分类信息失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取商品分类信息失败，原因="+msg.obj.toString(),"server.txt");
						break;
					case EVServerhttp.SETCLASSMAIN://子线程接收主线程消息获取商品分类信息
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取商品分类信息成功","server.txt");
						try 
						{
							updatevmcClass(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//初始化三:获取商品信息
						childhand=serverhttp.obtainHandler();
		        		Message childmsg2=childhand.obtainMessage();
		        		childmsg2.what=EVServerhttp.SETPRODUCTCHILD;
		        		childhand.sendMessage(childmsg2);
						break;
					//获取商品信息	
					case EVServerhttp.SETERRFAILRODUCTMAIN://子线程接收主线程消息获取商品信息失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取商品信息失败，原因="+msg.obj.toString(),"server.txt");
						break;
					case EVServerhttp.SETRODUCTMAIN://子线程接收主线程消息获取商品信息
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取商品信息成功","server.txt");
						try 
						{
							updatevmcProduct(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//初始化四:获取货道配置信息
						childhand=serverhttp.obtainHandler();
		        		Message childmsg3=childhand.obtainMessage();
		        		childmsg3.what=EVServerhttp.SETHUODAOCHILD;
		        		childhand.sendMessage(childmsg3);
						break;	
					//获取货道信息	
					case EVServerhttp.SETERRFAILHUODAOMAIN://子线程接收主线程消息获取货道信息失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取货道信息失败，原因="+msg.obj.toString(),"server.txt");
						break;
					case EVServerhttp.SETHUODAOMAIN://子线程接收主线程消息获取货道信息
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取货道信息成功","server.txt");
						try 
						{							
							updatevmcColumn(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//初始化五:允许心跳
						isev=true;
						break;	
					//网络故障
					case EVServerhttp.SETFAILMAIN://子线程接收主线程网络失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 失败，网络故障","server.txt");
						//返回给activity广播
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
						sendBroadcast(intent);	
						break;						
				}				
			}
			
		};
		//启动用户自己定义的类，启动线程
  		serverhttp=new EVServerhttp(mainhand);
  		thread=new Thread(serverhttp,"serverhttp Thread");
  		thread.start();
		
	    //每隔一段时间，心跳同步一次
	    timer.schedule(new TimerTask() { 
	        @Override 
	        public void run() { 
	        	if(isev)
	        	{
		        	//发送心跳命令到子线程中
	            	childhand=serverhttp.obtainHandler();
	        		Message childmsg=childhand.obtainMessage();
	        		childmsg.what=EVServerhttp.SETHEARTCHILD;
	        		childhand.sendMessage(childmsg);	
	        	}
	        } 
	    }, 15*1000, 3*60*1000);       // timeTask 

	}	
	
	//更新商品分类信息
	private void updatevmcClass(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		JSONArray arr1=jsonObject.getJSONArray("ProductClassList");
		for(int i=0;i<arr1.length();i++)
		{
			JSONObject object2=arr1.getJSONObject(i);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新商品分类CLASS_CODE="+object2.getString("CLASS_CODE")
					+"CLASS_NAME="+object2.getString("CLASS_NAME"),"server.txt");										
			// 创建InaccountDAO对象
        	vmc_classDAO classDAO = new vmc_classDAO(EVServerService.this);
            // 创建Tb_inaccount对象
        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(object2.getString("CLASS_CODE"), object2.getString("CLASS_NAME"),object2.getString("LAST_EDIT_TIME"),"");
        	classDAO.addorupdate(tb_vmc_class);// 修改
		}
	}
	
	//更新商品信息
	private void updatevmcProduct(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		JSONArray arr1=jsonObject.getJSONArray("ProductList");
		for(int i=0;i<arr1.length();i++)
		{
			JSONObject object2=arr1.getJSONObject(i);
			String product_Class_NO=(object2.getString("product_Class_NO").isEmpty())?"0":object2.getString("product_Class_NO");
			product_Class_NO=product_Class_NO.substring(product_Class_NO.lastIndexOf(',')+1,product_Class_NO.length());
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新商品product_NO="+object2.getString("product_NO")
					+"product_Name="+object2.getString("product_Name")+"product_Class_NO="+product_Class_NO,"server.txt");										
			// 创建InaccountDAO对象
			vmc_productDAO productDAO = new vmc_productDAO(EVServerService.this);
            //创建Tb_inaccount对象
			Tb_vmc_product tb_vmc_product = new Tb_vmc_product(object2.getString("product_NO"), object2.getString("product_Name"),object2.getString("product_TXT"),Float.parseFloat(object2.getString("market_Price")),
					Float.parseFloat(object2.getString("sales_Price")),0,object2.getString("create_Time"),object2.getString("last_Edit_Time"),"","","",0,0);
			productDAO.addorupdate(tb_vmc_product,product_Class_NO);// 修改
		}
	}
		
	//更新货道信息
	private void updatevmcColumn(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		JSONArray arr1=jsonObject.getJSONArray("PathList");
		for(int i=0;i<arr1.length();i++)
		{
			JSONObject object2=arr1.getJSONObject(i);
			int CABINET_NO=Integer.parseInt(object2.getString("CABINET_NO"));
			int PATH_NO=Integer.parseInt(object2.getString("PATH_NO"));
			String PATH_NOSTR=(PATH_NO<10)?("0"+object2.getString("PATH_NO")):object2.getString("PATH_NO");
			int PATH_REMAINING=Integer.parseInt(object2.getString("PATH_REMAINING"));
			int status=0;
			int j=0;
			//输出内容
	        Set<Map.Entry<String,Integer>> allset=huoSet.entrySet();  //实例化
	        Iterator<Map.Entry<String,Integer>> iter=allset.iterator();
	        while(iter.hasNext())
	        {
	            Map.Entry<String,Integer> me=iter.next();
				String huo=me.getKey();
				int cabid=Integer.parseInt(huo.substring(0, 1));
				int huoid=Integer.parseInt(huo.substring(1, huo.length()));
				status=me.getValue();				
			    if((CABINET_NO==cabid)&&(PATH_NO==huoid))
			    {
			    	j=1;
			    	break;
			    }
				//ToolClass.Log(ToolClass.INFO,"EV_SERVER","huo="+cabid+","+huoid+"sta="+me.getValue(),"server.txt");
	        } 			

	        //可以更新货道
			if(j==1)
			{		
				status=updatehuodaostatus(status,PATH_REMAINING);
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新货道CABINET_NO="+object2.getString("CABINET_NO")
						+"PATH_NO="+PATH_NOSTR+"PRODUCT_NO="+object2.getString("PRODUCT_NO")
						+"PATH_COUNT="+object2.getString("PATH_COUNT")+"status="+status,"server.txt");	
				// 创建InaccountDAO对象
    			vmc_columnDAO columnDAO = new vmc_columnDAO(EVServerService.this);
	            //创建Tb_inaccount对象
    			Tb_vmc_column tb_vmc_column = new Tb_vmc_column(object2.getString("CABINET_NO"), PATH_NOSTR,object2.getString("PRODUCT_NO"),
    					Integer.parseInt(object2.getString("PATH_COUNT")),Integer.parseInt(object2.getString("PATH_REMAINING")),
    					status,"");    			
    			columnDAO.addorupdate(tb_vmc_column);// 添加商品信息
			}
			//更新货道失败
			else
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新货道失败CABINET_NO="+object2.getString("CABINET_NO")
						+"PATH_NO="+PATH_NOSTR+"PRODUCT_NO="+object2.getString("PRODUCT_NO")
						+"PATH_COUNT="+object2.getString("PATH_COUNT"),"server.txt");										
			}			
		}
	}
	
	//更新货道状态信息
	//huostate从货道板上得到的货道状态，upremain从服务端下载的货道存货数量
	private int updatehuodaostatus(int huostate,int upremain)
	{
		int huostatus=0;
		if(huostate==0)//货道故障
			huostatus=2;
		else if(huostate==1)//货道正常
		{
			if(upremain>0)
	    		huostatus=1;
	    	else                             //缺货
	    		huostatus=3;	
		}
		return huostatus;
	}

}
