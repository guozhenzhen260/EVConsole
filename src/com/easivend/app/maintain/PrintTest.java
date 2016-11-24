package com.easivend.app.maintain;

import com.easivend.common.ToolClass;
import com.example.evconsole.R;
import com.example.printdemo.MyFunc;
import com.example.printdemo.SerialHelper;
import android.app.Activity;
import android.os.Bundle;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import com.bean.ComBean;
import com.printsdk.cmd.PrintCmd;

import android.os.Handler;
import android.os.Message;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class PrintTest extends Activity {
	public final static int NORMAL=1;//正常
	public final static int NOPOWER=2;//未连接或未上电
	public final static int NOMATCH=3;//异常[打印机和调用库不匹配]
	public final static int HEADOPEN=4;//打印机头打开
	public final static int CUTTERERR=5;//切刀未复位
	public final static int HEADHEAT=6;//打印头过热
	public final static int BLACKMARKERR=7;//黑标错误
	public final static int PAPEREXH=8;//纸尽
	public final static int PAPERWILLEXH=9;//纸将尽
	public final static int UNKNOWERR=10;//其他异常
	TextView txtMsg=null;
	CheckBox chktitle1=null,chktitle2=null,chkno=null,chksum=null,
			chkthank=null,chker=null,chkdate=null,chkdocter=null;
	EditText edittitle1=null,edittitle2=null,editthank=null,editer=null;
	Button btnopen=null,btnquery=null,btnprint=null,btnclose=null,btnsave=null,btnexit=null;
	boolean istitle1,istitle2,isno,issum,isthank,iser,isdate,isdocter;
	int serialno=0;
	String title1str,title2str,thankstr,erstr;
	SerialControl ComA;                  // 串口控制
	static DispQueueThread DispQueue;    // 刷新显示线程
	private boolean ercheck=false;//true正在打印机的操作中，请稍后。false没有打印机的操作
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // 国际化标志时间格式类
	SimpleDateFormat sdfdoc = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"); // 药房小票用
	float amount=0;
	private Handler printmainhand=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.printtest);
		printmainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what) 
				{
					case NORMAL:
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机正常","com.txt");
						txtMsg.setText(msg.obj.toString());
						break;
					case NOPOWER:	
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机未连接或未上电","com.txt");
						txtMsg.setText(msg.obj.toString());
						break;
					case NOMATCH:
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机异常[打印机和调用库不匹配]","com.txt");
						txtMsg.setText(msg.obj.toString());
						break;
					case HEADOPEN:	
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机打印机头打开","com.txt");
						txtMsg.setText(msg.obj.toString());
						break;
					case CUTTERERR:
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机切刀未复位","com.txt");
						txtMsg.setText(msg.obj.toString());
						break;
					case HEADHEAT:	
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机头过热","com.txt");
						txtMsg.setText(msg.obj.toString());
						break;
					case BLACKMARKERR:
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机黑标错误","com.txt");
						txtMsg.setText(msg.obj.toString());
						break;
					case PAPEREXH:	
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机纸尽","com.txt");
						txtMsg.setText(msg.obj.toString());
						break;
					case PAPERWILLEXH://这个也可以当正常状态使用
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机纸将尽","com.txt");
						txtMsg.setText(msg.obj.toString());
						break;	
					case UNKNOWERR:
						ToolClass.Log(ToolClass.INFO,"EV_COM","print打印机其他异常="+msg.obj,"com.txt");
						txtMsg.setText(msg.obj.toString());
						break;
				}
			}
		};
        ComA = SerialControl.getInstance();
        ComA.setbLoopData(PrintCmd.GetStatus());
        DispQueue = new DispQueueThread();
		DispQueue.start();
        
        edittitle1 = (EditText) findViewById(R.id.edittitle1);        
        edittitle2 = (EditText) findViewById(R.id.edittitle2);
        editthank = (EditText) findViewById(R.id.editthank);
        editer = (EditText) findViewById(R.id.editer);
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        chktitle1 = (CheckBox) findViewById(R.id.chktitle1);
        chktitle1.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				edittitle1.setEnabled(isChecked);
				istitle1=isChecked;
				if(isChecked==false)
				{
					edittitle1.setText("");
				}
				else
				{
					edittitle1.setText(title1str);
				}	
			}
		});
        chktitle2 = (CheckBox) findViewById(R.id.chktitle2);
        chktitle2.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				edittitle2.setEnabled(isChecked);
				istitle2=isChecked;
				if(isChecked==false)
				{
					edittitle2.setText("");
				}
				else
				{
					edittitle2.setText(title2str);
				}
			}
		});
        chkno = (CheckBox) findViewById(R.id.chkno);
        chkno.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				isno=isChecked;
			}
		});
        chksum = (CheckBox) findViewById(R.id.chksum);
        chksum.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				issum=isChecked;
			}
		});
        chkthank = (CheckBox) findViewById(R.id.chkthank);
        chkthank.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				editthank.setEnabled(isChecked);
				isthank=isChecked;
				if(isChecked==false)
				{
					editthank.setText("");
				}
				else
				{
					editthank.setText(thankstr);
				}
			}
		});
        chker = (CheckBox) findViewById(R.id.chker);
        chker.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				editer.setEnabled(isChecked);
				iser=isChecked;
				if(isChecked==false)
				{
					editer.setText("");
				}
				else
				{
					editer.setText(erstr);
				}
			}
		});
        chkdate = (CheckBox) findViewById(R.id.chkdate);
        chkdate.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				isdate=isChecked;
			}
		});
        chkdocter = (CheckBox) findViewById(R.id.chkdocter);
        chkdocter.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				isdocter=isChecked;
			}
		});
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        ReadSharedPreferencesPrinter();
        
        //打开
        btnopen = (Button)findViewById(R.id.btnopen);		
        btnopen.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	ComA.setPort(ToolClass.getPrintcom());            // 1.1 设定串口
				ComA.setBaudRate("9600");// 1.2 设定波特率
				OpenComPort(ComA); // 1.3 打开串口
		    }
		});
        // 查询状态
        btnquery = (Button)findViewById(R.id.btnquery);		
        btnquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	GetPrinterStates(ComA, PrintCmd.GetStatus()); 
		    }
		});
        //保存
        btnsave = (Button)findViewById(R.id.btnsave);		
        btnsave.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	SaveSharedPreferencesPrinter();
		    }
		});
        //打印
        btnprint = (Button)findViewById(R.id.btnprint);		
        btnprint.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {	
		    	if(isdocter)
		    	{
		    		PrintDocter();                             // 打印小票
		    	}
		    	else
		    	{
		    		PrintBankQueue();                             // 打印小票
		    	}
		    }
		});
        //关闭
        btnclose = (Button)findViewById(R.id.btnclose);		
        btnclose.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	CloseComPort(ComA);// 2.1 关闭串口
		    }
		});
        //退出
        btnexit = (Button)findViewById(R.id.btnexit);		
        btnexit.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	CloseComPort(ComA);// 2.1 关闭串口
		    	finish();
		    }
		});
	}
	
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
		chktitle1.setChecked(istitle1);
		edittitle1.setEnabled(istitle1);
		edittitle1.setText(title1str);
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
		chktitle2.setChecked(istitle2);
		edittitle2.setEnabled(istitle2);
		edittitle2.setText(title2str);
		//交易序号
		isno=user.getBoolean("isno",true);
		serialno=user.getInt("serialno", 1);
		chkno.setChecked(isno);		
		//金额统计
		issum=user.getBoolean("issum",true);
		chksum.setChecked(issum);
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
		chkthank.setChecked(isthank);
		editthank.setEnabled(isthank);
		editthank.setText(thankstr);
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
		chker.setChecked(iser);
		editer.setEnabled(iser);
		editer.setText(erstr);
		//当前时间
		isdate=user.getBoolean("isdate",true);
		chkdate.setChecked(isdate);
		//药房小票
		isdocter=user.getBoolean("isdocter",true);
		chkdocter.setChecked(isdocter);
    }
    //写入打印信息
    private void SaveSharedPreferencesPrinter()
    {
    	//文件是私有的
		SharedPreferences  user = getSharedPreferences("print_info",0);
		//需要接口进行编辑
		SharedPreferences.Editor edit=user.edit();
		//写入
		//标题一
		istitle1=chktitle1.isChecked();
		title1str=edittitle1.getText().toString();
		edit.putBoolean("istitle1", istitle1);
		edit.putString("title1str", title1str);
		//标题二
		istitle2=chktitle2.isChecked();
		title2str=edittitle2.getText().toString();
		edit.putBoolean("istitle2", istitle2);
		edit.putString("title2str", title2str);
		//交易序号
		isno=chkno.isChecked();
		edit.putBoolean("isno", isno);
		edit.putInt("serialno", serialno);
		//金额统计
		issum=chksum.isChecked();
		edit.putBoolean("issum", issum);
		//感谢提示
		isthank=chkthank.isChecked();
		thankstr=editthank.getText().toString();
		edit.putBoolean("isthank", isthank);
		edit.putString("thankstr", thankstr);
		//二维码
		iser=chker.isChecked();
		erstr=editer.getText().toString();
		edit.putBoolean("iser", iser);
		edit.putString("erstr", erstr);
		//当前时间
		isdate=chkdate.isChecked();
		edit.putBoolean("isdate", isdate);
		//药房小票
		isdocter=chkdocter.isChecked();
		edit.putBoolean("isdocter", isdocter);
		//提交更新
		edit.commit();
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
  		Message childmsg=printmainhand.obtainMessage();
  		try { 			
 			if (ComPort != null && ComPort.isOpen()) {
 				ComPort.send(sOut);
 				ercheck = true; 				
 				ToolClass.Log(ToolClass.INFO,"EV_COM","状态查询中","com.txt");
 			} else
 			{
 				ToolClass.Log(ToolClass.ERROR,"EV_COM","串口未打开","com.txt");
 				childmsg.what=UNKNOWERR;
 				childmsg.obj="串口未打开";
 				printmainhand.sendMessage(childmsg);
 			}
 		} catch (Exception ex) {
 			ToolClass.Log(ToolClass.ERROR,"EV_COM",ex.getMessage(),"com.txt");
 			childmsg.what=UNKNOWERR;
			childmsg.obj=ex.getMessage();
			printmainhand.sendMessage(childmsg);
 		}
  		
 	}
 	/**
	 * 打印销售单据
	 */
  	private void PrintBankQueue() {
		try {
			// 小票标题
			byte[] bValue = new byte[100];
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
			ComA.send(PrintCmd.PrintString("测试商品  单价1.00元\n", 0));
			ComA.send(PrintCmd.PrintString("_________________________________________\n\n", 0));
			//金额统计
			if(issum)
			{
				ComA.send(PrintCmd.PrintString("总计:1.00元\n", 0));
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
			ComA.send(PrintCmd.PrintString("160705  化痰祛斑胶囊0.32g*10粒*3板  57*4  228\n", 0));
			ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
			ComA.send(PrintCmd.PrintString("合计(人民币):                           228.00\n", 0));
			ComA.send(PrintCmd.PrintString("应收款:                                 228.00\n", 0));
			ComA.send(PrintCmd.PrintString("实收款:                                 228.00\n", 0));
			ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
			ComA.send(PrintCmd.PrintString("其中:                                        \n", 0));
			ComA.send(PrintCmd.PrintString(" 银联卡:                                228.00\n", 0));
			ComA.send(PrintCmd.PrintString("____________________________________________\n", 0));
			ComA.send(PrintCmd.PrintString("找回(人民币):                            0.00\n\n", 0));
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
			ToolClass.Log(ToolClass.INFO,"EV_COM","print返回状态：" + iState + "======="
					+ ComRecData.bRec[0],"com.txt");
			switch (iState) {
			case 0:
				ToolClass.Log(ToolClass.INFO,"EV_COM","print>>正常","com.txt");
				sMsg.append("正常");                 // 正常
				ercheck = true;
				childmsg.what=NORMAL;
				break;
			case 1:
				ToolClass.Log(ToolClass.INFO,"EV_COM","print>>未连接或未上电","com.txt");
				sMsg.append("未连接或未上电");//未连接或未上电
				ercheck = true;
				childmsg.what=NOPOWER;
				break;
			case 2:
				ToolClass.Log(ToolClass.INFO,"EV_COM","print>>异常[打印机和调用库不匹配]","com.txt");
				sMsg.append("异常[打印机和调用库不匹配]");               //异常[打印机和调用库不匹配]
				ercheck = false;
				childmsg.what=NOMATCH;
				break;
			case 3:
				ToolClass.Log(ToolClass.INFO,"EV_COM","print>>打印机头打开","com.txt");
				sMsg.append("打印机头打开");        //打印机头打开
				ercheck = true;
				childmsg.what=HEADOPEN;
				break;
			case 4:
				ToolClass.Log(ToolClass.INFO,"EV_COM","print>>切刀未复位","com.txt");
				sMsg.append("切刀未复位");         //切刀未复位
				ercheck = true;
				childmsg.what=CUTTERERR;
				break;
			case 5:
				ToolClass.Log(ToolClass.INFO,"EV_COM","print>>打印头过热","com.txt");
				sMsg.append("打印头过热");    // 打印头过热
				ercheck = true;
				childmsg.what=HEADHEAT;
				break;
			case 6:
				ToolClass.Log(ToolClass.INFO,"EV_COM","print>>黑标错误","com.txt");
				sMsg.append("黑标错误");         // 黑标错误
				ercheck = true;
				childmsg.what=BLACKMARKERR;
				break;
			case 7:
				ToolClass.Log(ToolClass.INFO,"EV_COM","print>>纸尽","com.txt");
				sMsg.append("纸尽");               //纸尽
				ercheck = true;
				childmsg.what=PAPEREXH;
				break;
			case 8:
				ToolClass.Log(ToolClass.INFO,"EV_COM","print>>纸将尽","com.txt");
				sMsg.append("纸将尽");           //纸将尽
				ercheck = true;
				childmsg.what=PAPERWILLEXH;
				break;
			default:
				break;
			}
			childmsg.obj=sMsg.toString();
		} catch (Exception ex) {
			childmsg.what=UNKNOWERR;
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

}
