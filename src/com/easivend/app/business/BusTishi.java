package com.easivend.app.business;

import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

public class BusTishi extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 5*60*1000; // 延迟5分钟	
	ImageView imgbtnbusgoodsback=null;
	private WebView webtishiInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.bustishi);
		
		//从商品分类页面中取得锁选中的提示信息类型
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		int infotype=bundle.getInt("infotype");
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<tishi提示类型="+infotype,"log.txt");
		 //得到提示描述
		 webtishiInfo = (WebView) findViewById(R.id.webtishiInfo); 
		 WebSettings settings = webtishiInfo.getSettings();
	     settings.setSupportZoom(true);
	     settings.setTextSize(WebSettings.TextSize.LARGEST);
	     webtishiInfo.getSettings().setSupportMultipleWindows(true);
	     webtishiInfo.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //设置滚动条样式
	     webtishiInfo.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
	     String info="敬请期待!";
	     //购买演示和提示信息
 		 vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
	     // 获取所有收入信息，并存储到List泛型集合中
	   	 Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
	   	 if(tb_inaccount!=null)
	   	 {
	   		 //购买演示
	   		 if(infotype==1)
	   		 {
	   			 if(ToolClass.isEmptynull(tb_inaccount.getDemo())==false)	 
	   			 {
	   				 info=tb_inaccount.getDemo();
	   			 }	   			 
	   		 }
	   		 //活动信息
	   		 else if(infotype==2)
	   		 {
	   			 if(ToolClass.isEmptynull(tb_inaccount.getEvent())==false)
	   			 {
	   				 info=tb_inaccount.getEvent();
	   			 }	   			 
	   		 }
	   	 }
	     
	     webtishiInfo.loadDataWithBaseURL(null,info, "text/html; charset=UTF-8","utf-8", null);//这种写法可以正确中文解码
		   
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
	
}
