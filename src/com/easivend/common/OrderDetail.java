/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           OrderDetail.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        用来传订单信息，这里面存放的主要是static函数，static成员，统一作为全局变量和全局函数用       
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import com.easivend.dao.vmc_orderDAO;
import com.easivend.model.Tb_vmc_order_pay;
import com.easivend.model.Tb_vmc_order_product;

public class OrderDetail 
{ 
	//订单总信息
	private static String proID = "";//出货id+商品名
	private static String proType = "";//1代表通过商品ID出货,2代表通过货道出货	
	//订单支付表 
	private static String ordereID = "";// 订单ID[pk]
	private static int payType = 0;// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
	private static int payStatus = 0;// 订单状态0出货成功，1出货失败，2支付失败，3未支付
	private static int RealStatus = 0;// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
	private static float smallNote = 0;// 纸币金额
	private static float smallConi = 0;// 硬币金额
	private static float smallAmount = 0;// 现金投入金额
	private static float smallCard = 0;// 非现金支付金额
	private static float shouldPay = 0;// 商品总金额，就是商品单价
	private static int shouldNo = 0;// 商品总数量,就是1个
	private static float realNote = 0;// 纸币退币金额
	private static float realCoin = 0;// 硬币退币金额
	private static float realAmount = 0;// 现金退币金额
	private static float debtAmount = 0;// 欠款金额
	private static float realCard = 0;// 非现金退币金额
	//订单详细信息表      
	private static String productID = "";// 商品ID
    private static int yujiHuo = 0;//预计出货:数量1个
    private static int realHuo = 0;//实际出货: 1或者0
    private static String cabID = "";//货柜号
    private static String columnID = "";//货道号
    private static int huoStatus = 0;//出货状态: 0出货成功，1出货失败
    
    //保存日志
    public static void addLog(Context context)
	{
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    	String date=df.format(new Date());
		vmc_orderDAO orderDAO = new vmc_orderDAO(context);// 创建InaccountDAO对象
		Tb_vmc_order_pay tb_vmc_order_pay = new Tb_vmc_order_pay(ordereID, payType, payStatus,
					RealStatus, smallNote, smallConi,
					smallAmount, smallCard, shouldPay, shouldNo,
					realNote, realCoin, realAmount, debtAmount,
					realCard, date);
		Tb_vmc_order_product tb_vmc_order_product=new Tb_vmc_order_product(ordereID, productID, yujiHuo,
	    		realHuo, cabID, columnID, huoStatus);
		orderDAO.add(tb_vmc_order_pay, tb_vmc_order_product);
		cleardata();
	}
    //清除数据
    public static void cleardata()
    {
    	//订单总信息
    	 proID = "";//出货id+商品名
    	 proType = "";//1代表通过商品ID出货,2代表通过货道出货	
    	//订单支付表 
    	 ordereID = "";// 订单ID[pk]
    	 payType = 0;// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
    	 payStatus = 0;// 订单状态0出货成功，1出货失败，2支付失败，3未支付
    	 RealStatus = 0;// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
    	 smallNote = 0;// 纸币金额
    	 smallConi = 0;// 硬币金额
    	 smallAmount = 0;// 现金投入金额
    	 smallCard = 0;// 非现金支付金额
    	 shouldPay = 0;// 商品总金额，就是商品单价
    	 shouldNo = 0;// 商品总数量,就是1个
    	 realNote = 0;// 纸币退币金额
    	 realCoin = 0;// 硬币退币金额
    	 realAmount = 0;// 现金退币金额
    	 debtAmount = 0;// 欠款金额
    	 realCard = 0;// 非现金退币金额
    	//订单详细信息表      
    	 productID = "";// 商品ID
         yujiHuo = 0;//预计出货:数量1个
         realHuo = 0;//实际出货: 1或者0
         cabID = "";//货柜号
         columnID = "";//货道号
         huoStatus = 0;//出货状态: 0出货成功，1出货失败
    }
    
	public static String getProID() {
		return proID;
	}
	public static void setProID(String proID) {
		OrderDetail.proID = proID;
	}
	public static String getProType() {
		return proType;
	}
	public static void setProType(String proType) {
		OrderDetail.proType = proType;
	}	
	public static String getOrdereID() {
		return ordereID;
	}
	public static void setOrdereID(String ordereID) {
		OrderDetail.ordereID = ordereID;
	}
	public static int getPayType() {
		return payType;
	}
	public static void setPayType(int payType) {
		OrderDetail.payType = payType;
	}
	public static int getPayStatus() {
		return payStatus;
	}
	public static void setPayStatus(int payStatus) {
		OrderDetail.payStatus = payStatus;
	}
	public static int getRealStatus() {
		return RealStatus;
	}
	public static void setRealStatus(int realStatus) {
		RealStatus = realStatus;
	}
	public static float getSmallNote() {
		return smallNote;
	}
	public static void setSmallNote(float smallNote) {
		OrderDetail.smallNote = smallNote;
	}
	public static float getSmallConi() {
		return smallConi;
	}
	public static void setSmallConi(float smallConi) {
		OrderDetail.smallConi = smallConi;
	}
	public static float getSmallAmount() {
		return smallAmount;
	}
	public static void setSmallAmount(float smallAmount) {
		OrderDetail.smallAmount = smallAmount;
	}
	public static float getSmallCard() {
		return smallCard;
	}
	public static void setSmallCard(float smallCard) {
		OrderDetail.smallCard = smallCard;
	}
	public static float getShouldPay() {
		return shouldPay;
	}
	public static void setShouldPay(float shouldPay) {
		OrderDetail.shouldPay = shouldPay;
	}
	public static int getShouldNo() {
		return shouldNo;
	}
	public static void setShouldNo(int shouldNo) {
		OrderDetail.shouldNo = shouldNo;
	}
	public static float getRealNote() {
		return realNote;
	}
	public static void setRealNote(float realNote) {
		OrderDetail.realNote = realNote;
	}
	public static float getRealCoin() {
		return realCoin;
	}
	public static void setRealCoin(float realCoin) {
		OrderDetail.realCoin = realCoin;
	}
	public static float getRealAmount() {
		return realAmount;
	}
	public static void setRealAmount(float realAmount) {
		OrderDetail.realAmount = realAmount;
	}
	public static float getDebtAmount() {
		return debtAmount;
	}
	public static void setDebtAmount(float debtAmount) {
		OrderDetail.debtAmount = debtAmount;
	}
	public static float getRealCard() {
		return realCard;
	}
	public static void setRealCard(float realCard) {
		OrderDetail.realCard = realCard;
	}
	public static String getProductID() {
		return productID;
	}
	public static void setProductID(String productID) {
		OrderDetail.productID = productID;
	}
	public static int getYujiHuo() {
		return yujiHuo;
	}
	public static void setYujiHuo(int yujiHuo) {
		OrderDetail.yujiHuo = yujiHuo;
	}
	public static int getRealHuo() {
		return realHuo;
	}
	public static void setRealHuo(int realHuo) {
		OrderDetail.realHuo = realHuo;
	}
	public static String getCabID() {
		return cabID;
	}
	public static void setCabID(String cabID) {
		OrderDetail.cabID = cabID;
	}
	public static String getColumnID() {
		return columnID;
	}
	public static void setColumnID(String columnID) {
		OrderDetail.columnID = columnID;
	}
	public static int getHuoStatus() {
		return huoStatus;
	}
	public static void setHuoStatus(int huoStatus) {
		OrderDetail.huoStatus = huoStatus;
	}
    
    
    	
	
    
    
}
