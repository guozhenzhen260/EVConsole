/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_order_pay 订单支付表          
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;

import java.util.Date;

public class Tb_vmc_order_pay 
{
	private String ordereID;// 订单ID[pk]
	private int payType;// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
	private int payStatus;// 订单状态0出货成功，1出货失败，2支付失败，3未支付
	private int RealStatus;// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
	private float smallNote;// 纸币金额
	private float smallConi;// 硬币金额
	private float smallAmount;// 现金投入金额
	private float smallCard;// 非现金支付金额
	private float shouldPay;// 商品总金额
	private int shouldNo;// 商品总数量
	private float realNote;// 纸币退币金额
	private float realCoin;// 硬币退币金额
	private float realAmount;// 现金退币金额
	private float debtAmount;// 欠款金额
	private float realCard;// 非现金退币金额
	private Date payTime;//支付时间
	// 定义有参构造函数，用来初始化收入信息实体类中的各个字段
	public Tb_vmc_order_pay(String ordereID, int payType, int payStatus,
			int realStatus, float smallNote, float smallConi,
			float smallAmount, float smallCard, float shouldPay, int shouldNo,
			float realNote, float realCoin, float realAmount, float debtAmount,
			float realCard, Date payTime) {
		super();
		this.ordereID = ordereID;
		this.payType = payType;
		this.payStatus = payStatus;
		RealStatus = realStatus;
		this.smallNote = smallNote;
		this.smallConi = smallConi;
		this.smallAmount = smallAmount;
		this.smallCard = smallCard;
		this.shouldPay = shouldPay;
		this.shouldNo = shouldNo;
		this.realNote = realNote;
		this.realCoin = realCoin;
		this.realAmount = realAmount;
		this.debtAmount = debtAmount;
		this.realCard = realCard;
		this.payTime = payTime;
	}
	public String getOrdereID() {
		return ordereID;
	}
	public void setOrdereID(String ordereID) {
		this.ordereID = ordereID;
	}
	public int getPayType() {
		return payType;
	}
	public void setPayType(int payType) {
		this.payType = payType;
	}
	public int getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(int payStatus) {
		this.payStatus = payStatus;
	}
	public int getRealStatus() {
		return RealStatus;
	}
	public void setRealStatus(int realStatus) {
		RealStatus = realStatus;
	}
	public float getSmallNote() {
		return smallNote;
	}
	public void setSmallNote(float smallNote) {
		this.smallNote = smallNote;
	}
	public float getSmallConi() {
		return smallConi;
	}
	public void setSmallConi(float smallConi) {
		this.smallConi = smallConi;
	}
	public float getSmallAmount() {
		return smallAmount;
	}
	public void setSmallAmount(float smallAmount) {
		this.smallAmount = smallAmount;
	}
	public float getSmallCard() {
		return smallCard;
	}
	public void setSmallCard(float smallCard) {
		this.smallCard = smallCard;
	}
	public float getShouldPay() {
		return shouldPay;
	}
	public void setShouldPay(float shouldPay) {
		this.shouldPay = shouldPay;
	}
	public int getShouldNo() {
		return shouldNo;
	}
	public void setShouldNo(int shouldNo) {
		this.shouldNo = shouldNo;
	}
	public float getRealNote() {
		return realNote;
	}
	public void setRealNote(float realNote) {
		this.realNote = realNote;
	}
	public float getRealCoin() {
		return realCoin;
	}
	public void setRealCoin(float realCoin) {
		this.realCoin = realCoin;
	}
	public float getRealAmount() {
		return realAmount;
	}
	public void setRealAmount(float realAmount) {
		this.realAmount = realAmount;
	}
	public float getDebtAmount() {
		return debtAmount;
	}
	public void setDebtAmount(float debtAmount) {
		this.debtAmount = debtAmount;
	}
	public float getRealCard() {
		return realCard;
	}
	public void setRealCard(float realCard) {
		this.realCard = realCard;
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	
}
