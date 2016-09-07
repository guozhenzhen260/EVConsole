package com.easivend.fragment;

import com.easivend.common.OrderDetail;
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

public class BuszhiposFragment extends Fragment 
{
	TextView txtbuszhiposcount=null,txtbuszhiposAmount=null,txtbuszhipostime=null,
			txtbuszhipostsxx=null;
	ImageButton imgbtnbuszhiposqxzf=null;
	ImageView imgbtnbusgoodsback=null;
	float amount=0;//商品需要支付金额	 
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
    private BuszhiposFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BuszhiposFragInteraction)
        {
            listterner = (BuszhiposFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BuszhiposFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface BuszhiposFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        //void BusgoodsselectSwitch(int buslevel);//切换到BusZhixx页面
        void BuszhiposFinish();      //切换到business页面
    }
    @Override
    public void onDetach() {
        super.onDetach();

        listterner = null;
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_buszhipos, container, false);  
		context=this.getActivity();//获取activity的context
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();		
		txtbuszhiposcount= (TextView) view.findViewById(R.id.txtbuszhiposcount);
		txtbuszhiposcount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiposAmount= (TextView) view.findViewById(R.id.txtbuszhiposAmount);
		txtbuszhiposAmount.setText(String.valueOf(amount));
		txtbuszhipostime = (TextView) view.findViewById(R.id.txtbuszhipostime);
		txtbuszhipostsxx = (TextView) view.findViewById(R.id.txtbuszhipostsxx);
		imgbtnbuszhiposqxzf = (ImageButton) view.findViewById(R.id.imgbtnbuszhiposqxzf);
		imgbtnbuszhiposqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	listterner.BuszhiposFinish();//步骤二、fragment向activity发送回调信息
		    }
		});
		this.imgbtnbusgoodsback=(ImageView)view.findViewById(R.id.imgbtnbusgoodsback);
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	listterner.BuszhiposFinish();//步骤二、fragment向activity发送回调信息
		    }
		});
		return view;
	}

}
