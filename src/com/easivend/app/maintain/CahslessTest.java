package com.easivend.app.maintain;

import com.easivend.common.ToolClass;
import com.example.evconsole.R;
import com.landfone.common.utils.IUserCallback;
import com.landfoneapi.mispos.Display;
import com.landfoneapi.mispos.DisplayType;
import com.landfoneapi.mispos.ErrCode;
import com.landfoneapi.mispos.ILfListener;
import com.landfoneapi.mispos.LfMISPOSApi;
import com.landfoneapi.protocol.pkg.REPLY;
import com.landfoneapi.protocol.pkg._04_GetRecordReply;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CahslessTest extends Activity {
	public final static int OPENSUCCESS=1;//打开成功
	public final static int OPENFAIL=2;//打开失败
	public final static int CLOSESUCCESS=3;//关闭成功
	public final static int CLOSEFAIL=4;//关闭失败
	public final static int COSTSUCCESS=5;//扣款成功
	public final static int COSTFAIL=6;//扣款失败
	public final static int QUERYSUCCESS=7;//查询成功
	public final static int QUERYFAIL=8;//查询失败
	public final static int DELETESUCCESS=9;//撤销成功
	public final static int DELETEFAIL=10;//撤销失败
	public final static int PAYOUTSUCCESS=11;//退款成功
	public final static int PAYOUTFAIL=12;//退款失败
	private TextView txtcashlesstest=null;
	private EditText edtcashlesstest=null;
	private Button btncashlesstestopen=null,btncashlesstestok=null,btncashlesstestquery=null
			,btncashlesstestdelete=null,btncashlesstestpayout=null,btncashlesstestclose=null,
			btncashlesstestcancel=null;
	private Handler mainhand=null;
	private LfMISPOSApi mMyApi = new LfMISPOSApi();
	//退款参数
	private String rfd_card_no = "";
	private String rfd_spec_tmp_serial = "";
	float amount=0;//商品需要支付金额
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cashlesstest);	
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		mainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what) 
				{
					case OPENSUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case OPENFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case CLOSESUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case CLOSEFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case COSTSUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case COSTFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case QUERYSUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case QUERYFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case DELETESUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case DELETEFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;	
					case PAYOUTSUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case PAYOUTFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;		
				}
			}
		};				
		txtcashlesstest = (TextView)findViewById(R.id.txtcashlesstest);
		edtcashlesstest = (EditText)findViewById(R.id.edtcashlesstest);
		//打开
		btncashlesstestopen = (Button)findViewById(R.id.btncashlesstestopen);
		btncashlesstestopen.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开读卡器"+ToolClass.getCardcom(),"com.txt");
		    	txtcashlesstest.setText("打开读卡器.."+ToolClass.getCardcom());
		    	//ip、端口、串口、波特率必须准确"121.40.30.62", 18080
				mMyApi.pos_init(ToolClass.getPosip(), Integer.parseInt(ToolClass.getPosipport())
						,ToolClass.getCardcom(), "9600", mIUserCallback);
		    }
		});
		//扣款
		btncashlesstestok = (Button)findViewById(R.id.btncashlesstestok);
		btncashlesstestok.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {	
		    	amount=Float.parseFloat(edtcashlesstest.getText().toString());
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器扣款="+amount,"com.txt");
		    	txtcashlesstest.setText("读卡器扣款="+amount);
				mMyApi.pos_purchase(ToolClass.MoneySend(amount), mIUserCallback);				
		    }
		});
		//查询
		btncashlesstestquery = (Button)findViewById(R.id.btncashlesstestquery);		
		btncashlesstestquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {	
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器获取交易信息","com.txt");
		    	txtcashlesstest.setText("读卡器获取交易信息");
		    	mMyApi.pos_getrecord("000000000000000", "00000000","000000", mIUserCallback);
		    }
		});
		//撤销交易
		btncashlesstestdelete = (Button)findViewById(R.id.btncashlesstestdelete);		
		btncashlesstestdelete.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 操作撤销（刷卡前）..","com.txt");
		    	txtcashlesstest.setText("POS 操作撤销（刷卡前）..");
		    	mMyApi.pos_cancel();
		    }
		});
		//退款
		btncashlesstestpayout = (Button)findViewById(R.id.btncashlesstestpayout);
		btncashlesstestpayout.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器退款="+amount,"com.txt");
		    	txtcashlesstest.setText("读卡器退款="+amount);
		    	mMyApi.pos_refund(rfd_card_no,ToolClass.MoneySend(amount),rfd_spec_tmp_serial, mIUserCallback);
		    }
		});
		//关闭
		btncashlesstestclose = (Button)findViewById(R.id.btncashlesstestclose);
		btncashlesstestclose.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
		    	txtcashlesstest.setText("POS 关闭读卡器");
				mMyApi.pos_release();
		    }
		});
		//退出
		btncashlesstestcancel = (Button)findViewById(R.id.btncashlesstestcancel);		
		btncashlesstestcancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		
		    	mMyApi.pos_release();
		    	finish();
		    }
		});
	}
	
	//接口返回
	private IUserCallback mIUserCallback = new IUserCallback(){
		@Override
		public void onResult(REPLY rst) 
		{
			if(rst!=null) 
			{
				Message childmsg=mainhand.obtainMessage();
				//info(rst.op + ":" + rst.code + "," + rst.code_info);
				//【操作标识符】LfMISPOSApi下“OP_”开头的静态变量，如：LfMISPOSApi.OP_INIT、LfMISPOSApi.OP_PURCHASE等等
				//打开串口
				if(rst.op.equals(LfMISPOSApi.OP_INIT))
				{
					//【返回码和信息】code和code_info的返回/说明，见com.landfoneapi.mispos.ErrCode
					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开成功"+ToolClass.getCardcom(),"com.txt");
						childmsg.what=OPENSUCCESS;
						childmsg.obj="打开成功"+ToolClass.getCardcom();
					}else{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开失败"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info,"com.txt");						
						childmsg.what=OPENFAIL;
						childmsg.obj="打开失败"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//关闭串口
				else if(rst.op.equals(LfMISPOSApi.OP_RELEASE))
				{
					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭成功","com.txt");
						childmsg.what=CLOSESUCCESS;
						childmsg.obj="关闭成功";
					}else{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");						
						childmsg.what=CLOSEFAIL;
						childmsg.obj="关闭失败,code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//扣款
				else if(rst.op.equals(LfMISPOSApi.OP_PURCHASE))
				{
					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 扣款成功","com.txt");
						childmsg.what=COSTSUCCESS;
						childmsg.obj="扣款成功";
					}
					else if(rst.code.equals(ErrCode._XY.getCode())){
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 撤销成功","com.txt");
  						childmsg.what=CahslessTest.DELETESUCCESS;
  						childmsg.obj="撤销成功";
  					}
					else{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 扣款失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						childmsg.what=COSTFAIL;
						childmsg.obj="扣款失败,code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//退款
  				else if(rst.op.equals(LfMISPOSApi.OP_REFUND))
  				{
  					//返回00，代表成功
					if(rst.code.equals(ErrCode._00.getCode()))
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 退款成功","com.txt");
						childmsg.what=CahslessTest.PAYOUTSUCCESS;
  						childmsg.obj="退款成功";
					}else
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 退款失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						childmsg.what=CahslessTest.PAYOUTFAIL;
						childmsg.obj="退款失败,code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//返回结果
				else if(rst.op.equals(LfMISPOSApi.OP_GETRECORD))
				{
					//返回00，代表成功
					if(rst.code.equals(ErrCode._00.getCode()))
					{
						String tmp = "单据:特定信息=";
						tmp += "[" + ((_04_GetRecordReply) (rst)).getSpecInfoField();//特定信息【会员卡需要！！】
						/*特定信息说明
						+储值卡号(19)
						+终端流水号(6)
						+终端编号(8)
						+批次号(6)
						+商户号(15)
						+商户名称(60)
						+会员名称(60)
						+交易时间(6)
						+交易日期(8)
						+交易单号(14)
						+消费金额(12)
						+账户余额(12)
						+临时交易流水号（26）
						以上都是定长，金额都是定长12位，前补0，其他不足位数后补空格

						* */
						tmp += "],商户代码=[" + ((_04_GetRecordReply) (rst)).getMer();//商户代码
						tmp += "],终端号=[" + ((_04_GetRecordReply) (rst)).getTmn();//终端号
						tmp += "],卡号=[" + ((_04_GetRecordReply) (rst)).getCardNo();//卡号
						tmp += "],交易批次号=[" + ((_04_GetRecordReply) (rst)).getTransacionBatchNo();//交易批次号
						tmp += "],原交易类型=[" + ((_04_GetRecordReply) (rst)).getTransacionVoucherNo();//原交易类型
						tmp += "],交易日期和时间=[" + ((_04_GetRecordReply) (rst)).getTransacionDatetime();//交易日期和时间
						tmp += "],交易金额=[" + ((_04_GetRecordReply) (rst)).getTransacionAmount();//交易金额
						tmp +="]";
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 查询成功="+tmp,"com.txt");

  						//退款参数获取
						String tmp_spec = ((_04_GetRecordReply) (rst)).getSpecInfoField();
						int tmp_spec_len = tmp_spec!=null?tmp_spec.length():0;
						//【金额】
						//rfd_amt_fen = amount;//使用上次全额，测试金额都是1分
						//【退款卡号】
						if(tmp_spec!=null && tmp_spec_len>(2+19)){
							rfd_card_no = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring(0+2,2+19);
						}
						//【临时交易流水号】
						if(tmp_spec!=null && tmp_spec_len>26){
							rfd_spec_tmp_serial = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring((tmp_spec_len-26),tmp_spec_len);
						}else{//使用空格时，表示上一次的【临时交易流水号】
							rfd_spec_tmp_serial = String.format("%1$-26s","");
						}
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 退款参数=金额"+amount+"卡号="+rfd_card_no+"流水号="+rfd_spec_tmp_serial,"com.txt");
						childmsg.what=QUERYSUCCESS;
						childmsg.obj="查询成功="+tmp;
					}
					else
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 查询失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						childmsg.what=QUERYFAIL;
						childmsg.obj="查询失败";
					}
				}
				mainhand.sendMessage(childmsg);
			}
		}

		@Override
		public void onProcess(Display dpl) {//过程和提示信息
			if(dpl!=null) {
				//lcd(dpl.getType() + "\n" + dpl.getMsg());

				//【提示信息类型】type的说明，见com.landfoneapi.mispos.DisplayType
				if(dpl.getType().equals(DisplayType._4.getType())){
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 通讯提示<<"+dpl.getMsg(),"com.txt");
				}

			}
		}
	};

}
