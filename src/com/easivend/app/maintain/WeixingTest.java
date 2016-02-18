package com.easivend.app.maintain;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.ToolClass;
import com.easivend.http.Weixinghttp;
import com.easivend.http.Zhifubaohttp;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WeixingTest extends Activity 
{
	private ImageView imgweixingtest=null;
	private TextView txtweixingtest=null;
	private EditText edtweixingtest=null;
	private Button btnweixingtestok=null,btnweixingtestcancel=null,btnweixingtestquery=null
			,btnweixingtestdelete=null,btnweixingtestpayout=null;
	private ProgressBar barweixingtest=null;
	
	private Handler mainhand=null,childhand=null;
	private String out_trade_no=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weixingtest);	
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		mainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				barweixingtest.setVisibility(View.GONE);
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Weixinghttp.SETMAIN://子线程接收主线程消息
						imgweixingtest.setImageBitmap(ToolClass.createQRImage(msg.obj.toString()));
						txtweixingtest.setText("微信交易结果:"+msg.obj.toString());
						break;
					case Weixinghttp.SETFAILNETCHILD://子线程接收主线程消息
						txtweixingtest.setText("微信交易结果:"+msg.obj.toString());
						break;	
					case Weixinghttp.SETPAYOUTMAIN://子线程接收主线程消息
						txtweixingtest.setText("微信交易结果:退款成功");
						break;
					case Weixinghttp.SETDELETEMAIN://子线程接收主线程消息
						txtweixingtest.setText("微信交易结果:撤销成功");
						break;	
					case Weixinghttp.SETQUERYMAINSUCC://子线程接收主线程消息	
						txtweixingtest.setText("微信交易结果:交易成功");
						break;		
					case Weixinghttp.SETFAILPROCHILD://子线程接收主线程消息
					case Weixinghttp.SETFAILBUSCHILD://子线程接收主线程消息	
					case Weixinghttp.SETFAILQUERYPROCHILD://子线程接收主线程消息
					case Weixinghttp.SETFAILQUERYBUSCHILD://子线程接收主线程消息		
					case Weixinghttp.SETQUERYMAIN://子线程接收主线程消息	
					case Weixinghttp.SETFAILPAYOUTPROCHILD://子线程接收主线程消息		
					case Weixinghttp.SETFAILPAYOUTBUSCHILD://子线程接收主线程消息
					case Weixinghttp.SETFAILDELETEPROCHILD://子线程接收主线程消息		
					case Weixinghttp.SETFAILDELETEBUSCHILD://子线程接收主线程消息	
						txtweixingtest.setText("微信交易结果:"+msg.obj.toString());
						break;		
				}				
			}
			
		};	
		//启动用户自己定义的类
		final Weixinghttp weixinghttp=new Weixinghttp(mainhand);
		ExecutorService thread=Executors.newFixedThreadPool(3);
		thread.execute(weixinghttp);	
		imgweixingtest = (ImageView) findViewById(R.id.imgweixingtest);
		edtweixingtest = (EditText) findViewById(R.id.edtweixingtest);
		txtweixingtest = (TextView) findViewById(R.id.txtweixingtest);
		barweixingtest= (ProgressBar) findViewById(R.id.barweixingtest);
		//获得id信息
		Intent intent=getIntent();
		final String id=intent.getStringExtra("id");
		Log.i("EV_JNI","Send0.0="+id);
		//发送订单
		btnweixingtestok = (Button)findViewById(R.id.btnweixingtestok);
		btnweixingtestok.setOnClickListener(new OnClickListener() {			
		    @Override
		    public void onClick(View arg0) {
		    	barweixingtest.setVisibility(View.VISIBLE);
		    	// 将信息发送到子线程中
		    	childhand=weixinghttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Weixinghttp.SETCHILD;
				JSONObject ev=null;
				try {
					ev=new JSONObject();
					SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //精确到毫秒 
			        String datetime = tempDate.format(new java.util.Date()).toString(); 					
			        out_trade_no=id+datetime;
			        ev.put("out_trade_no", out_trade_no);
					ev.put("total_fee", edtweixingtest.getText());
					Log.i("EV_JNI","Send0.1="+ev.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				childmsg.obj=ev;
				childhand.sendMessage(childmsg);
		    }
		});
		//查询
		btnweixingtestquery = (Button)findViewById(R.id.btnweixingtestquery);	
		btnweixingtestquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	barweixingtest.setVisibility(View.VISIBLE);
		    	// 将信息发送到子线程中
		    	childhand=weixinghttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Weixinghttp.SETQUERYCHILD;
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
		});
		//撤销交易
		btnweixingtestdelete = (Button)findViewById(R.id.btnweixingtestdelete);	
		btnweixingtestdelete.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	barweixingtest.setVisibility(View.VISIBLE);
		    	// 将信息发送到子线程中
		    	childhand=weixinghttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Weixinghttp.SETDELETECHILD;
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
		});
		//退款
		btnweixingtestpayout = (Button)findViewById(R.id.btnweixingtestpayout);	
		btnweixingtestpayout.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	barweixingtest.setVisibility(View.VISIBLE);
		    	// 将信息发送到子线程中
		    	childhand=weixinghttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Weixinghttp.SETPAYOUTCHILD;
				JSONObject ev=null;
				try {
					ev=new JSONObject();
					ev.put("out_trade_no", out_trade_no);		
					//ev.put("out_trade_no", "000120150301113215800");
					ev.put("total_fee", edtweixingtest.getText());
					ev.put("refund_fee", edtweixingtest.getText());
					SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //精确到毫秒 
			        String datetime = tempDate.format(new java.util.Date()).toString(); 					
			        ev.put("out_refund_no", id+datetime);
					Log.i("EV_JNI","Send0.1="+ev.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				childmsg.obj=ev;
				childhand.sendMessage(childmsg);
		    }
		});	
		//退出
		btnweixingtestcancel = (Button)findViewById(R.id.btnweixingtestcancel);		
		btnweixingtestcancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
	}
	
}
