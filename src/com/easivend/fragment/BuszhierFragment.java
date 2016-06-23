package com.easivend.fragment;


import com.easivend.app.business.BusPort;
import com.easivend.app.business.BusPort.BusPortFragInteraction;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.http.Zhifubaohttp;
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

public class BuszhierFragment extends Fragment 
{
	TextView txtbuszhiercount=null,txtbuszhiamerount=null,txtbuszhierrst=null,txtbuszhiertime=null;
	ImageButton imgbtnbuszhierqxzf=null;
	ImageView ivbuszhier=null;
	private int recLen = 180; 
	private int queryLen = 0; 
    private TextView txtView; 
    ImageView imgbtnbusgoodsback=null;
//  Timer timer = new Timer(); 
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;
    private String zhifutype = "3";//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
    private float amount=0;
    //线程进行支付宝二维码操作
//    private Thread thread=null;
//    private Handler mainhand=null,childhand=null;   
    private String out_trade_no=null;
    Zhifubaohttp zhifubaohttp=null;
    private int iszhier=0;//1成功生成了二维码,0没有成功生成二维码
	private Context context;
	//=========================
    //fragment与activity回调相关
    //=========================
    /**
     * 用来与外部activity交互的
     */
    private BuszhierFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BuszhierFragInteraction)
        {
            listterner = (BuszhierFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BuszhierFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface BuszhierFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        //void BusgoodsselectSwitch(int buslevel);//切换到BusZhixx页面
        void BuszhierFinish();      //切换到business页面
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
		View view = inflater.inflate(R.layout.fragment_buszhier, container, false);  
		context=this.getActivity();//获取activity的context
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
		txtbuszhiercount= (TextView) view.findViewById(R.id.txtbuszhiercount);
		txtbuszhiercount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiamerount= (TextView) view.findViewById(R.id.txtbuszhiamerount);
		txtbuszhiamerount.setText(String.valueOf(amount));
		txtbuszhierrst= (TextView) view.findViewById(R.id.txtbuszhierrst);
		ivbuszhier= (ImageView) view.findViewById(R.id.ivbuszhier);
		imgbtnbuszhierqxzf = (ImageButton) view.findViewById(R.id.imgbtnbuszhierqxzf);
		imgbtnbuszhierqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {	
		    	listterner.BuszhierFinish();//步骤二、fragment向activity发送回调信息
		    }
		});
		this.imgbtnbusgoodsback=(ImageView)view.findViewById(R.id.imgbtnbusgoodsback);
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	listterner.BuszhierFinish();//步骤二、fragment向activity发送回调信息
		    }
		});
		
		/**
	     * 用来与其他fragment交互的,
	     * 步骤五、当Fragment被加载到activity的时候，注册回调信息
	     * @param activity
	     */
		BusPort.setCallBack(new buportInterfaceImp());
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
			txtbuszhierrst.setText(str);
		}

		@Override
		public void BusportTbje(String str) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportChjg(int sta) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportSend(String str) {
			// TODO Auto-generated method stub
			//txtbuszhierrst.setText("交易结果:"+str);
			txtbuszhierrst.setText("交易结果:请扫描二维码");
			ivbuszhier.setImageBitmap(ToolClass.createQRImage(str));
		}

		
	}
}
