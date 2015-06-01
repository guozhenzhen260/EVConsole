/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        java调用JNI接口封装类                   
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.evprotocol;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;




/*********************************************************************************************************
**定义 EVprotocol 接口封装类
*********************************************************************************************************/
public class EVprotocol {
	public EVprotocol(){		//构造函数 默认开启线程处理回调数据
		if(ev_thread == null){
			ev_thread = new EV_Thread();
			ev_thread.start();
		}
	} 
	
	
	
	//加载JNI动态链接库
	static{
		System.loadLibrary("EVdirver");
		
	}
	
	
	
	
	
	/*********************************************************************************************************
	 **定义请求包类型
	*********************************************************************************************************/
	private static final int EV_NONE 		= 0;	//保留类型
	private static final int EV_REGISTER 	= 1;	//串口注册
	private static final int EV_RELEASE 	= 2;	//串口释放
	
	//=====================快递柜类型==============================================================================
	private static final int EV_BENTO_OPEN 	= 11;	//快递柜开门
	private static final int EV_BENTO_CHECK = 12;	//快递柜查询
	private static final int EV_BENTO_LIGHT = 13;	//快递柜照明
	private static final int EV_BENTO_COOL 	= 14;	//快递柜制冷
	private static final int EV_BENTO_HOT 	= 15;	//快递柜加热
	
	//=====================MDB现金模组类型==============================================================================
	private static final int EV_MDB_INIT 	= 21;	//MDB设备初始化
	private static final int EV_MDB_ENABLE 	= 22;	//MDB设备使能
	private static final int EV_MDB_HEART 	= 23;	//MDB设备心跳
	private static final int EV_MDB_B_INFO 	= 24;	//MDB纸币器信息
	private static final int EV_MDB_C_INFO 	= 25;	//MDB硬币器信息
	private static final int EV_MDB_COST 	= 26;	//MDB设备扣款
	private static final int EV_MDB_PAYBACK = 27;	//MDB设备退币
	private static final int EV_MDB_PAYOUT 	= 28;	//MDB设备找币



	
	/*********************************************************************************************************
	 **定义请求包类接口
	*********************************************************************************************************/
	public static class RequestObject{
		public int type;
		public String portName;
		public int fd;
		public int addr;
		public int box;
		public int opt;
		
		public int bill;
		public int coin;
		
		public int billPay;
		public int coinPay;
		public int cost;
		
		
	};
	
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_portRegister
	** Descriptions		:		串口注册接口 	[异步]
	** input parameters	:       portName 串口号 例如"COM1"
	** output parameters:		无
	** Returned value	:		1：指令发送成功  0：指令发送失败
	*  具体的结果通过回调返回json包 		 例如： EV_JSON={"EV_json":{"EV_type":1,"port":"/dev/ttymxc2","port_id":0}}
	*  							"EV_type" = EV_REGISTER = 1;表示串口注册包类型
	*                           "port"    表示返回的串口号,就是portName的值
	*							"port_id":表示返回的串口编号，如果失败则返回 -1
	*********************************************************************************************************/
	public static int EV_portRegister(String portName)
	{
		RequestObject req = new RequestObject();
		req.type = EV_REGISTER;
		req.portName = portName;
		return pushReq(req);
	}
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_portRelease
	** Descriptions		:		串口释放接口  [异步]
	** input parameters	:       port_id 串口编号
	** output parameters:		无
	** Returned value	:		1：指令发送成功  0：指令发送失败
	*	具体的结果通过回调返回json包	  例如： EV_JSON={"EV_json":{"EV_type":2,"result":1}}
	*							"EV_type"= EV_RELEASE = 2; 表示串口释放包类型
	*							"result":表示操作结果    1:表示成功释放   0:表示释放失败
	*********************************************************************************************************/
	public  static int EV_portRelease(int port_id)
	{
		RequestObject req = new RequestObject();
		req.type = EV_RELEASE;
		req.fd = port_id;
		return pushReq(req);
	}
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_bentoOpen
	** Descriptions		:		快递柜开门接口  [异步]
	** input parameters	:       port_id:串口编号, addr:柜子地址 01-16,box:开门的格子号 1-88
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	返回json包     例如： EV_JSON={"EV_json":{"EV_type":11,"port_id":0,"addr":1,"box":1,"is_success":1,"result":1}}
	*							"EV_type"= EV_BENTO_OPEN = 11; 表开门结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result": 	表示处理结果	1:开门成功   0:开门失败
	*********************************************************************************************************/
	public  static int EV_bentoOpen(int port_id,int addr,int box)
	{
		RequestObject req = new RequestObject();
		req.type = EV_BENTO_OPEN;
		req.fd = port_id;
		req.addr = addr;
		req.box = box;
		return pushReq(req);
	}
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_bentoCheck
	** Descriptions		:		快递柜查询接口  [异步]
	** input parameters	:       port_id:串口编号,addr:柜子地址 01-16
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":12,"port_id":0,"addr":0,"is_success":1,"ID":"xxxxxxxxx1",
	*								"cool":0,"hot":0,"light":1,"sum":88,[]}}
	*							"EV_type"= EV_BENTO_CHECK = 12: 表查询结果回应包类型
	*							"port_id":原样返回,"addr":原样返回柜子地址
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"ID":驱动板的ID号
	*							"cool":是否支持制冷 	 	1:支持 0:不支持
	*							"hot":是否支持加热  		1:支持 0:不支持
	*							"light":是否支持照明  	1:支持 0:不支持
	*							"sum":货道总数	例如货道总数88 则默认 货道编号 1-88
	*********************************************************************************************************/
	public  static int EV_bentoCheck(int port_id,int addr)
	{
		RequestObject req = new RequestObject();
		req.type = EV_BENTO_CHECK;
		req.fd = port_id;
		req.addr = addr;
		return pushReq(req);
	}
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_bentoLight
	** Descriptions		:		快递柜照明控制接口  [异步]
	** input parameters	:       port_id:串口编号,addr:柜子地址 01-16,opt:开照明控制 1:开  0:关
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":13,"port_id":0,"addr":1,"opt":1,"is_success":1,"result":1}}
	*							"EV_type"= EV_BENTO_LIGHT = 13: 表照明控制结果回应包类型
	*							"port_id":原样返回,"addr":原样返回柜子地址,"opt":原样返回操作.
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result": 表示处理结果	1:成功   0:失败
	*********************************************************************************************************/
	public  static int EV_bentoLight(int port_id,int addr,int opt)
	{
		RequestObject req = new RequestObject();
		req.type = EV_BENTO_LIGHT;
		req.fd = port_id;
		req.addr = addr;
		req.opt = opt;
		return pushReq(req);
	}
	
	
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbInit
	** Descriptions		:		MDB初始化接口  [异步]
	** input parameters	:       port_id:串口编号,bill:操作纸币器  1:操作,0:不操作,coin:操作硬币器  1:操作,0:不操作
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":21,"port_id":0,"bill":1,"coin":1,"is_success":1,"bill_result":1,"coin_result":1}}
	*							"EV_type"= EV_MDB_INIT = 21: 表MDB初始化结果回应包类型
	*							"port_id":原样返回,"bill":原样返回柜子地址,"coin":原样返回操作.
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"bill_result": 纸币器处理结果	1:成功   0:失败
	*							"coin_result": 纸币器处理结果	1:成功   0:失败
	*********************************************************************************************************/
	public  static int EV_mdbInit(int port_id,int bill,int coin)
	{
		RequestObject req = new RequestObject();
		req.type = EV_MDB_INIT;
		req.fd = port_id;
		req.bill = bill;
		req.coin = coin;
		return pushReq(req);
	}
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbEnable
	** Descriptions		:		MDB使能接口  [异步]
	** input parameters	:       port_id:串口编号; bill:操作纸币器  1:操作 0:不操作;coin:操作硬币器  1:操作,0:不操作 ;opt:操作 1:使能 0:禁能
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":22,"port_id":0,"bill":1,"coin":1,"opt":1,"is_success":1,"bill_result":1,"coin_result":1}}
	*							"EV_type"= EV_MDB_ENABLE = 22: 表MDB使能结果回应包类型
	*							"port_id":原样返回,"bill":原样返回柜子地址,"coin":原样返回操作;"opt":原样返回
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"bill_result": 纸币器处理结果	1:成功   0:失败
	*							"coin_result": 纸币器处理结果	1:成功   0:失败
	*********************************************************************************************************/
	public  static int EV_mdbEnable(int port_id,int bill,int coin,int opt)
	{
		RequestObject req = new RequestObject();
		req.type = EV_MDB_ENABLE;
		req.fd = port_id;
		req.bill = bill;
		req.coin = coin;
		req.opt = opt;
		return pushReq(req);
	}
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbHeart
	** Descriptions		:		MDB心跳查询接口  [异步]
	** input parameters	:       port_id:串口编号;
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":23,"port_id":0,"is_success":1,
	*							"bill_enable":1,"bill_payback":0,"bill_err":0,"bill_recv":0,"bill_remain":0,
	*							"coin_enable":1,"coin_payback":0,"coin_err":0,"coin_recv":0,"coin_remain":0}}
	*							"EV_type"= EV_MDB_HEART = 23: 表示MDB心跳查询结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"bill_enable": 纸币器使能状态		1:使能   0:禁能
	*							"bill_payback": 纸币器退币按钮触发	1:触发   0:非触发
	*							"bill_err":纸币器故障状态			0:正常   非0 为故障码
	*							"bill_recv":纸币器当前收币金额	以分为单位
	*							"bill_remain":纸币器当前储币金额	以分为单位
	*	*						"coin_enable": 硬币器使能状态		1:使能   0:禁能
	*							"coin_payback": 硬币器退币按钮触发	1:触发   0:非触发
	*							"coin_err":硬币器故障状态			0:正常   非0 为故障码
	*							"coin_recv":硬币器当前收币金额	以分为单位
	*							"coin_remain":硬币器当前储币金额	以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbHeart(int port_id)
	{
		RequestObject req = new RequestObject();
		req.type = EV_MDB_HEART;
		req.fd = port_id;
		return pushReq(req);
	}
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVmdbBillInfoCheck
	** Descriptions		:		MDB纸币器查询接口  [异步]
	** input parameters	:       port_id:串口编号;
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":24,"port_id":0,"is_success":1,
	*							"acceptor":2,"dispenser":2,"code":"ITL","sn":"12312....","model":"NV9",
	*							"ver":"1212","capacity":500,"ch_r":[],"ch_d":[]}}
	*							"EV_type"= EV_MDB_B_INFO = 24: 表示MDB纸币器信息查询结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"acceptor": 纸币接收器协议类型	0:接收器关闭	2:MDB协议接收器
	*							"dispenser": 纸币找零器协议类型	0:找零器关闭	2:MDB协议找零器
	*							"code": 纸币器厂商代码			例如：ITL
	*							"sn":纸币器序列号
	*							"model":纸币器型号				例如:NV9
	*							ver:纸币器软件版本号
	*							capacity:纸币器储币量	
	*							ch_r:纸币器接收器通道面值 		以分为单位
	*							ch_d:纸币器找零器通道面值		以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbBillInfoCheck(int port_id)
	{
		RequestObject req = new RequestObject();
		req.type = EV_MDB_B_INFO;
		req.fd = port_id;
		return pushReq(req);
	}
	
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbCoinInfoCheck
	** Descriptions		:		MDB硬币器信息查询接口  [异步]
	** input parameters	:       port_id:串口编号;
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":25,"port_id":0,"is_success":1,
	*							"acceptor":2,"dispenser":2,"code":"MEI","sn":"12312....","model":"***",
	*							"ver":"1212","capacity":500,"ch_r":[],"ch_d":[]}}
	*							"EV_type"= EV_MDB_C_INFO = 25: 表示MDB硬币器信息查询结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"acceptor": 硬币接收器协议类型	0:接收器关闭	1:并行硬币脉冲协议  2:MDB协议接收器 3:串行硬币脉冲协议
	*							"dispenser": 硬币找零器协议类型	0:找零器关闭	1:hopper 串口232协议  2:MDB协议找零器
	*							"code": 硬币器厂商代码			例如：MEI
	*							"sn":硬币器序列号
	*							"model":硬币器型号			
	*							ver:硬币器软件版本号
	*							capacity:硬币器储币量	
	*							ch_r:硬币器接收器通道面值 		以分为单位
	*							ch_d:硬币器找零器通道面值		以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbCoinInfoCheck(int port_id)
	{
		RequestObject req = new RequestObject();
		req.type = EV_MDB_C_INFO;
		req.fd = port_id;
		return pushReq(req);
	}
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbCost
	** Descriptions		:		MDB扣款接口  [异步]
	** input parameters	:       port_id:串口编号;cost:扣款金额  以分为单位
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":26,"port_id":0,"cost":100,"is_success":1,
	*							"result":1,"bill_recv":0,"coin_recv":0}}
	*							"EV_type"= EV_MDB_COST = 26: 表示MDB扣款结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:扣款成功     0:扣款失败
	*							"bill_recv":纸币器当前收币金额	  以分为单位
	*							"coin_recv":硬币器当前收币金额	  以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbCost(int port_id,int cost)
	{
		RequestObject req = new RequestObject();
		req.type = EV_MDB_COST;
		req.fd = port_id;
		req.cost = cost;
		return pushReq(req);
	}
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbPayback
	** Descriptions		:		MDB退币接口  [异步]
	** input parameters	:       port_id:串口编号;bill:操作纸币器  1:操作,0:不操作,coin:操作硬币器  1:操作,0:不操作
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	返回json包     例如： EV_JSON={"EV_json":{"EV_type":26,"port_id":0,"bill":1,"coin":1,"is_success":1,
	*							"result":1,"bill_changed":0,"coin_changed":100}}
	*							"EV_type"= EV_MDB_COST = 26: 表示MDB退币结果回应包类型
	*							"port_id":原样返回,"bill":原样返回,"coin":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:退币成功     0:退币失败
	*							"bill_changed":纸币器当前找币金额	  以分为单位
	*							"coin_changed":硬币器当前找币金额	  以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbPayback(int port_id,int bill,int coin)
	{
		RequestObject req = new RequestObject();
		req.type = EV_MDB_PAYBACK;
		req.fd = port_id;
		req.bill = bill;
		req.coin = coin;
		return pushReq(req);
	}
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbPayout
	** Descriptions		:		MDB找币接口  [异步]
	** input parameters	:       port_id:串口编号;bill:操作纸币器  1:操作,0:不操作,coin:操作硬币器  1:操作,0:不操作;
	*							billPayout:纸币器下发找币金额 分为单位 ;coinPayout:硬币器下发找币金额 分为单位 
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	返回json包     例如： EV_JSON={"EV_json":{"EV_type":28,"port_id":0,"bill":0,"coin":1,
	*							"billPayout":0,"coinPayout":100,"is_success":1,
	*							"result":1,"bill_changed":0,"coin_changed":100}}
	*							"EV_type"= EV_MDB_PAYOUT = 28: 表示MDB找币结果回应包类型
	*							"port_id":原样返回,"bill":原样返回,"coin":原样返回,"billPayout":原样返回,"coinPayout":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:找币成功     0:找币失败
	*							"bill_changed":纸币器当前找币金额	  以分为单位
	*							"coin_changed":硬币器当前找币金额	  以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbPayout(int port_id,int bill,int coin,int billPay,int coinPay)
	{
		RequestObject req = new RequestObject();
		req.type = EV_MDB_PAYOUT;
		req.fd = port_id;
		req.bill = bill;
		req.coin = coin;
		req.billPay = billPay;
		req.coinPay = coinPay;
		return pushReq(req);
	}
	
	
	
	
	
	
	/*********************************************************************************************************
	 **以下是用户注册回调函数的接口，用户可以注册复杂的函数。该回调是在java层的线程运行
	*********************************************************************************************************/	
	private static Collection<EV_listener> listeners = null;//定义事件监听队列
	
	
	
	/*********************************************************************************************************
	** Function name:     	addListener
	** Descriptions:	             注册回调监听接口 用户想要监听回调只需要注册即可
	** input parameters:    EV_listener：接口类
	** output parameters:   无
	** Returned value:      无
	*********************************************************************************************************/
	public static void addListener(EV_listener listener){
		 if(listeners == null){
			 listeners = new HashSet<EV_listener>();
	     }
		 listeners.add(listener);
	}
	
	
	/*********************************************************************************************************
	** Function name:     	removeListener
	** Descriptions:	             注销注册回调监听接口 用户不想要监听回调只需注销即可
	** input parameters:    EV_listener：接口类
	** output parameters:   无
	** Returned value:      无
	*********************************************************************************************************/
	public static void removeListener(EV_listener listener){
		if(listeners != null){
			listeners.remove(listener);
		}
	}
	
	
	/*********************************************************************************************************
	** Function name:     	notifyListeners
	** Descriptions:	           内部执行回调的入口
	** input parameters:    String：JNI回应的json包
	** output parameters:   无
	** Returned value:      无
	*********************************************************************************************************/
	public static void notifyListeners(String json){
		if(listeners == null){return;}
	    Iterator<EV_listener> iter = listeners.iterator();
	    while(iter.hasNext()){
	    	EV_listener listener = (EV_listener)iter.next();
	        listener.do_json(json);
	    }
	 }
	
	/*********************************************************************************************************
	** Function name:     	EV_listener
	** Descriptions:	           定义事件回调接口
	** input parameters:    无
	** output parameters:   无
	** Returned value:      无
	*********************************************************************************************************/
	public interface EV_listener extends EventListener{
	    public void do_json(String json);
	}
	
	
	
	/*********************************************************************************************************
	 **以下是本接口类的内部方法不对外提供
	*********************************************************************************************************/
	
	/*********************************************************************************************************
	 **定义请求包队列 及函数接口
	*********************************************************************************************************/
	private static Queue<RequestObject> req_list = null;
	public static int pushReq(RequestObject req){
		if(req_list == null){
			req_list = new LinkedList<RequestObject>();
		}
		//System.out.print("pushReq:" + req + "\n\n");
		boolean ok = req_list.offer(req);
		
		if(ev_thread == null){
			ev_thread = new EV_Thread();
			ev_thread.start();
		}
		int ret = (ok == true) ? 1 : 0;
		return ret;
		
	}
	
	public static RequestObject pollReq(){
		if(req_list == null){
			return null;
		}
		else{
			return req_list.poll();
		}
	}
	
	
	public static void req_handle(){
		RequestObject req = pollReq();
		if(req == null){
			//System.out.printf("req_handle:req=null\n");
			return;
		}
		System.out.printf("req_handle:req.type=%d\n", req.type);
		String rptJson = null; 
		switch(req.type){
			case EV_REGISTER:
				rptJson = EVPortRegister(req.portName);
				break;
			case EV_RELEASE:
				rptJson = EVPortRelease(req.fd);
				break;
			case EV_BENTO_OPEN:
				rptJson = EVBentoOpen(req.fd, req.addr, req.box);
				break;
			case EV_BENTO_CHECK:
				rptJson = EVBentoCheck(req.fd, req.addr);
				break;
			case EV_BENTO_LIGHT:
				rptJson = EVBentoLight(req.fd, req.addr,req.opt);
				break;
			case EV_MDB_INIT:
				rptJson = EVmdbInit(req.fd, req.bill,req.coin);
				break;
			case EV_MDB_ENABLE:
				rptJson = EVmdbEnable(req.fd, req.bill,req.coin,req.opt);
				break;
			case EV_MDB_HEART:
				rptJson = EVmdbHeart(req.fd);
				break;
			case EV_MDB_B_INFO:
				rptJson = EVmdbBillInfoCheck(req.fd);
				break;
			case EV_MDB_C_INFO:
				rptJson = EVmdbCoinInfoCheck(req.fd);
				break;
			case EV_MDB_COST:
				rptJson = EVmdbCost(req.fd,req.cost);
				break;
			case EV_MDB_PAYBACK:
				rptJson = EVmdbPayback(req.fd,req.bill,req.coin);
				break;
			case EV_MDB_PAYOUT:
				rptJson = EVmdbPayout(req.fd,req.bill,req.coin,req.billPay,req.coinPay);
				break;
			default:
				rptJson = "";
				break;
		};
		
		if(rptJson != null){
			System.out.println("EV_JSON=" + rptJson);
			notifyListeners(rptJson);
		}
	}
	

	
	
	/*********************************************************************************************************
	 **内部线程专门用于处理回调结果 用于处理复杂的事情 应用层可以尽情的干你需要的事情
	*********************************************************************************************************/
	private static EV_Thread ev_thread = null;
	private static class EV_Thread extends Thread{
		public void run(){			
			while(true){
					req_handle();
					//System.out.printf("EV_Thread:runing...\n");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
	
	/*********************************************************************************************************
	 **JNI库接口函数，注意以下函数接口均为阻塞方式 用户调用需谨慎
	*********************************************************************************************************/
	
	
	/*********************************************************************************************************
	** Function name	:		EVPortRegister
	** Descriptions		:		串口注册接口  [同步]
	** input parameters	:       portName 串口号 例如"COM1"
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":1,"port_id":0}}
	*							"EV_type" = EV_REGISTER = 1; 表示串口注册包类型
	*							"port_id":表示返回的串口编号，如果失败则返回 -1
	*********************************************************************************************************/
	public  native static String EVPortRegister(String portName);
	
	
	/*********************************************************************************************************
	** Function name	:		EVPortRelease 
	** Descriptions		:		串口释放接口  [同步]
	** input parameters	:       port_id 串口编号
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":2,"result":1}}
	*							"EV_type"= EV_RELEASE = 2； 表示串口释放包类型
	*							"result":表示操作结果    1:表示成功释放   0:表示释放失败
	*********************************************************************************************************/
	public  native static String EVPortRelease(int port_id);
	
	
	
	
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVBentoOpen
	** Descriptions		:		快递柜开门接口  [同步]
	** input parameters	:       port_id:串口编号,addr:柜子地址 00-15,box:开门的格子号 1-88
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":11,"port_id":0,"addr":0,"box":1,"is_success":1,"result":1}}
	*							"EV_type"= EV_BENTO_OPEN = 11: 表开门结果回应包类型
	*							"port_id":原样返回,"addr":原样返回柜子地址,"box":原样返回柜内格子号.
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result": 表示处理结果	1:开门成功   0:开门失败
	*********************************************************************************************************/
	public  native static String EVBentoOpen(int port_id,int addr,int box);
	
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVBentoCheck
	** Descriptions		:		快递柜查询接口  [同步]
	** input parameters	:       port_id:串口编号,addr:柜子地址 00-15
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":12,"port_id":0,"addr":0,"is_success":1,"ID":"xxxxxxxxx1",
	*								"cool":0,"hot":0,"light":1,"sum":88,[]}}
	*							"EV_type"= EV_BENTO_CHECK = 12: 表查询结果回应包类型
	*							"port_id":原样返回,"addr":原样返回柜子地址
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"ID":驱动板的ID号
	*							"cool":是否支持制冷 	 	1:支持 0:不支持
	*							"hot":是否支持加热  		1:支持 0:不支持
	*							"light":是否支持照明  	1:支持 0:不支持
	*							"sum":货道总数	例如货道总数88 则默认 货道编号 1-88
	*********************************************************************************************************/
	public  native static String EVBentoCheck(int port_id,int addr);
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVBentoLight
	** Descriptions		:		快递柜照明控制接口  [同步]
	** input parameters	:       port_id:串口编号,addr:柜子地址 00-15,opt:开照明控制 1:开  0:关
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":13,"port_id":0,"addr":0,"opt":1,"is_success":1,"result":1}}
	*							"EV_type"= EV_BENTO_LIGHT = 13: 表照明结果回应包类型
	*							"port_id":原样返回,"addr":原样返回柜子地址,"opt":原样返回操作.
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result": 表示处理结果	1:成功   0:失败
	*********************************************************************************************************/
	public  native static String EVBentoLight(int port_id,int addr,int opt);
	
	
	/*********************************************************************************************************
	** Function name	:		EVmdbInit
	** Descriptions		:		MDB初始化接口  [同步]
	** input parameters	:       port_id:串口编号,bill:操作纸币器  1:操作,0:不操作,coin:操作硬币器  1:操作,0:不操作
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":21,"port_id":0,"bill":1,"coin":1,"is_success":1,"bill_result":1,"coin_result":1}}
	*							"EV_type"= EV_MDB_INIT = 21: 表示MDB初始化结果回应包类型
	*							"port_id":原样返回,"bill":原样返回柜子地址,"coin":原样返回操作.
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"bill_result": 纸币器处理结果	1:成功   0:失败
	*							"coin_result": 纸币器处理结果	1:成功   0:失败
	*********************************************************************************************************/
	public  native static String EVmdbInit(int port_id,int bill,int coin);
	
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVmdbEnable
	** Descriptions		:		MDB使能接口  [同步]
	** input parameters	:       port_id:串口编号; bill:操作纸币器  1:操作 0:不操作;coin:操作硬币器  1:操作,0:不操作 ;opt:操作 1:使能 0:禁能
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":22,"port_id":0,"bill":1,"coin":1,"opt":1,"is_success":1,"bill_result":1,"coin_result":1}}
	*							"EV_type"= EV_MDB_ENABLE = 22: 表示MDB使能结果回应包类型
	*							"port_id":原样返回,"bill":原样返回柜子地址,"coin":原样返回操作;"opt":原样返回
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"bill_result": 纸币器处理结果	1:成功   0:失败
	*							"coin_result": 纸币器处理结果	1:成功   0:失败
	*********************************************************************************************************/
	public  native static String EVmdbEnable(int port_id,int bill,int coin,int opt);
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVmdbHeart
	** Descriptions		:		MDB心跳查询接口  [同步]
	** input parameters	:       port_id:串口编号;
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":23,"port_id":0,"is_success":1,
	*							"bill_enable":1,"bill_payback":0,"bill_err":0,"bill_recv":0,"bill_remain":0,
	*							"coin_enable":1,"coin_payback":0,"coin_err":0,"coin_recv":0,"coin_remain":0}}
	*							"EV_type"= EV_MDB_HEART = 23: 表示MDB心跳查询结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"bill_enable": 纸币器使能状态		1:使能   0:禁能
	*							"bill_payback": 纸币器退币按钮触发	1:触发   0:非触发
	*							"bill_err":纸币器故障状态			0:正常   非0 为故障码
	*							"bill_recv":纸币器当前收币金额	以分为单位
	*							"bill_remain":纸币器当前储币金额	以分为单位
	*	*						"coin_enable": 硬币器使能状态		1:使能   0:禁能
	*							"coin_payback": 硬币器退币按钮触发	1:触发   0:非触发
	*							"coin_err":硬币器故障状态			0:正常   非0 为故障码
	*							"coin_recv":硬币器当前收币金额	以分为单位
	*							"coin_remain":硬币器当前储币金额	以分为单位
	*********************************************************************************************************/
	public  native static String EVmdbHeart(int port_id);
	
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVmdbBillInfoCheck
	** Descriptions		:		MDB纸币器查询接口  [同步]
	** input parameters	:       port_id:串口编号;
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":24,"port_id":0,"is_success":1,
	*							"acceptor":2,"dispenser":2,"code":"ITL","sn":"12312....","model":"NV9",
	*							"ver":"1212","capacity":500,"ch_r":[],"ch_d":[]}}
	*							"EV_type"= EV_MDB_B_INFO = 24: 表示MDB纸币器信息查询结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"acceptor": 纸币接收器协议类型	0:接收器关闭	2:MDB协议接收器
	*							"dispenser": 纸币找零器协议类型	0:找零器关闭	2:MDB协议找零器
	*							"code": 纸币器厂商代码			例如：ITL
	*							"sn":纸币器序列号
	*							"model":纸币器型号				例如:NV9
	*							ver:纸币器软件版本号
	*							capacity:纸币器储币量	
	*							ch_r:纸币器接收器通道面值 		以分为单位
	*							ch_d:纸币器找零器通道面值		以分为单位
	*********************************************************************************************************/
	public  native static String EVmdbBillInfoCheck(int port_id);
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVmdbCoinInfoCheck
	** Descriptions		:		MDB硬币器信息查询接口  [同步]
	** input parameters	:       port_id:串口编号;
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":25,"port_id":0,"is_success":1,
	*							"acceptor":2,"dispenser":2,"code":"MEI","sn":"12312....","model":"***",
	*							"ver":"1212","capacity":500,"ch_r":[],"ch_d":[]}}
	*							"EV_type"= EV_MDB_C_INFO = 25: 表示MDB硬币器信息查询结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"acceptor": 硬币接收器协议类型	0:接收器关闭	1:并行硬币脉冲协议  2:MDB协议接收器 3:串行硬币脉冲协议
	*							"dispenser": 硬币找零器协议类型	0:找零器关闭	1:hopper 串口232协议  2:MDB协议找零器
	*							"code": 硬币器厂商代码			例如：MEI
	*							"sn":硬币器序列号
	*							"model":硬币器型号			
	*							ver:硬币器软件版本号
	*							capacity:硬币器储币量	
	*							ch_r:硬币器接收器通道面值 		以分为单位
	*							ch_d:硬币器找零器通道面值		以分为单位
	*********************************************************************************************************/
	public  native static String EVmdbCoinInfoCheck(int port_id);
	
	
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVmdbCost
	** Descriptions		:		MDB扣款接口  [同步]
	** input parameters	:       port_id:串口编号;cost:扣款金额  以分为单位
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":26,"port_id":0,"cost":100,"is_success":1,
	*							"result":1,"bill_recv":0,"coin_recv":0}}
	*							"EV_type"= EV_MDB_COST = 26: 表示MDB扣款结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:扣款成功     0:扣款失败
	*							"bill_recv":纸币器当前收币金额	  以分为单位
	*							"coin_recv":硬币器当前收币金额	  以分为单位
	*********************************************************************************************************/
	public  native static String EVmdbCost(int port_id,int cost);
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVmdbPayback
	** Descriptions		:		MDB退币接口  [同步]
	** input parameters	:       port_id:串口编号;bill:操作纸币器  1:操作,0:不操作,coin:操作硬币器  1:操作,0:不操作
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":26,"port_id":0,"bill":1,"coin":1,"is_success":1,
	*							"result":1,"bill_changed":0,"coin_changed":100}}
	*							"EV_type"= EV_MDB_COST = 26: 表示MDB退币结果回应包类型
	*							"port_id":原样返回,"bill":原样返回,"coin":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:退币成功     0:退币失败
	*							"bill_changed":纸币器当前找币金额	  以分为单位
	*							"coin_changed":硬币器当前找币金额	  以分为单位
	*********************************************************************************************************/
	public  native static String EVmdbPayback(int port_id,int bill,int coin);
	
	
	
	/*********************************************************************************************************
	** Function name	:		EVmdbPayout
	** Descriptions		:		MDB找币接口  [同步]
	** input parameters	:       port_id:串口编号;bill:操作纸币器  1:操作,0:不操作,coin:操作硬币器  1:操作,0:不操作;
	*							billPayout:纸币器下发找币金额 分为单位 ;coinPayout:硬币器下发找币金额 分为单位 
	** output parameters:		无
	** Returned value	:		返回json包     例如： EV_JSON={"EV_json":{"EV_type":26,"port_id":0,"bill":0,"coin":1,
	*							"billPayout":0,"coinPayout":100,"is_success":1,
	*							"result":1,"bill_changed":0,"coin_changed":100}}
	*							"EV_type"= EV_MDB_COST = 26: 表示MDB找币结果回应包类型
	*							"port_id":原样返回,"bill":原样返回,"coin":原样返回,"billPayout":原样返回,"coinPayout":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:找币成功     0:找币失败
	*							"bill_changed":纸币器当前找币金额	  以分为单位
	*							"coin_changed":硬币器当前找币金额	  以分为单位
	*********************************************************************************************************/
	public  native static String EVmdbPayout(int port_id,int bill,int coin,int billPay,int coinPay);
	
	
	
}