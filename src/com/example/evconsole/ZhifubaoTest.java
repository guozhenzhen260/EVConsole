package com.example.evconsole;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.evprotocol.ToolClass;
import com.easivend.http.Zhifubaohttp;

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
import android.widget.TextView;

public class ZhifubaoTest extends Activity 
{
	private ImageView imgzhifubaotest=null;
	private TextView txtzhifubaotest=null;
	private EditText edtzhifubaotest=null;
	private Button btnzhifubaotestok=null,btnzhifubaotestcancel=null,btnzhifubaotestquery=null
			,btnzhifubaotestdelete=null,btnzhifubaotestpayout=null;
	
	private Handler mainhand=null,childhand=null;
	private String out_trade_no=null;
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
					case Zhifubaohttp.SETMAIN://���߳̽������߳���Ϣ
						imgzhifubaotest.setImageBitmap(ToolClass.createQRImage(msg.obj.toString()));
						txtzhifubaotest.setText("���׽��:"+msg.obj.toString());
						break;
					case Zhifubaohttp.SETPAYOUTMAIN://���߳̽������߳���Ϣ
						txtzhifubaotest.setText("���׽��:�˿�ɹ�");
						break;
					case Zhifubaohttp.SETDELETEMAIN://���߳̽������߳���Ϣ
						txtzhifubaotest.setText("���׽��:�����ɹ�");
						break;	
					case Zhifubaohttp.SETFAILPROCHILD://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILBUSCHILD://���߳̽������߳���Ϣ	
					case Zhifubaohttp.SETFAILQUERYPROCHILD://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILQUERYBUSCHILD://���߳̽������߳���Ϣ		
					case Zhifubaohttp.SETQUERYMAIN://���߳̽������߳���Ϣ						
					case Zhifubaohttp.SETFAILPAYOUTPROCHILD://���߳̽������߳���Ϣ		
					case Zhifubaohttp.SETFAILPAYOUTBUSCHILD://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILDELETEPROCHILD://���߳̽������߳���Ϣ		
					case Zhifubaohttp.SETFAILDELETEBUSCHILD://���߳̽������߳���Ϣ	
						txtzhifubaotest.setText("���׽��:"+msg.obj.toString());
						break;	
				}				
			}
			
		};	
		//�����û��Լ��������
		final Zhifubaohttp zhifubaohttp=new Zhifubaohttp(mainhand);
		Thread thread=new Thread(zhifubaohttp,"Zhifubaohttp Thread");
		thread.start();
		imgzhifubaotest = (ImageView) findViewById(R.id.imgzhifubaotest);
		edtzhifubaotest = (EditText) findViewById(R.id.edtzhifubaotest);
		txtzhifubaotest = (TextView) findViewById(R.id.txtzhifubaotest);
		//���id��Ϣ
		Intent intent=getIntent();
		final String id=intent.getStringExtra("id");
		Log.i("EV_JNI","Send0.0="+id);
		btnzhifubaotestok = (Button)findViewById(R.id.btnzhifubaotestok);
		btnzhifubaotestok.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	// ����Ϣ���͵����߳���
		    	childhand=zhifubaohttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Zhifubaohttp.SETCHILD;
				JSONObject ev=null;
				try {
					ev=new JSONObject();
					SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddhhmmssSSS"); //��ȷ������ 
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
		//��ѯ
		btnzhifubaotestquery = (Button)findViewById(R.id.btnzhifubaotestquery);	
		btnzhifubaotestquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	// ����Ϣ���͵����߳���
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
		//��������
		btnzhifubaotestdelete = (Button)findViewById(R.id.btnzhifubaotestdelete);	
		btnzhifubaotestdelete.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	// ����Ϣ���͵����߳���
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
		//�˿�
		btnzhifubaotestpayout = (Button)findViewById(R.id.btnzhifubaotestpayout);	
		btnzhifubaotestpayout.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	// ����Ϣ���͵����߳���
		    	childhand=zhifubaohttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Zhifubaohttp.SETPAYOUTCHILD;
				JSONObject ev=null;
				try {
					ev=new JSONObject();
					ev.put("out_trade_no", out_trade_no);		
					//ev.put("out_trade_no", "000120150301113215800");
					ev.put("refund_amount", edtzhifubaotest.getText());
					SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddhhmmssSSS"); //��ȷ������ 
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
		//�˳�
		btnzhifubaotestcancel = (Button)findViewById(R.id.btnzhifubaotestcancel);		
		btnzhifubaotestcancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
	}

}