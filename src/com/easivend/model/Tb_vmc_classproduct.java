/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        Tb_vmc_classproduct 商品与类别关联表           
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;

import java.util.Date;

public class Tb_vmc_classproduct 
{
	private String classID;// 商品类别ID
	private String productID;// 商品编号	
	private Date classTime;//最后更新时间
	// 定义有参构造函数，用来初始化收入信息实体类中的各个字段
	public Tb_vmc_classproduct(String classID, String productID, Date classTime) {
		super();
		this.classID = classID;
		this.productID = productID;
		this.classTime = classTime;
	}
	public String getClassID() {
		return classID;
	}
	public void setClassID(String classID) {
		this.classID = classID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public Date getClassTime() {
		return classTime;
	}
	public void setClassTime(Date classTime) {
		this.classTime = classTime;
	}
	
}
