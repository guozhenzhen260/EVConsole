package com.easivend.fragment;

import java.util.List;

import com.easivend.app.business.BusPort;
import com.easivend.app.business.BusPort.BusPortFragInteraction;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.example.evconsole.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BushuoFragment extends Fragment
{
	private String proID = null;
	private String productID = null;
	private String proType = null;
	private String cabID = null;
	private String huoID = null;
    private float prosales = 0;
    private int count = 0;
    private int zhifutype = 0;//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
    private TextView txtbushuoname = null;
    private ImageView ivbushuoquhuo=null;
    private int tempx=0;
    private String draw=null,info=null;
    private int cabinetvar=0,huodaoNo=0,cabinetTypevar=0;
    private vmc_columnDAO columnDAO =null; 
    //出货结果
    private int status=0;//出货结果	
    private Context context;
    //=========================
    //fragment与activity回调相关
    //=========================
    /**
     * 用来与外部activity交互的
     */
    private BushuoFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BushuoFragInteraction)
        {
            listterner = (BushuoFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BushuoFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface BushuoFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void BushuoChuhuoOpt(int cabinetvar,int huodaoNo,int cabinetTypevar);      //发送出货指令
        void BushuoFinish(int status);      //结束出货页面
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
		View view = inflater.inflate(R.layout.fragment_bushuo, container, false);  
		context=this.getActivity();//获取activity的context
		
		//从商品页面中取得锁选中的商品
//		Intent intent=getIntent();
//		Bundle bundle=intent.getExtras();
		proID=OrderDetail.getProID();
		productID=OrderDetail.getProductID();
		proType=OrderDetail.getProType();
		cabID=OrderDetail.getCabID();
		huoID=OrderDetail.getColumnID();
		prosales=OrderDetail.getShouldPay();//商品单价
		count=OrderDetail.getShouldNo();//数量
		zhifutype=OrderDetail.getPayType();
		txtbushuoname=(TextView)view.findViewById(R.id.txtbushuoname);
		
  	    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
				+productID+" proType="
				+proType+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
				+count+" zhifutype="+zhifutype,"log.txt");		
  	    txtbushuoname.setText(proID+"["+prosales+"]"+"->等待出货");
		this.ivbushuoquhuo =(ImageView) view.findViewById(R.id.ivbushuoquhuo);
		ivbushuoquhuo.setVisibility(View.GONE);
		
		//****
		//出货
		//****
		chuhuoopt(tempx);
		/**
	     * 用来与其他fragment交互的,
	     * 步骤五、当Fragment被加载到activity的时候，注册回调信息
	     * @param activity
	     */
		BusPort.setCallBack(new buportInterfaceImp());
		return view;
	}
    
    //出货,返回值0失败,1出货指令成功，等待返回结果,2出货完成
  	private void chuhuoopt(int huox)
  	{
  		// 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_column表中
   	    columnDAO = new vmc_columnDAO(context);
   	    txtbushuoname.setText(proID+"["+prosales+"]"+"->正在出货,请稍候...");
  		//1.计算出出货货道
  		//按商品id出货
  		if(proType.equals("1")==true)
  		{
  	 	    // 获取所有收入信息，并存储到Map集合中
  			List<String> alllist = columnDAO.getproductColumn(productID);
  			cabinetvar=Integer.parseInt(alllist.get(0));
  			huodaoNo=Integer.parseInt(alllist.get(1));
  			cabinetTypevar=Integer.parseInt(alllist.get(2));
  			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<[1]商品cabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar,"log.txt"); 
  		}
  		//按货道id出货
  		else if(proType.equals("2")==true)
  		{
  	 	    // 获取所有收入信息，并存储到Map集合中
  			String alllist = columnDAO.getcolumnType(cabID);
  			cabinetvar=Integer.parseInt(cabID);
  			huodaoNo=Integer.parseInt(huoID);
  			cabinetTypevar=Integer.parseInt(alllist);
  			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<[2]商品cabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar,"log.txt"); 
  		}
  		listterner.BushuoChuhuoOpt(cabinetvar,huodaoNo,cabinetTypevar);//步骤二、fragment向activity发送回调信息  		
  	}
  	//修改存货数量
  	private void chuhuoupdate(int cabinetvar,int huodaoNo)
  	{
  		String cab=null,huo=null;
  		cab=String.valueOf(cabinetvar);
  		//货道id=1到9，改为01到09
          if(huodaoNo<10)
          {
          	huo="0"+String.valueOf(huodaoNo);
          }
          else
          {
          	huo=String.valueOf(huodaoNo);
          }	
          //扣除存货余量
  		columnDAO.update(cab,huo);		
  	}
  	
  	//记录日志出货结果type=1出货成功，0出货失败
  	private void chuhuoLog(int type)
  	{
  		OrderDetail.setYujiHuo(1);
  		OrderDetail.setCabID(String.valueOf(cabinetvar));
  		OrderDetail.setColumnID(String.valueOf(huodaoNo));
  		if(type==1)//出货成功
  		{
  			OrderDetail.setPayStatus(0);
  			OrderDetail.setRealHuo(1);
  			OrderDetail.setHuoStatus(0);
  		}
  		else//出货失败
  		{
  			OrderDetail.setPayStatus(1);
  			OrderDetail.setRealHuo(0);
  			OrderDetail.setHuoStatus(1);
  		}
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
			
		}

		@Override
		public void BusportTbje(String str) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportChjg(int sta) {
			// TODO Auto-generated method stub
			status=sta;//出货结果	
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<Fragment出货结果"+"device=["+cabinetvar+"],hdid=["+huodaoNo+"],status=["+status+"]","log.txt");	
			//1.更新出货结果
			//扣除存货余量
			chuhuoupdate(cabinetvar,huodaoNo);
			//出货成功
			if(status==1)
			{
				txtbushuoname.setText(proID+"["+prosales+"]"+"->出货完成，请到"+cabinetvar+"柜"+huodaoNo+"货道取商品");
				txtbushuoname.setTextColor(android.graphics.Color.BLUE);
				chuhuoLog(1);//记录日志
			}
			else
			{
				txtbushuoname.setText(proID+"["+prosales+"]"+"->"+cabinetvar+"柜"+huodaoNo+"货道出货失败，未扣钱");
				txtbushuoname.setTextColor(android.graphics.Color.RED);
				chuhuoLog(0);//记录日志
			}
									
			//3.退回找零页面
			listterner.BushuoFinish(status);//步骤二、fragment向activity发送回调信息  		
//			ivbushuoquhuo.setVisibility(View.VISIBLE);
// 	    	new Handler().postDelayed(new Runnable() 
//			{
//                @Override
//                public void run() 
//                {	   
//                	//退出时，返回intent
//    	            Intent intent=new Intent();
//    	            intent.putExtra("status", status);//出货结果
//                	if(zhifutype==0)//现金支付
//                	{                        			
//        	            setResult(BusZhiAmount.RESULT_CANCELED,intent);                    	            
//            		}
//                	else if(zhifutype==3)//支付宝二维码
//                	{
//        	            setResult(BusZhier.RESULT_CANCELED,intent);                    	            
//            		}
//                	else if(zhifutype==4)//微信扫描
//                	{                        			
//        	            setResult(BusZhiwei.RESULT_CANCELED,intent);                    	            
//            		}
//                	finish();
////                	//出货完成,把非现金模块去掉
////                	if(status==0)
////                	{
////                		if(BusZhier.BusZhierAct!=null)
////                			BusZhier.BusZhierAct.finish(); 
////                		if(BusZhiwei.BusZhiweiAct!=null)
////                			BusZhiwei.BusZhiweiAct.finish(); 
////                		OrderDetail.addLog(BusHuo.this);
////                	}
////                	//出货失败，退到非现金模块进行退币操作
////                	else
////                	{
////                		if(BusZhier.BusZhierAct!=null)
////                		{
////                			//退出时，返回intent
////            	            Intent intent=new Intent();
////            	            setResult(BusZhier.RESULT_CANCELED,intent);
////                		}
////                		if(BusZhiwei.BusZhiweiAct!=null)
////                		{
////                			//退出时，返回intent
////            	            Intent intent=new Intent();
////            	            setResult(BusZhiwei.RESULT_CANCELED,intent);
////                		}
////					}		                        	
//                    
//                }
//
//			}, SPLASH_DISPLAY_LENGHT);
		}

		@Override
		public void BusportSend(String str) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportMovie() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportAds() {
			// TODO Auto-generated method stub
			
		}
	}
}
