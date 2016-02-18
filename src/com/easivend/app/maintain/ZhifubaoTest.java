package com.easivend.app.maintain;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.ToolClass;
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

public class ZhifubaoTest extends Activity 
{
	private ImageView imgzhifubaotest=null;
	private TextView txtzhifubaotest=null;
	private EditText edtzhifubaotest=null;
	private Button btnzhifubaotestok=null,btnzhifubaotestcancel=null,btnzhifubaotestquery=null
			,btnzhifubaotestdelete=null,btnzhifubaotestpayout=null;
	private ProgressBar barzhifubaotest=null;
	
	private Handler mainhand=null,childhand=null;
	private String out_trade_no=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zhifubaotest);	
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		mainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				barzhifubaotest.setVisibility(View.GONE);
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Zhifubaohttp.SETMAIN://子线程接收主线程消息
						imgzhifubaotest.setImageBitmap(ToolClass.createQRImage(msg.obj.toString()));
						txtzhifubaotest.setText("支付宝交易结果:"+msg.obj.toString());
						break;
					case Zhifubaohttp.SETFAILNETCHILD://子线程接收主线程消息
						txtzhifubaotest.setText("支付宝交易结果:"+msg.obj.toString());
						break;	
					case Zhifubaohttp.SETPAYOUTMAIN://子线程接收主线程消息
						txtzhifubaotest.setText("支付宝交易结果:退款成功");
						break;
					case Zhifubaohttp.SETDELETEMAIN://子线程接收主线程消息
						txtzhifubaotest.setText("支付宝交易结果:撤销成功");
						break;
					case Zhifubaohttp.SETQUERYMAINSUCC://交易成功
						txtzhifubaotest.setText("支付宝交易结果:交易成功");
						break;	
					case Zhifubaohttp.SETFAILPROCHILD://子线程接收主线程消息
					case Zhifubaohttp.SETFAILBUSCHILD://子线程接收主线程消息	
					case Zhifubaohttp.SETFAILQUERYPROCHILD://子线程接收主线程消息
					case Zhifubaohttp.SETFAILQUERYBUSCHILD://子线程接收主线程消息		
					case Zhifubaohttp.SETQUERYMAIN://子线程接收主线程消息						
					case Zhifubaohttp.SETFAILPAYOUTPROCHILD://子线程接收主线程消息		
					case Zhifubaohttp.SETFAILPAYOUTBUSCHILD://子线程接收主线程消息
					case Zhifubaohttp.SETFAILDELETEPROCHILD://子线程接收主线程消息		
					case Zhifubaohttp.SETFAILDELETEBUSCHILD://子线程接收主线程消息	
						txtzhifubaotest.setText("支付宝交易结果:"+msg.obj.toString());
						break;	
				}				
			}
			
		};	
		//启动用户自己定义的类
		final Zhifubaohttp zhifubaohttp=new Zhifubaohttp(mainhand);
		ExecutorService thread=Executors.newFixedThreadPool(3);
		thread.execute(zhifubaohttp);	
		imgzhifubaotest = (ImageView) findViewById(R.id.imgzhifubaotest);
		edtzhifubaotest = (EditText) findViewById(R.id.edtzhifubaotest);
		txtzhifubaotest = (TextView) findViewById(R.id.txtzhifubaotest);
		barzhifubaotest= (ProgressBar) findViewById(R.id.barzhifubaotest);
		//获得id信息
		Intent intent=getIntent();
		final String id=intent.getStringExtra("id");
		Log.i("EV_JNI","Send0.0="+id);
		//发送订单
		btnzhifubaotestok = (Button)findViewById(R.id.btnzhifubaotestok);
		btnzhifubaotestok.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	barzhifubaotest.setVisibility(View.VISIBLE);
		    	// 将信息发送到子线程中
		    	childhand=zhifubaohttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Zhifubaohttp.SETCHILD;
				JSONObject ev=null;
				try {
					ev=new JSONObject();
					SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //精确到毫秒 
			        String datetime = tempDate.format(new java.util.Date()).toString(); 					
			        out_trade_no=id+datetime;
			        ev.put("out_trade_no", out_trade_no);
					ev.put("total_fee", edtzhifubaotest.getText());
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
		btnzhifubaotestquery = (Button)findViewById(R.id.btnzhifubaotestquery);	
		btnzhifubaotestquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	barzhifubaotest.setVisibility(View.VISIBLE);
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
		});
		//撤销交易
		btnzhifubaotestdelete = (Button)findViewById(R.id.btnzhifubaotestdelete);	
		btnzhifubaotestdelete.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	barzhifubaotest.setVisibility(View.VISIBLE);
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
		});
		//退款
		btnzhifubaotestpayout = (Button)findViewById(R.id.btnzhifubaotestpayout);	
		btnzhifubaotestpayout.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	barzhifubaotest.setVisibility(View.VISIBLE);
		    	// 将信息发送到子线程中
		    	childhand=zhifubaohttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Zhifubaohttp.SETPAYOUTCHILD;
				JSONObject ev=null;
				try {
					ev=new JSONObject();
					ev.put("out_trade_no", out_trade_no);		
					//ev.put("out_trade_no", "000120150301113215800");
					ev.put("refund_amount", edtzhifubaotest.getText());
					SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //精确到毫秒 
			        String datetime = tempDate.format(new java.util.Date()).toString(); 					
			        ev.put("out_request_no", id+datetime);
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
		btnzhifubaotestcancel = (Button)findViewById(R.id.btnzhifubaotestcancel);		
		btnzhifubaotestcancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	finish();
		    }
		});
	}

}
