package com.easivend.app.business;

import com.easivend.common.ToolClass;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.GridView;
import android.widget.LinearLayout;

public class Busgoods extends Activity 
{
	GridView gvbusgoodsProduct=null;
	String proclassID=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busgoods);
		this.gvbusgoodsProduct=(GridView) findViewById(R.id.gvbusgoodsProduct); 
		//动态设置控件高度
    	//
    	DisplayMetrics  dm = new DisplayMetrics();  
        //取得窗口属性  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        //窗口的宽度  
        int screenWidth = dm.widthPixels;          
        //窗口高度  
        int screenHeight = dm.heightPixels;      
        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<屏幕"+screenWidth
				+"],["+screenHeight+"]");	
		
    	LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gvbusgoodsProduct.getLayoutParams(); // 取控件mGrid当前的布局参数
    	linearParams.height =  screenHeight-170;// 当控件的高强制设成75象素
    	gvbusgoodsProduct.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
    	//从商品分类页面中取得锁选中的商品类型
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		proclassID=bundle.getString("proclassID");
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proclassID="+proclassID);
		if(proclassID!=null)
		{
			
		}
	}
	
}
