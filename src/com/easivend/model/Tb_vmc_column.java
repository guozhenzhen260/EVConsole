/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_column 货道表      
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;

import java.util.Date;

public class Tb_vmc_column
{
	private String cabineID;// 货柜号
    private String columnID;// 货道号
    private String tihuoPwd;// 货道号
    private String productID;// 商品ID
    private int pathCount;// 货道总数
    private int pathRemain;// 货道剩余商品数量
    private int columnStatus;// 货道状态0,未设置,1正确，2故障，3卖完
    private String lasttime;//货道最新更新时间
    private int path_id;//下载的编号
    private int isupload;//是否已经上传，1是，0不是
 // 定义有参构造函数，用来初始化收入信息实体类中的各个字段
	public Tb_vmc_column(String cabineID, String columnID, String tihuoPwd,String productID,
			int pathCount, int pathRemain, int columnStatus, String lasttime,
			int path_id, int isupload) {
		super();
		this.cabineID = cabineID;
		this.columnID = columnID;
		this.tihuoPwd = tihuoPwd;
		this.productID = productID;
		this.pathCount = pathCount;
		this.pathRemain = pathRemain;
		this.columnStatus = columnStatus;
		this.lasttime = lasttime;
		this.path_id = path_id;
		this.isupload = isupload;
	}
	
	public String getTihuoPwd() {
		return tihuoPwd;
	}

	public void setTihuoPwd(String tihuoPwd) {
		this.tihuoPwd = tihuoPwd;
	}

	public String getCabineID() {
		return cabineID;
	}
	public void setCabineID(String cabineID) {
		this.cabineID = cabineID;
	}
	public String getColumnID() {
		return columnID;
	}
	public void setColumnID(String columnID) {
		this.columnID = columnID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public int getPathCount() {
		return pathCount;
	}
	public void setPathCount(int pathCount) {
		this.pathCount = pathCount;
	}
	public int getPathRemain() {
		return pathRemain;
	}
	public void setPathRemain(int pathRemain) {
		this.pathRemain = pathRemain;
	}
	public int getColumnStatus() {
		return columnStatus;
	}
	public void setColumnStatus(int columnStatus) {
		this.columnStatus = columnStatus;
	}
	public String getLasttime() {
		return lasttime;
	}
	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}
	public int getPath_id() {
		return path_id;
	}
	public void setPath_id(int path_id) {
		this.path_id = path_id;
	}
	public int getIsupload() {
		return isupload;
	}
	public void setIsupload(int isupload) {
		this.isupload = isupload;
	}
    
}
