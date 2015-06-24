package com.easivend.app.business;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.http.Zhifubaohttp;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class BusZhiAmount  extends Activity
{
	private final int SPLASH_DISPLAY_LENGHT = 1000; // 延迟1秒
	public static BusZhiAmount BusZhiAmountAct=null;
	private final static int REQUEST_CODE=1;//声明请求标识
	TextView txtbuszhiamountcount=null,txtbuszhiamountAmount=null,txtbuszhiamountbillAmount=null,txtbuszhiamounttime=null,
			txtbuszhiamounttsxx=null;
	ImageButton imgbtnbuszhiamountqxzf=null,imgbtnbuszhiamountqtzf=null;
	float amount=0;//商品需要支付金额
	float billmoney=0,coinmoney=0,money=0;//投币金额
	float RealNote=0,RealCoin=0,RealAmount=0;//退币金额
	private int recLen = 180; 
	private int queryLen = 0; 
    private TextView txtView; 
    Timer timer = new Timer();
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;
    private String zhifutype = "0";//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
//    private String id="";
    private String out_trade_no=null;
    private int iszhiamount=0;//1成功投入钱,0没有成功投入钱
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buszhiamount);
		BusZhiAmountAct = this;
		//从商品页面中取得锁选中的商品
//		Intent intent=getIntent();
//		Bundle bundle=intent.getExtras();
//		proID=bundle.getString("proID");
//		productID=bundle.getString("productID");
//		proType=bundle.getString("proType");
//		cabID=bundle.getString("cabID");
//		huoID=bundle.getString("huoID");
//		prosales=bundle.getString("prosales");
//		count=bundle.getString("count");
//		reamin_amount=bundle.getString("reamin_amount");
		out_trade_no=ToolClass.out_trade_no(BusZhiAmount.this);
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
		OrderDetail.setOrdereID(out_trade_no);
    	OrderDetail.setPayType(Integer.parseInt(zhifutype));
		txtbuszhiamountcount= (TextView) findViewById(R.id.txtbuszhiamountcount);
		txtbuszhiamountcount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiamountAmount= (TextView) findViewById(R.id.txtbuszhiamountAmount);
		txtbuszhiamountAmount.setText(String.valueOf(amount));
		txtbuszhiamountbillAmount= (TextView) findViewById(R.id.txtbuszhiamountbillAmount);		
		txtbuszhiamounttime = (TextView) findViewById(R.id.txtbuszhiamounttime);
		txtbuszhiamounttsxx = (TextView) findViewById(R.id.txtbuszhiamounttsxx);
		timer.schedule(task, 1000, 1000);       // timeTask 
		imgbtnbuszhiamountqxzf = (ImageButton) findViewById(R.id.imgbtnbuszhiamountqxzf);
		imgbtnbuszhiamountqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();
		    }
		});
		imgbtnbuszhiamountqtzf = (ImageButton) findViewById(R.id.imgbtnbuszhiamountqtzf);
		imgbtnbuszhiamountqtzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finishActivity();
		    }
		});
		//*****************
		//注册投币找零监听器
		//*****************
		EVprotocolAPI.setCallBack(new jniInterfaceImp());

		//延时1s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {            	
        		//打开纸币硬币器
            	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,1);
            }

		}, SPLASH_DISPLAY_LENGHT);
	}
	
	//创建一个专门处理单击接口的子类
	private class jniInterfaceImp implements JNIInterface
	{

		@Override
		public void jniCallback(Map<String, Object> allSet) {
			float payin_amount=0,reamin_amount=0,payout_amount=0;
			// TODO Auto-generated method stub	
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<mdb设备结果","log.txt");
			Map<String, Object> Set= allSet;
			int jnirst=(Integer)Set.get("EV_TYPE");
			switch (jnirst)
			{
				case EVprotocolAPI.EV_MDB_ENABLE://接收子线程投币金额消息							
					break;
				case EVprotocolAPI.EV_MDB_B_INFO:	
					break;
				case EVprotocolAPI.EV_MDB_C_INFO:
					break;	
				case EVprotocolAPI.EV_MDB_HEART://心跳查询
					String bill_enable=((Integer)Set.get("bill_enable")==1)?"":"纸币器不可用";
					String coin_enable=((Integer)Set.get("coin_enable")==1)?"":"硬币器不可用";
					txtbuszhiamounttsxx.setText("提示信息："+bill_enable+coin_enable);
				  	billmoney=ToolClass.MoneyRec((Integer)Set.get("bill_recv"));	
				  	coinmoney=ToolClass.MoneyRec((Integer)Set.get("coin_recv"));
				  	money=billmoney+coinmoney;
				  	if(money>0)
				  	{
				  		iszhiamount=1;
				  		txtbuszhiamountbillAmount.setText(String.valueOf(money));
				  		OrderDetail.setSmallNote(billmoney);
				  		OrderDetail.setSmallConi(coinmoney);
				  		OrderDetail.setSmallAmount(money);
				  		if(money>=amount)
				  		{
				  			timer.cancel(); 
				  			tochuhuo();
				  		}
				  	}
					break;
				case EVprotocolAPI.EV_MDB_PAYOUT://找零
					break;
				case EVprotocolAPI.EV_MDB_PAYBACK://退币
					RealNote=ToolClass.MoneyRec((Integer)Set.get("bill_changed"));	
					RealCoin=ToolClass.MoneyRec((Integer)Set.get("coin_changed"));	
					RealAmount=RealNote+RealCoin;						
					OrderDetail.setRealNote(RealNote);
			    	OrderDetail.setRealCoin(RealCoin);
			    	OrderDetail.setRealAmount(RealAmount);
			    	//退币完成
			    	if(RealAmount==money)
			    	{
			    		OrderDetail.setRealStatus(1);//退款完成				    		
			    	}
			    	//欠款
			    	else
			    	{
			    		OrderDetail.setRealStatus(3);//退款失败
			    		OrderDetail.setDebtAmount(money-RealAmount);//欠款金额
			    	}
			    	OrderDetail.addLog(BusZhiAmount.this);
					//关闭纸币硬币器
		  	    	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);			  	    	
		  	    	finish();
					break; 	
			}				
		}
		
	}
	
	//调用倒计时定时器
	TimerTask task = new TimerTask() { 
        @Override 
        public void run() { 
  
            runOnUiThread(new Runnable() {      // UI thread 
                @Override 
                public void run() { 
                    recLen--; 
                    txtbuszhiamounttime.setText("倒计时:"+recLen); 
                    if(recLen <= 0)
                    { 
                        timer.cancel(); 
                        finishActivity();
                    } 
                    //发送查询交易指令
                    queryLen++;
                    if(queryLen>=2)
                    {
                    	queryLen=0;
                    	EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
                    }                    
                } 
            }); 
        } 
    };
    //出货界面
    private void tochuhuo()
    {        
    	Intent intent = null;// 创建Intent对象                
    	intent = new Intent(BusZhiAmount.this, BusHuo.class);// 使用Accountflag窗口初始化Intent
//    	intent.putExtra("out_trade_no", out_trade_no);
//    	intent.putExtra("proID", proID);
//    	intent.putExtra("productID", productID);
//    	intent.putExtra("proType", proType);
//    	intent.putExtra("cabID", cabID);
//    	intent.putExtra("huoID", huoID);
//    	intent.putExtra("prosales", prosales);
//    	intent.putExtra("count", count);
//    	intent.putExtra("reamin_amount", reamin_amount);
//    	intent.putExtra("zhifutype", zhifutype);    	
    	startActivityForResult(intent, REQUEST_CODE);// 打开Accountflag
    }
    //接收BusHuo返回信息
  	@Override
  	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  		// TODO Auto-generated method stub
  		if(requestCode==REQUEST_CODE)
  		{
  			if(resultCode==BusZhiAmount.RESULT_CANCELED)
  			{
  				Bundle bundle=data.getExtras();
  				int status=bundle.getInt("status");//出货结果1成功,0失败
  				//1.
  				//出货成功,扣钱
				if(status==1)
				{
					//扣钱
		  	    	EVprotocolAPI.EV_mdbCost(ToolClass.getCom_id(),ToolClass.MoneySend(amount));
		  	    	money-=amount;
				}
				//出货失败,不扣钱
				else
				{					
				}
				//2.更新投币金额
  				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<退款money="+money,"log.txt");
  				txtbuszhiamountbillAmount.setText(String.valueOf(money));
  				//没剩下余额了，不退币
  				if(money==0)
  				{  					
			    	OrderDetail.addLog(BusZhiAmount.this);
					//关闭纸币硬币器
		  	    	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);			  	    	
		  	    	finish();
  				}
  				//退币
  				else 
  				{
  					EVprotocolAPI.setCallBack(new jniInterfaceImp());
  					//退币
  	  	  	    	EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
				}  				
  			}			
  		}
  	}
    //结束界面
  	private void finishActivity()
  	{
  		timer.cancel(); 
  		if(iszhiamount==1)
  		{
  			OrderDetail.setPayStatus(2);//支付失败
  			//退币
  	    	EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
  		} 
  		else 
  		{
  			//关闭纸币硬币器
  	    	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);  	    	
  			finish();
		}  		
  	}
}


