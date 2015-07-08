package com.easivend.view;

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
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.model.Tb_vmc_class;
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
    //获取货柜信息
    String[] cabinetID = null;//用来分离出货柜编号
	int[] cabinetType=null;//货柜类型
	int m = 0;// 定义一个开始标识
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
			case EVServerhttp.SETCHILD:
				String vmc_no=bundle.getString("vmc_no");
				String vmc_auth_code=bundle.getString("vmc_auth_code");
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","receiver:vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code,"server.txt");
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
		//注册出货监听器
  	    EVprotocolAPI.setCallBack(new JNIInterface() {
			
			@Override
			public void jniCallback(Map<String, Object> allSet) {
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<huodao货道相关","server.txt");
				Map<String, Object> Set= allSet;
				int jnirst=(Integer)Set.get("EV_TYPE");
				switch (jnirst)
				{
					case EVprotocolAPI.EV_TRADE_RPT://接收子线程消息
//						device=allSet.get("device");//出货柜号
//						status=allSet.get("status");//出货结果
//						hdid=allSet.get("hdid");//货道id
//						hdtype=allSet.get("type");//出货类型
//						cost=ToolClass.MoneyRec(allSet.get("cost"));//扣钱
//						totalvalue=ToolClass.MoneyRec(allSet.get("totalvalue"));//剩余金额
//						huodao=allSet.get("huodao");//剩余存货数量
//						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<出货结果"+"device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
//								+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");	
//						
//						txthuorst.setText("device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
//								+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");
//						sethuorst(status);
						break;
					case EVprotocolAPI.EV_COLUMN_RPT://接收子线程消息
//						huoSet.clear();
//						//输出内容
//				        Set<Entry<String, Integer>> allmap=Set.entrySet();  //实例化
//				        Iterator<Entry<String, Integer>> iter=allmap.iterator();
//				        while(iter.hasNext())
//				        {
//				            Entry<String, Integer> me=iter.next();
//				            if(me.getKey().equals("EV_TYPE")!=true)
//				            	huoSet.put(me.getKey(), me.getValue());
//				        } 
//						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道状态:"+huoSet.toString());	
//						showhuodao();						
						break;
					case EVprotocolAPI.EV_BENTO_CHECK://格子柜查询
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<huodao格子柜查询","server.txt");
//						String tempno=null;
//						
//						cool=(Integer)Set.get("cool");
//						hot=(Integer)Set.get("hot");
//						light=(Integer)Set.get("light");
//						ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道cool:"+cool+",hot="+hot+",light="+light,"log.txt");
//						if(light>0)
//						{
//							txtlight.setText("支持");
//							switchlight.setEnabled(true);
//							
//						}
//						else
//						{
//							txtlight.setText("不支持");
//							switchlight.setEnabled(false);
//						}
//						if(cool>0)
//						{
//							txtcold.setText("支持");
//							switcold.setEnabled(true);
//						}
//						else
//						{
//							txtcold.setText("不支持");
//							switcold.setEnabled(false);
//						}
//						if(hot>0)
//						{
//							txthot.setText("支持");
//							switchhot.setEnabled(true);
//						}
//						else
//						{
//							txthot.setText("不支持");
//							switchhot.setEnabled(false);
//						}
//						
//						huoSet.clear();
//						//输出内容
//				        Set<Entry<String, Object>> allmap=Set.entrySet();  //实例化
//				        Iterator<Entry<String, Object>> iter=allmap.iterator();
//				        while(iter.hasNext())
//				        {
//				            Entry<String, Object> me=iter.next();
//				            if(
//				               (me.getKey().equals("EV_TYPE")!=true)&&(me.getKey().equals("cool")!=true)
//				               &&(me.getKey().equals("hot")!=true)&&(me.getKey().equals("light")!=true)
//				            )   
//				            {
//				            	if(Integer.parseInt(me.getKey())<10)
//				    				tempno="0"+me.getKey();
//				    			else 
//				    				tempno=me.getKey();
//				            	
//				            	huoSet.put(tempno, (Integer)me.getValue());
//				            }
//				        } 
//				        huonum=huoSet.size();
//						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+huonum+"货道状态:"+huoSet.toString(),"log.txt");	
//						showhuodao();						
						break;	
					case EVprotocolAPI.EV_BENTO_OPEN://格子柜出货
						break;	
					case EVprotocolAPI.EV_BENTO_LIGHT://格子柜开灯
						break;	
				}
			}
		});
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
							getcolumnstat();
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
	
	//获取当前货道信息
	private void getcolumnstat()
	{		
		vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(EVServerService.this);// 创建InaccountDAO对象
	    // 1.获取所有柜号
	    List<Tb_vmc_cabinet> listinfos = cabinetDAO.getScrollData();
	    cabinetID = new String[listinfos.size()];// 设置字符串数组的长度
	    cabinetType=new int[listinfos.size()];// 设置字符串数组的长度	    
	    // 遍历List泛型集合
	    for (Tb_vmc_cabinet tb_inaccount : listinfos) 
	    {
	        cabinetID[m] = tb_inaccount.getCabID();
	        cabinetType[m]= tb_inaccount.getCabType();
	        ToolClass.Log(ToolClass.INFO,"EV_SERVER","获取柜号="+cabinetID[m]+"类型="+cabinetType[m],"server.txt");
		    m++;// 标识加1
	    }
	    m=0;
	    //2.获取所有货道号
	    queryhuodao(Integer.parseInt(cabinetID[m]),cabinetType[m]);
	}
	//获取本柜所有货道号
	private void queryhuodao(int cabinetsetvar,int cabinetTypesetvar)
	{
		//格子柜
		if(cabinetTypesetvar==5)
		{
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","APP<<huodao格子柜相关","server.txt");
			EVprotocolAPI.EV_bentoCheck(ToolClass.getBentcom_id(),cabinetsetvar);
		}
		//普通柜
		else 
		{
			EVprotocolAPI.getColumn(cabinetsetvar);
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
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新货道CABINET_NO="+object2.getString("CABINET_NO")
					+"PATH_NO="+object2.getString("PATH_NO")+"PRODUCT_NO="+object2.getString("PRODUCT_NO")
					+"PATH_COUNT="+object2.getString("PATH_COUNT"),"server.txt");										
//			// 创建InaccountDAO对象
//			vmc_productDAO productDAO = new vmc_productDAO(EVServerService.this);
//            //创建Tb_inaccount对象
//			Tb_vmc_product tb_vmc_product = new Tb_vmc_product(object2.getString("product_NO"), object2.getString("product_Name"),object2.getString("product_TXT"),Float.parseFloat(object2.getString("market_Price")),
//					Float.parseFloat(object2.getString("sales_Price")),0,object2.getString("create_Time"),object2.getString("last_Edit_Time"),"","","",0,0);
//			productDAO.addorupdate(tb_vmc_product,product_Class_NO);// 修改
		}
	}

}
