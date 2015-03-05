/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           Vmc_ProductAdapter.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        读取商品表中的所有商品信息，补充到商品数据结构数组中   
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.common;

import java.util.List;

import android.content.Context;

import com.easivend.dao.vmc_productDAO;
import com.easivend.model.Tb_vmc_product;

public class Vmc_ProductAdapter
{	 
	 private String[] proID = null;
	 private String[] productID = null;
     private String[] proImage = null;
     private String[] promarket = null;
     private String[] prosales = null;
     
     // 商品表中的所有商品信息补充到商品数据结构数组中
    public void showProInfo(Context context,String param,String sort) 
 	{
 	    //ArrayAdapter<String> arrayAdapter = null;// 创建ArrayAdapter对象
 	    List<Tb_vmc_product> listinfos=null;//数据表list类集
 	    // 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_product表中
 	    vmc_productDAO productdao = new vmc_productDAO(context);
 	    if(param.isEmpty()==true)
 	    {
 		    // 获取所有收入信息，并存储到List泛型集合中
 		    listinfos = productdao.getScrollData(0, (int) productdao.getCount(),sort);
 	    }
 	    else
 	    {
 		    // 获取所有收入信息，并存储到List泛型集合中
 		    listinfos = productdao.getScrollData(param,sort);
 	    }
 	    proID = new String[listinfos.size()];// 设置字符串数组的长度
 	    productID = new String[listinfos.size()];// 设置字符串数组的长度
 	    proImage = new String[listinfos.size()];// 设置字符串数组的长度
 	    promarket = new String[listinfos.size()];// 设置字符串数组的长度
 	    prosales = new String[listinfos.size()];// 设置字符串数组的长度
 	    int m = 0;// 定义一个开始标识
 	    // 遍历List泛型集合
 	    for (Tb_vmc_product tb_inaccount : listinfos) 
 	    {
 	        // 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
 	    	proID[m] = tb_inaccount.getProductID()+"-"+tb_inaccount.getProductName();
 	    	productID[m] = tb_inaccount.getProductID();
 	    	proImage[m] = tb_inaccount.getAttBatch1();
 	    	promarket[m] = String.valueOf(tb_inaccount.getMarketPrice());
 	    	prosales[m] = String.valueOf(tb_inaccount.getSalesPrice());
 	    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+proID[m]+" marketPrice="
 					+promarket[m]+" salesPrice="+prosales[m]+" attBatch1="
 					+proImage[m]+" attBatch2="+tb_inaccount.getAttBatch2()+" attBatch3="+tb_inaccount.getAttBatch3());
 	        m++;// 标识加1
 	    }
 	    
 	}
	public String[] getProID() {
		return proID;
	}
	public void setProID(String[] proID) {
		this.proID = proID;
	}
	public String[] getProductID() {
		return productID;
	}
	public void setProductID(String[] productID) {
		this.productID = productID;
	}
	public String[] getProImage() {
		return proImage;
	}
	public void setProImage(String[] proImage) {
		this.proImage = proImage;
	}
	public String[] getPromarket() {
		return promarket;
	}
	public void setPromarket(String[] promarket) {
		this.promarket = promarket;
	}
	public String[] getProsales() {
		return prosales;
	}
	public void setProsales(String[] prosales) {
		this.prosales = prosales;
	}
    
}
