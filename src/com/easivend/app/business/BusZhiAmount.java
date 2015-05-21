package com.easivend.app.business;

import java.text.SimpleDateFormat;

import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class BusZhiAmount  extends Activity
{
	private final int SPLASH_DISPLAY_LENGHT = 5000; // 延迟5秒
	public static BusZhiAmount BusZhiAmountAct=null;
	TextView txtbuszhiamountcount=null,txtbuszhiamountAmount=null,txtbuszhiamountbillAmount=null;
	ImageButton imgbtnbuszhiamountqxzf=null,imgbtnbuszhiamountqtzf=null;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
		float amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
		txtbuszhiamountcount= (TextView) findViewById(R.id.txtbuszhiamountcount);
		txtbuszhiamountcount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiamountAmount= (TextView) findViewById(R.id.txtbuszhiamountAmount);
		txtbuszhiamountAmount.setText(String.valueOf(amount));
		txtbuszhiamountbillAmount= (TextView) findViewById(R.id.txtbuszhiamountbillAmount);
		txtbuszhiamountbillAmount.setText(String.valueOf(OrderDetail.getSmallAmount()));
		imgbtnbuszhiamountqxzf = (ImageButton) findViewById(R.id.imgbtnbuszhiamountqxzf);
		imgbtnbuszhiamountqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(BusZhiSelect.BusZhiSelectAct!=null)
		    		BusZhiSelect.BusZhiSelectAct.finish(); 
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finish();
		    }
		});
		imgbtnbuszhiamountqtzf = (ImageButton) findViewById(R.id.imgbtnbuszhiamountqtzf);
		imgbtnbuszhiamountqtzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
		//5秒后进入出货页面
		new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {            						
    	        out_trade_no=ToolClass.out_trade_no(BusZhiAmount.this);
            	Intent intent = null;// 创建Intent对象                
            	intent = new Intent(BusZhiAmount.this, BusHuo.class);// 使用Accountflag窗口初始化Intent
//            	intent.putExtra("out_trade_no", out_trade_no);
//            	intent.putExtra("proID", proID);
//            	intent.putExtra("productID", productID);
//            	intent.putExtra("proType", proType);
//            	intent.putExtra("cabID", cabID);
//            	intent.putExtra("huoID", huoID);
//            	intent.putExtra("prosales", prosales);
//            	intent.putExtra("count", count);
//            	intent.putExtra("reamin_amount", reamin_amount);
//            	intent.putExtra("zhifutype", zhifutype);
            	OrderDetail.setOrdereID(out_trade_no);
            	OrderDetail.setPayType(Integer.parseInt(zhifutype));
            	startActivity(intent);// 打开Accountflag
            }

		}, SPLASH_DISPLAY_LENGHT);
	}
	
}
