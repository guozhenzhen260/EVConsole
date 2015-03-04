/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.easivend.alipay;
/**
 * @支付宝调用包
 * 
 * AlipayConfig文件配置类，调用AlipayConfigAPI函数里面的方法来配置
 * ->md5.java用来加密和解密->alipaycore,alipaysubmit用来封装包
 * ->httprespons,httprequester用来连接然后发送包，完了接收返回包
 * 
 * 返回的包，用AlipayConfigAPI进行解包->alipaynotify校验包是否正确
 * 
 * ,AlipayConfigAPI，httprequester用来与Zhifubaohttp进行交互
 */