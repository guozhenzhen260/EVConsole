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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.view.DogService;
import com.easivend.view.EVServerService;
import com.easivend.weixing.WeiConfigAPI;
import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.app.business.BusLand;
import com.easivend.app.business.BusPort;
import com.easivend.common.PictureAdapter;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;


import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Display;
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
	TextView txtcom=null,txtbentcom=null,txtcolumncom=null;
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
    //EVprotocolAPI ev=null;
    int comopen=0,bentopen=0,columnopen=0;//1串口正在打开，0串口没有打开,-1打开设备返回失败,2串口打开设备完成    
    String com=null,bentcom=null,columncom=null,server="";
    final static int REQUEST_CODE=1;   
    //获取货柜信息
    private String[] cabinetID = null;//用来分离出货柜编号
    private int[] cabinetType=null;//货柜类型
    private int huom = 0;// 定义一个开始标识
    Map<String,Integer> huoSet=new HashMap<String,Integer>();
    //Dog服务相关
    int isallopen=1;//是否保持持续一直打开,1一直打开,0关闭后不打开
	private final int SPLASH_DISPLAY_LENGHT = 5000; // 延迟5秒
	//Server服务相关
	EVServerReceiver receiver;
	private int issuc=0;//0准备串口初始化，1可以开始签到，2签到成功	
	private boolean issale=false;//true是否已经自动打开过售卖页面了，如果打开过，就不再打开了
	Map<String, String> vmcmap;
	Timer timer = new Timer();
	
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
		
		
		//注册串口监听器
		EVprotocolAPI.setCallBack(new jniInterfaceImp());
		dialog= ProgressDialog.show(MaintainActivity.this,"同步服务器","请稍候...");
				
		//==========
		//Dog服务相关
		//==========
		//启动服务
		startService(new Intent(this,DogService.class));
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
        		sendBroadcast(intent); 
            }

		}, SPLASH_DISPLAY_LENGHT);
		
		
		
		//=============
		//Server服务相关
		//=============
		//3.开启服务
		startService(new Intent(MaintainActivity.this,EVServerService.class));
		//4.注册接收器
		receiver=new EVServerReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserverrec");
		this.registerReceiver(receiver,filter);
		//7.发送指令广播给EVServerService
		vmcmap = ToolClass.getvmc_no(MaintainActivity.this);		
		timer.schedule(new TimerTask() { 
	        @Override 
	        public void run() { 
	  
	            runOnUiThread(new Runnable() {      // UI thread 
	                @Override 
	                public void run() { 
	                	if(issuc==2) 
	                	{
	                		//先查询设备信息，再上报给服务器
	                		EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
						}
	                } 
	            }); 
	        } 
	    }, 5*1000, 10*60*1000);       // timeTask 10*60
		
		//================
		//串口配置和注册相关
		//================
		txtcom=(TextView)super.findViewById(R.id.txtcom);
		txtbentcom=(TextView)super.findViewById(R.id.txtbentcom);
		txtcolumncom=(TextView)super.findViewById(R.id.txtcolumncom);
		ToolClass.SetDir();		
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
			
	        //串口设置
	        EVprotocolAPI.vmcEVStart();//开启监听
	        //现金模块
			if(com.equals("")==true)
			{
				txtcom.setText(com+"[现金模块]串口未开启");		
			}
			else 
			{
				//打开主柜串口		
				comopen = EVprotocolAPI.EV_portRegister(com);
				if(comopen == 1)
				{
					txtcom.setText(com+"[现金模块]串口正在准备连接");			
				}
				else
				{
					txtcom.setText(com+"[现金模块]串口打开失败");
				}	
			}
			//格子柜
			if(bentcom.equals("")==true)
			{
				txtbentcom.setText(bentcom+"[格子柜]串口未开启");		
			}
			else
			{
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
			//主柜
			if(columncom.equals("")==true)
			{
				txtcolumncom.setText(columncom+"[主柜]串口未开启");		
			}
			else
			{
				//打开格子柜
				columnopen = EVprotocolAPI.EV_portRegister(columncom);
				if(columnopen == 1)
				{
					txtcolumncom.setText(columncom+"[主柜]串口正在准备连接");			
				}
				else
				{
					txtcolumncom.setText(columncom+"[主柜]串口打开失败");
				}
			}
			
			if((comopen!=1)&&(bentopen!=1)&&(columnopen!=1))
			{
				dialog.dismiss();
			}	
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
	
	//创建一个专门处理单击接口的子类
	private class jniInterfaceImp implements JNIInterface
	{
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
					//现金模块初始化完成
					if(Set.get("port_com").equals(com))
					{
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现金模块连接完成","log.txt");	
						ToolClass.setCom_id((Integer)Set.get("port_id"));
						if((Integer)Set.get("port_id")>=0)
						{
							txtcom.setText(com+"[现金模块]连接完成");
							comopen=2;
						}
						else
						{
							txtcom.setText(bentcom+"[现金模块]连接失败");	
							comopen=-1;
						}				
					}
					//格子柜初始化完成
					else if(Set.get("port_com").equals(bentcom))
					{
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<格子柜连接完成","log.txt");	
						ToolClass.setBentcom_id((Integer)Set.get("port_id"));
						//初始化货道信息
						if((Integer)Set.get("port_id")>=0)
						{
							txtbentcom.setText(bentcom+"[格子柜]连接完成");	
							getcolumnstat();
						}
						else
						{
							txtbentcom.setText(bentcom+"[格子柜]连接失败");
							bentopen=-1;														
						}
					}
					//主柜初始化完成
					else if(Set.get("port_com").equals(columncom))
					{
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<主柜连接完成","log.txt");	
						ToolClass.setColumncom_id((Integer)Set.get("port_id"));
						//初始化货道信息
						if((Integer)Set.get("port_id")>=0)
						{
							txtcolumncom.setText(columncom+"[主柜]连接完成");
							//有拖格子柜时，由格子柜来初始化货道,没有就用主柜来初始化货道
							if(bentopen!=1)
							{
								getcolumnstat();
							}
//							else 
//							{
//								columnopen=2;
//							}
							
						}
						else
						{
							txtcolumncom.setText(columncom+"[主柜]连接失败");
							columnopen=-1;							
						}
					}
					//判断条件后，可以开始签到操作							
					onInit();
					break;
				//主柜查询	
				case EVprotocolAPI.EV_COLUMN_CHECK:	
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<返回货道状态:","log.txt");	
					String tempno2=null;
				
					//输出内容
			        Set<Entry<String, Object>> allmap=Set.entrySet();  //实例化
			        Iterator<Entry<String, Object>> iter=allmap.iterator();
			        while(iter.hasNext())
			        {
			            Entry<String, Object> me=iter.next();
			            if(
			               (me.getKey().equals("EV_TYPE")!=true)
			            )   
			            {
			            	if(Integer.parseInt(me.getKey())<10)
			    				tempno2="0"+me.getKey();
			    			else 
			    				tempno2=me.getKey();
			            	
			            	huoSet.put(cabinetID[huom]+tempno2,(Integer)me.getValue());
			            }
			        } 
			        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+huoSet.size()+"货道状态:"+huoSet.toString(),"log.txt");	
			        huom++;
			        if(huom<cabinetID.length)
			        {
			        	//2.获取所有货道号
			    	    queryhuodao(Integer.parseInt(cabinetID[huom]),cabinetType[huom]);
			        }
			        else
			        {
			        	columnopen=2;
			        	bentopen=2;
						//可以开始签到操作
			        	onInit();
					}
					break;
				//格子柜查询	
				case EVprotocolAPI.EV_BENTO_CHECK:
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<返回货道状态:","log.txt");	
					String tempno=null;
				
					//输出内容
			        Set<Entry<String, Object>> allmap2=Set.entrySet();  //实例化
			        Iterator<Entry<String, Object>> iter2=allmap2.iterator();
			        while(iter2.hasNext())
			        {
			            Entry<String, Object> me=iter2.next();
			            if(
			               (me.getKey().equals("EV_TYPE")!=true)&&(me.getKey().equals("cool")!=true)
			               &&(me.getKey().equals("hot")!=true)&&(me.getKey().equals("light")!=true)
			            )   
			            {
			            	if(Integer.parseInt(me.getKey())<10)
			    				tempno="0"+me.getKey();
			    			else 
			    				tempno=me.getKey();
			            	
			            	huoSet.put(cabinetID[huom]+tempno,(Integer)me.getValue());
			            }
			        } 
			        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+huoSet.size()+"货道状态:"+huoSet.toString(),"log.txt");	
			        huom++;
			        if(huom<cabinetID.length)
			        {
			        	//2.获取所有货道号
			    	    queryhuodao(Integer.parseInt(cabinetID[huom]),cabinetType[huom]);
			        }
			        else
			        {
			        	columnopen=2;
			        	bentopen=2;
						//可以开始签到操作
			        	onInit();
					}
					break;	
				//现金设备状态查询
				case EVprotocolAPI.EV_MDB_HEART://心跳查询
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现金设备状态:","log.txt");	
					
					int bill_err=ToolClass.getvmcStatus(Set,1);
					int coin_err=ToolClass.getvmcStatus(Set,2);
					//上报给服务器
					Intent intent=new Intent();
    				intent.putExtra("EVWhat", EVServerhttp.SETDEVSTATUCHILD);
    				intent.putExtra("bill_err", bill_err);
    				intent.putExtra("coin_err", coin_err);
    				intent.setAction("android.intent.action.vmserversend");//action与接收器相同
    				sendBroadcast(intent); 
					break; 	
			}
		}
	}
	
	private void onInit()
	{	
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<comopen="+comopen+"bentopen="+bentopen+"columnopen="+columnopen,"log.txt");	
		if((comopen!=1)&&(bentopen!=1)&&(columnopen!=1)) 
		{
	    	Intent intent=new Intent();
			intent.putExtra("EVWhat", EVServerhttp.SETCHILD);
			intent.putExtra("vmc_no", vmcmap.get("vmc_no"));
			intent.putExtra("vmc_auth_code", vmcmap.get("vmc_auth_code"));
			//传递数据
	        final SerializableMap myMap=new SerializableMap();
	        myMap.setMap(huoSet);//将map数据添加到封装的myMap<span></span>中
	        Bundle bundle=new Bundle();
	        bundle.putSerializable("huoSet", myMap);
	        intent.putExtras(bundle);
			intent.setAction("android.intent.action.vmserversend");//action与接收器相同
			sendBroadcast(intent);  
		}
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
		        		sendBroadcast(intent);
			        }
				}
			}
		}
		//注册串口监听器
		EVprotocolAPI.setCallBack(new jniInterfaceImp());	
	}
	
	//=============
	//货柜相关
	//=============
	//获取当前货道信息
	private void getcolumnstat()
	{		
		vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(MaintainActivity.this);// 创建InaccountDAO对象
	    // 1.获取所有柜号
	    List<Tb_vmc_cabinet> listinfos = cabinetDAO.getScrollData();
	    cabinetID = new String[listinfos.size()];// 设置字符串数组的长度
	    cabinetType=new int[listinfos.size()];// 设置字符串数组的长度	    
	    // 遍历List泛型集合
	    for (Tb_vmc_cabinet tb_inaccount : listinfos) 
	    {
	        cabinetID[huom] = tb_inaccount.getCabID();
	        cabinetType[huom]= tb_inaccount.getCabType();
	        ToolClass.Log(ToolClass.INFO,"EV_JNI","获取柜号="+cabinetID[huom]+"类型="+cabinetType[huom],"log.txt");
		    huom++;// 标识加1
	    }
	    huom=0;
	    if(listinfos.size()>0)
	    {
		    //2.获取所有货道号
		    queryhuodao(Integer.parseInt(cabinetID[huom]),cabinetType[huom]);
	    }
	    else 
	    {
	    	bentopen=-1;
	    	columnopen=-1;
	    	//可以开始签到操作
	    	onInit();
		}
	}
	
	//获取本柜所有货道号
	private void queryhuodao(int cabinetsetvar,int cabinetTypesetvar)
	{
		//格子柜
		if(cabinetTypesetvar==5)
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<huodao格子柜查询","log.txt");
			EVprotocolAPI.EV_bentoCheck(ToolClass.getBentcom_id(),cabinetsetvar);
		}
		//普通柜
		else 
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<huodao普通柜查询","log.txt");
			EVprotocolAPI.getColumn(ToolClass.getColumncom_id(),1,1);
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
				issuc=2;	
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
	
	@Override
	protected void onDestroy() {
		EVprotocolAPI.vmcEVStop();//关闭监听
		//关闭串口
		if(comopen!=0)	
			EVprotocolAPI.EV_portRelease(ToolClass.getCom_id());
		if(bentopen!=0)
			EVprotocolAPI.EV_portRelease(ToolClass.getBentcom_id());
		if(columnopen!=0)
			EVprotocolAPI.EV_portRelease(ToolClass.getColumncom_id());
		EVprotocolAPI.vmcEVStop();//关闭监听		
		timer.cancel(); //关闭定时器
		//=============
		//Server服务相关
		//=============
		//5.解除注册接收器
		MaintainActivity.this.unregisterReceiver(receiver);
		//6.结束服务
		stopService(new Intent(MaintainActivity.this, EVServerService.class));
		// TODO Auto-generated method stub
		super.onDestroy();		
	}
}


