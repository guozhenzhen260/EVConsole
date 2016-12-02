package com.easivend.fragment;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.app.business.BusPort;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_productDAO;
import com.easivend.model.Tb_vmc_product;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BusgoodsselectFragment extends Fragment 
{
	ImageView ivbusgoodselProduct=null,imgbtnbusgoodsback=null;
	ImageView ivbuszhiselamount=null;
	TextView txtbusgoodselName=null,txtbusgoodselAmount=null;
	WebView webproductDesc;
	Button btnreturn=null;
	private String proID = null;
	private String productID = null;
	private String proImage = null;	
    private String prosales = null;
    private String procount = null;
    private String proType=null;
    private String cabID = null;
	private String huoID = null;
	private Context context;
	//扫码
    private String editstr="";
	private int editread=0;
	EditText editTextTimeCOMA;
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	//=========================
    //fragment与activity回调相关
    //=========================
    /**
     * 用来与外部activity交互的
     */
    private BusgoodsselectFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BusgoodsselectFragInteraction)
        {
            listterner = (BusgoodsselectFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BusgoodsselectFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface BusgoodsselectFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void BusgoodsselectSwitch(int buslevel);//切换到BusZhixx页面
        void BusgoodsSwitch();//切换到busgoods页面
        void BusgoodsselectFinish();      //切换到business页面
    }
    @Override
    public void onDetach() {
        super.onDetach();

        listterner = null;
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_busgoodsselect, container, false);  
		//步骤六，接收activity传入的数据
		//从商品页面中取得锁选中的商品
		Bundle bundle = getArguments();//获得从activity中传递过来的值
		proID=bundle.getString("proID");
		productID=bundle.getString("productID");
		proImage=bundle.getString("proImage");
		prosales=bundle.getString("prosales");
		procount=bundle.getString("procount");
		proType=bundle.getString("proType");
		cabID=bundle.getString("cabID");
		huoID=bundle.getString("huoID");
		context=this.getActivity();//获取activity的context
		//扫码模块
		editTextTimeCOMA=(EditText)view.findViewById(R.id.editTextTimeCOMA);
        editTextTimeCOMA.setInputType(InputType.TYPE_NULL);  
        editTextTimeCOMA.setFocusable(true);
		editTextTimeCOMA.setFocusableInTouchMode(true);
		editTextTimeCOMA.requestFocus();
		editTextTimeCOMA.setText("");
		editTextTimeCOMA.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				editstr=s.toString().trim();	
				//Log.i("EV_JNI","String s="+editstr);
				editread=100;
			}
		});
        timer.scheduleWithFixedDelay(task, 100, 10, TimeUnit.MILLISECONDS);
		
        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="+productID+" proImage="
					+proImage+" prosales="+prosales+" procount="
					+procount+" proType="+proType+" cabID="+cabID+" huoID="+huoID,"log.txt");
		ivbusgoodselProduct = (ImageView) view.findViewById(R.id.ivbusgoodselProduct);
		/*为什么图片一定要转化为 Bitmap格式的！！ */
        Bitmap bitmap = ToolClass.getLoacalBitmap(proImage); //从本地取图片(在cdcard中获取)  //
        ivbusgoodselProduct.setImageBitmap(bitmap);// 设置图像的二进制值
		txtbusgoodselName = (TextView) view.findViewById(R.id.txtbusgoodselName);
		txtbusgoodselName.setText(proID);
		txtbusgoodselAmount = (TextView) view.findViewById(R.id.txtbusgoodselAmount);
		if(Integer.parseInt(procount)>0)
		{
			txtbusgoodselAmount.setText(prosales);
		}
		else
		{
			txtbusgoodselAmount.setText("已售罄");
		}	
		//得到商品描述
		webproductDesc = (WebView) view.findViewById(R.id.webproductDesc); 
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
		ivbuszhiselamount = (ImageView) view.findViewById(R.id.ivbuszhiselamount);
		ivbuszhiselamount.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
//			    	Intent intent = null;// 创建Intent对象                
//	            	intent = new Intent(context, BusZhiAmount.class);// 使用Accountflag窗口初始化Intent
//	            	startActivity(intent);// 打开Accountflag
			    	listterner.BusgoodsselectSwitch(BusPort.BUSZHIAMOUNT);
		    	}
		    }
		});						
    	imgbtnbusgoodsback=(ImageView)view.findViewById(R.id.imgbtnbusgoodsback);
    	imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	listterner.BusgoodsselectFinish();//步骤二、fragment向activity发送回调信息
		    }
		});
    	btnreturn=(Button)view.findViewById(R.id.btnreturn);
	    btnreturn.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	listterner.BusgoodsSwitch();//步骤二、fragment向activity发送回调信息
		    }
		});	
		return view;
	}
	//调用倒计时定时器
    TimerTask task = new TimerTask() { 
    	@Override 
        public void run() { 
  
    		((Activity)context).runOnUiThread(new Runnable() {      // UI thread 
		         @Override 
		        public void run()
		        { 
		        	 if(editread>0)
		        		 editread--;
		        	 if(editread==0)
		        	 {
		        		 if(editstr.equals("")==false)
		        		 {
		        			 editstr="";
		        			 editTextTimeCOMA.setText("");
		        			 editTextTimeCOMA.setFocusable(true);
		     				 editTextTimeCOMA.setFocusableInTouchMode(true);
		     				 editTextTimeCOMA.requestFocus();  
		        		 }
		        	 }
		        } 
            });
        }     	    
    };
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
