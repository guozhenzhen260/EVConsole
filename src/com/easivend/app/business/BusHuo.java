package com.easivend.app.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.easivend.app.maintain.GoodsManager;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class BusHuo extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 10000; // 延迟10秒
	private String proID = null;
	private String productID = null;
	private String proType = null;
	private String cabID = null;
	private String huoID = null;
    private float prosales = 0;
    private int count = 0;
    private float reamin_amount = 0;
    private int zhifutype = 0;//0代表使用非现金,1代表使用现金
    private String data[][]=null;
    private ListView lvbushuo = null;
    //定义显示的内容包装，因为他有两列，因此就两个
    private List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
    private SimpleAdapter simpleada = null;//进行数据的转换操作
    private ImageView ivbushuoquhuo=null;
    private int tempx=0;
    private String draw=null,info=null;
    private int cabinetvar=0,huodaoNo=0,cabinetTypevar=0;
    private vmc_columnDAO columnDAO =null;
    private int huorst=0;
    //出货结果
    private int device=0;//出货柜号		
	private int status=0;//出货结果
	private int hdid=0;//货道id
	private int hdtype=0;//出货类型
	private float cost=0;//扣钱
	private float totalvalue=0;//剩余金额
	private int huodao=0;//剩余存货数量
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bushuo);	
		//删除前面的activity
		if(BusgoodsClass.BusgoodsClassAct!=null)
			BusgoodsClass.BusgoodsClassAct.finish(); 
		if(Busgoods.BusgoodsAct!=null)
			Busgoods.BusgoodsAct.finish(); 
		if(BusgoodsSelect.BusgoodsSelectAct!=null)
			BusgoodsSelect.BusgoodsSelectAct.finish(); 
		if(BusZhiSelect.BusZhiSelectAct!=null)
    		BusZhiSelect.BusZhiSelectAct.finish(); 
		if(BusZhiAmount.BusZhiAmountAct!=null)
			BusZhiAmount.BusZhiAmountAct.finish(); 
		
		//注册出货监听器
  	    EVprotocolAPI.setCallBack(new JNIInterface() {
			
			@Override
			public void jniCallback(Map<String, Integer> allSet) {
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<bushuo货道相关");
				Map<String, Integer> Set= allSet;
				int jnirst=Set.get("EV_TYPE");
				switch (jnirst)
				{
					case EVprotocolAPI.EV_TRADE_RPT://接收子线程消息
						device=allSet.get("device");//出货柜号
						status=allSet.get("status");//出货结果
						hdid=allSet.get("hdid");//货道id
						hdtype=allSet.get("type");//出货类型
						cost=ToolClass.MoneyRec(allSet.get("cost"));//扣钱
						totalvalue=ToolClass.MoneyRec(allSet.get("totalvalue"));//剩余金额
						huodao=allSet.get("huodao");//剩余存货数量
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<出货结果"+"device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
								+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");	
						if(status==0)
						{
							data[tempx][0]=String.valueOf(R.drawable.yes);
							data[tempx][1]=proID+"["+prosales+"]"+"->出货完成，请到"+cabinetvar+"柜"+huodaoNo+"货道取商品";
							//扣除存货余量
							chuhuoupdate(cabinetvar,huodaoNo);
						}
						else
						{
							data[tempx][0]=String.valueOf(R.drawable.no);
							data[tempx][1]=proID+"["+prosales+"]"+"->"+cabinetvar+"柜"+huodaoNo+"货道出货失败，未扣钱";
							//扣除存货余量
							chuhuoupdate(cabinetvar,huodaoNo);
						}
						updateListview();
						tempx++;
						huorst=0;
						while((huorst!=1)&&(tempx<count))
				 	    {
				 	    	huorst=chuhuoopt(tempx);
				 	    	if(huorst==2)
							{
								data[tempx][0]=String.valueOf(R.drawable.yes);
								data[tempx][1]=proID+"["+prosales+"]"+"->出货完成，请到"+cabinetvar+"柜"+huodaoNo+"货道取商品";
								updateListview();
								tempx++;
								//扣除存货余量
								chuhuoupdate(cabinetvar,huodaoNo);
							}
							else if(huorst==0)
							{
								data[tempx][0]=String.valueOf(R.drawable.no);
								data[tempx][1]=proID+"["+prosales+"]"+"->"+cabinetvar+"柜"+huodaoNo+"货道出货失败，未扣钱";
								updateListview();
								tempx++;
								//扣除存货余量
								chuhuoupdate(cabinetvar,huodaoNo);
							}
				 	    }
						if(tempx>=count)
				 	    {
							ivbushuoquhuo.setVisibility(View.VISIBLE);
				 	    	new Handler().postDelayed(new Runnable() 
							{
		                        @Override
		                        public void run() 
		                        {
		                        	//出货完成,把非现金模块去掉
		                        	if(status==0)
		                        	{
		                        		if(BusZhier.BusZhierAct!=null)
		                        			BusZhier.BusZhierAct.finish(); 
		                        		if(BusZhiwei.BusZhiweiAct!=null)
		                        			BusZhiwei.BusZhiweiAct.finish(); 
		                        	}
		                        	//出货失败，退到非现金模块进行退币操作
		                        	else
		                        	{
		                        		if(BusZhier.BusZhierAct!=null)
		                        		{
		                        			//退出时，返回intent
		                    	            Intent intent=new Intent();
		                    	            setResult(BusZhier.RESULT_CANCELED,intent);
		                        		}
		                        		if(BusZhiwei.BusZhiweiAct!=null)
		                        		{
		                        			//退出时，返回intent
		                    	            Intent intent=new Intent();
		                    	            setResult(BusZhiwei.RESULT_CANCELED,intent);
		                        		}
									}
		                            finish();
		                        }

							}, SPLASH_DISPLAY_LENGHT);
				 	    }
						break;					
				}
			}
		}); 
  	    
		//从商品页面中取得锁选中的商品
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		proID=bundle.getString("proID");
		productID=bundle.getString("productID");
		proType=bundle.getString("proType");
		cabID=bundle.getString("cabID");
		huoID=bundle.getString("huoID");
		prosales=Float.parseFloat(bundle.getString("prosales"));
		count=Integer.parseInt(bundle.getString("count"));
		reamin_amount=Float.parseFloat(bundle.getString("reamin_amount"));
		zhifutype=Integer.parseInt(bundle.getString("zhifutype"));
		        
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
				+productID+" proType="
				+proType+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
				+count+" reamin_amount="+reamin_amount+" zhifutype="+zhifutype);
		this.data=new String[count][2];
		draw=String.valueOf(R.drawable.shuaxin);
		info=proID+"["+prosales+"]"+"->等待出货";
		for(int x=0;x<count;x++)
		{
			data[x][0]=draw;
			data[x][1]=info;
		}
		this.lvbushuo =(ListView) super.findViewById(R.id.lvbushuo);		
		updateListview();
		this.ivbushuoquhuo =(ImageView) super.findViewById(R.id.ivbushuoquhuo);
		ivbushuoquhuo.setVisibility(View.GONE);
		
		//*******************
		//按顺序出货
		//*******************
		// 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_column表中
 	    columnDAO = new vmc_columnDAO(this);
 	    while((huorst!=1)&&(tempx<count))
 	    {
 	    	huorst=chuhuoopt(tempx);
 	    	if(huorst==2)
			{
				data[tempx][0]=String.valueOf(R.drawable.yes);
				data[tempx][1]=proID+"["+prosales+"]"+"->出货完成，请到"+cabinetvar+"柜"+huodaoNo+"货道取商品";
				updateListview();
				tempx++;	
				//扣除存货余量
				chuhuoupdate(cabinetvar,huodaoNo);
			}
			else if(huorst==0)
			{
				data[tempx][0]=String.valueOf(R.drawable.no);
				data[tempx][1]=proID+"["+prosales+"]"+"->"+cabinetvar+"柜"+huodaoNo+"货道出货失败，未扣钱";
				updateListview();
				tempx++;
				//扣除存货余量
				chuhuoupdate(cabinetvar,huodaoNo);
			}
 	    }
 	    if(tempx>=count)
 	    {
 	    	ivbushuoquhuo.setVisibility(View.VISIBLE);
 	    	//延时10s
 	    	new Handler().postDelayed(new Runnable() 
			{
                @Override
                public void run() 
                {
                	//出货完成,把非现金模块去掉
                	if(BusZhier.BusZhierAct!=null)
            			BusZhier.BusZhierAct.finish(); 	
                	if(BusZhiwei.BusZhiweiAct!=null)
            			BusZhiwei.BusZhiweiAct.finish(); 
                    finish();
                }

			}, SPLASH_DISPLAY_LENGHT);
 	    }
	}
	
	//加载出货信息到列表中
	private void updateListview()
	{
		int x=0;
		this.listMap.clear();
		for(x=0;x<count;x++)
		{
		  	Map<String,String> map = new HashMap<String,String>();//定义Map集合，保存每一行数据
		   	map.put("ivbushuostatus", data[x][0]);//产品名称
	    	map.put("txtbushuoname", data[x][1]);//产品状态
	    	this.listMap.add(map);//保存数据行
		}
		//将这个构架加载到data_list中
		this.simpleada = new SimpleAdapter(this,this.listMap,R.layout.bushuolist,
		    		new String[]{"ivbushuostatus","txtbushuoname"},//Map中的key名称
		    		new int[]{R.id.ivbushuostatus,R.id.txtbushuoname});
		this.lvbushuo.setAdapter(this.simpleada);
	}
	
	//出货,返回值0失败,1出货指令成功，等待返回结果,2出货完成
	private int chuhuoopt(int huox)
	{
		int huorst=0;
		int rst=0;
		data[huox][1]=proID+"["+prosales+"]"+"->正在出货,请稍候...";
		updateListview();
		//1.计算出出货货道
		//按商品id出货
		if(proType.equals("1")==true)
		{
	 	    // 获取所有收入信息，并存储到Map集合中
			List<String> alllist = columnDAO.getproductColumn(productID);
			cabinetvar=Integer.parseInt(alllist.get(0));
			huodaoNo=Integer.parseInt(alllist.get(1));
			cabinetTypevar=Integer.parseInt(alllist.get(2));
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品cabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar); 
		}
		//按货道id出货
		else if(proType.equals("2")==true)
		{
	 	    // 获取所有收入信息，并存储到Map集合中
			String alllist = columnDAO.getcolumnType(cabID);
			cabinetvar=Integer.parseInt(cabID);
			huodaoNo=Integer.parseInt(huoID);
			cabinetTypevar=Integer.parseInt(alllist);
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品cabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar); 
		}
		//2.计算出出货是否要用金额
		int typevar=0;
		float sales=0;
		//非现金
		if(zhifutype==0)
		{
			sales=0;
		}
		//现金
		else if(zhifutype==1)
		{
			sales=prosales;
		}
		//计算出类型
		if(sales>0)
			typevar=0;
		else 
			typevar=2;
		
		//格子柜
		if(cabinetTypevar==5)
		{
			rst=EVprotocolAPI.bentoOpen(cabinetvar,huodaoNo);
			if(rst==0)//出货发送失败
			{
				huorst=0;
			}
			else if(rst==1)//出货发送成功
			{
				huorst=2;
			}
			
		}
		//普通柜
		else 
		{
			rst=EVprotocolAPI.trade(cabinetvar,huodaoNo,typevar,
		    			ToolClass.MoneySend(sales));
			if(rst==0)//出货发送失败
			{
				huorst=0;
			}
			else if(rst==1)//出货发送成功
			{
				huorst=1;
			}
		}
		return huorst;
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
}
