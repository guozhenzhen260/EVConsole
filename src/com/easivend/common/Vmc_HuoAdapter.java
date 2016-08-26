package com.easivend.common;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import android.content.Context;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.model.Tb_vmc_column;
import com.easivend.model.Tb_vmc_product;

public class Vmc_HuoAdapter 
{
	private String[] huoID = null;
	private String[] huoproID = null;
    private String[] huoRemain = null;
    private String[] huolasttime = null;
    private String[] huoname = null;
    private String[] proImage = null;
    // 商品表中的所有商品信息补充到商品数据结构数组中
    public void showProInfo(Context context,String param,Map<String, Integer> set,String cabID) 
 	{
 	    List<Tb_vmc_column> listinfos=null;//数据表list类集
 	    // 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_column表中
 	    vmc_columnDAO columnDAO = new vmc_columnDAO(context);
 	    vmc_productDAO productDAO = new vmc_productDAO(context);// 创建InaccountDAO对象
	    
 	    // 获取所有收入信息，并存储到List泛型集合中
 		listinfos = columnDAO.getScrollData(cabID);
 	   
    	
 	    huoID = new String[set.size()];// 设置字符串数组的长度
 	    huoproID = new String[set.size()];// 设置字符串数组的长度
 	    huoRemain = new String[set.size()];// 设置字符串数组的长度
 	    huolasttime = new String[set.size()];// 设置字符串数组的长度 	
 	    huoname = new String[set.size()];// 设置字符串数组的长度 	
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
           huoname[m]="";
           //System.out.println(me.getKey()+"--"+me.getValue());
           //遍历货道对应的商品信息
           // 遍历List泛型集合
    	   for (Tb_vmc_column tb_inaccount : listinfos) 
    	   {
    	    	if(
    	    			(cabID.equals(tb_inaccount.getCabineID())==true)
    	    			&&(huoID[m].equals(tb_inaccount.getColumnID())==true)
    	          )
    	    	{
	    	    	// 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
	    	    	huoproID[m] = tb_inaccount.getProductID();
	    	    	huoRemain[m] = String.valueOf(tb_inaccount.getPathRemain());
	    	    	huolasttime[m] = tb_inaccount.getLasttime().substring(0, 10);  
	    	    	//得到这个商品id对应的图片,和名称
	    	    	if(huoproID[m].equals("0")!=true)
	    	    	{
		    	    	// 获取所有收入信息，并存储到List泛型集合中
		    		    Tb_vmc_product tb_product = productDAO.find(huoproID[m]);
		    		    if(tb_product!=null)
		    		    {
		    		    	proImage[m] = tb_product.getAttBatch1().toString();
		    		    	huoname[m] = tb_product.getProductName().toString();
		    		    }
	    	    	}
	    	    	break;
    	    	}
    	   }
           m++;// 标识加1
       } 
 	    
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
	public String[] getHuoname() {
		return huoname;
	}
	public void setHuoname(String[] huoname) {
		this.huoname = huoname;
	}
	
    
}
