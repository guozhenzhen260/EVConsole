package com.example.evconsole;

import com.easivend.evprotocol.ToolClass;
import com.easivend.http.Zhifubaohttp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class WeixingTest extends Activity 
{
	private ImageView imgweixingtest=null;
	private EditText edtweixingtest=null;
	private Button btnweixingtestok=null,btnweixingtestcancel=null;
	private final int SETWEIMAIN=3;//what标记,主线程接收到子线程微信金额二维码
	private final int SETWEICHILD=4;//what标记,发送给子线程微信交易
	private Handler mainhand=null,childhand=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weixingtest);	
		mainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case SETWEIMAIN://子线程接收主线程消息
						imgweixingtest.setImageBitmap(ToolClass.createQRImage(msg.obj.toString()));
						break;
				}				
			}
			
		};	
		//启动用户自己定义的类
		final Zhifubaohttp zhifubaohttp=new Zhifubaohttp(mainhand);
		Thread thread=new Thread(zhifubaohttp,"Zhifubaohttp Thread");
		thread.start();
		imgweixingtest = (ImageView) findViewById(R.id.imgweixingtest);
		edtweixingtest = (EditText) findViewById(R.id.edtweixingtest);
		
		btnweixingtestok = (Button)findViewById(R.id.btnweixingtestok);
		btnweixingtestok.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	// 将信息发送到子线程中
		    	childhand=zhifubaohttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=SETWEICHILD;
				childmsg.obj=mainhand.getLooper().getThread().getName()+"-->Hello World";
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
