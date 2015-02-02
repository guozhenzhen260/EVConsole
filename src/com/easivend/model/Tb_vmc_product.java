/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_product 商品表         
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;

import java.util.Date;

public class Tb_vmc_product 
{
	private String productID;// 商品编号[PK]
	private String productName;// 商品全名
	private String productDesc;// 商品描述
	private float marketPrice;// 商品原价,如”20.00”
	private float salesPrice;// 优惠价,如”20.00”
	private int shelfLife;// 保质期
	private String downloadTime;//下载时间
	private String onloadTime;//本地修改时间,时间字符串如”2013-01-01T12:00:00”
	private String attBatch1;// 商品图片路径1
	private String attBatch2;// 商品图片路径2
	private String attBatch3;// 商品图片路径3
	private int paixu;// 排序
	private int isdelete;// 是否删除（0不删除，1删除）
	// 定义有参构造函数，用来初始化收入信息实体类中的各个字段
	public Tb_vmc_product(String productID, String productName,
			String productDesc, float marketPrice, float salesPrice,
			int shelfLife, String downloadTime, String onloadTime,
			String attBatch1, String attBatch2, String attBatch3, int paixu,
			int isdelete) {
		super();
		this.productID = productID;
		this.productName = productName;
		this.productDesc = productDesc;
		this.marketPrice = marketPrice;
		this.salesPrice = salesPrice;
		this.shelfLife = shelfLife;
		this.downloadTime = downloadTime;
		this.onloadTime = onloadTime;
		this.attBatch1 = attBatch1;
		this.attBatch2 = attBatch2;
		this.attBatch3 = attBatch3;
		this.paixu = paixu;
		this.isdelete = isdelete;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public float getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(float marketPrice) {
		this.marketPrice = marketPrice;
	}
	public float getSalesPrice() {
		return salesPrice;
	}
	public void setSalesPrice(float salesPrice) {
		this.salesPrice = salesPrice;
	}
	public int getShelfLife() {
		return shelfLife;
	}
	public void setShelfLife(int shelfLife) {
		this.shelfLife = shelfLife;
	}
	public String getDownloadTime() {
		return downloadTime;
	}
	public void setDownloadTime(String downloadTime) {
		this.downloadTime = downloadTime;
	}
	public String getOnloadTime() {
		return onloadTime;
	}
	public void setOnloadTime(String onloadTime) {
		this.onloadTime = onloadTime;
	}
	public String getAttBatch1() {
		return attBatch1;
	}
	public void setAttBatch1(String attBatch1) {
		this.attBatch1 = attBatch1;
	}
	public String getAttBatch2() {
		return attBatch2;
	}
	public void setAttBatch2(String attBatch2) {
		this.attBatch2 = attBatch2;
	}
	public String getAttBatch3() {
		return attBatch3;
	}
	public void setAttBatch3(String attBatch3) {
		this.attBatch3 = attBatch3;
	}
	public int getPaixu() {
		return paixu;
	}
	public void setPaixu(int paixu) {
		this.paixu = paixu;
	}
	public int getIsdelete() {
		return isdelete;
	}
	public void setIsdelete(int isdelete) {
		this.isdelete = isdelete;
	}
	
	
}
