package cn.yoc.unionPay.po;

/**
 * @author Yocer
 * @version 2017/6/15
 * @description
 */
public class UnionOrder {

    private String txnAmt; //消费金额 单位分例如 20 表示 20分
    private String orderId; //订单号
    private String txnTime; //下单时间
    private String termId; //终端号 例如设备号
    private String refundAmt; //退款金额

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(String refundAmt) {
        this.refundAmt = refundAmt;
    }

	@Override
	public String toString() {
		return "UnionOrder [txnAmt=" + txnAmt + ", orderId=" + orderId
				+ ", txnTime=" + txnTime + ", termId=" + termId
				+ ", refundAmt=" + refundAmt + "]";
	}
    
}
