package com.easivend.app.business;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.bean.ComBean;
import com.easivend.app.maintain.PrintTest;
import com.easivend.common.AudioSound;
import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.COMService;
import com.example.evconsole.R;
import com.example.printdemo.MyFunc;
import com.example.printdemo.SerialHelper;
import com.printsdk.cmd.PrintCmd;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class BusHuo extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 500; // 延迟2秒		
	private String proID = null;
	private String productID = null;
	private String proType = null;
	private String cabID = null;
	private String huoID = null;
    private float prosales = 0;
    private int count = 0;
    private int zhifutype = 0;//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描,5取货密码
    private TextView txtbushuoname = null;
    private ImageView ivbushuoquhuo=null;
    private int tempx=0;
    private int cabinetvar=0,huodaoNo=0,cabinetTypevar=0;
    private vmc_columnDAO columnDAO =null; 
    //出货结果
    private int status=0;//出货结果	
    //=================
    //COM服务相关
    //=================
  	LocalBroadcastManager comBroadreceiver;
  	COMReceiver comreceiver;
    //=================
    //打印机相关
    //=================
  	boolean istitle1,istitle2,isno,issum,isthank,iser,isdate,isdocter;
	int serialno=0;//流水号
	String title1str,title2str,thankstr,erstr;
	SerialControl ComA;                  // 串口控制
	static DispQueueThread DispQueue;    // 刷新显示线程
	private boolean ercheck=false;//true正在打印机的操作中，请稍后。false没有打印机的操作
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // 国际化标志时间格式类
	SimpleDateFormat sdfdoc = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"); // 药房小票用
	private Handler printmainhand=null;
	private int isPrinter=0;//0没有设置打印机，1有设置打印机
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.bushuo);	
		//删除前面的activity
		if(BusgoodsClass.BusgoodsClassAct!=null)
			BusgoodsClass.BusgoodsClassAct.finish(); 
		if(Busgoods.BusgoodsAct!=null)
			Busgoods.BusgoodsAct.finish(); 
		if(BusgoodsSelect.BusgoodsSelectAct!=null)
			BusgoodsSelect.BusgoodsSelectAct.finish(); 
		
		
		//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		 
  	    
		//从商品页面中取得锁选中的商品
//		Intent intent=getIntent();
//		Bundle bundle=intent.getExtras();
		proID=OrderDetail.getProID();
		productID=OrderDetail.getProductID();
		proType=OrderDetail.getProType();
		cabID=OrderDetail.getCabID();
		huoID=OrderDetail.getColumnID();
		prosales=OrderDetail.getShouldPay();//商品单价
		count=OrderDetail.getShouldNo();//数量
		zhifutype=OrderDetail.getPayType();
		txtbushuoname=(TextView)findViewById(R.id.txtbushuoname);
				
  	    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品orderID="+OrderDetail.getOrdereID()+"proID="+proID+" productID="
				+productID+" proType="
				+proType+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
				+count+" zhifutype="+zhifutype,"log.txt");		
  	    this.ivbushuoquhuo =(ImageView) super.findViewById(R.id.ivbushuoquhuo);  	    
  	    Bitmap bitmap=ToolClass.ReadAdshuoFile();
	    if(bitmap!=null)
	    {
	    	this.ivbushuoquhuo.setImageBitmap(bitmap);// 设置图像的二进制值
	    }
	    else
	    {
	    	ivbushuoquhuo.setImageResource(R.drawable.chuwaitland);
	    }
		//****
		//出货
		//****
		chuhuoopt(tempx);
		
		//=================
	    //打印机相关
	    //=================
		printmainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what) 
				{
					case PrintTest.NORMAL:
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机正常","com.txt");
						if(isPrinter==1)
							isPrinter=2;
						break;
					case PrintTest.NOPOWER:
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机未连接或未上电","com.txt");
						break;
					case PrintTest.NOMATCH:
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机异常[打印机和调用库不匹配]","com.txt");
						if(isPrinter==1)
							isPrinter=2;
						break;
					case PrintTest.HEADOPEN:	
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机打印机头打开","com.txt");
						break;
					case PrintTest.CUTTERERR:
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机切刀未复位","com.txt");
						break;
					case PrintTest.HEADHEAT:
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机头过热","com.txt");
						break;
					case PrintTest.BLACKMARKERR:
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机黑标错误","com.txt");
						break;
					case PrintTest.PAPEREXH:	
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机纸尽","com.txt");
						break;
					case PrintTest.PAPERWILLEXH://这个也可以当正常状态使用	
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机纸将尽","com.txt");
						if(isPrinter==1)
							isPrinter=2;
						break;
					case PrintTest.UNKNOWERR: 
						ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印机其他异常="+msg.obj,"com.txt");
						break;
				}				
			}
		};
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(BusHuo.this);// 创建InaccountDAO对象
	    // 得到设备ID号
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		isPrinter=tb_inaccount.getIsNet();
    	}
    	ToolClass.Log(ToolClass.INFO,"EV_COM","isPrinter=" + isPrinter,"com.txt");
        if(isPrinter>0)
        {
        	ToolClass.Log(ToolClass.INFO,"EV_COM","打开打印机","com.txt");
        	ReadSharedPreferencesPrinter();
	    	ComA = SerialControl.getInstance();
	        ComA.setbLoopData(PrintCmd.GetStatus());
	        DispQueue = new DispQueueThread();
			DispQueue.start();
			//打开串口
			ComA.setPort(ToolClass.getPrintcom());            // 1.1 设定串口
			ComA.setBaudRate("9600");// 1.2 设定波特率
			OpenComPort(ComA); // 1.3 打开串口			
        }
	}
			
	//出货,返回值0失败,1出货指令成功，等待返回结果,2出货完成
	private void chuhuoopt(int huox)
	{
		
		// 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_column表中
 	    columnDAO = new vmc_columnDAO(this);
 	    //txtbushuoname.setText(proID+"["+prosales+"]"+"->正在出货,请稍候...");
		//1.计算出出货货道
		//按商品id出货
		if(proType.equals("1")==true)
		{
	 	    // 获取所有收入信息，并存储到Map集合中
			List<String> alllist = columnDAO.getproductColumn(productID);
			cabinetvar=Integer.parseInt(alllist.get(0));
			huodaoNo=Integer.parseInt(alllist.get(1));
			cabinetTypevar=Integer.parseInt(alllist.get(2));
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品cabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar,"log.txt"); 
		}
		//按货道id出货
		else if(proType.equals("2")==true)
		{
	 	    // 获取所有收入信息，并存储到Map集合中
			String alllist = columnDAO.getcolumnType(cabID);
			cabinetvar=Integer.parseInt(cabID);
			huodaoNo=Integer.parseInt(huoID);
			cabinetTypevar=Integer.parseInt(alllist);
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品cabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar,"log.txt"); 
		}
		
		
		new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {	  
            	//2.计算货物金额
        		float cost=0;
        		if(zhifutype==0)
        		{
        			cost=prosales;
        		}
        		ToolClass.Log(ToolClass.INFO,"EV_JNI",
        		    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
        		    	+" column="+huodaoNo
        		    	+" cost="+cost
        		    	,"log.txt");
        		Intent intent = new Intent();
        		//4.发送指令广播给COMService
        		intent.putExtra("EVWhat", COMService.EV_CHUHUOCHILD);	
        		intent.putExtra("cabinet", cabinetvar);	
        		intent.putExtra("column", huodaoNo);	
        		intent.putExtra("cost", ToolClass.MoneySend(cost));
        		intent.setAction("android.intent.action.comsend");//action与接收器相同
        		comBroadreceiver.sendBroadcast(intent);
            }

		}, SPLASH_DISPLAY_LENGHT);
	}
	//修改存货数量
	private void chuhuoupdate(int cabinetvar,int huodaoNo)
	{
		String cab=null,huo=null;
		cab=String.valueOf(cabinetvar);
		//货道id=1到9，改为01到09
        if(huodaoNo<10)
        {
        	huo="0"+String.valueOf(huodaoNo);
        }
        else
        {
        	huo=String.valueOf(huodaoNo);
        }	
        //扣除存货余量
		columnDAO.update(cab,huo);		
	}
	
	//记录日志出货结果type=1出货成功，0出货失败
	private void chuhuoLog(int type)
	{
		OrderDetail.setYujiHuo(1);
		OrderDetail.setCabID(String.valueOf(cabinetvar));
		OrderDetail.setColumnID(String.valueOf(huodaoNo));
		if(type==1)//出货成功
		{
			OrderDetail.setPayStatus(0);
			OrderDetail.setRealHuo(1);
			OrderDetail.setHuoStatus(0);
		}
		else//出货失败
		{
			OrderDetail.setPayStatus(1);
			OrderDetail.setRealHuo(0);
			OrderDetail.setHuoStatus(1);
		}
	}
	
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
			//操作返回	
			case COMThread.EV_OPTMAIN: 
				SerializableMap serializableMap2 = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set2=serializableMap2.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusHuo 货道操作="+Set2,"com.txt");
				int EV_TYPE=Set2.get("EV_TYPE");
				if((EV_TYPE==EVprotocol.EV_BENTO_OPEN)||(EV_TYPE==EVprotocol.EV_COLUMN_OPEN))
				{
					status=Set2.get("result");//出货结果
					ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<BusHuo出货结果"+"device=["+cabinetvar+"],hdid=["+huodaoNo+"],status=["+status+"]","com.txt");	
					
					//不是自提密码的，才扣除存货余量
					if(OrderDetail.getPayType()!=5)
					{
						//1.更新出货结果
						//扣除存货余量
						chuhuoupdate(cabinetvar,huodaoNo);
					}					
					//出货成功
					if(status==1)
					{
						txtbushuoname.setText(proID+"["+prosales+"]"+"->出货完成，请到"+cabinetvar+"柜"+huodaoNo+"货道取商品!");
						txtbushuoname.setTextColor(android.graphics.Color.BLUE);
						chuhuoLog(1);//记录日志
						ivbushuoquhuo.setImageResource(R.drawable.chusuccessland);
						//格子柜
						if(cabinetTypevar==5)
						{
							AudioSound.playbushuogezi();
						}
						//普通柜
						else
						{
							AudioSound.playbushuotang();
						}
						//=======
						//打印机相关
						//=======
						//打印
						if((isPrinter>0)&&(zhifutype!=5))
				        {
							new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {	 					            	
									ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo打印凭证...","com.txt");
									if(isdocter)
							    	{
							    		PrintDocter();                             // 打印小票
							    	}
							    	else
							    	{
							    		PrintBankQueue();                             // 打印小票
							    	}									
					            }

							}, 600);
							
				        }
					}
					else
					{
						txtbushuoname.setText(proID+"["+prosales+"]"+"->"+cabinetvar+"柜"+huodaoNo+"货道出货失败，您没有被扣款!");
						txtbushuoname.setTextColor(android.graphics.Color.RED);
						chuhuoLog(0);//记录日志
						ivbushuoquhuo.setImageResource(R.drawable.chufailland);
					}
										
											
					//3.退回找零页面
		 	    	new Handler().postDelayed(new Runnable() 
					{
	                    @Override
	                    public void run() 
	                    {	   
	                    	//退出时，返回intent
	        	            Intent intentrec=new Intent();
	        	            intentrec.putExtra("status", status);//出货结果
	                    	if(zhifutype==0)//现金支付
	                    	{           
	                    		intentrec.putExtra("cabinetvar", cabinetvar);//出货柜号	                    		
	            	            BusHuo.this.setResult(BusZhiAmount.RESULT_CANCELED,intentrec);                    	            
	                		}
	                    	else if(zhifutype==1)//银联
	                    	{
	                    		BusHuo.this.setResult(BusZhipos.RESULT_CANCELED,intentrec);                    	            
	                		}
	                    	else if(zhifutype==3)//支付宝二维码
	                    	{
	                    		BusHuo.this.setResult(BusZhier.RESULT_CANCELED,intentrec);                    	            
	                		}
	                    	else if(zhifutype==4)//微信扫描
	                    	{                        			
	                    		BusHuo.this.setResult(BusZhiwei.RESULT_CANCELED,intentrec);                    	            
	                		}
	                    	else if(zhifutype==5)//提货码
	                    	{                        			
	                    		BusHuo.this.setResult(BusZhitihuo.RESULT_CANCELED,intentrec);                    	            
	                		}
	                    	else if(zhifutype==-1)//取货码
	                    	{                        			
	                    		BusHuo.this.setResult(0x03,intentrec);                    	            
	                		}
	                    	finish();	
	                    }
	
					}, 6000);
					break;
				}
			//按钮返回
			case COMThread.EV_BUTTONMAIN:
				SerializableMap serializableMap = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusHuo 按键操作="+Set,"com.txt");
				break;
			}			
		}

	}
	
	//=================
    //打印机相关
    //=================
	//读取打印信息
    private void ReadSharedPreferencesPrinter()
    {
    	//文件是私有的
    	SharedPreferences  user = getSharedPreferences("print_info",0);
    	//读取
    	//标题一
    	istitle1=user.getBoolean("istitle1",true);
    	if(istitle1==false)
    	{
    		title1str="";
    	}
    	else
    	{
    		title1str=user.getString("title1str", "自助售货机");
    	}
		//标题二
    	istitle2=user.getBoolean("istitle2",true);
    	if(istitle2==false)
    	{
    		title2str="";
    	}
    	else
    	{
    		title2str=user.getString("title2str", "交易凭证");
    	}
		//交易序号
		isno=user.getBoolean("isno",true);
		serialno=user.getInt("serialno", 1);
		//金额统计
		issum=user.getBoolean("issum",true);
		//感谢提示
    	isthank=user.getBoolean("isthank",true);
    	if(isthank==false)
    	{
    		thankstr="";
    	}
    	else
    	{
    		thankstr=user.getString("thankstr", "谢谢使用自助售货机,我们将竭诚为您服务!");
    	}
		//二维码
    	iser=user.getBoolean("iser",true);
    	if(iser==false)
    	{
    		erstr="";
    	}
    	else
    	{
    		erstr=user.getString("erstr", "http://www.easivend.com.cn/");
    	}
		//当前时间
		isdate=user.getBoolean("isdate",true);	
		//药房小票
		isdocter=user.getBoolean("isdocter",true);
    }
    
  
    //写入流水号信息
    private void SaveSharedPreferencesSerialno()
    {
    	//文件是私有的
		SharedPreferences  user = getSharedPreferences("print_info",0);
		//需要接口进行编辑
		SharedPreferences.Editor edit=user.edit();
		//写入
		//交易序号
		edit.putInt("serialno", serialno);
		//提交更新
		edit.commit();
    }
    
    // ------------------打开串口--------------------
    private void OpenComPort(SerialHelper ComPort) {
		try {
			ComPort.open();
		} catch (SecurityException e) {
			ToolClass.Log(ToolClass.ERROR,"EV_COM","没有读/写权限","com.txt");
		} catch (IOException e) {
			ToolClass.Log(ToolClass.ERROR,"EV_COM","未知错误","com.txt");
		} catch (InvalidParameterException e) {
			ToolClass.Log(ToolClass.ERROR,"EV_COM","参数错误","com.txt");
		}
	}
    
    // ------------------关闭串口--------------------
 	private void CloseComPort(SerialHelper ComPort) {
 		if (ComPort != null) {
 			ComPort.stopSend();
 			ComPort.close();
 		}
 	}
 	
    // -------------------------查询状态---------------------------
 	private void GetPrinterStates(SerialHelper ComPort, byte[] sOut) {
  		try { 			
 			if (ComPort != null && ComPort.isOpen()) {
 				ComPort.send(sOut);
 				ercheck = true; 				
 				ToolClass.Log(ToolClass.INFO,"EV_COM","打印机状态查询...","com.txt");
 			} else
 			{
 				ToolClass.Log(ToolClass.ERROR,"EV_COM","打印机串口未打开","com.txt"); 				
 			}
 		} catch (Exception ex) {
 			ToolClass.Log(ToolClass.ERROR,"EV_COM","打印机串口打开异常="+ex.getMessage(),"com.txt"); 			
 		}
  		
 	}
 	/**
	 * 打印销售单据
	 */
	private void PrintBankQueue() {
		try {
			// 小票标题
//			byte[] bValue = new byte[100];
			ComA.send(PrintCmd.SetBold(0));
			ComA.send(PrintCmd.SetAlignment(1));
			ComA.send(PrintCmd.SetSizetext(1, 1));
			//标题一
			if(istitle1)
			{
				ComA.send(PrintCmd.PrintString(title1str+"\n\n", 0));
			}
			//标题二
			if(istitle2)
			{
				ComA.send(PrintCmd.PrintString(title2str+"\n\n", 0));
			}
			//交易序号
			if(isno)
			{
				ComA.send(PrintCmd.SetBold(1));
				ComA.send(PrintCmd.PrintString(String.format("%04d", serialno++)+"\n\n", 0));
			}
			// 小票主要内容
			CleanPrinter(); // 清理缓存，缺省模式
			ComA.send(PrintCmd.PrintString(OrderDetail.getProID()+"  单价"+prosales+"元\n", 0));
			ComA.send(PrintCmd.PrintString("_________________________________________\n\n", 0));
			//金额统计
			if(issum)
			{
				ComA.send(PrintCmd.PrintString("总计:"+prosales*count+"元\n", 0));
			}
			//感谢提示
			if(isthank)
			{
				ComA.send(PrintCmd.PrintString(thankstr+"\n", 0));
			}
			// 二维码
			if(iser)
			{
				ComA.send(PrintCmd.PrintFeedline(2));    
				PrintQRCode();  // 二维码打印                          
				ComA.send(PrintCmd.PrintFeedline(2));   
			}
			//当前时间
			if(isdate)
			{
				ComA.send(PrintCmd.SetAlignment(2));
				ComA.send(PrintCmd.PrintString(sdf.format(new Date()).toString() + "\n\n", 1));
			}
			// 走纸4行,再切纸,清理缓存
			PrintFeedCutpaper(4);  
			SaveSharedPreferencesSerialno();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 打印药房小票
	 */
	private void PrintDocter() {
		try {
			// 小票标题
			ComA.send(PrintCmd.SetAlignment(1));
			ComA.send(PrintCmd.PrintString("药房小票\n", 0));
			CleanPrinter(); // 清理缓存，缺省模式	
			//交易序号
			if(isno)
			{
				ComA.send(PrintCmd.PrintString("收据号:                                  "+String.format("%06d", 385017+(serialno++))+"\n", 0));
			}					
			//当前时间
			if(isdate)
			{
				ComA.send(PrintCmd.PrintString(sdfdoc.format(new Date()).toString() + "\n\n", 1));
			}
			ComA.send(PrintCmd.PrintString("批号      品名规格     价格*数量     小计\n", 0));
			ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
			//ComA.send(PrintCmd.PrintString("160705  化痰祛斑胶囊0.32g*10粒*3板  57*4  228\n", 0));
			ComA.send(PrintCmd.PrintString(OrderDetail.getProID()+"   "+prosales+"*"+count+"   "+prosales+"\n", 0));
			ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
			ComA.send(PrintCmd.PrintString("合计(人民币):                         "+prosales*count+"\n", 0));
			ComA.send(PrintCmd.PrintString("应收款:                               "+prosales*count+"\n", 0));
			//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
			if(zhifutype==0)
			{
				ComA.send(PrintCmd.PrintString("实收款:                              "+OrderDetail.getSmallAmount()+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("其中:                                        \n", 0));
				ComA.send(PrintCmd.PrintString(" 银联卡支付:                      0.00\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("找回(人民币):                      "+(OrderDetail.getSmallAmount()-prosales)+"\n\n", 0));
			}
			else if(zhifutype==1)
			{
				ComA.send(PrintCmd.PrintString("实收款:                              "+prosales*count+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("其中:                                        \n", 0));
				ComA.send(PrintCmd.PrintString(" 银联卡支付:                      "+prosales*count+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("找回(人民币):                      0.00\n\n", 0));
			}
			else if(zhifutype==3)
			{
				ComA.send(PrintCmd.PrintString("实收款:                              "+prosales*count+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("其中:                                        \n", 0));
				ComA.send(PrintCmd.PrintString(" 支付宝支付:                      "+prosales*count+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("找回(人民币):                      0.00\n\n", 0));
			}
			else if(zhifutype==4)
			{
				ComA.send(PrintCmd.PrintString("实收款:                              "+prosales*count+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("其中:                                        \n", 0));
				ComA.send(PrintCmd.PrintString("  微信支付:                      "+prosales*count+"\n", 0));
				ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
				ComA.send(PrintCmd.PrintString("找回(人民币):                      0.00\n\n", 0));
			}
			ComA.send(PrintCmd.PrintString("请您保留小票以便售后服务谢谢合作药品是特殊商品，非质量问题恕不退换\n", 0));			
			// 走纸4行,再切纸,清理缓存
			PrintFeedCutpaper(4);  
			SaveSharedPreferencesSerialno();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 打印二维码
	private void PrintQRCode() throws IOException {
		byte[] bValue = new byte[50];
		bValue = PrintCmd.PrintQrcode(erstr, 25, 7, 1);
		ComA.send(bValue);
	}
	
	// 走纸换行，再切纸，清理缓存
	private void PrintFeedCutpaper(int iLine) throws IOException{
		ComA.send(PrintCmd.PrintFeedline(iLine));
		ComA.send(PrintCmd.PrintCutpaper(0));
		ComA.send(PrintCmd.SetClean());
	}
	// 清理缓存，缺省模式
	private void CleanPrinter(){
		ComA.send(PrintCmd.SetClean());
	}
    
	// -------------------------刷新显示线程---------------------------
	private class DispQueueThread extends Thread {
		private Queue<ComBean> QueueList = new LinkedList<ComBean>();
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				final ComBean ComData;
				while ((ComData = QueueList.poll()) != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							DispRecData(ComData);
						}
					});
					try {
						Thread.sleep(200);// 显示性能高的话，可以把此数值调小。
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		public synchronized void AddQueue(ComBean ComData) {
			QueueList.add(ComData);
		}
	}
	
	// ------------------------显示接收数据----------------------------
	 /*
	  * 0 打印机正常 、1 打印机未连接或未上电、2 打印机和调用库不匹配 
	  * 3 打印头打开 、4 切刀未复位 、5 打印头过热 、6 黑标错误 、7 纸尽 、8 纸将尽
	  */
	private void DispRecData(ComBean ComRecData) {
		Message childmsg=printmainhand.obtainMessage();
		StringBuilder sMsg = new StringBuilder();
		try {
			sMsg.append(MyFunc.ByteArrToHex(ComRecData.bRec));
			int iState = PrintCmd.CheckStatus(ComRecData.bRec); // 检查状态
			ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo返回状态：" + iState + "======="
					+ ComRecData.bRec[0],"com.txt");
			switch (iState) {
			case 0:
				ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo>>正常","com.txt");
				sMsg.append("正常");                 // 正常
				ercheck = true;
				childmsg.what=PrintTest.NORMAL;
				break;
			case 1:
				ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo>>未连接或未上电","com.txt");
				sMsg.append("未连接或未上电");//未连接或未上电
				ercheck = true;
				childmsg.what=PrintTest.NOPOWER;
				break;
			case 2:
				ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo>>异常[打印机和调用库不匹配]","com.txt");
				sMsg.append("异常[打印机和调用库不匹配]");               //异常[打印机和调用库不匹配]
				ercheck = false;
				childmsg.what=PrintTest.NOMATCH;
				break;
			case 3:
				ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo>>打印机头打开","com.txt");
				sMsg.append("打印机头打开");        //打印机头打开
				ercheck = true;
				childmsg.what=PrintTest.HEADOPEN;
				break;
			case 4:
				ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo>>切刀未复位","com.txt");
				sMsg.append("切刀未复位");         //切刀未复位
				ercheck = true;
				childmsg.what=PrintTest.CUTTERERR;
				break;
			case 5:
				ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo>>打印头过热","com.txt");
				sMsg.append("打印头过热");    // 打印头过热
				ercheck = true;
				childmsg.what=PrintTest.HEADHEAT;
				break;
			case 6:
				ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo>>黑标错误","com.txt");
				sMsg.append("黑标错误");         // 黑标错误
				ercheck = true;
				childmsg.what=PrintTest.BLACKMARKERR;
				break;
			case 7:
				ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo>>纸尽","com.txt");
				sMsg.append("纸尽");               //纸尽
				ercheck = true;
				childmsg.what=PrintTest.PAPEREXH;
				break;
			case 8:
				ToolClass.Log(ToolClass.INFO,"EV_COM","bushuo>>纸将尽","com.txt");
				sMsg.append("纸将尽");           //纸将尽
				ercheck = true;
				childmsg.what=PrintTest.PAPERWILLEXH;
				break;
			default:
				break;
			}
			childmsg.obj=sMsg.toString();
		} catch (Exception ex) {
			childmsg.what=PrintTest.UNKNOWERR;
			childmsg.obj=ex.getMessage();
		}
		printmainhand.sendMessage(childmsg);
	}
    // -------------------------底层串口控制类---------------------------
    private static class SerialControl extends SerialHelper {
		public SerialControl() {
		}
		private static SerialControl single = null;
		// 静态工厂方法
		public static SerialControl getInstance() {
			if (single == null) {
				single = new SerialControl();
			}
			return single;
		}
		@Override
		protected void onDataReceived(final ComBean ComRecData) {
			DispQueue.AddQueue(ComRecData);// 线程定时刷新显示(推荐)
		}
	}
	
	@Override
	protected void onDestroy() {
		//=============
		//打印机相关
		//=============
		if(isPrinter>0)
		{
			CloseComPort(ComA);// 2.1 关闭串口
		}
		//=============
		//COM服务相关
		//=============
		//5.解除注册接收器
		comBroadreceiver.unregisterReceiver(comreceiver);	
		super.onDestroy();		
	}
}
