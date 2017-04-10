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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.model.Tb_vmc_product;

public class Vmc_ProductAdapter
{	 
	 private String[] proID = null;
	 private String[] productID = null;
	 private String[] productName = null;
     private String[] proImage = null;
     private String[] promarket = null;
     private String[] prosales = null;
     private String[] procount = null;
     List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
     
     // 商品表中的所有商品信息补充到商品数据结构数组中
     //param过滤条件,sort排序条件,classID分类信息
    public void showProInfo(Context context,String param,String sort,String classID) 
 	{
 	    //ArrayAdapter<String> arrayAdapter = null;// 创建ArrayAdapter对象
 	    List<Tb_vmc_product> listinfos=null;//数据表list类集
 	    // 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_product表中
 	    vmc_productDAO productdao = new vmc_productDAO(context);
 	    //无过滤条件查询
 	    if(ToolClass.isEmptynull(param)==true)
 	    {
 	    	//分类查询
 	    	if(ToolClass.isEmptynull(classID)!=true)
 	    	{
 			    // 获取所有收入信息，并存储到List泛型集合中
 			    listinfos = productdao.getScrollData(classID);
 		    }	
 	    	else
 	    	{
	 		    // 获取所有收入信息，并存储到List泛型集合中
	 		    listinfos = productdao.getScrollData(0, (int) productdao.getCount(),sort);
 	    	}
 	    }
 	    //有过滤条件查询
 	    else 
 	    {
 		    // 获取所有收入信息，并存储到List泛型集合中
 		    listinfos = productdao.getScrollData(param,sort);
 	    }
 	    proID = new String[listinfos.size()];// 设置字符串数组的长度
 	    productID = new String[listinfos.size()];// 设置字符串数组的长度
 	    productName = new String[listinfos.size()];// 设置字符串数组的长度
 	    proImage = new String[listinfos.size()];// 设置字符串数组的长度
 	    promarket = new String[listinfos.size()];// 设置字符串数组的长度
 	    prosales = new String[listinfos.size()];// 设置字符串数组的长度
 	    procount = new String[listinfos.size()];// 设置字符串数组的长度
 	    int m = 0;// 定义一个开始标识 	
 	    list.clear();
 	    // 遍历List泛型集合
 	    for (Tb_vmc_product tb_inaccount : listinfos) 
 	    {
 	    	Map<String,Object> map=new HashMap<String, Object>();
 	        // 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
 	    	map.put("proID", tb_inaccount.getProductID()+"-"+tb_inaccount.getProductName());
 	    	map.put("productID", tb_inaccount.getProductID());
 	    	map.put("productName", tb_inaccount.getProductName());
 	    	map.put("proImage", tb_inaccount.getAttBatch1());
 	    	map.put("promarket", String.valueOf(tb_inaccount.getMarketPrice()));
 	    	map.put("prosales", String.valueOf(tb_inaccount.getSalesPrice()));
 	    	//得到这个商品id对应的存货数量
	    	if(tb_inaccount.getProductID()!=null)
	    	{
	    		vmc_columnDAO columnDAO = new vmc_columnDAO(context);// 创建InaccountDAO对象
    		    // 获取所有收入信息，并存储到List泛型集合中
    		    map.put("procount", String.valueOf(columnDAO.getproductCount(tb_inaccount.getProductID())));
	    	}	    		    	
	    	list.add(map); 	    	    	
 	    }
 	    try {
			list=ToolClass.listSort(list);
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品sort="+list.toString(),"log.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	    for(Map<String,Object> map:list)  
        {  
 	        // 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
 	    	proID[m] = map.get("proID").toString();
 	    	productID[m] = map.get("productID").toString();
 	    	productName[m] = map.get("productName").toString();
 	    	proImage[m] = map.get("proImage").toString();
 	    	promarket[m] = map.get("promarket").toString();
 	    	prosales[m] = map.get("prosales").toString();
 	    	procount[m] = map.get("procount").toString();
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
	public String[] getProcount() {
		return procount;
	}
	public void setProcount(String[] procount) {
		this.procount = procount;
	}
	public String[] getProductName() {
		return productName;
	}
	public void setProductName(String[] productName) {
		this.productName = productName;
	}
    
}
