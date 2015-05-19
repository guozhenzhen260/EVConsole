/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_class 商品类别表           
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;

import java.util.Date;

public class Tb_vmc_class 
{
	private String classID;// 商品类别ID
	private String className;// 商品类别名称	
	private String classTime;//最后更新时间
	private String attBatch1;// 商品图片路径
	// 定义有参构造函数，用来初始化收入信息实体类中的各个字段
	public Tb_vmc_class(String classID, String className,String classTime,String attBatch1) 
	{
		super();
		this.classID = classID;
		this.className = className;		
		this.classTime = classTime;
		this.attBatch1 = attBatch1;
	}
	public String getClassID() {
		return classID;
	}
	public void setClassID(String classID) {
		this.classID = classID;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}	
	public String getClassTime() {
		return classTime;
	}
	public void setClassTime(String classTime) {
		this.classTime = classTime;
	}
	public String getAttBatch1() {
		return attBatch1;
	}
	public void setAttBatch1(String attBatch1) {
		this.attBatch1 = attBatch1;
	}
	
}
