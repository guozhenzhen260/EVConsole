package com.easivend.app.business;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.app.maintain.GoodsManager;
import com.easivend.app.maintain.ParamManager;
import com.easivend.common.OrderDetail;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.http.EVServerhttp;
import com.easivend.http.Zhifubaohttp;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;

public class BusZhier extends Activity
{
	private final int SPLASH_DISPLAY_LENGHT = 1500; // 延迟1.5秒
	//进度对话框
	ProgressDialog dialog= null;
	public static BusZhier BusZhierAct=null;
	private final static int REQUEST_CODE=1;//声明请求标识
	TextView txtbuszhiercount=null,txtbuszhiamerount=null,txtbuszhierrst=null,txtbuszhiertime=null;
	ImageButton imgbtnbuszhierqxzf=null,imgbtnbuszhierqtzf=null;
	ImageView ivbuszhier=null;
	private final int SPLASH_TIMEOUT_LENGHT = 5*60; //  5*60延迟5分钟
	private int recLen = SPLASH_TIMEOUT_LENGHT; 
	private int queryLen = 0; 
    private TextView txtView; 
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;
    private String zhifutype = "3";//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
    private float amount=0;
    //线程进行支付宝二维码操作
    private ExecutorService thread=null;
    private Handler mainhand=null,childhand=null;   
    private String out_trade_no=null;
    Zhifubaohttp zhifubaohttp=null;
    private int iszhier=0;//1成功生成了二维码,0没有成功生成二维码
    private boolean ercheck=false;//true正在二维码的线程操作中，请稍后。false没有二维码的线程操作
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buszhier);
		BusZhierAct = this;
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
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
		txtbuszhiercount= (TextView) findViewById(R.id.txtbuszhiercount);
		txtbuszhiercount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiamerount= (TextView) findViewById(R.id.txtbuszhiamerount);
		txtbuszhiamerount.setText(String.valueOf(amount));
		txtbuszhierrst= (TextView) findViewById(R.id.txtbuszhierrst);
		txtbuszhiertime= (TextView) findViewById(R.id.txtbuszhiertime);
		ivbuszhier= (ImageView) findViewById(R.id.ivbuszhier);
		timer.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);       // timeTask 
		imgbtnbuszhierqxzf = (ImageButton) findViewById(R.id.imgbtnbuszhierqxzf);
		imgbtnbuszhierqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	 
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();
		    }
		});
		imgbtnbuszhierqtzf = (ImageButton) findViewById(R.id.imgbtnbuszhierqtzf);
		imgbtnbuszhierqtzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finishActivity();
		    }
		});
		//***********************
		//线程进行支付宝二维码操作
		//***********************
		mainhand=new Handler()
		{
			int con=0;
			@Override
			public void handleMessage(Message msg) {
				//barzhifubaotest.setVisibility(View.GONE);
				ercheck=false;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Zhifubaohttp.SETMAIN://子线程接收主线程消息
						ivbuszhier.setImageBitmap(ToolClass.createQRImage(msg.obj.toString()));
						txtbuszhierrst.setText("交易结果:"+msg.obj.toString());
						iszhier=1;
						break;
					case Zhifubaohttp.SETFAILNETCHILD://子线程接收主线程消息
						txtbuszhierrst.setText("交易结果:重试"+msg.obj.toString()+con);
						con++;
						break;		
					case Zhifubaohttp.SETPAYOUTMAIN://子线程接收主线程消息
						txtbuszhierrst.setText("交易结果:退款成功");
						dialog.dismiss();
						finish();
						break;
					case Zhifubaohttp.SETDELETEMAIN://子线程接收主线程消息
						txtbuszhierrst.setText("交易结果:撤销成功");
						timer.shutdown(); 
						finish();
						break;	
					case Zhifubaohttp.SETQUERYMAINSUCC://交易成功
						txtbuszhierrst.setText("交易结果:交易成功");
						//reamin_amount=String.valueOf(amount);
						timer.shutdown(); 
						tochuhuo();
						break;
					case Zhifubaohttp.SETQUERYMAIN://子线程接收主线程消息
					case Zhifubaohttp.SETFAILPROCHILD://子线程接收主线程消息
					case Zhifubaohttp.SETFAILBUSCHILD://子线程接收主线程消息	
					case Zhifubaohttp.SETFAILQUERYPROCHILD://子线程接收主线程消息
					case Zhifubaohttp.SETFAILQUERYBUSCHILD://子线程接收主线程消息	
					case Zhifubaohttp.SETFAILPAYOUTPROCHILD://子线程接收主线程消息		
					case Zhifubaohttp.SETFAILPAYOUTBUSCHILD://子线程接收主线程消息
					case Zhifubaohttp.SETFAILDELETEPROCHILD://子线程接收主线程消息		
					case Zhifubaohttp.SETFAILDELETEBUSCHILD://子线程接收主线程消息	
						txtbuszhierrst.setText("交易结果:"+msg.obj.toString());
						break;	
				}				
			}
			
		};	
		//启动用户自己定义的类
		zhifubaohttp=new Zhifubaohttp(mainhand);
		thread=Executors.newFixedThreadPool(3);
		thread.execute(zhifubaohttp);	
		//延时
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {            	
        		//发送订单
        		sendzhier();
            }

		}, SPLASH_DISPLAY_LENGHT);		
	}
	
	//发送订单
	private void sendzhier()
	{	
		if(ercheckopt())
  		{
	    	// 将信息发送到子线程中
	    	childhand=zhifubaohttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Zhifubaohttp.SETCHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				out_trade_no=ToolClass.out_trade_no(BusZhier.this);// 创建InaccountDAO对象;
		        ev.put("out_trade_no", out_trade_no);
				ev.put("total_fee", String.valueOf(amount));
				Log.i("EV_JNI","Send0.1="+ev.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			childmsg.obj=ev;
			childhand.sendMessage(childmsg);
  		}
	}
	//查询交易
	private void queryzhier()
	{
		if(ercheckopt())
  		{
			// 将信息发送到子线程中
	    	childhand=zhifubaohttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Zhifubaohttp.SETQUERYCHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				ev.put("out_trade_no", out_trade_no);		
				//ev.put("out_trade_no", "000120150301113215800");	
				Log.i("EV_JNI","Send0.1="+ev.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			childmsg.obj=ev;
			childhand.sendMessage(childmsg);
  		}
	}
	//撤销交易
	private void deletezhier()
	{
		if(ercheckopt())
  		{
			// 将信息发送到子线程中
	    	childhand=zhifubaohttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Zhifubaohttp.SETDELETECHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				ev.put("out_trade_no", out_trade_no);		
				//ev.put("out_trade_no", "000120150301092857698");	
				Log.i("EV_JNI","Send0.1="+ev.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			childmsg.obj=ev;
			childhand.sendMessage(childmsg);
  		}
	}
	//退款
	private void payoutzhier()
	{
		if(ercheckopt())
  		{
			// 将信息发送到子线程中
	    	childhand=zhifubaohttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Zhifubaohttp.SETPAYOUTCHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				ev.put("out_trade_no", out_trade_no);		
				ev.put("refund_amount", String.valueOf(amount));
				ev.put("out_request_no", ToolClass.out_trade_no(BusZhier.this));
				Log.i("EV_JNI","Send0.1="+ev.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			childmsg.obj=ev;
			childhand.sendMessage(childmsg);
  		}
	}
	//调用倒计时定时器
	Runnable task = new Runnable() { 
         @Override 
        public void run() { 
            recLen--; 
            txtbuszhiertime.setText("倒计时:"+recLen); 
            //退出页面
            if(recLen <= 0)
            { 
                timer.shutdown(); 
                timeoutfinishActivity();
            } 
            
            
            //发送查询交易指令
            if(iszhier==1)
            {
                queryLen++;
                if(queryLen>=4)
                {
                	queryLen=0;
                	queryzhier();
                }
            }
            //发送订单交易指令
            else if(iszhier==0)
            {
                queryLen++;
                if(queryLen>=10)
                {
                	queryLen=0;
                	//发送订单
            		sendzhier();
                }
            }
        }             
    };
	//结束界面
	private void finishActivity()
	{
		//如果需要撤销，必须作撤销操作
		if(iszhier==1)
			deletezhier();
		else 
		{
			timer.shutdown(); 
			finish();
		}
	}
	//用于超时的结束界面
	private void timeoutfinishActivity()
	{
		//如果需要撤销，而且线程可以操作，才作撤销操作，否则直接退出页面
		if((iszhier==1)&&(ercheck==false))
			deletezhier();
		else 
		{
			timer.shutdown(); 
			finish();
		}
	}
	
	//跳到出货页面
	private void tochuhuo()
	{
		Intent intent = null;// 创建Intent对象                
    	intent = new Intent(BusZhier.this, BusHuo.class);// 使用Accountflag窗口初始化Intent
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
    	OrderDetail.setOrdereID(out_trade_no);
    	OrderDetail.setPayType(Integer.parseInt(zhifutype));
    	OrderDetail.setSmallCard(amount);
    	startActivityForResult(intent, REQUEST_CODE);// 打开Accountflag
	}
	//判断是否处在二维码的线程操作中,true表示可以操作了,false不能操作
  	private boolean ercheckopt()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		if(ercheck==false)
  		{
  			ercheck=true;
  			return true;
  		}
  		else
  		{
  			return false;
		}
  	}
	//接收BusHuo返回信息
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==REQUEST_CODE)
		{
			if(resultCode==BusZhier.RESULT_CANCELED)
			{
				Bundle bundle=data.getExtras();
  				int status=bundle.getInt("status");//出货结果1成功,0失败
  			    //1.
  				//出货成功,结束交易
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<无退款","log.txt");
					OrderDetail.addLog(BusZhier.this);					
					finish();
				}
				//出货失败,退钱
				else
				{	
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<退款amount="+amount,"log.txt");
					dialog= ProgressDialog.show(BusZhier.this,"正在退款中","请稍候...");
					payoutzhier();//退款操作
					OrderDetail.setRealStatus(1);//记录退币成功
					OrderDetail.setRealCard(amount);//记录退币金额
					OrderDetail.addLog(BusZhier.this);					
				}				
			}			
		}
	}
	
	
	
}
