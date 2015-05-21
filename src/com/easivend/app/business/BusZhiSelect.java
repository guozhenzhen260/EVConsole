package com.easivend.app.business;

import com.easivend.app.maintain.ParamManager;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class BusZhiSelect extends Activity
{
	public static BusZhiSelect BusZhiSelectAct=null;
	ImageView ivbuszhiselamount=null,ivbuszhiselzhier=null,ivbuszhiselweixing=null;
	ImageButton imgbtnbuszhiselfanhui=null;
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buszhiselect);
		BusZhiSelectAct = this;
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
		ivbuszhiselamount = (ImageView) findViewById(R.id.ivbuszhiselamount);
		ivbuszhiselamount.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = null;// 创建Intent对象                
            	intent = new Intent(BusZhiSelect.this, BusZhiAmount.class);// 使用Accountflag窗口初始化Intent
//            	intent.putExtra("proID", proID);
//            	intent.putExtra("productID", productID);
//            	intent.putExtra("proType", proType);
//            	intent.putExtra("cabID", cabID);
//            	intent.putExtra("huoID", huoID);
//            	intent.putExtra("prosales", prosales);
//            	intent.putExtra("count", count);
//            	intent.putExtra("reamin_amount", reamin_amount);
            	startActivity(intent);// 打开Accountflag
		    }
		});
		ivbuszhiselzhier = (ImageView) findViewById(R.id.ivbuszhiselzhier);
		ivbuszhiselzhier.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = null;// 创建Intent对象                
            	intent = new Intent(BusZhiSelect.this, BusZhier.class);// 使用Accountflag窗口初始化Intent
//            	intent.putExtra("proID", proID);
//            	intent.putExtra("productID", productID);
//            	intent.putExtra("proType", proType);
//            	intent.putExtra("cabID", cabID);
//            	intent.putExtra("huoID", huoID);
//            	intent.putExtra("prosales", prosales);
//            	intent.putExtra("count", count);
//            	intent.putExtra("reamin_amount", reamin_amount);
            	startActivity(intent);// 打开Accountflag
		    }
		});
		ivbuszhiselweixing = (ImageView) findViewById(R.id.ivbuszhiselweixing);	
		ivbuszhiselweixing.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = null;// 创建Intent对象                
            	intent = new Intent(BusZhiSelect.this, BusZhiwei.class);// 使用Accountflag窗口初始化Intent
//            	intent.putExtra("proID", proID);
//            	intent.putExtra("productID", productID);
//            	intent.putExtra("proType", proType);
//            	intent.putExtra("cabID", cabID);
//            	intent.putExtra("huoID", huoID);
//            	intent.putExtra("prosales", prosales);
//            	intent.putExtra("count", count);
//            	intent.putExtra("reamin_amount", reamin_amount);
            	startActivity(intent);// 打开Accountflag
		    }
		});
		imgbtnbuszhiselfanhui = (ImageButton) findViewById(R.id.imgbtnbuszhiselfanhui);	
		imgbtnbuszhiselfanhui.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
		//*********************
		//搜索可以得到的支付方式
		//*********************
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(BusZhiSelect.this);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		if(tb_inaccount.getAmount()!=1)
    		{
    			ivbuszhiselamount.setVisibility(View.GONE);//关闭
    		}
    		else
    		{
    			ivbuszhiselamount.setVisibility(View.VISIBLE);//打开
    		}	
    		if(tb_inaccount.getZhifubaoer()!=1)
    		{
    			ivbuszhiselzhier.setVisibility(View.GONE);//关闭
    		}
    		else
    		{
    			ivbuszhiselzhier.setVisibility(View.VISIBLE);//打开
    		}
    		if(tb_inaccount.getWeixing()!=1)
    		{
    			ivbuszhiselweixing.setVisibility(View.GONE);//关闭
    		}
    		else
    		{
    			ivbuszhiselweixing.setVisibility(View.VISIBLE);//打开
    		}
    	}
	}
	
}
