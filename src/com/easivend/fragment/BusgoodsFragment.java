package com.easivend.fragment;

import java.util.HashMap;
import java.util.Map;

import com.easivend.app.business.Busgoods;
import com.easivend.app.business.BusgoodsClass;
import com.easivend.app.business.BusgoodsSelect;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_ProductAdapter;
import com.easivend.fragment.BusgoodsclassFragment.BusgoodsclassFragInteraction;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;

public class BusgoodsFragment extends Fragment
{
	// 定义商品列表
	Vmc_ProductAdapter productAdapter=null;
	Gallery gvbusgoodsProduct=null;
	String proclassID=null;
	ImageButton imgbtnbusgoodsback=null;
	private String[] proID = null;
	private String[] productID = null;
	private String[] proImage = null;
    private String[] prosales = null;
    private String[] procount = null;
    private Context context;
    //=========================
    //fragment与activity回调相关
    //=========================
    /**
     * 用来与外部activity交互的
     */
    private BusgoodsFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BusgoodsFragInteraction)
        {
            listterner = (BusgoodsFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BusgoodsFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface BusgoodsFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void BusgoodsSwitch(Map<String, String> str);//切换到BusgoodsSelect页面
        void BusgoodsFinish();      //切换到business页面
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
		View view = inflater.inflate(R.layout.fragment_busgoods, container, false);  
		//步骤六，接收activity传入的数据
		//从商品分类页面中取得锁选中的商品类型
		Bundle data = getArguments();//获得从activity中传递过来的值
		proclassID=data.getString("proclassID");
		context=this.getActivity();//获取activity的context
		this.gvbusgoodsProduct=(Gallery) view.findViewById(R.id.gvbusgoodsProduct);
		this.imgbtnbusgoodsback=(ImageButton)view.findViewById(R.id.imgbtnbusgoodsback);
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proclassID="+proclassID,"log.txt");
		//如果存在商品分类id
		if((proclassID!=null)&&(proclassID.isEmpty()!=true))
		{
			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proclassID查询");
			// 商品表中的所有商品信息补充到商品数据结构数组中
	    	productAdapter=new Vmc_ProductAdapter();
	    	productAdapter.showProInfo(context,"","",proclassID);  
	    	ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProductName(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(),productAdapter.getProcount(), context);// 创建pictureAdapter对象
	    	gvbusgoodsProduct.setAdapter(adapter);// 为GridView设置数据源
	    	gvbusgoodsProduct.setSelection(adapter.getCount()/2);//中间的图片选中
	    	proID=productAdapter.getProID();
	    	productID=productAdapter.getProductID();
	    	proImage=productAdapter.getProImage();
	    	prosales=productAdapter.getProsales();
	    	procount=productAdapter.getProcount();
		}
		//如果不存在商品分类id
		else 
		{
			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品全部查询");
			// 商品表中的所有商品信息补充到商品数据结构数组中
	    	productAdapter=new Vmc_ProductAdapter();
	    	productAdapter.showProInfo(context,"","shoudong","");  
	    	ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProductName(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(),productAdapter.getProcount(), context);// 创建pictureAdapter对象
	    	gvbusgoodsProduct.setAdapter(adapter);// 为GridView设置数据源
	    	gvbusgoodsProduct.setSelection(adapter.getCount()/2);//中间的图片选中
	    	proID=productAdapter.getProID();
	    	productID=productAdapter.getProductID();
	    	proImage=productAdapter.getProImage();
	    	prosales=productAdapter.getProsales();
	    	procount=productAdapter.getProcount();
		}
		
		
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	listterner.BusgoodsFinish();//步骤二、fragment向activity发送回调信息
		    }
		});
		
		gvbusgoodsProduct.setOnItemClickListener(new OnItemClickListener() {// 为GridView设置项单击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(Integer.parseInt(procount[arg2])>0)
                {
//	            	Intent intent = null;// 创建Intent对象                
//	            	intent = new Intent(Busgoods.this, BusgoodsSelect.class);// 使用Accountflag窗口初始化Intent
//	            	intent.putExtra("proID", proID[arg2]);
//	            	intent.putExtra("productID", productID[arg2]);
//	            	intent.putExtra("proImage", proImage[arg2]);
//	            	intent.putExtra("prosales", prosales[arg2]);
//	            	intent.putExtra("procount", procount[arg2]);
//	            	intent.putExtra("proType", "1");//1代表通过商品ID出货,2代表通过货道出货
//	            	intent.putExtra("cabID", "");//出货柜号,proType=1时无效
//		        	intent.putExtra("huoID", "");//出货货道号,proType=1时无效
//	            	startActivity(intent);// 打开Accountflag
                	//步骤二、fragment向activity发送回调信息
                	Map<String, String>str=new HashMap<String, String>();
                	str.put("proID", proID[arg2]);
                	str.put("productID", productID[arg2]);
	            	str.put("proImage", proImage[arg2]);
	            	str.put("prosales", prosales[arg2]);
	            	str.put("procount", procount[arg2]);
	            	str.put("proType", "1");//1代表通过商品ID出货,2代表通过货道出货
	            	str.put("cabID", "");//出货柜号,proType=1时无效
		        	str.put("huoID", "");//出货货道号,proType=1时无效
                	listterner.BusgoodsSwitch(str);//步骤二、fragment向activity发送回调信息
                }
            }
        });
		return view;
	}
}
