package com.easivend.app.business;

import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BusgoodsSelect extends Activity 
{
	public static BusgoodsSelect BusgoodsSelectAct=null;
	ImageView ivbusgoodselProduct=null;
	TextView txtbusgoodselName=null,txtbusgoodselPrice=null,txtbusgoodselNum=null,txtbusgoodselNo=null,
			txtbusgoodselAmount=null;
	ImageButton imgbtnbusgoodselbuy=null,imgbtnbusgoodselcancel=null;
	private String proID = null;
	private String productID = null;
	private String proImage = null;	
    private String prosales = null;
    private String procount = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busgoodsselect);
		BusgoodsSelectAct = this;
		//从商品页面中取得锁选中的商品
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		proID=bundle.getString("proID");
		productID=bundle.getString("productID");
		proImage=bundle.getString("proImage");
		prosales=bundle.getString("prosales");
		procount=bundle.getString("procount");
		
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="+productID+" proImage="
					+proImage+" prosales="+prosales+" procount="
					+procount);
		ivbusgoodselProduct = (ImageView) findViewById(R.id.ivbusgoodselProduct);
		/*为什么图片一定要转化为 Bitmap格式的！！ */
        Bitmap bitmap = ToolClass.getLoacalBitmap(proImage); //从本地取图片(在cdcard中获取)  //
        ivbusgoodselProduct.setImageBitmap(bitmap);// 设置图像的二进制值
		txtbusgoodselName = (TextView) findViewById(R.id.txtbusgoodselName);
		txtbusgoodselName.setText(proID);
		txtbusgoodselPrice = (TextView) findViewById(R.id.txtbusgoodselPrice);
		txtbusgoodselPrice.setText(prosales);
		txtbusgoodselNum = (TextView) findViewById(R.id.txtbusgoodselNum);
		txtbusgoodselNum.setText(procount);
		txtbusgoodselNo = (TextView) findViewById(R.id.txtbusgoodselNo);
		txtbusgoodselAmount = (TextView) findViewById(R.id.txtbusgoodselAmount);
		if(Integer.parseInt(txtbusgoodselNum.getText().toString())>0)
			txtbusgoodselAmount.setText(prosales);
		else
			txtbusgoodselAmount.setText("0");
		imgbtnbusgoodselbuy = (ImageButton) findViewById(R.id.imgbtnbusgoodselbuy);		
		imgbtnbusgoodselbuy.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(txtbusgoodselNum.getText().toString())>0)
		    	{
			    	Intent intent = null;// 创建Intent对象                
	            	intent = new Intent(BusgoodsSelect.this, BusZhiSelect.class);// 使用Accountflag窗口初始化Intent
	            	//intent.putExtra("proID", proID);
	            	//intent.putExtra("productID", productID);
	            	//intent.putExtra("proType", "1");//1代表通过商品ID出货,2代表通过货道出货
	            	//intent.putExtra("cabID", "0");//出货柜号,proType=1时无效
	            	//intent.putExtra("huoID", "0");//出货货道号,proType=1时无效
	            	//intent.putExtra("prosales", prosales);
	            	//intent.putExtra("count", txtbusgoodselNo.getText().toString());
	            	//intent.putExtra("reamin_amount", reamin_amount);
	            	OrderDetail.setProID(proID);
	            	OrderDetail.setProductID(productID);
	            	OrderDetail.setProType("1");
	            	OrderDetail.setShouldPay(Float.parseFloat(prosales));
	            	OrderDetail.setShouldNo(1);
	            	
	            	startActivity(intent);// 打开Accountflag
		    	}
		    }
		});
		this.imgbtnbusgoodselcancel=(ImageButton)findViewById(R.id.imgbtnbusgoodselcancel);
		imgbtnbusgoodselcancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
	}
	
}
