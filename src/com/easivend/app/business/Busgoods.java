package com.easivend.app.business;

import java.util.Map;

import com.easivend.app.maintain.GoodsManager;
import com.easivend.app.maintain.HuodaoTest;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_ProductAdapter;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Busgoods extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 5*60*1000; // 延迟5分钟	
	public static Busgoods BusgoodsAct=null;
	// 定义商品列表
	Vmc_ProductAdapter productAdapter=null;
	GridView gvbusgoodsProduct=null;
	String proclassID=null;
	ImageButton imgbtnbusgoodsback=null;
	private String[] proID = null;
	private String[] productID = null;
	private String[] proImage = null;
    private String[] prosales = null;
    private String[] procount = null;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.busgoods);
		BusgoodsAct = this;
		this.gvbusgoodsProduct=(GridView) findViewById(R.id.gvbusgoodsProduct);
		this.imgbtnbusgoodsback=(ImageButton)findViewById(R.id.imgbtnbusgoodsback);
//		//动态设置控件高度
//    	//
//    	DisplayMetrics  dm = new DisplayMetrics();  
//        //取得窗口属性  
//        getWindowManager().getDefaultDisplay().getMetrics(dm);  
//        //窗口的宽度  
//        int screenWidth = dm.widthPixels;          
//        //窗口高度  
//        int screenHeight = dm.heightPixels;      
//        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<屏幕"+screenWidth
//				+"],["+screenHeight+"]","log.txt");	
//		
//    	LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gvbusgoodsProduct.getLayoutParams(); // 取控件mGrid当前的布局参数
//    	linearParams.height =  screenHeight-170;// 当控件的高强制设成75象素
//    	gvbusgoodsProduct.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
    	//从商品分类页面中取得锁选中的商品类型
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		proclassID=bundle.getString("proclassID");
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proclassID="+proclassID,"log.txt");
		//如果存在商品分类id
//		if((proclassID!=null)&&(proclassID.isEmpty()!=true))
//		{
//			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proclassID查询");
//			// 商品表中的所有商品信息补充到商品数据结构数组中
//	    	productAdapter=new Vmc_ProductAdapter();
//	    	productAdapter.showProInfo(this,"","",proclassID);  
//	    	ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProductName(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(),productAdapter.getProcount(), this);// 创建pictureAdapter对象
//	    	gvbusgoodsProduct.setAdapter(adapter);// 为GridView设置数据源
//	    	proID=productAdapter.getProID();
//	    	productID=productAdapter.getProductID();
//	    	proImage=productAdapter.getProImage();
//	    	prosales=productAdapter.getProsales();
//	    	procount=productAdapter.getProcount();
//		}
//		//如果不存在商品分类id
//		else 
//		{
//			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品全部查询");
//			// 商品表中的所有商品信息补充到商品数据结构数组中
//	    	productAdapter=new Vmc_ProductAdapter();
//	    	productAdapter.showProInfo(this,"","shoudong","");  
//	    	ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProductName(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(),productAdapter.getProcount(), this);// 创建pictureAdapter对象
//	    	gvbusgoodsProduct.setAdapter(adapter);// 为GridView设置数据源
//	    	proID=productAdapter.getProID();
//	    	productID=productAdapter.getProductID();
//	    	proImage=productAdapter.getProImage();
//	    	prosales=productAdapter.getProsales();
//	    	procount=productAdapter.getProcount();
//		}
		VmcProductThread vmcProductThread=new VmcProductThread();
    	vmcProductThread.execute();
    	
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(BusgoodsClass.BusgoodsClassAct!=null)
					BusgoodsClass.BusgoodsClassAct.finish(); 
				finish();
		    }
		});
		
		gvbusgoodsProduct.setOnItemClickListener(new OnItemClickListener() {// 为GridView设置项单击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(Integer.parseInt(procount[arg2])>0)
                {
	            	Intent intent = null;// 创建Intent对象                
	            	intent = new Intent(Busgoods.this, BusgoodsSelect.class);// 使用Accountflag窗口初始化Intent
	            	intent.putExtra("proID", proID[arg2]);
	            	intent.putExtra("productID", productID[arg2]);
	            	intent.putExtra("proImage", proImage[arg2]);
	            	intent.putExtra("prosales", prosales[arg2]);
	            	intent.putExtra("procount", procount[arg2]);
	            	intent.putExtra("proType", "1");//1代表通过商品ID出货,2代表通过货道出货
	            	intent.putExtra("cabID", "");//出货柜号,proType=1时无效
		        	intent.putExtra("huoID", "");//出货货道号,proType=1时无效
	            	startActivity(intent);// 打开Accountflag
                }
            }
        });
		//5分钟钟之后退出页面
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {	
            	if(BusgoodsClass.BusgoodsClassAct!=null)
					BusgoodsClass.BusgoodsClassAct.finish(); 
				finish(); 
            }

		}, SPLASH_DISPLAY_LENGHT);
	}
	
	//****************
	//异步线程，用于查询记录
	//****************
	private class VmcProductThread extends AsyncTask<Void,Void,Vmc_ProductAdapter>
	{

		@Override
		protected Vmc_ProductAdapter doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// 商品表中的所有商品信息补充到商品数据结构数组中
	    	productAdapter=new Vmc_ProductAdapter();
	    	//如果存在商品分类id
			if((proclassID!=null)&&(proclassID.isEmpty()!=true))
			{
				productAdapter.showProInfo(Busgoods.this,"","",proclassID);  
			}
			//如果不存在商品分类id
			else
			{
				productAdapter.showProInfo(Busgoods.this,"","shoudong","");  
			}
			return productAdapter;
		}

		@Override
		protected void onPostExecute(Vmc_ProductAdapter productAdapter) {
			// TODO Auto-generated method stub
			ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProductName(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(),productAdapter.getProcount(), Busgoods.this);// 创建pictureAdapter对象
	    	gvbusgoodsProduct.setAdapter(adapter);// 为GridView设置数据源
	    	proID=productAdapter.getProID();
	    	productID=productAdapter.getProductID();
	    	proImage=productAdapter.getProImage();
	    	prosales=productAdapter.getProsales();
	    	procount=productAdapter.getProcount();	    	
		}
				
	}

}
