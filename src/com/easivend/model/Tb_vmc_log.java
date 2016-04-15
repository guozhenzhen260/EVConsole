/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_log 操作日志信息表 
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;


public class Tb_vmc_log 
{
	private String logID;// 操作ID[pk]
    private int logType;// 操作类型0添加,1修改,2删除
    private String logDesc;// 操作描述
    private String logTime;//操作时间
 // 定义有参构造函数，用来初始化收入信息实体类中的各个字段
	public Tb_vmc_log(String logID, int logType, String logDesc, String logTime) {
		super();
		this.logID = logID;
		this.logType = logType;
		this.logDesc = logDesc;
		this.logTime = logTime;
	}
	public String getLogID() {
		return logID;
	}
	public void setLogID(String logID) {
		this.logID = logID;
	}
	public int getLogType() {
		return logType;
	}
	public void setLogType(int logType) {
		this.logType = logType;
	}
	public String getLogDesc() {
		return logDesc;
	}
	public void setLogDesc(String logDesc) {
		this.logDesc = logDesc;
	}
	public String getLogTime() {
		return logTime;
	}
	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}
    
}
