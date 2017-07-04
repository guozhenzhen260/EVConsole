package com.easivend.fragment;

import com.easivend.app.business.BusPort;
import com.easivend.app.business.BusPort.BusPortFragInteraction;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BuszhiamountFragment extends Fragment 
{
	TextView txtbuszhiamountcount=null,txtbuszhiamountAmount=null,txtbuszhiamountbillAmount=null,txtbuszhiamounttime=null,
			txtbuszhiamounttsxx=null;
	ImageButton imgbtnbuszhiamountqxzf=null,imgbtnbuszhiamountqtzf=null;
	ImageView imgbtnbusgoodsback=null;
	float amount=0;//商品需要支付金额	
	ImageView ivbuszhiselamount=null,ivbuszhiselzhier=null,ivbuszhiselweixing=null,ivbuszhiselpos=null;
	ImageView ivbuszhier=null,ivbuszhiwei=null,ivbusyinlian=null;
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;    
//    private String id="";
    private String out_trade_no=null;    
	private Context context;
	//=========================
    //fragment与activity回调相关
    //=========================
    /**
     * 用来与外部activity交互的
     */
    private BuszhiamountFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BuszhiamountFragInteraction)
        {
            listterner = (BuszhiamountFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BuszhiamountFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface BuszhiamountFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        //void BusgoodsselectSwitch(int buslevel);//切换到BusZhixx页面
        void BuszhiamountFinish();      //切换到business页面
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
		View view = inflater.inflate(R.layout.fragment_buszhiamount, container, false);  
		context=this.getActivity();//获取activity的context
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();		
		txtbuszhiamountcount= (TextView) view.findViewById(R.id.txtbuszhiamountcount);
		txtbuszhiamountcount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiamountAmount= (TextView) view.findViewById(R.id.txtbuszhiamountAmount);
		txtbuszhiamountAmount.setText(String.valueOf(amount));
		txtbuszhiamountbillAmount= (TextView) view.findViewById(R.id.txtbuszhiamountbillAmount);		
		txtbuszhiamounttime = (TextView) view.findViewById(R.id.txtbuszhiamounttime);
		txtbuszhiamounttsxx = (TextView) view.findViewById(R.id.txtbuszhiamounttsxx);
		imgbtnbuszhiamountqxzf = (ImageButton) view.findViewById(R.id.imgbtnbuszhiamountqxzf);
		imgbtnbuszhiamountqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	listterner.BuszhiamountFinish();//步骤二、fragment向activity发送回调信息
		    }
		});
		this.imgbtnbusgoodsback=(ImageView)view.findViewById(R.id.imgbtnbusgoodsback);
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	listterner.BuszhiamountFinish();//步骤二、fragment向activity发送回调信息
		    }
		});
		/**
	     * 用来与其他fragment交互的,
	     * 步骤五、当Fragment被加载到activity的时候，注册回调信息
	     * @param activity
	     */
		BusPort.setCallBack(new buportInterfaceImp());
		ivbuszhiselamount = (ImageView) view.findViewById(R.id.ivbuszhiselamount);
		ivbuszhiselzhier = (ImageView) view.findViewById(R.id.ivbuszhiselzhier);
		ivbuszhiselweixing = (ImageView) view.findViewById(R.id.ivbuszhiselweixing);	
		ivbuszhiselpos = (ImageView) view.findViewById(R.id.ivbuszhiselpos);
		ivbuszhier = (ImageView) view.findViewById(R.id.ivbuszhier);
		ivbusyinlian = (ImageView) view.findViewById(R.id.ivbusyinlian);
		ivbuszhiwei = (ImageView) view.findViewById(R.id.ivbuszhiwei);
		//*********************
		//搜索可以得到的支付方式
		//*********************
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		if(tb_inaccount.getAmount()==0)
    		{
    			ivbuszhiselamount.setVisibility(View.GONE);//关闭
    		}
    		else
    		{
    			ivbuszhiselamount.setVisibility(View.VISIBLE);//打开
    		}	
    		if(tb_inaccount.getZhifubaoer()==0)
    		{
    			ivbuszhiselzhier.setVisibility(View.GONE);//关闭
    			ivbuszhier.setVisibility(View.GONE);//关闭
    		}
    		else
    		{
    			ivbuszhiselzhier.setVisibility(View.VISIBLE);//打开
    			ivbuszhier.setVisibility(View.VISIBLE);//打开
    		}
    		if(tb_inaccount.getWeixing()==0)
    		{
    			ivbuszhiselweixing.setVisibility(View.GONE);//关闭
    			ivbuszhiwei.setVisibility(View.GONE);//关闭
    		}
    		else
    		{
    			ivbuszhiselweixing.setVisibility(View.VISIBLE);//打开
    			ivbuszhiwei.setVisibility(View.VISIBLE);//打开
    		}
    		//pos机                                                                             银联二维码
    		if((tb_inaccount.getZhifubaofaca()==0)&&(tb_inaccount.getPrinter()==0))
    		{
    			ivbuszhiselpos.setVisibility(View.GONE);//关闭
    			ivbusyinlian.setVisibility(View.GONE);//关闭
    		}
    		//pos机打开
    		else if(tb_inaccount.getZhifubaofaca()==1)
    		{
    			ivbuszhiselpos.setVisibility(View.VISIBLE);//打开
    			ivbusyinlian.setVisibility(View.GONE);//关闭
    		}    		
    		//银联二维码打开
    		else if(tb_inaccount.getPrinter()==1)
    		{
    			ivbuszhiselpos.setVisibility(View.VISIBLE);//打开
    			ivbusyinlian.setVisibility(View.VISIBLE);//打开
    		} 
    	}
		return view;
	}
	
	private class buportInterfaceImp implements BusPortFragInteraction//加载接口
	{
		/**
	     * 用来与其他fragment交互的,
	     * 步骤三、实现BusPortFragInteraction接口
	     * @param activity
	     */
		@Override
		public void BusportTsxx(String str) {
			// TODO Auto-generated method stub
			txtbuszhiamounttsxx.setText(str);
		}

		@Override
		public void BusportTbje(String str) {
			// TODO Auto-generated method stub
			txtbuszhiamountbillAmount.setText(str);
		}

		@Override
		public void BusportChjg(int sta) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportSend(String str) {
			// TODO Auto-generated method stub
			ivbuszhier.setImageBitmap(ToolClass.createQRImage(str));
		}

		@Override
		public void BusportSendWei(String str) {
			// TODO Auto-generated method stub
			ivbuszhiwei.setImageBitmap(ToolClass.createQRImage(str));
		}
		
		@Override
		public void BusportSendYinlian(String str) {
			// TODO Auto-generated method stub
			ivbusyinlian.setImageBitmap(ToolClass.createQRImage(str));
		}

		
	}
		
}


