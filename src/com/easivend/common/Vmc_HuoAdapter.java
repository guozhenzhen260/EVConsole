package com.easivend.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;

import com.easivend.dao.vmc_productDAO;
import com.easivend.model.Tb_vmc_product;

public class Vmc_HuoAdapter 
{
	private String[] huoID = null;
	private String[] huoproID = null;
    private String[] huoRemain = null;
    private String[] huolasttime = null;
    private String[] proImage = null;
    // 商品表中的所有商品信息补充到商品数据结构数组中
    public void showProInfo(Context context,String param,Map<String, Integer> set) 
 	{
// 	    List<Tb_vmc_product> listinfos=null;//数据表list类集
// 	    // 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_product表中
// 	    vmc_productDAO productdao = new vmc_productDAO(context);
// 	    if(param.isEmpty()==true)
// 	    {
// 		    // 获取所有收入信息，并存储到List泛型集合中
// 		    listinfos = productdao.getScrollData(0, (int) productdao.getCount(),sort);
// 	    }
// 	    else
// 	    {
// 		    // 获取所有收入信息，并存储到List泛型集合中
// 		    listinfos = productdao.getScrollData(param,sort);
// 	    }
    	
 	    huoID = new String[set.size()];// 设置字符串数组的长度
 	    huoproID = new String[set.size()];// 设置字符串数组的长度
 	    huoRemain = new String[set.size()];// 设置字符串数组的长度
 	    huolasttime = new String[set.size()];// 设置字符串数组的长度 	 
 	    proImage = new String[set.size()];// 设置字符串数组的长度 	 
 	    int m = 0;// 定义一个开始标识
 	    //输出全部内容
 	   Set<Entry<String, Integer>> allset=set.entrySet();  //实例化
       Iterator<Entry<String, Integer>> iter=allset.iterator();
       while(iter.hasNext())
       {
           Entry<String, Integer> me=iter.next();
           huoID[m]=me.getKey();
           huoproID[m]="0";
           huoRemain[m]="0";
           huolasttime[m]="0";
           proImage[m]="0";
           //System.out.println(me.getKey()+"--"+me.getValue());
           m++;// 标识加1
       } 
// 	    int m = 0;// 定义一个开始标识
// 	    // 遍历List泛型集合
// 	    for (Tb_vmc_product tb_inaccount : listinfos) 
// 	    {
// 	        // 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
// 	    	proID[m] = tb_inaccount.getProductID()+"-"+tb_inaccount.getProductName();
// 	    	productID[m] = tb_inaccount.getProductID();
// 	    	proImage[m] = tb_inaccount.getAttBatch1();
// 	    	promarket[m] = String.valueOf(tb_inaccount.getMarketPrice());
// 	    	prosales[m] = String.valueOf(tb_inaccount.getSalesPrice());
// 	    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+proID[m]+" marketPrice="
// 					+promarket[m]+" salesPrice="+prosales[m]+" attBatch1="
// 					+proImage[m]+" attBatch2="+tb_inaccount.getAttBatch2()+" attBatch3="+tb_inaccount.getAttBatch3());
// 	        m++;// 标识加1
// 	    }
 	    
 	}
	public String[] getHuoID() {
		return huoID;
	}
	public void setHuoID(String[] huoID) {
		this.huoID = huoID;
	}
	public String[] getHuoproID() {
		return huoproID;
	}
	public void setHuoproID(String[] huoproID) {
		this.huoproID = huoproID;
	}
	public String[] getHuoRemain() {
		return huoRemain;
	}
	public void setHuoRemain(String[] huoRemain) {
		this.huoRemain = huoRemain;
	}
	public String[] getHuolasttime() {
		return huolasttime;
	}
	public void setHuolasttime(String[] huolasttime) {
		this.huolasttime = huolasttime;
	}
	public String[] getProImage() {
		return proImage;
	}
	public void setProImage(String[] proImage) {
		this.proImage = proImage;
	}
    
}
