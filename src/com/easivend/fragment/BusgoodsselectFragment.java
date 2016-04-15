package com.easivend.fragment;

import com.easivend.app.business.BusPort;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BusgoodsselectFragment extends Fragment 
{
	ImageView ivbusgoodselProduct=null,imgbtnbusgoodselback=null;
	ImageView ivbuszhiselamount=null,ivbuszhiselzhier=null,ivbuszhiselweixing=null;
	TextView txtbusgoodselName=null,txtbusgoodselAmount=null;
	private String proID = null;
	private String productID = null;
	private String proImage = null;	
    private String prosales = null;
    private String procount = null;
    private String proType=null;
    private String cabID = null;
	private String huoID = null;
	private Context context;
	
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
		ivbuszhiselzhier = (ImageView) view.findViewById(R.id.ivbuszhiselzhier);
		ivbuszhiselzhier.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
//			    	Intent intent = null;// 创建Intent对象                
//	            	intent = new Intent(context, BusZhier.class);// 使用Accountflag窗口初始化Intent
//	            	startActivity(intent);// 打开Accountflag
			    	listterner.BusgoodsselectSwitch(BusPort.BUSZHIER);
		    	}
		    }
		});
		ivbuszhiselweixing = (ImageView) view.findViewById(R.id.ivbuszhiselweixing);	
		ivbuszhiselweixing.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(Integer.parseInt(procount)>0)
		    	{
			    	sendzhifu();
//			    	Intent intent = null;// 创建Intent对象                
//	            	intent = new Intent(context, BusZhiwei.class);// 使用Accountflag窗口初始化Intent
//	            	startActivity(intent);// 打开Accountflag
			    	listterner.BusgoodsselectSwitch(BusPort.BUSZHIWEI);
		    	}
		    }
		});
		//*********************
		//搜索可以得到的支付方式
		//*********************
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
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
		imgbtnbusgoodselback=(ImageButton)view.findViewById(R.id.imgbtnbusgoodselback);
		imgbtnbusgoodselback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	listterner.BusgoodsselectFinish();//步骤二、fragment向activity发送回调信息
		    }
		});
		return view;
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
