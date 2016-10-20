package com.easivend.app.business;

import java.util.HashMap;
import java.util.Map;

import com.easivend.app.business.BusLand.COMReceiver;
import com.easivend.app.maintain.MaintainActivity;
import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.model.Tb_vmc_product;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
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
	ImageView ivbuszhiselamount=null,ivbuszhiselzhier=null,ivbuszhiselweixing=null,
			ivbuszhiselpos=null,ivbuszhiseltihuo=null;
	TextView txtbusgoodselName=null,txtbusgoodselAmount=null;
	WebView webproductDesc;
	private String proID = null;
	private String productID = null;
	private String proImage = null;	
    private String prosales = null;
    private String procount = null;
    private String proType=null;
    private String cabID = null;
	private String huoID = null;
	//=================
    //COM服务相关
    //=================
  	LocalBroadcastManager comBroadreceiver;
  	COMReceiver comreceiver;
    
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
		//=============
  		//COM服务相关
  		//=============
		//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		
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
		//得到商品描述
		if(ToolClass.getOrientation()==1)
		{
			webproductDesc = (WebView) findViewById(R.id.webproductDesc); 
			vmc_productDAO productDAO = new vmc_productDAO(this);// 创建InaccountDAO对象
		    Tb_vmc_product tb_vmc_product = productDAO.find(productID);
		    if(ToolClass.isEmptynull(tb_vmc_product.getProductDesc())!=true)
		    {
		    	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品Desc="+tb_vmc_product.getProductDesc().toString(),"log.txt");
			    WebSettings settings = webproductDesc.getSettings();
			    settings.setSupportZoom(true);
			    settings.setTextSize(WebSettings.TextSize.LARGEST);
			    webproductDesc.getSettings().setSupportMultipleWindows(true);
			    webproductDesc.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //设置滚动条样式
			    webproductDesc.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
			    webproductDesc.loadDataWithBaseURL(null,tb_vmc_product.getProductDesc().toString(), "text/html; charset=UTF-8","utf-8", null);//这种写法可以正确中文解码
		    }
		    else
		    {
		    	webproductDesc.setVisibility(View.GONE);
		    }
		}
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
		ivbuszhiselpos = (ImageView) findViewById(R.id.ivbuszhiselpos);	
		ivbuszhiselpos.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
			    	Intent intent = null;// 创建Intent对象                
	            	intent = new Intent(BusgoodsSelect.this, BusZhipos.class);// 使用Accountflag窗口初始化Intent
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
    			if(tb_inaccount.getAmount()==0)
        		{
        			ivbuszhiselamount.setVisibility(View.GONE);//关闭
        		}
        		else
        		{
        			ivbuszhiselamount.setVisibility(View.VISIBLE);//打开
        			zhifucount++;
        		}	
        		if(tb_inaccount.getZhifubaoer()==0)
        		{
        			ivbuszhiselzhier.setVisibility(View.GONE);//关闭
        		}
        		else
        		{
        			ivbuszhiselzhier.setVisibility(View.VISIBLE);//打开
        			zhifucount++;
        		}
        		if(tb_inaccount.getWeixing()==0)
        		{
        			ivbuszhiselweixing.setVisibility(View.GONE);//关闭
        		}
        		else
        		{
        			ivbuszhiselweixing.setVisibility(View.VISIBLE);//打开
        			zhifucount++;
        		}
        		if(tb_inaccount.getZhifubaofaca()==0)
        		{
        			ivbuszhiselpos.setVisibility(View.GONE);//关闭
        		}
        		else
        		{
        			ivbuszhiselpos.setVisibility(View.VISIBLE);//打开
        			zhifucount++;
        		}
        		switch(zhifucount)
        		{
        			case 3:
        			case 4:		
        				ivbuszhiselamount.setImageResource(R.drawable.amountnormalland);
        				ivbuszhiselzhier.setImageResource(R.drawable.zhiernormalland);
        				ivbuszhiselweixing.setImageResource(R.drawable.weixingnormalland);
        				ivbuszhiselpos.setImageResource(R.drawable.zhiposnormal);
        				break;
        			case 2:
        			case 1:	
        				ivbuszhiselamount.setImageResource(R.drawable.amountlargeland);
        				ivbuszhiselzhier.setImageResource(R.drawable.zhierlargeland);
        				ivbuszhiselweixing.setImageResource(R.drawable.weixinglargeland);
        				ivbuszhiselpos.setImageResource(R.drawable.zhiposlarge);
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
	
	//2.创建COMReceiver的接收器广播，用来接收服务器同步的内容
	public class COMReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			//操作返回	
			case COMThread.EV_BUTTONMAIN: 
					SerializableMap serializableMap2 = (SerializableMap) bundle.get("result");
					Map<String, Integer> Set2=serializableMap2.getMap();
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusSelect 按键操作="+Set2,"com.txt");
					int EV_TYPE=Set2.get("EV_TYPE");
					//上报货道按键
					if(EV_TYPE==COMThread.EV_BUTTONRPT_HUODAO)
					{
						if(ToolClass.checkCLIENT_STATUS_SERVICE())
						{
							//跳转商品
							
							cabID="1";
						    int huono=Set2.get("btnvalue");
						    huoID=(huono<=9)?("0"+huono):String.valueOf(huono);
						    vmc_columnDAO columnDAO = new vmc_columnDAO(context);// 创建InaccountDAO对象		    
						    Tb_vmc_product tb_inaccount = columnDAO.getColumnproduct(cabID,huoID);
						    if(tb_inaccount!=null)
						    {	
							    productID=tb_inaccount.getProductID().toString();
							    prosales=String.valueOf(tb_inaccount.getSalesPrice());
							    proImage=tb_inaccount.getAttBatch1();
							    proID=productID+"-"+tb_inaccount.getProductName().toString();
							    procount="1";
							    proType="2";
							    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="+productID+" proImage="
										+proImage+" prosales="+prosales+" procount="
										+procount+" proType="+proType+" cabID="+cabID+" huoID="+huoID,"log.txt");						    
	
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
								//得到商品描述
								if(ToolClass.getOrientation()==1)
								{
									vmc_productDAO productDAO = new vmc_productDAO(context);// 创建InaccountDAO对象
								    Tb_vmc_product tb_vmc_product = productDAO.find(productID);
								    if(ToolClass.isEmptynull(tb_vmc_product.getProductDesc())!=true)
								    {
								    	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品Desc="+tb_vmc_product.getProductDesc().toString(),"log.txt");
									    WebSettings settings = webproductDesc.getSettings();
									    settings.setSupportZoom(true);
									    settings.setTextSize(WebSettings.TextSize.LARGEST);
									    webproductDesc.getSettings().setSupportMultipleWindows(true);
									    webproductDesc.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //设置滚动条样式
									    webproductDesc.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
									    webproductDesc.loadDataWithBaseURL(null,tb_vmc_product.getProductDesc().toString(), "text/html; charset=UTF-8","utf-8", null);//这种写法可以正确中文解码
								    }
								    else
								    {
								    	webproductDesc.setVisibility(View.GONE);
								    }
								}
						    }
						    else
						    {
						    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
										+productID+" proType="
										+"2"+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
										+"1","log.txt");						    
							    // 弹出信息提示
							    ToolClass.failToast("抱歉，本商品已售完！");					
						    }
						}
					}	
					break;
				
			}			
		}

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
    	//=============
  		//COM服务相关
  		//=============
  		//5.解除注册接收器
  		comBroadreceiver.unregisterReceiver(comreceiver);
	}
	
	@Override
	protected void onDestroy() {
		//=============
		//COM服务相关
		//=============
		//5.解除注册接收器
		comBroadreceiver.unregisterReceiver(comreceiver);
		super.onDestroy();	
	}
	
}
