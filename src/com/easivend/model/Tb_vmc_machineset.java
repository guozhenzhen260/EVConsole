/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_machineset机器配置表           
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.model;

import java.util.Date;

public class Tb_vmc_machineset 
{
	private int machID;// [pk]
    private int islogo;// 是否使用logo，1使用,默认0
    private int audioWork;// 工作日开启音量大小，0到100
    private Date audioWorkstart;//工作日开启时间
    private Date audioWorkend;//工作日结束时间
    private int audioSun;// 节假日开启音量大小，0到100
    private Date audioSunstart;//节假日开启时间
    private Date audioSunend;//节假日结束时间
    private int tempWork;// 开启温度大小，0到50，0.1为精度
    private Date tempWorkstart;//温度工作日开启时间
    private Date tempWorkend;//温度工作日结束时间
    private Date tempSunstart;//温度节假日开启时间
    private Date tempSunend;//温度节假日结束时间
    private Date ligntWorkstart;//照明工作日开启时间
    private Date ligntWorkend;//照明工作日结束时间
    private Date ligntSunstart;//照明节假日开启时间
    private Date ligntSunend;//照明节假日结束时间
    private Date coldWorkstart;//制冷工作日开启时间
    private Date coldWorkend;//制冷工作日结束时间
    private Date coldSunstart;//制冷节假日开启时间
    private Date coldSunend;//制冷节假日结束时间
    private Date chouWorkstart;//除臭工作日开启时间
    private Date chouWorkend;//除臭工作日结束时间
    private Date chouSunstart;//除臭节假日开启时间
    private Date chouSunend;//除臭节假日结束时间
    // 定义有参构造函数，用来初始化收入信息实体类中的各个字段
	public Tb_vmc_machineset(int machID, int islogo, int audioWork,
			Date audioWorkstart, Date audioWorkend, int audioSun,
			Date audioSunstart, Date audioSunend, int tempWork,
			Date tempWorkstart, Date tempWorkend, Date tempSunstart,
			Date tempSunend, Date ligntWorkstart, Date ligntWorkend,
			Date ligntSunstart, Date ligntSunend, Date coldWorkstart,
			Date coldWorkend, Date coldSunstart, Date coldSunend,
			Date chouWorkstart, Date chouWorkend, Date chouSunstart,
			Date chouSunend) {
		super();
		this.machID = machID;
		this.islogo = islogo;
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
	public int getMachID() {
		return machID;
	}
	public void setMachID(int machID) {
		this.machID = machID;
	}
	public int getIslogo() {
		return islogo;
	}
	public void setIslogo(int islogo) {
		this.islogo = islogo;
	}
	public int getAudioWork() {
		return audioWork;
	}
	public void setAudioWork(int audioWork) {
		this.audioWork = audioWork;
	}
	public Date getAudioWorkstart() {
		return audioWorkstart;
	}
	public void setAudioWorkstart(Date audioWorkstart) {
		this.audioWorkstart = audioWorkstart;
	}
	public Date getAudioWorkend() {
		return audioWorkend;
	}
	public void setAudioWorkend(Date audioWorkend) {
		this.audioWorkend = audioWorkend;
	}
	public int getAudioSun() {
		return audioSun;
	}
	public void setAudioSun(int audioSun) {
		this.audioSun = audioSun;
	}
	public Date getAudioSunstart() {
		return audioSunstart;
	}
	public void setAudioSunstart(Date audioSunstart) {
		this.audioSunstart = audioSunstart;
	}
	public Date getAudioSunend() {
		return audioSunend;
	}
	public void setAudioSunend(Date audioSunend) {
		this.audioSunend = audioSunend;
	}
	public int getTempWork() {
		return tempWork;
	}
	public void setTempWork(int tempWork) {
		this.tempWork = tempWork;
	}
	public Date getTempWorkstart() {
		return tempWorkstart;
	}
	public void setTempWorkstart(Date tempWorkstart) {
		this.tempWorkstart = tempWorkstart;
	}
	public Date getTempWorkend() {
		return tempWorkend;
	}
	public void setTempWorkend(Date tempWorkend) {
		this.tempWorkend = tempWorkend;
	}
	public Date getTempSunstart() {
		return tempSunstart;
	}
	public void setTempSunstart(Date tempSunstart) {
		this.tempSunstart = tempSunstart;
	}
	public Date getTempSunend() {
		return tempSunend;
	}
	public void setTempSunend(Date tempSunend) {
		this.tempSunend = tempSunend;
	}
	public Date getLigntWorkstart() {
		return ligntWorkstart;
	}
	public void setLigntWorkstart(Date ligntWorkstart) {
		this.ligntWorkstart = ligntWorkstart;
	}
	public Date getLigntWorkend() {
		return ligntWorkend;
	}
	public void setLigntWorkend(Date ligntWorkend) {
		this.ligntWorkend = ligntWorkend;
	}
	public Date getLigntSunstart() {
		return ligntSunstart;
	}
	public void setLigntSunstart(Date ligntSunstart) {
		this.ligntSunstart = ligntSunstart;
	}
	public Date getLigntSunend() {
		return ligntSunend;
	}
	public void setLigntSunend(Date ligntSunend) {
		this.ligntSunend = ligntSunend;
	}
	public Date getColdWorkstart() {
		return coldWorkstart;
	}
	public void setColdWorkstart(Date coldWorkstart) {
		this.coldWorkstart = coldWorkstart;
	}
	public Date getColdWorkend() {
		return coldWorkend;
	}
	public void setColdWorkend(Date coldWorkend) {
		this.coldWorkend = coldWorkend;
	}
	public Date getColdSunstart() {
		return coldSunstart;
	}
	public void setColdSunstart(Date coldSunstart) {
		this.coldSunstart = coldSunstart;
	}
	public Date getColdSunend() {
		return coldSunend;
	}
	public void setColdSunend(Date coldSunend) {
		this.coldSunend = coldSunend;
	}
	public Date getChouWorkstart() {
		return chouWorkstart;
	}
	public void setChouWorkstart(Date chouWorkstart) {
		this.chouWorkstart = chouWorkstart;
	}
	public Date getChouWorkend() {
		return chouWorkend;
	}
	public void setChouWorkend(Date chouWorkend) {
		this.chouWorkend = chouWorkend;
	}
	public Date getChouSunstart() {
		return chouSunstart;
	}
	public void setChouSunstart(Date chouSunstart) {
		this.chouSunstart = chouSunstart;
	}
	public Date getChouSunend() {
		return chouSunend;
	}
	public void setChouSunend(Date chouSunend) {
		this.chouSunend = chouSunend;
	}
    
}
