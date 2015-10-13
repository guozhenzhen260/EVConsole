package com.easivend.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.easivend.app.business.Busgoods;
import com.easivend.app.business.BusgoodsClass;
import com.easivend.app.business.BusgoodsSelect;
import com.easivend.app.maintain.MaintainActivity;
import com.easivend.app.maintain.ParamManager;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.fragment.MoviewlandFragment.MovieFragInteraction;
import com.easivend.model.Tb_vmc_product;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.DogService;
import com.example.evconsole.R;

import android.R.bool;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class BusinesslandFragment extends Fragment 
{	
	final static int REQUEST_CODE=1; 	
	EditText txtadsTip=null;
	ImageButton btnads1=null,btnads2=null,btnads3=null,btnads4=null,btnads5=null,btnads6=null,
			   btnads7=null,btnads8=null,btnads9=null,btnadscancel=null,btnadsenter=null;
	ImageButton btnadsclass=null,btnadscuxiao=null,btnadsbuysale=null,btnadsquhuo=null,btnads0=null;	
	Intent intent=null;
	private static int count=0;
	private static String huo="";
	//发送出货指令
    private String proID = null;
	private String productID = null;
	private String proImage = null;
	private String cabID = null;
	private String huoID = null;
    private String prosales = null; 
    private Context context;
    //密码框
    private static int pwdcount=0;
    
    //=========================
    //fragment与activity回调相关
    //=========================
    /**
     * 用来与外部activity交互的
     */
    private BusFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof MovieFragInteraction)
        {
            listterner = (BusFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BusFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface BusFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void finishBusiness();//关闭activity页面
        void gotoBusiness(int buslevel,Map<String, String>str);  //暂停倒计时定时器，并且跳转到商品页面
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
		View view = inflater.inflate(R.layout.fragment_businessland, container, false);  
		context=this.getActivity();//获取activity的context		
		//=======
		//操作模块
		//=======
		txtadsTip = (EditText) view.findViewById(R.id.txtadsTip);
		txtadsTip.setFocusable(false);//不让该edittext获得焦点
		txtadsTip.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// 关闭软键盘，这样当点击该edittext的时候，不会弹出系统自带的输入法
				txtadsTip.setInputType(InputType.TYPE_NULL);
				return false;
			}
		});
		btnads1 = (ImageButton) view.findViewById(R.id.btnads1);		
		btnads1.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("1",1);
		    }
		});
		btnads2 = (ImageButton) view.findViewById(R.id.btnads2);
		btnads2.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("2",1);
		    }
		});
		btnads3 = (ImageButton) view.findViewById(R.id.btnads3);
		btnads3.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("3",1);
		    }
		});
		btnads4 = (ImageButton) view.findViewById(R.id.btnads4);
		btnads4.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("4",1);
		    }
		});
		btnads5 = (ImageButton) view.findViewById(R.id.btnads5);
		btnads5.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("5",1);
		    }
		});
		btnads6 = (ImageButton) view.findViewById(R.id.btnads6);
		btnads6.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("6",1);
		    }
		});
		btnads7 = (ImageButton) view.findViewById(R.id.btnads7);
		btnads7.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("7",1);
		    }
		});
		btnads8 = (ImageButton) view.findViewById(R.id.btnads8);
		btnads8.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("8",1);
		    }
		});
		btnads9 = (ImageButton) view.findViewById(R.id.btnads9);
		btnads9.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("9",1);
		    }
		});
		btnads0 = (ImageButton) view.findViewById(R.id.btnads0);
		btnads0.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("0",1);
		    }
		});
		btnadscancel = (ImageButton) view.findViewById(R.id.btnadscancel);
		btnadscancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	chuhuo("0",0);
		    }
		});
		btnadsenter = (ImageButton) view.findViewById(R.id.btnadsenter);
		btnadsenter.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	passwd();
		    }
		});
		btnadsclass = (ImageButton) view.findViewById(R.id.btnadsclass);
		btnadsclass.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	vmc_classDAO classdao = new vmc_classDAO(context);// 创建InaccountDAO对象
		    	long count=classdao.getCount();
		    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品类型数量="+count,"log.txt");
		    	if(count>0)
		    	{
			    	//intent = new Intent(context, BusgoodsClass.class);// 使用Accountflag窗口初始化Intent
			    	//startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
		    		listterner.gotoBusiness(1,null);
		    	}
		    	else
		    	{
//		    		intent = new Intent(context, Busgoods.class);// 使用Accountflag窗口初始化Intent
//                	intent.putExtra("proclassID", "");
//                	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag		    		
                	listterner.gotoBusiness(2,null);
		    	}
		    	
		    }
		});
		return view;  
	}
	//num出货柜号,type=1输入数字，type=0回退数字
    private void chuhuo(String num,int type)
    {    	
		if(type==1)
		{
			if(count<3)
	    	{
	    		count++;
	    		huo=huo+num;
	    		txtadsTip.setText(huo);
	    	}
		}
		else if(type==0)
		{
			if(count>0)
			{
				count--;
				huo=huo.substring(0,huo.length()-1);
				if(count==0)
					txtadsTip.setText("");
				else
					txtadsTip.setText(huo);
			}
		}  
		if(count==3)
		{
			cabID=huo.substring(0,1);
		    huoID=huo.substring(1,huo.length());
		    vmc_columnDAO columnDAO = new vmc_columnDAO(context);// 创建InaccountDAO对象		    
		    Tb_vmc_product tb_inaccount = columnDAO.getColumnproduct(cabID,huoID);
		    if(tb_inaccount!=null)
		    {
			    productID=tb_inaccount.getProductID().toString();
			    prosales=String.valueOf(tb_inaccount.getSalesPrice());
			    proImage=tb_inaccount.getAttBatch1();
			    proID=productID+"-"+tb_inaccount.getProductName().toString();
			    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
						+productID+" proType="
						+"2"+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
						+"1","log.txt");
			    count=0;
			    huo="";
			    txtadsTip.setText("");
//				Intent intent = null;// 创建Intent对象                
//	        	intent = new Intent(context, BusgoodsSelect.class);// 使用Accountflag窗口初始化Intent
//	        	intent.putExtra("proID", proID);
//	        	intent.putExtra("productID", productID);
//	        	intent.putExtra("proImage", proImage);
//	        	intent.putExtra("prosales", prosales);
//	        	intent.putExtra("procount", "1");
//	        	intent.putExtra("proType", "2");//1代表通过商品ID出货,2代表通过货道出货
//	        	intent.putExtra("cabID", cabID);//出货柜号,proType=1时无效
//	        	intent.putExtra("huoID", huoID);//出货货道号,proType=1时无效
//
//
////	        	OrderDetail.setProID(proID);
////            	OrderDetail.setProductID(productID);
////            	OrderDetail.setProType("2");
////            	OrderDetail.setCabID(cabID);
////            	OrderDetail.setColumnID(huoID);
////            	OrderDetail.setShouldPay(Float.parseFloat(prosales));
////            	OrderDetail.setShouldNo(1);
//	        	
//	        	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
	        	Map<String, String>str=new HashMap<String, String>();
	        	str.put("proID", proID);
	        	str.put("productID", productID);
	        	str.put("proImage", proImage);
	        	str.put("prosales", prosales);
	        	str.put("procount", "1");
	        	str.put("proType", "2");//1代表通过商品ID出货,2代表通过货道出货
	        	str.put("cabID", cabID);//出货柜号,proType=1时无效
	        	str.put("huoID", huoID);//出货货道号,proType=1时无效
	        	listterner.gotoBusiness(3,str);
		    }
		    else
		    {
		    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
						+productID+" proType="
						+"2"+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
						+"1","log.txt");
			    count=0;
			    huo="";
			    txtadsTip.setText("");
			}
		    
		}
    } 
    
    //调出密码框
    private void passwd()
    {
    	pwdcount++;
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<pwdcount="+pwdcount,"log.txt");
    	if(pwdcount>=10)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<pwdinfo","log.txt");
    		pwdcount=0;
    		passdialog();
    	}
    }
    
    //密码框
    private void passdialog()
    {
    	View myview=null;  
    	
		// TODO Auto-generated method stub
		LayoutInflater factory = LayoutInflater.from(context);
		myview=factory.inflate(R.layout.selectinteger, null);
		final EditText dialoginte=(EditText) myview.findViewById(R.id.dialoginte);
		
		Dialog dialog = new AlertDialog.Builder(context)
		.setTitle("设置")
		.setPositiveButton("保存", new DialogInterface.OnClickListener() 	
		{
				
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				boolean istrue=false;
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<数值="+dialoginte.getText().toString(),"log.txt");
				//调出维护页面密码
				vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
			    // 获取所有收入信息，并存储到List泛型集合中
		    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
		    	if(tb_inaccount!=null)
		    	{
		    		String Pwd=tb_inaccount.getMainPwd().toString();
		    		if(Pwd==null)
		    		{
		    			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<数值=null","log.txt");
		    			istrue=passcmp(null,dialoginte.getText().toString());
		    		}
		    		else
		    		{
		    			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<数值="+Pwd,"log.txt");
		    			istrue=passcmp(Pwd,dialoginte.getText().toString());
		    		}
		    	}
		    	else
		    	{
		    		istrue=passcmp(null,dialoginte.getText().toString());
				}
		    	
		    	if(istrue)
		    	{
		    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<确定退出","log.txt");
		    		//步骤二、fragment向activity发送回调信息
		        	listterner.finishBusiness();
		    	}
			}
		})
		.setNegativeButton("取消",  new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
    	{			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// TODO Auto-generated method stub				
			}
    	})
		.setView(myview)//这里将对话框布局文件加入到对话框中
		.create();
		dialog.show();
    }
    
    //密码框比对
    private boolean passcmp(String pwd,String value)
    {
    	boolean istrue=false;
    	if((pwd==null)||(pwd.equals("")==true))
    	{
    		if(value.equals("83718557"))
    		{
    			istrue=true;
    		}
    	}
    	else
    	{
    		if(value.equals(pwd))
    		{
    			istrue=true;
    		}
    	}
    	return istrue;
    }
}
