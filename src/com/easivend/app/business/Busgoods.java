package com.easivend.app.business;

import java.util.Map;

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
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
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
    private float reamin_amount=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busgoods);
		BusgoodsAct = this;
		this.gvbusgoodsProduct=(GridView) findViewById(R.id.gvbusgoodsProduct);
		this.imgbtnbusgoodsback=(ImageButton)findViewById(R.id.imgbtnbusgoodsback);
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
		//如果存在商品分类id
		if(proclassID!=null)
		{
			// 商品表中的所有商品信息补充到商品数据结构数组中
	    	productAdapter=new Vmc_ProductAdapter();
	    	productAdapter.showProInfo(this,"","",proclassID);  
	    	ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(), this);// 创建pictureAdapter对象
	    	gvbusgoodsProduct.setAdapter(adapter);// 为GridView设置数据源
	    	proID=productAdapter.getProID();
	    	productID=productAdapter.getProductID();
	    	proImage=productAdapter.getProImage();
	    	prosales=productAdapter.getProsales();
	    	procount=productAdapter.getProcount();
		}
		
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	backDialog();
		    }
		});
		gvbusgoodsProduct.setOnItemClickListener(new OnItemClickListener() {// 为GridView设置项单击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = null;// 创建Intent对象                
            	intent = new Intent(Busgoods.this, BusgoodsSelect.class);// 使用Accountflag窗口初始化Intent
            	intent.putExtra("proID", proID[arg2]);
            	intent.putExtra("productID", productID[arg2]);
            	intent.putExtra("proImage", proImage[arg2]);
            	intent.putExtra("prosales", prosales[arg2]);
            	intent.putExtra("procount", procount[arg2]);
            	intent.putExtra("reamin_amount", String.valueOf(reamin_amount));
            	startActivity(intent);// 打开Accountflag
            }
        });
//		//注册投币找零监听器
//  	    EVprotocolAPI.setCallBack(new JNIInterface() 
//		{
//			
//			@Override
//			public void jniCallback(Map<String, Integer> allSet) {
//				float payin_amount=0,reamin_amount=0,payout_amount=0;
//				// TODO Auto-generated method stub	
//				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<payinout结果");
//				Map<String, Integer> Set= allSet;
//				int jnirst=Set.get("EV_TYPE");
//				switch (jnirst)
//				{
//					case EVprotocolAPI.EV_PAYIN_RPT://接收子线程投币金额消息						
//						payin_amount=ToolClass.MoneyRec((Integer) allSet.get("payin_amount"));
//						reamin_amount=ToolClass.MoneyRec((Integer) allSet.get("reamin_amount"));
//						ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<投币:"+payin_amount
//								+"总共:"+reamin_amount);							
//						//txtpayin.setText(String.valueOf(payin_amount));
//						//txtreamin.setText(String.valueOf(reamin_amount));
//						break;
//					case EVprotocolAPI.EV_PAYOUT_RPT://接收子线程找零金额消息
//						payout_amount=ToolClass.MoneyRec((Integer) allSet.get("payout_amount"));
//						reamin_amount=ToolClass.MoneyRec((Integer) allSet.get("reamin_amount"));
//						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<找零金额"+String.valueOf(payout_amount));							
//						//txtpaymoney.setText(String.valueOf(payout_amount));
//						break;	
//				}				
//			}
//			
//		});
//		EVprotocolAPI.cashControl(1);//打开收币设备	
	}

		
	private void backDialog()
	{
		
		//创建警告对话框
    	Dialog alert=new AlertDialog.Builder(Busgoods.this)
    		.setTitle("对话框")//标题
    		.setMessage("您确定要结束购物吗？")//表示对话框中得内容
    		.setIcon(R.drawable.ic_launcher)//设置logo
    		.setPositiveButton("结束", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
    			{				
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) 
	    				{
	    					// TODO Auto-generated method stub	
	    					if(BusgoodsClass.BusgoodsClassAct!=null)
	    						BusgoodsClass.BusgoodsClassAct.finish(); 
	    					finish();
	    				}
    		      }
    			)		    		        
		        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
		        	{			
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							// TODO Auto-generated method stub				
						}
		        	}
		        )
		        .create();//创建一个对话框
		        alert.show();//显示对话框
		        //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<flag="+flag[0]); 
	}
	
	
}
