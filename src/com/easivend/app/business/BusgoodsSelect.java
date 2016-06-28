package com.easivend.app.business;

import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_productDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_product;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BusgoodsSelect extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 5*60*1000; // 延迟5分钟	
	public static BusgoodsSelect BusgoodsSelectAct=null;
	ImageView ivbusgoodselProduct=null,imgbtnbusgoodsback=null;
	ImageView ivbuszhiselamount=null,ivbuszhiselzhier=null,ivbuszhiselweixing=null,ivbuszhiseltihuo=null;
	TextView txtbusgoodselName=null,txtbusgoodselAmount=null;
	private String proID = null;
	private String productID = null;
	private String proImage = null;	
    private String prosales = null;
    private String procount = null;
    private String proType=null;
    private String cabID = null;
	private String huoID = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
		proType=bundle.getString("proType");
		cabID=bundle.getString("cabID");
		huoID=bundle.getString("huoID");
		
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="+productID+" proImage="
					+proImage+" prosales="+prosales+" procount="
					+procount+" proType="+proType+" cabID="+cabID+" huoID="+huoID,"log.txt");
		ivbusgoodselProduct = (ImageView) findViewById(R.id.ivbusgoodselProduct);
		/*为什么图片一定要转化为 Bitmap格式的！！ */
        Bitmap bitmap = ToolClass.getLoacalBitmap(proImage); //从本地取图片(在cdcard中获取)  //
        ivbusgoodselProduct.setImageBitmap(bitmap);// 设置图像的二进制值
		txtbusgoodselName = (TextView) findViewById(R.id.txtbusgoodselName);
		txtbusgoodselName.setText(proID);
		txtbusgoodselAmount = (TextView) findViewById(R.id.txtbusgoodselAmount);
		if(Integer.parseInt(procount)>0)
		{
			txtbusgoodselAmount.setText(prosales);
		}
		else
		{
			txtbusgoodselAmount.setText("已售罄");
		}
//		//得到商品描述
//		webproductDesc = (WebView) findViewById(R.id.webproductDesc); 
//		vmc_productDAO productDAO = new vmc_productDAO(BusgoodsSelect.this);// 创建InaccountDAO对象
//	    Tb_vmc_product tb_vmc_product = productDAO.find(productID);
//	    if(tb_vmc_product.getProductDesc().isEmpty()!=true) 
//	    {
//	    	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品Desc="+tb_vmc_product.getProductDesc().toString(),"log.txt");
//		    WebSettings settings = webproductDesc.getSettings();
//		    settings.setSupportZoom(true);
//		    settings.setTextSize(WebSettings.TextSize.LARGEST);
//		    webproductDesc.getSettings().setSupportMultipleWindows(true);
//		    webproductDesc.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //设置滚动条样式
//		    webproductDesc.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
//		    webproductDesc.loadDataWithBaseURL(null,tb_vmc_product.getProductDesc().toString(), "text/html; charset=UTF-8","utf-8", null);//这种写法可以正确中文解码
//	    }
//	    else
//	    {
//	    	webproductDesc.setVisibility(View.GONE);
//	    }
		ivbuszhiselamount = (ImageView) findViewById(R.id.ivbuszhiselamount);
		ivbuszhiselamount.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// 创建Intent对象                
	            	intent = new Intent(BusgoodsSelect.this, BusZhiAmount.class);// 使用Accountflag窗口初始化Intent
	            	startActivity(intent);// 打开Accountflag
		    	}
		    }
		});
		ivbuszhiselzhier = (ImageView) findViewById(R.id.ivbuszhiselzhier);
		ivbuszhiselzhier.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// 创建Intent对象                
	            	intent = new Intent(BusgoodsSelect.this, BusZhier.class);// 使用Accountflag窗口初始化Intent
	            	startActivity(intent);// 打开Accountflag
		    	}
		    }
		});
		ivbuszhiselweixing = (ImageView) findViewById(R.id.ivbuszhiselweixing);	
		ivbuszhiselweixing.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// 创建Intent对象                
	            	intent = new Intent(BusgoodsSelect.this, BusZhiwei.class);// 使用Accountflag窗口初始化Intent
	            	startActivity(intent);// 打开Accountflag
		    	}
		    }
		});
		ivbuszhiseltihuo = (ImageView) findViewById(R.id.ivbuszhiseltihuo);	
		ivbuszhiseltihuo.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// 创建Intent对象                
	            	intent = new Intent(BusgoodsSelect.this, BusZhitihuo.class);// 使用Accountflag窗口初始化Intent
	            	startActivity(intent);// 打开Accountflag
		    	}
		    }
		});
		//*********************
		//搜索可以得到的支付方式
		//*********************
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(BusgoodsSelect.this);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		int zhifucount=0;
    		//有打开提货码功能
    		if(tb_inaccount.getPrinter()==1)
    		{
    			//可以提货
    			if(ToolClass.getzhitihuotype(BusgoodsSelect.this, cabID, huoID))
        		{
        			ivbuszhiseltihuo.setVisibility(View.VISIBLE);//打开
        			ivbuszhiselamount.setVisibility(View.GONE);//关闭
        			ivbuszhiselzhier.setVisibility(View.GONE);//关闭
        			ivbuszhiselweixing.setVisibility(View.GONE);//关闭
        			zhifucount++;
        		}
    			else
    			{
    				ivbuszhiseltihuo.setVisibility(View.GONE);//关闭
    				if(tb_inaccount.getAmount()!=1)
            		{
            			ivbuszhiselamount.setVisibility(View.GONE);//关闭
            		}
            		else
            		{
            			ivbuszhiselamount.setVisibility(View.VISIBLE);//打开
            			zhifucount++;
            		}	
            		if(tb_inaccount.getZhifubaoer()!=1)
            		{
            			ivbuszhiselzhier.setVisibility(View.GONE);//关闭
            		}
            		else
            		{
            			ivbuszhiselzhier.setVisibility(View.VISIBLE);//打开
            			zhifucount++;
            		}
            		if(tb_inaccount.getWeixing()!=1)
            		{
            			ivbuszhiselweixing.setVisibility(View.GONE);//关闭
            		}
            		else
            		{
            			ivbuszhiselweixing.setVisibility(View.VISIBLE);//打开
            			zhifucount++;
            		}
    			}
    		}
    		//没有打开提货码功能
    		else
    		{
    			ivbuszhiseltihuo.setVisibility(View.GONE);//关闭
    			if(tb_inaccount.getAmount()!=1)
        		{
        			ivbuszhiselamount.setVisibility(View.GONE);//关闭
        		}
        		else
        		{
        			ivbuszhiselamount.setVisibility(View.VISIBLE);//打开
        			zhifucount++;
        		}	
        		if(tb_inaccount.getZhifubaoer()!=1)
        		{
        			ivbuszhiselzhier.setVisibility(View.GONE);//关闭
        		}
        		else
        		{
        			ivbuszhiselzhier.setVisibility(View.VISIBLE);//打开
        			zhifucount++;
        		}
        		if(tb_inaccount.getWeixing()!=1)
        		{
        			ivbuszhiselweixing.setVisibility(View.GONE);//关闭
        		}
        		else
        		{
        			ivbuszhiselweixing.setVisibility(View.VISIBLE);//打开
        			zhifucount++;
        		}
        		switch(zhifucount)
        		{
        			case 3:
        				ivbuszhiselamount.setImageResource(R.drawable.amountnormalland);
        				ivbuszhiselzhier.setImageResource(R.drawable.zhiernormalland);
        				ivbuszhiselweixing.setImageResource(R.drawable.weixingnormalland);
        				break;
        			case 2:
        			case 1:	
        				ivbuszhiselamount.setImageResource(R.drawable.amountlargeland);
        				ivbuszhiselzhier.setImageResource(R.drawable.zhierlargeland);
        				ivbuszhiselweixing.setImageResource(R.drawable.weixinglargeland);
        				break;	
        		}
    		}    		    			
    	}		
    	imgbtnbusgoodsback=(ImageView)findViewById(R.id.imgbtnbusgoodsback);
    	imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});		
		//5分钟钟之后退出页面
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {	
            	finish();
            }

		}, SPLASH_DISPLAY_LENGHT);
	}
	
	private void sendzhifu()
	{
		OrderDetail.setProID(proID);
    	OrderDetail.setProductID(productID);
    	OrderDetail.setProType(proType);
    	OrderDetail.setShouldPay(Float.parseFloat(prosales));
    	OrderDetail.setShouldNo(1);
    	OrderDetail.setCabID(cabID);
    	OrderDetail.setColumnID(huoID);
	}
	
}
