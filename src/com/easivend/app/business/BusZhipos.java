package com.easivend.app.business;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.app.maintain.CahslessTest;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;
import com.landfone.common.utils.IUserCallback;
import com.landfoneapi.mispos.Display;
import com.landfoneapi.mispos.DisplayType;
import com.landfoneapi.mispos.ErrCode;
import com.landfoneapi.mispos.LfMISPOSApi;
import com.landfoneapi.protocol.pkg.REPLY;
import com.landfoneapi.protocol.pkg._04_GetRecordReply;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BusZhipos extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 200; // 延迟1.5秒
	//进度对话框
	ProgressDialog dialog= null;
	public static BusZhipos BusZhiposAct=null;
	private final static int REQUEST_CODE=1;//声明请求标识
	TextView txtbuszhiposcount=null,txtbuszhiposAmount=null,txtbuszhipostime=null,
			txtbuszhipostsxx=null;
	ImageButton imgbtnbuszhiposqxzf=null,imgbtnbuszhiposqtzf=null;
	ImageView imgbtnbusgoodsback=null;
	private final int SPLASH_TIMEOUT_LENGHT = 5*60; //  5*60延迟5分钟
	private int recLen = SPLASH_TIMEOUT_LENGHT; 
	private int queryLen = 0; 
	private int ispayoutopt=0;//1正在进行退币操作,0未进行退币操作
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
    
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;
    private String zhifutype = "1";//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
	float amount=0;//商品需要支付金额
	private LfMISPOSApi mMyApi = new LfMISPOSApi();
    private Handler posmainhand=null;
    private int iszhipos=0;//1成功发送了扣款请求,0没有发送成功扣款请求，2刷卡扣款已经完成并且金额足够
    private String out_trade_no=null;
    //退款参数
	private String rfd_card_no = "";
	private String rfd_spec_tmp_serial = "";
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buszhipos);
		BusZhiposAct = this;
		timer.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);       // timeTask 
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
		txtbuszhiposcount= (TextView) findViewById(R.id.txtbuszhiposcount);
		txtbuszhiposcount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiposAmount= (TextView) findViewById(R.id.txtbuszhiposAmount);
		txtbuszhiposAmount.setText(String.valueOf(amount));
		txtbuszhipostime = (TextView) findViewById(R.id.txtbuszhipostime);
		txtbuszhipostsxx = (TextView) findViewById(R.id.txtbuszhipostsxx);
		out_trade_no=ToolClass.out_trade_no(BusZhipos.this);// 创建InaccountDAO对象;
		imgbtnbuszhiposqxzf = (ImageButton) findViewById(R.id.imgbtnbuszhiposqxzf);
		imgbtnbuszhiposqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();
		    }
		});
		imgbtnbuszhiposqtzf = (ImageButton) findViewById(R.id.imgbtnbuszhiposqtzf);
		imgbtnbuszhiposqtzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finishActivity();
		    }
		});
		this.imgbtnbusgoodsback=(ImageView)findViewById(R.id.imgbtnbusgoodsback);
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();
		    }
		});
		posmainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what) 
				{
					case CahslessTest.OPENSUCCESS:
						break;
					case CahslessTest.OPENFAIL:	
						break;
					case CahslessTest.CLOSESUCCESS:
						break;
					case CahslessTest.CLOSEFAIL:	
						break;
					case CahslessTest.COSTSUCCESS:
						txtbuszhipostsxx.setText("提示信息：付款完成");
						iszhipos=2;
						//延时
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {         
				            	//读卡器获取交易信息
								mMyApi.pos_getrecord("000000000000000", "00000000","000000", mIUserCallback);
							}

						}, 300);
						break;
					case CahslessTest.COSTFAIL:	
						txtbuszhipostsxx.setText("提示信息：扣款失败");
						iszhipos=0;
						break;
					case CahslessTest.QUERYSUCCESS:
					case CahslessTest.QUERYFAIL:	
						//txtbuszhipostsxx.setText("单据信息："+SpecInfoField);
						tochuhuo();						
						break;
					case CahslessTest.DELETESUCCESS:
					case CahslessTest.DELETEFAIL:	
						//延时
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {         
				            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
								finish();
							}

						}, 300);						
						break;						
					case CahslessTest.PAYOUTSUCCESS:
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(1);//记录退币成功
							OrderDetail.setRealCard(amount);//记录退币金额
							OrderDetail.addLog(BusZhipos.this);
							ispayoutopt=0;
							//结束交易页面
							txtbuszhipostsxx.setText("提示信息：退款成功");
							//延时
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {         
					            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
					            	dialog.dismiss();
									finish();
								}

							}, 300);							
						}
						break;
					case CahslessTest.PAYOUTFAIL:	
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(3);//记录退币失败
							OrderDetail.setRealCard(0);//记录退币金额
							OrderDetail.setDebtAmount(amount);//欠款金额
							OrderDetail.addLog(BusZhipos.this);
							ispayoutopt=0;
							//结束交易页面
							txtbuszhipostsxx.setText("提示信息：退款成功");
							//延时
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {         
					            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
					            	dialog.dismiss();
									finish();
								}

							}, 300);
						}
						break;		
				}
			}
		};
		//打开串口
		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开读卡器"+ToolClass.getCardcom(),"com.txt");
		//ip、端口、串口、波特率必须准确
		mMyApi.pos_init("121.40.30.62", 18080
				,ToolClass.getCardcom(), "9600", mIUserCallback);		
		//延时
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {         
            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器扣款="+amount,"com.txt");
            	txtbuszhipostsxx.setText("提示信息：请刷卡");
            	mMyApi.pos_purchase(ToolClass.MoneySend(amount), mIUserCallback);	
		    	iszhipos=1;
            }

		}, SPLASH_DISPLAY_LENGHT);
	}
	
    //调用倒计时定时器
	TimerTask task = new TimerTask() { 
	      @Override 
	      public void run() { 
	
	          runOnUiThread(new Runnable() {      // UI thread 
		         @Override 
		        public void run()
		        { 
		            recLen--; 
		            txtbuszhipostime.setText("倒计时:"+recLen); 
		            //退出页面
		            if(recLen <= 0)
		            { 
		            	timeoutfinishActivity();
		            } 
		            
		            
		            //发送查询交易指令
		            if(iszhipos==1)
		            {		                
		            }
		            //发送订单交易指令
		            else if(iszhipos==0)
		            {		              
		            }
		        } 
	          });
	      }      
	  };
	//结束界面
	private void finishActivity()
	{
		//如果本次扫码已经结束，可以购买，则不进行退款操作
    	if(iszhipos==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhier退币按钮无效","log.txt");
    	}
    	else if(iszhipos==1)
    		deletezhipos();
		else 
		{			
			ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
			finish();
		}
	}
	//用于超时的结束界面
	private void timeoutfinishActivity()
	{
		finishActivity();
	}
	
	//撤销交易
  	private void deletezhipos()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 操作撤销（刷卡前）..","com.txt");
    	mMyApi.pos_cancel();
  	}
  	
    //退款交易
  	private void payoutzhipos()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器退款="+amount,"com.txt");
    	mMyApi.pos_refund(rfd_card_no,ToolClass.MoneySend(amount),rfd_spec_tmp_serial, mIUserCallback);
  	}
	
	//接口返回
  	private IUserCallback mIUserCallback = new IUserCallback(){
  		@Override
  		public void onResult(REPLY rst) 
  		{
  			if(rst!=null) 
  			{
  				Message childmsg=posmainhand.obtainMessage();
  				//info(rst.op + ":" + rst.code + "," + rst.code_info);
  				//【操作标识符】LfMISPOSApi下“OP_”开头的静态变量，如：LfMISPOSApi.OP_INIT、LfMISPOSApi.OP_PURCHASE等等
  				//打开串口
  				if(rst.op.equals(LfMISPOSApi.OP_INIT))
  				{
  					//【返回码和信息】code和code_info的返回/说明，见com.landfoneapi.mispos.ErrCode
  					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开成功"+ToolClass.getCardcom(),"com.txt");
  						childmsg.what=CahslessTest.OPENSUCCESS;
  						childmsg.obj="打开成功"+ToolClass.getCardcom();
  					}else{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开失败"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info,"com.txt");						
  						childmsg.what=CahslessTest.OPENFAIL;
  						childmsg.obj="打开失败"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info;
  					}
  				}
  				//关闭串口
  				else if(rst.op.equals(LfMISPOSApi.OP_RELEASE))
  				{
  					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭成功","com.txt");
  						childmsg.what=CahslessTest.CLOSESUCCESS;
  						childmsg.obj="关闭成功";
  					}else{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");						
  						childmsg.what=CahslessTest.CLOSEFAIL;
  						childmsg.obj="关闭失败,code:"+rst.code+",info:"+rst.code_info;
  					}
  				}
  				//扣款
  				else if(rst.op.equals(LfMISPOSApi.OP_PURCHASE))
  				{
  					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 扣款成功","com.txt");
  						childmsg.what=CahslessTest.COSTSUCCESS;
  						childmsg.obj="扣款成功";
  					}
  					else if(rst.code.equals(ErrCode._XY.getCode())){
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 撤销成功","com.txt");
  						childmsg.what=CahslessTest.DELETESUCCESS;
  						childmsg.obj="撤销成功";
  					}
  					else
  					{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 扣款失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
  						childmsg.what=CahslessTest.COSTFAIL;
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
						childmsg.what=CahslessTest.QUERYSUCCESS;
  						childmsg.obj=((_04_GetRecordReply) (rst)).getSpecInfoField();
  					}
  					else
  					{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 查询失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
  						childmsg.what=CahslessTest.QUERYFAIL;
  						childmsg.obj="";
  					}
  				}
  				posmainhand.sendMessage(childmsg);
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
  	
    //跳到出货页面
  	private void tochuhuo()
  	{
  		Intent intent = null;// 创建Intent对象                
      	intent = new Intent(BusZhipos.this, BusHuo.class);// 使用Accountflag窗口初始化Intent
//      	intent.putExtra("out_trade_no", out_trade_no);
//      	intent.putExtra("proID", proID);
//      	intent.putExtra("productID", productID);
//      	intent.putExtra("proType", proType);
//      	intent.putExtra("cabID", cabID);
//      	intent.putExtra("huoID", huoID);
//      	intent.putExtra("prosales", prosales);
//      	intent.putExtra("count", count);
//      	intent.putExtra("reamin_amount", reamin_amount);
//      	intent.putExtra("zhifutype", zhifutype);
      	OrderDetail.setOrdereID(out_trade_no);
      	OrderDetail.setPayType(Integer.parseInt(zhifutype));
      	OrderDetail.setSmallCard(amount);
      	startActivityForResult(intent, REQUEST_CODE);// 打开Accountflag
  	}
  	
    //接收BusHuo返回信息
  	@Override
  	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  		// TODO Auto-generated method stub
  		if(requestCode==REQUEST_CODE)
  		{
  			if(resultCode==BusZhier.RESULT_CANCELED)
  			{
  				Bundle bundle=data.getExtras();
    				int status=bundle.getInt("status");//出货结果1成功,0失败
    			    //1.
    				//出货成功,结束交易
  				if(status==1)
  				{
  					ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<无退款","com.txt");
  					OrderDetail.addLog(BusZhipos.this);
  					finish();
  				}
  				//出货失败,退钱
  				else
  				{	
  					ispayoutopt=1;
  					ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<退款amount="+amount,"com.txt");
  					dialog= ProgressDialog.show(BusZhipos.this,"正在退款中","请稍候...");
  					payoutzhipos();//退款操作									
  				}				
  			}			
  		}
  	}
  	
  	@Override
	protected void onDestroy() {
  		timer.shutdown(); 
		mMyApi.pos_release();
		super.onDestroy();		
	}

}
