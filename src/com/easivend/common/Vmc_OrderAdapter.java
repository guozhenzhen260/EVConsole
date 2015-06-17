package com.easivend.common;

import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.easivend.app.maintain.Order;
import com.easivend.dao.vmc_orderDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.model.Tb_vmc_order_pay;
import com.easivend.model.Tb_vmc_order_product;
import com.easivend.model.Tb_vmc_product;

public class Vmc_OrderAdapter
{
	int    count=0;
	//总支付订单
	String[] ordereID;// 订单ID[pk]
	String[] payType;// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
	String[] payStatus;// 订单状态0出货成功，1出货失败，2支付失败，3未支付
	String[] RealStatus;// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
	String[] smallNote;// 纸币金额
	String[] smallConi;// 硬币金额
	String[] smallAmount;// 现金投入金额
	String[] smallCard;// 非现金支付金额
	String[] shouldPay;// 商品总金额
	String[] shouldNo;// 商品总数量
	String[] realNote;// 纸币退币金额
	String[] realCoin;// 硬币退币金额
	String[] realAmount;// 现金退币金额
	String[] debtAmount;// 欠款金额
	String[] realCard;// 非现金退币金额
	String[] payTime;//支付时间
	//详细支付订单
	String[] productID;//商品id
	String[] cabID;//货柜号
    String[] columnID;//货道号
    //商品信息
    String[] productName;// 商品全名
    String[] salesPrice;// 优惠价,如”20.00”
    
    //数字类型订单信息
    //总支付订单
  	double[] smallNotevalue;// 纸币金额
  	double[] smallConivalue;// 硬币金额
  	double[] smallAmountvalue;// 现金投入金额
  	double[] smallCardvalue;// 非现金支付金额
  	double[] shouldPayvalue;// 商品总金额
  	double[] shouldNovalue;// 商品总数量
  	double[] realNotevalue;// 纸币退币金额
  	double[] realCoinvalue;// 硬币退币金额
  	double[] realAmountvalue;// 现金退币金额
  	double[] debtAmountvalue;// 欠款金额
  	double[] realCardvalue;// 非现金退币金额
  	//商品信息
  	double[] salesPricevalue;// 优惠价,如”20.00”
    
	//给报表提供信息
	public void grid(Context context,int mYear,int mMon,int mDay,int eYear,int eMon,int eDay)
	{
		String mYearStr=null,mMonthStr=null,mDayStr=null;
		String eYearStr=null,eMonthStr=null,eDayStr=null;
		
		mYearStr=((mYear<10)?("0"+String.valueOf(mYear)):String.valueOf(mYear));
		mMonthStr=((mMon<10)?("0"+String.valueOf(mMon)):String.valueOf(mMon));
		mDayStr=((mDay<10)?("0"+String.valueOf(mDay)):String.valueOf(mDay));
		eYearStr=((eYear<10)?("0"+String.valueOf(eYear)):String.valueOf(eYear));
		eMonthStr=((eMon<10)?("0"+String.valueOf(eMon)):String.valueOf(eMon));
		eDayStr=((eDay<10)?("0"+String.valueOf(eDay)):String.valueOf(eDay));
		// 创建InaccountDAO对象
		vmc_orderDAO orderDAO = new vmc_orderDAO(context);
		vmc_productDAO productDAO = new vmc_productDAO(context);// 创建InaccountDAO对象
		String start=mYearStr+"-"+mMonthStr+"-"+mDayStr;
		String end=eYearStr+"-"+eMonthStr+"-"+eDayStr;			
		List<Tb_vmc_order_pay> listinfos=orderDAO.getScrollPay(start,end);
		String[] strInfos = new String[listinfos.size()];
		//总支付订单
		ordereID = new String[listinfos.size()];
		payType = new String[listinfos.size()];
		payStatus = new String[listinfos.size()];
		RealStatus = new String[listinfos.size()];
		smallNote = new String[listinfos.size()];// 纸币金额
		smallConi = new String[listinfos.size()];// 硬币金额
		smallAmount = new String[listinfos.size()];// 现金投入金额
		smallCard = new String[listinfos.size()];// 非现金支付金额
		shouldPay = new String[listinfos.size()];// 商品总金额
		shouldNo = new String[listinfos.size()];// 商品总数量
		realNote = new String[listinfos.size()];// 纸币退币金额
		realCoin = new String[listinfos.size()];// 硬币退币金额
		realAmount = new String[listinfos.size()];// 现金退币金额
		debtAmount = new String[listinfos.size()];// 欠款金额
		realCard = new String[listinfos.size()];// 非现金退币金额
		payTime = new String[listinfos.size()];//支付时间
		//详细支付订单
		productID = new String[listinfos.size()];//商品id
		cabID = new String[listinfos.size()];//货柜号
	    columnID = new String[listinfos.size()];//货道号
	    //商品信息
	    productName = new String[listinfos.size()];// 商品全名
	    salesPrice = new String[listinfos.size()];// 优惠价,如”20.00”
	    
	    //数字类型订单信息
	    smallNotevalue= new double[listinfos.size()];// 纸币金额
	    smallConivalue= new double[listinfos.size()];// 硬币金额
	    smallAmountvalue= new double[listinfos.size()];// 现金投入金额
	    smallCardvalue= new double[listinfos.size()];// 非现金支付金额
	    shouldPayvalue= new double[listinfos.size()];// 商品总金额
	    shouldNovalue= new double[listinfos.size()];// 商品总数量
	    realNotevalue= new double[listinfos.size()];// 纸币退币金额
	    realCoinvalue= new double[listinfos.size()];// 硬币退币金额
	    realAmountvalue= new double[listinfos.size()];// 现金退币金额
	    debtAmountvalue= new double[listinfos.size()];// 欠款金额
	    realCardvalue= new double[listinfos.size()];// 非现金退币金额
	    //商品信息
	    salesPricevalue= new double[listinfos.size()];// 优惠价,如”20.00”
	    		
	    		
		count=listinfos.size();
		int m=0;
		// 遍历List泛型集合
	    for (Tb_vmc_order_pay tb_inaccount : listinfos) 
	    {
	    	//总支付订单
	    	ordereID[m]= tb_inaccount.getOrdereID();
	    	payType[m] = ToolClass.typestr(0,tb_inaccount.getPayType());
			payStatus[m] = ToolClass.typestr(1,tb_inaccount.getPayStatus());
			RealStatus[m] = ToolClass.typestr(2,tb_inaccount.getRealStatus());
			smallNote[m] = String.valueOf(tb_inaccount.getSmallNote());// 纸币金额
			smallConi[m] = String.valueOf(tb_inaccount.getSmallConi());// 硬币金额
			smallAmount[m] = String.valueOf(tb_inaccount.getSmallAmount());// 现金投入金额
			smallCard[m] = String.valueOf(tb_inaccount.getSmallCard());// 非现金支付金额
			shouldPay[m] = String.valueOf(tb_inaccount.getShouldPay());// 商品总金额
			shouldNo[m] = String.valueOf(tb_inaccount.getShouldNo());// 商品总数量
			realNote[m] = String.valueOf(tb_inaccount.getRealNote());// 纸币退币金额
			realCoin[m] = String.valueOf(tb_inaccount.getRealCoin());// 硬币退币金额
			realAmount[m] = String.valueOf(tb_inaccount.getRealAmount());// 现金退币金额
			debtAmount[m] = String.valueOf(tb_inaccount.getDebtAmount());// 欠款金额
			realCard[m] = String.valueOf(tb_inaccount.getRealCard());// 非现金退币金额
			payTime[m] = String.valueOf(tb_inaccount.getPayTime());//支付时间
			//数字类型订单信息
		    smallNotevalue[m]= tb_inaccount.getSmallNote();// 纸币金额
		    smallConivalue[m]= tb_inaccount.getSmallConi();// 硬币金额
		    smallAmountvalue[m]= tb_inaccount.getSmallAmount();// 现金投入金额
		    smallCardvalue[m]= tb_inaccount.getSmallCard();// 非现金支付金额
		    shouldPayvalue[m]= tb_inaccount.getShouldPay();// 商品总金额
		    shouldNovalue[m]= tb_inaccount.getShouldNo();// 商品总数量
		    realNotevalue[m]= tb_inaccount.getRealNote();// 纸币退币金额
		    realCoinvalue[m]= tb_inaccount.getRealCoin();// 硬币退币金额
		    realAmountvalue[m]= tb_inaccount.getRealAmount();// 现金退币金额
		    debtAmountvalue[m]= tb_inaccount.getDebtAmount();// 欠款金额
		    realCardvalue[m]= tb_inaccount.getRealCard();// 非现金退币金额
			if(ordereID[m]!=null)
			{
				//详细支付订单
				Tb_vmc_order_product tb_vmc_order_product=orderDAO.getScrollProduct(ordereID[m]);
				productID[m] = tb_vmc_order_product.getProductID();
				cabID[m] = tb_vmc_order_product.getCabID();
				columnID[m] = tb_vmc_order_product.getColumnID();
			}
			if(productID[m]!=null)
			{
				//商品信息
				Tb_vmc_product tb_product = productDAO.find(productID[m]);
				if(tb_product!=null)
				{
					productName[m]=tb_product.getProductName();
					salesPrice[m]=String.valueOf(tb_product.getSalesPrice());
					
					//数字类型订单信息
				    salesPricevalue[m]= tb_product.getSalesPrice();// 优惠价,如”20.00”
				}
			}
			
		    
		    
	    	m++;// 标识加1
	    }
				
	}
	
	//删除报表提供信息
	public void delgrid(Context context,int mYear,int mMon,int mDay,int eYear,int eMon,int eDay)
	{
		String mYearStr=null,mMonthStr=null,mDayStr=null;
		String eYearStr=null,eMonthStr=null,eDayStr=null;
		
		mYearStr=((mYear<10)?("0"+String.valueOf(mYear)):String.valueOf(mYear));
		mMonthStr=((mMon<10)?("0"+String.valueOf(mMon)):String.valueOf(mMon));
		mDayStr=((mDay<10)?("0"+String.valueOf(mDay)):String.valueOf(mDay));
		eYearStr=((eYear<10)?("0"+String.valueOf(eYear)):String.valueOf(eYear));
		eMonthStr=((eMon<10)?("0"+String.valueOf(eMon)):String.valueOf(eMon));
		eDayStr=((eDay<10)?("0"+String.valueOf(eDay)):String.valueOf(eDay));
		// 创建InaccountDAO对象
		vmc_orderDAO orderDAO = new vmc_orderDAO(context);
		vmc_productDAO productDAO = new vmc_productDAO(context);// 创建InaccountDAO对象
		String start=mYearStr+"-"+mMonthStr+"-"+mDayStr;
		String end=eYearStr+"-"+eMonthStr+"-"+eDayStr;			
		orderDAO.detele(start,end);						
	}
	    
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String[] getOrdereID() {
		return ordereID;
	}

	public void setOrdereID(String[] ordereID) {
		this.ordereID = ordereID;
	}

	public String[] getPayType() {
		return payType;
	}

	public void setPayType(String[] payType) {
		this.payType = payType;
	}

	public String[] getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String[] payStatus) {
		this.payStatus = payStatus;
	}

	public String[] getRealStatus() {
		return RealStatus;
	}

	public void setRealStatus(String[] realStatus) {
		RealStatus = realStatus;
	}

	public String[] getProductName() {
		return productName;
	}

	public void setProductName(String[] productName) {
		this.productName = productName;
	}

	public String[] getSmallAmount() {
		return smallAmount;
	}

	public void setSmallAmount(String[] smallAmount) {
		this.smallAmount = smallAmount;
	}

	public String[] getCabID() {
		return cabID;
	}

	public void setCabID(String[] cabID) {
		this.cabID = cabID;
	}

	public String[] getColumnID() {
		return columnID;
	}

	public void setColumnID(String[] columnID) {
		this.columnID = columnID;
	}

	public String[] getPayTime() {
		return payTime;
	}

	public void setPayTime(String[] payTime) {
		this.payTime = payTime;
	}
	public String[] getSmallNote() {
		return smallNote;
	}
	public void setSmallNote(String[] smallNote) {
		this.smallNote = smallNote;
	}
	public String[] getSmallConi() {
		return smallConi;
	}
	public void setSmallConi(String[] smallConi) {
		this.smallConi = smallConi;
	}
	public String[] getSmallCard() {
		return smallCard;
	}
	public void setSmallCard(String[] smallCard) {
		this.smallCard = smallCard;
	}
	public String[] getShouldPay() {
		return shouldPay;
	}
	public void setShouldPay(String[] shouldPay) {
		this.shouldPay = shouldPay;
	}
	public String[] getShouldNo() {
		return shouldNo;
	}
	public void setShouldNo(String[] shouldNo) {
		this.shouldNo = shouldNo;
	}
	public String[] getRealNote() {
		return realNote;
	}
	public void setRealNote(String[] realNote) {
		this.realNote = realNote;
	}
	public String[] getRealCoin() {
		return realCoin;
	}
	public void setRealCoin(String[] realCoin) {
		this.realCoin = realCoin;
	}
	public String[] getRealAmount() {
		return realAmount;
	}
	public void setRealAmount(String[] realAmount) {
		this.realAmount = realAmount;
	}
	public String[] getDebtAmount() {
		return debtAmount;
	}
	public void setDebtAmount(String[] debtAmount) {
		this.debtAmount = debtAmount;
	}
	public String[] getRealCard() {
		return realCard;
	}
	public void setRealCard(String[] realCard) {
		this.realCard = realCard;
	}
	public String[] getProductID() {
		return productID;
	}
	public void setProductID(String[] productID) {
		this.productID = productID;
	}
	public String[] getSalesPrice() {
		return salesPrice;
	}
	public void setSalesPrice(String[] salesPrice) {
		this.salesPrice = salesPrice;
	}

	public double[] getSmallNotevalue() {
		return smallNotevalue;
	}

	public void setSmallNotevalue(double[] smallNotevalue) {
		this.smallNotevalue = smallNotevalue;
	}

	public double[] getSmallConivalue() {
		return smallConivalue;
	}

	public void setSmallConivalue(double[] smallConivalue) {
		this.smallConivalue = smallConivalue;
	}

	public double[] getSmallAmountvalue() {
		return smallAmountvalue;
	}

	public void setSmallAmountvalue(double[] smallAmountvalue) {
		this.smallAmountvalue = smallAmountvalue;
	}

	public double[] getSmallCardvalue() {
		return smallCardvalue;
	}

	public void setSmallCardvalue(double[] smallCardvalue) {
		this.smallCardvalue = smallCardvalue;
	}

	public double[] getShouldPayvalue() {
		return shouldPayvalue;
	}

	public void setShouldPayvalue(double[] shouldPayvalue) {
		this.shouldPayvalue = shouldPayvalue;
	}

	public double[] getShouldNovalue() {
		return shouldNovalue;
	}

	public void setShouldNovalue(double[] shouldNovalue) {
		this.shouldNovalue = shouldNovalue;
	}

	public double[] getRealNotevalue() {
		return realNotevalue;
	}

	public void setRealNotevalue(double[] realNotevalue) {
		this.realNotevalue = realNotevalue;
	}

	public double[] getRealCoinvalue() {
		return realCoinvalue;
	}

	public void setRealCoinvalue(double[] realCoinvalue) {
		this.realCoinvalue = realCoinvalue;
	}

	public double[] getRealAmountvalue() {
		return realAmountvalue;
	}

	public void setRealAmountvalue(double[] realAmountvalue) {
		this.realAmountvalue = realAmountvalue;
	}

	public double[] getDebtAmountvalue() {
		return debtAmountvalue;
	}

	public void setDebtAmountvalue(double[] debtAmountvalue) {
		this.debtAmountvalue = debtAmountvalue;
	}

	public double[] getRealCardvalue() {
		return realCardvalue;
	}

	public void setRealCardvalue(double[] realCardvalue) {
		this.realCardvalue = realCardvalue;
	}

	public double[] getSalesPricevalue() {
		return salesPricevalue;
	}

	public void setSalesPricevalue(double[] salesPricevalue) {
		this.salesPricevalue = salesPricevalue;
	}
	
}
