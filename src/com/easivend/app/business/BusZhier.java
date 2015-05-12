package com.easivend.app.business;

import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class BusZhier extends Activity
{
	public static BusZhier BusZhierAct=null;
	TextView txtbuszhiercount=null,txtbuszhiamerount=null,txtbuszhierrst=null;
	ImageButton imgbtnbuszhierqxzf=null,imgbtnbuszhierqtzf=null;
	private String proID = null;
	private String productID = null;
	private String proType = null;
	private String cabID = null;
	private String huoID = null;
    private String prosales = null;
    private String count = null;
    private String reamin_amount = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buszhier);
		BusZhierAct = this;
		//从商品页面中取得锁选中的商品
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		proID=bundle.getString("proID");
		productID=bundle.getString("productID");
		proType=bundle.getString("proType");
		cabID=bundle.getString("cabID");
		huoID=bundle.getString("huoID");
		prosales=bundle.getString("prosales");
		count=bundle.getString("count");
		reamin_amount=bundle.getString("reamin_amount");
		float amount=Float.parseFloat(prosales)*Integer.parseInt(count);
		reamin_amount=bundle.getString("reamin_amount");
		txtbuszhiercount= (TextView) findViewById(R.id.txtbuszhiercount);
		txtbuszhiercount.setText(count);
		txtbuszhiamerount= (TextView) findViewById(R.id.txtbuszhiamerount);
		txtbuszhiamerount.setText(String.valueOf(amount));
		txtbuszhierrst= (TextView) findViewById(R.id.txtbuszhierrst);
		imgbtnbuszhierqxzf = (ImageButton) findViewById(R.id.imgbtnbuszhierqxzf);
		imgbtnbuszhierqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(BusZhiSelect.BusZhiSelectAct!=null)
		    		BusZhiSelect.BusZhiSelectAct.finish(); 
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finish();
		    }
		});
		imgbtnbuszhierqtzf = (ImageButton) findViewById(R.id.imgbtnbuszhierqtzf);
		imgbtnbuszhierqtzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
	}
	
}
