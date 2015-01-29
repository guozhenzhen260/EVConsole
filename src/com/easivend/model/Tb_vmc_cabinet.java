/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_cabinet 柜类型表          
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;

public class Tb_vmc_cabinet 
{
	private String cabID;// 货柜号[pk]
    private int cabType;// 货柜类型,0无货道,1弹簧货道，2 老式升降台，3 升降台+传送带，4 升降台+弹簧，5格子柜
 // 定义有参构造函数，用来初始化收入信息实体类中的各个字段
	public Tb_vmc_cabinet(String cabID, int cabType) {
		super();
		this.cabID = cabID;
		this.cabType = cabType;
	}
	public String getCabID() {
		return cabID;
	}
	public void setCabID(String cabID) {
		this.cabID = cabID;
	}
	public int getCabType() {
		return cabType;
	}
	public void setCabType(int cabType) {
		this.cabType = cabType;
	}
    
}
