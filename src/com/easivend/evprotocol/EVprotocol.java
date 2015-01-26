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
			ev_thread = new EV_Thread(this);
			ev_thread.start();
		}
		ev = this;//如果有构造则将指针传递给静态变量
	} 
	
	private static EVprotocol ev = null;
	public static EVprotocol obtain(){ //节约内存消耗
		if(ev == null){
			ev = new EVprotocol();
		}
		return ev;
	}
	
	//加载JNI动态链接库
	static{
		System.loadLibrary("EVprotocol");
		
	}
	
	
	/*********************************************************************************************************
	** Function name:     	EV_callBack
	** Descriptions:	    VMC回调入口（所有较长的结果回应都会通过该函数回调显示）
	** input parameters:    json_msg:回应结果的JSON包。 
	** output parameters:   无
	** Returned value:      无
	*********************************************************************************************************/
	public static void EV_callBack(String json_msg)
	{
		System.out.println("JSON:"+json_msg);		
		addMsg(json_msg);
	}

	
	/*********************************************************************************************************
	** Function name:     	vmcStart
	** Descriptions:	    VMC主控板开启接口
	** input parameters:    portName 串口号 例如/dev/tty0
	** output parameters:   无
	** Returned value:      1:开启成功      -1:开启失败  （直接返回 不进行回调）
	*********************************************************************************************************/
	public  native int vmcStart(String portName);
	
	
	
	
	
	/*********************************************************************************************************
	** Function name:     	vmcStop
	** Descriptions:	    VMC主控板断开接口 与vmcStart配套使用 一般程序结束调用
	** input parameters:    无
	** output parameters:   无
	** Returned value:      无（直接返回 不进行回调）
	*********************************************************************************************************/
	public  native void vmcStop();
	
	
	/*********************************************************************************************************
	** Function name:     	trade
	** Descriptions:	    VMC出货接口  
	**						PC发送该指令后，首先判断返回值为1则请求发送成功。然后通过回调函数返回出货的结果进行解析
	**						回调JSON包 格式 
	** input parameters:    cabinet:柜号  column:货道号   type:支付方式  type= 0 :现金  type = 1  非现金    
	**					    cost:扣款金额(单位:分 ;如果type=1则该值必须为0)
	** output parameters:   无
	** Returned value:      1请求发送成功   0:请求发送失败  
	*********************************************************************************************************/
	public  native int trade(int cabinet,int column,int type,int cost);
	
	
	
	/*********************************************************************************************************
	** Function name:     	payout
	** Descriptions:	    VMC出币接口
	**						PC发送该指令后，首先判断返回值为1则请求发送成功。然后通过回调函数返回出货的结果进行解析
	** input parameters:    value:要出币的金额(单位:分)
	** output parameters:   无
	** Returned value:      1请求发送成功   0:请求发送失败
	*********************************************************************************************************/
	public  native int payout(long value);
	
	
	
	/*********************************************************************************************************
	** Function name:     	getStatus
	** Descriptions:	             获取VMC状态接口  PC发送该指令后，首先判断返回值为1则请求发送成功。然后通过回调函数返回出货的结果进行解析
	** input parameters:    无
	** output parameters:   无
	** Returned value:      1请求发送成功   0:请求发送失败
	*********************************************************************************************************/
	public  native int getStatus();
	
	
	/*********************************************************************************************************
	** Function name:     	getRemainAmount
	** Descriptions:	             获取VMC投币余额  不进行回调 直接返回当前余额 
	** input parameters:    无
	** output parameters:   无
	** Returned value:      返回当前余额 单位:分
	*********************************************************************************************************/
	public  native long getRemainAmount();
	
	
	
	/*********************************************************************************************************
	** Function name:     	cashControl
	** Descriptions:	             控制现金设备 直接返回 不进行回调
	** input parameters:    flag 1:开启现金设备  0关闭现金设备
	** output parameters:   无
	** Returned value:      1成功  0失败
	*********************************************************************************************************/
	public  native int cashControl(int flag);
	
	
	
	/*********************************************************************************************************
	** Function name:     	setDate
	** Descriptions:	             设置下位机时间 直接返回 不进行回调
	** input parameters:    date 日期 格式"2014-10-10 12:24:24"
	** output parameters:   无
	** Returned value:      1成功  0失败
	*********************************************************************************************************/
	public  native int setDate(String date);
	
	
	/*********************************************************************************************************
	** Function name:     	cabinetControl
	** Descriptions:	            柜内设备控制 直接返回 不进行回调
	** input parameters:    cabinet 柜号（1:柜1  2:柜2）  dev 设备(1:制冷   2:加热 3:照明  4:除臭)       flag 1开  0关
	** output parameters:   无
	** Returned value:      1成功  0失败
	*********************************************************************************************************/
	public  native int cabinetControl(int cabinet,int dev,int flag);
	
	
	
	
	
	
	/*********************************************************************************************************
	** Function name:     	bentoRegister
	** Descriptions:	             便利柜初始化接口
	** input parameters:    portName 串口号 例如/dev/tty0
	** output parameters:   无
	** Returned value:      1:初始化成功      -1:初始化失败 (失败原因为串口打开失败)
	*********************************************************************************************************/
	public  native int bentoRegister(String portName);
	
	
	
	/*********************************************************************************************************
	** Function name:     	bentoRelease
	** Descriptions:	             便利柜释放资源接口（与bentoRegister配套调用）
	** input parameters:    无
	** output parameters:   无
	** Returned value:      1:成功      （调用必定成功）
	*********************************************************************************************************/
	public  native int bentoRelease();
	

	/*********************************************************************************************************
	** Function name:     	bentoOpen
	** Descriptions:	             便利柜开格子接口
	** input parameters:    cabinet:柜号     box:格子号
	** output parameters:   无
	** Returned value:      1:打开成功      0:打开失败
	*********************************************************************************************************/
	public  native int bentoOpen(int cabinet,int box);
	
	
	
	/*********************************************************************************************************
	** Function name:     	bentoLight
	** Descriptions:	             便利柜照明控制接口
	** input parameters:    cabinet:柜号     flag:1 表示照明开    0:表示照明关
	** output parameters:   无
	** Returned value:      1:打开成功      0:打开失败
	*********************************************************************************************************/
	public  native int bentoLight(int cabinet,int flag);//flag 1开灯  0关灯
	
	
	/*********************************************************************************************************
	** Function name:     	bentoCheck
	** Descriptions:	             便利柜状态查询接口
	** input parameters:    cabinet:柜号     
	** output parameters:   无
	** Returned value:      返回的是一个JSON包 例如{{}}
	*********************************************************************************************************/
	public  native String bentoCheck(int cabinet);
	
	
	
	
	
	
	
	
	
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
	public void addListener(EV_listener listener){
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
	public void removeListener(EV_listener listener){
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
	    Iterator<EV_listener> iter = listeners.iterator();
	    while(iter.hasNext())
	    {
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
	private static Queue<String> queue_json = null;
	public  static void addMsg(String json){
		if(queue_json == null)
			queue_json = new LinkedList<String>();
		queue_json.offer(json); 
	}
	
	public String pollMsg(){
		if(queue_json != null)
			return queue_json.poll();
		else
			return null;
	}
	
	public static void EV_msg_handle(){
		if(queue_json == null)
			return;
		if(listeners != null){
			if(listeners.isEmpty() == false){
				String json = queue_json.poll();
				if(json != null)
					notifyListeners(json);
				return;
			}
		}
		else{
			if(queue_json.size() >= 50){//没有注册事件监听 则默认保留消息50条
				queue_json.poll();
			}	
		}

		

		
				
	}
	
	
	/*********************************************************************************************************
	 **内部线程专门用于处理回调结果 用于处理复杂的事情 应用层可以尽情的干你需要的事情
	*********************************************************************************************************/
	private static EV_Thread ev_thread = null;
	private static class EV_Thread extends Thread{
		public EV_Thread(EVprotocol ev){
			this.ev = ev;
		}
		private EVprotocol ev = null;
		public void run(){			
			while(true){
					EV_msg_handle();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
	
	//JNI 静态回调函数 示例代码 没有用
//	public static void EV_callBackStatic(int i) 
//	{
//		System.out.println("EV_callBackStatic...");
//	}
	
}