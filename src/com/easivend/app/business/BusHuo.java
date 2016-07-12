package com.easivend.app.business;

import java.util.List;
import java.util.Map;
import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.view.COMService;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
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
    private int zhifutype = 0;//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
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
		
  	    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
				+productID+" proType="
				+proType+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
				+count+" zhifutype="+zhifutype,"log.txt");		
  	    txtbushuoname.setText(proID+"["+prosales+"]"+"->等待出货");
		this.ivbushuoquhuo =(ImageView) super.findViewById(R.id.ivbushuoquhuo);
		
		
		//****
		//出货
		//****
		chuhuoopt(tempx);
	}
			
	//出货,返回值0失败,1出货指令成功，等待返回结果,2出货完成
	private void chuhuoopt(int huox)
	{
		
		// 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_column表中
 	    columnDAO = new vmc_columnDAO(this);
 	    txtbushuoname.setText(proID+"["+prosales+"]"+"->正在出货,请稍候...");
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
        		ToolClass.Log(ToolClass.INFO,"EV_JNI",
        		    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
        		    	+" column="+huodaoNo		    	
        		    	,"log.txt");
        		Intent intent = new Intent();
        		//4.发送指令广播给COMService
        		intent.putExtra("EVWhat", COMService.EV_CHUHUOCHILD);	
        		intent.putExtra("cabinet", cabinetvar);	
        		intent.putExtra("column", huodaoNo);	
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
	            	            BusHuo.this.setResult(BusZhiAmount.RESULT_CANCELED,intentrec);                    	            
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
	
					}, 3000);
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
	
	@Override
	protected void onDestroy() {
		//=============
		//COM服务相关
		//=============
		//5.解除注册接收器
		comBroadreceiver.unregisterReceiver(comreceiver);	
		super.onDestroy();		
	}
}
