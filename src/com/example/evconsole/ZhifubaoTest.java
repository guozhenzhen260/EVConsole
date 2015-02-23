package com.example.evconsole;

import com.easivend.evprotocol.ToolClass;
import com.easivend.http.PostZhifubaoInterface;
import com.easivend.http.Zhifubaohttp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ZhifubaoTest extends Activity 
{
	private ImageView imgzhifubaotest=null;
	private EditText edtzhifubaotest=null;
	private Button btnzhifubaotestok=null,btnzhifubaotestcancel=null;
	private final int SETMAIN=1;//what标记,主线程接收到子线程支付宝金额二维码
	private final int SETCHILD=2;//what标记,发送给子线程支付宝交易
	private Handler mainhand=null,childhand=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zhifubaotest);	
		mainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case SETMAIN://子线程接收主线程消息
						imgzhifubaotest.setImageBitmap(ToolClass.createQRImage(msg.obj.toString()));
						break;
				}				
			}
			
		};	
		//启动用户自己定义的类
		final Zhifubaohttp zhifubaohttp=new Zhifubaohttp(mainhand);
		Thread thread=new Thread(zhifubaohttp,"Zhifubaohttp Thread");
		thread.start();
		imgzhifubaotest = (ImageView) findViewById(R.id.imgzhifubaotest);
		edtzhifubaotest = (EditText) findViewById(R.id.edtzhifubaotest);
		
		btnzhifubaotestok = (Button)findViewById(R.id.btnzhifubaotestok);
		btnzhifubaotestok.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	// 将信息发送到子线程中
		    	childhand=zhifubaohttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=SETCHILD;
				childmsg.obj=mainhand.getLooper().getThread().getName()+"-->Hello World";
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
