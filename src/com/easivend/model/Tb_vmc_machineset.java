/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified String:  2015-01-10
** Last Version:         
** Descriptions:        vmc_machineset机器配置表           
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created String:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;



public class Tb_vmc_machineset 
{
	private String logoStr;// 是否使用logo，1使用,默认0
    private int audioWork;// 工作日开启音量大小，0到100
    private String audioWorkstart;//工作日开启时间
    private String audioWorkend;//工作日结束时间
    private int audioSun;// 节假日开启音量大小，0到100
    private String audioSunstart;//节假日开启时间
    private String audioSunend;//节假日结束时间
    private int tempWork;// 开启温度大小，0到50，0.1为精度
    private String tempWorkstart;//温度工作日开启时间
    private String tempWorkend;//温度工作日结束时间
    private String tempSunstart;//温度节假日开启时间
    private String tempSunend;//温度节假日结束时间
    private String ligntWorkstart;//照明工作日开启时间
    private String ligntWorkend;//照明工作日结束时间
    private String ligntSunstart;//照明节假日开启时间
    private String ligntSunend;//照明节假日结束时间
    private String coldWorkstart;//制冷工作日开启时间
    private String coldWorkend;//制冷工作日结束时间
    private String coldSunstart;//制冷节假日开启时间
    private String coldSunend;//制冷节假日结束时间
    private String chouWorkstart;//除臭工作日开启时间
    private String chouWorkend;//除臭工作日结束时间
    private String chouSunstart;//除臭节假日开启时间
    private String chouSunend;//除臭节假日结束时间
    // 定义有参构造函数，用来初始化收入信息实体类中的各个字段
	public Tb_vmc_machineset(String logoStr, int audioWork,
			String audioWorkstart, String audioWorkend, int audioSun,
			String audioSunstart, String audioSunend, int tempWork,
			String tempWorkstart, String tempWorkend, String tempSunstart,
			String tempSunend, String ligntWorkstart, String ligntWorkend,
			String ligntSunstart, String ligntSunend, String coldWorkstart,
			String coldWorkend, String coldSunstart, String coldSunend,
			String chouWorkstart, String chouWorkend, String chouSunstart,
			String chouSunend) {
		super();
		this.logoStr = logoStr;
		this.audioWork = audioWork;
		this.audioWorkstart = audioWorkstart;
		this.audioWorkend = audioWorkend;
		this.audioSun = audioSun;
		this.audioSunstart = audioSunstart;
		this.audioSunend = audioSunend;
		this.tempWork = tempWork;
		this.tempWorkstart = tempWorkstart;
		this.tempWorkend = tempWorkend;
		this.tempSunstart = tempSunstart;
		this.tempSunend = tempSunend;
		this.ligntWorkstart = ligntWorkstart;
		this.ligntWorkend = ligntWorkend;
		this.ligntSunstart = ligntSunstart;
		this.ligntSunend = ligntSunend;
		this.coldWorkstart = coldWorkstart;
		this.coldWorkend = coldWorkend;
		this.coldSunstart = coldSunstart;
		this.coldSunend = coldSunend;
		this.chouWorkstart = chouWorkstart;
		this.chouWorkend = chouWorkend;
		this.chouSunstart = chouSunstart;
		this.chouSunend = chouSunend;
	}	
	public String getLogoStr() {
		return logoStr;
	}
	public void setLogoStr(String logoStr) {
		this.logoStr = logoStr;
	}
	public int getAudioWork() {
		return audioWork;
	}
	public void setAudioWork(int audioWork) {
		this.audioWork = audioWork;
	}
	public String getAudioWorkstart() {
		return audioWorkstart;
	}
	public void setAudioWorkstart(String audioWorkstart) {
		this.audioWorkstart = audioWorkstart;
	}
	public String getAudioWorkend() {
		return audioWorkend;
	}
	public void setAudioWorkend(String audioWorkend) {
		this.audioWorkend = audioWorkend;
	}
	public int getAudioSun() {
		return audioSun;
	}
	public void setAudioSun(int audioSun) {
		this.audioSun = audioSun;
	}
	public String getAudioSunstart() {
		return audioSunstart;
	}
	public void setAudioSunstart(String audioSunstart) {
		this.audioSunstart = audioSunstart;
	}
	public String getAudioSunend() {
		return audioSunend;
	}
	public void setAudioSunend(String audioSunend) {
		this.audioSunend = audioSunend;
	}
	public int getTempWork() {
		return tempWork;
	}
	public void setTempWork(int tempWork) {
		this.tempWork = tempWork;
	}
	public String getTempWorkstart() {
		return tempWorkstart;
	}
	public void setTempWorkstart(String tempWorkstart) {
		this.tempWorkstart = tempWorkstart;
	}
	public String getTempWorkend() {
		return tempWorkend;
	}
	public void setTempWorkend(String tempWorkend) {
		this.tempWorkend = tempWorkend;
	}
	public String getTempSunstart() {
		return tempSunstart;
	}
	public void setTempSunstart(String tempSunstart) {
		this.tempSunstart = tempSunstart;
	}
	public String getTempSunend() {
		return tempSunend;
	}
	public void setTempSunend(String tempSunend) {
		this.tempSunend = tempSunend;
	}
	public String getLigntWorkstart() {
		return ligntWorkstart;
	}
	public void setLigntWorkstart(String ligntWorkstart) {
		this.ligntWorkstart = ligntWorkstart;
	}
	public String getLigntWorkend() {
		return ligntWorkend;
	}
	public void setLigntWorkend(String ligntWorkend) {
		this.ligntWorkend = ligntWorkend;
	}
	public String getLigntSunstart() {
		return ligntSunstart;
	}
	public void setLigntSunstart(String ligntSunstart) {
		this.ligntSunstart = ligntSunstart;
	}
	public String getLigntSunend() {
		return ligntSunend;
	}
	public void setLigntSunend(String ligntSunend) {
		this.ligntSunend = ligntSunend;
	}
	public String getColdWorkstart() {
		return coldWorkstart;
	}
	public void setColdWorkstart(String coldWorkstart) {
		this.coldWorkstart = coldWorkstart;
	}
	public String getColdWorkend() {
		return coldWorkend;
	}
	public void setColdWorkend(String coldWorkend) {
		this.coldWorkend = coldWorkend;
	}
	public String getColdSunstart() {
		return coldSunstart;
	}
	public void setColdSunstart(String coldSunstart) {
		this.coldSunstart = coldSunstart;
	}
	public String getColdSunend() {
		return coldSunend;
	}
	public void setColdSunend(String coldSunend) {
		this.coldSunend = coldSunend;
	}
	public String getChouWorkstart() {
		return chouWorkstart;
	}
	public void setChouWorkstart(String chouWorkstart) {
		this.chouWorkstart = chouWorkstart;
	}
	public String getChouWorkend() {
		return chouWorkend;
	}
	public void setChouWorkend(String chouWorkend) {
		this.chouWorkend = chouWorkend;
	}
	public String getChouSunstart() {
		return chouSunstart;
	}
	public void setChouSunstart(String chouSunstart) {
		this.chouSunstart = chouSunstart;
	}
	public String getChouSunend() {
		return chouSunend;
	}
	public void setChouSunend(String chouSunend) {
		this.chouSunend = chouSunend;
	}
    
}
