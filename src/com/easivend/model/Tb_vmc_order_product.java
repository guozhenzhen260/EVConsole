/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_order_product 订单详细信息表      
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;

public class Tb_vmc_order_product 
{
	private String orderID;// 订单ID
    private String productID;// 商品ID
    private int yujiHuo;//预计出货:数量1个
    private int realHuo;//实际出货: 1或者0
    private String cabID;//货柜号
    private String columnID;//货道号
    private int huoStatus;//出货状态: 0出货成功，1出货失败
    // 定义有参构造函数，用来初始化收入信息实体类中的各个字段
    public Tb_vmc_order_product(String orderID, String productID, int yujiHuo,
    		int realHuo, String cabID, String columnID, int huoStatus) 
	{
		super();
		this.orderID = orderID;
		this.productID = productID;
		this.yujiHuo = yujiHuo;
		this.realHuo = realHuo;
		this.cabID = cabID;
		this.columnID = columnID;
		this.huoStatus = huoStatus;
	}
	public String getOrderID() {
		return orderID;
	}
	
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public int getYujiHuo() {
		return yujiHuo;
	}
	public void setYujiHuo(int yujiHuo) {
		this.yujiHuo = yujiHuo;
	}
	public int getRealHuo() {
		return realHuo;
	}
	public void setRealHuo(int realHuo) {
		this.realHuo = realHuo;
	}
	public String getCabID() {
		return cabID;
	}
	public void setCabID(String cabID) {
		this.cabID = cabID;
	}
	public String getColumnID() {
		return columnID;
	}
	public void setColumnID(String columnID) {
		this.columnID = columnID;
	}
	public int getHuoStatus() {
		return huoStatus;
	}
	public void setHuoStatus(int huoStatus) {
		this.huoStatus = huoStatus;
	}
    
}
