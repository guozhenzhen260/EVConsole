/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           AudioSound.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        ¼ÓÔØ±³¾°ÉùÒôÎÄ¼þ    
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.common;

import java.util.HashMap;
import java.util.Map;

import com.example.evconsole.R;


import android.media.AudioManager;
import android.media.SoundPool;

public class AudioSound 
{
	private static SoundPool soudpool;
	private static Map<Integer,Integer> soudmap=new HashMap<Integer,Integer>();
	public static void initsound()
	{
		soudpool=new SoundPool(15, AudioManager.STREAM_SYSTEM, 0);
        soudmap.put(1, soudpool.load(ToolClass.getContext(), R.raw.welcome,1));
        soudmap.put(2, soudpool.load(ToolClass.getContext(), R.raw.business,1));
        soudmap.put(3, soudpool.load(ToolClass.getContext(), R.raw.busselect,1));
        soudmap.put(4, soudpool.load(ToolClass.getContext(), R.raw.buszhiamount,1));
        soudmap.put(5, soudpool.load(ToolClass.getContext(), R.raw.buszhipos,1));
        soudmap.put(6, soudpool.load(ToolClass.getContext(), R.raw.buszhier,1));
        soudmap.put(7, soudpool.load(ToolClass.getContext(), R.raw.buszhiwei,1));
        soudmap.put(8, soudpool.load(ToolClass.getContext(), R.raw.bushuogezi,1));
        soudmap.put(9, soudpool.load(ToolClass.getContext(), R.raw.bushuotang,1));
        soudmap.put(10, soudpool.load(ToolClass.getContext(), R.raw.buspayout,1));
        soudmap.put(11, soudpool.load(ToolClass.getContext(), R.raw.busfinish,1));
        soudmap.put(12, soudpool.load(ToolClass.getContext(), R.raw.welcomeegg,1));
	}
	
	public static void playwelcome()
	{
		soudpool.play(soudmap.get(1), 1, 1, 0, 0, 1);
	}
	public static void playbusiness()
	{
		soudpool.play(soudmap.get(2), 1, 1, 0, 0, 1);
	}
	public static void playbusselect()
	{
		soudpool.play(soudmap.get(3), 1, 1, 0, 0, 1);
	}
	public static void playbuszhiamount()
	{
		soudpool.play(soudmap.get(4), 1, 1, 0, 0, 1);
	}
	public static void playbuszhipos()
	{
		soudpool.play(soudmap.get(5), 1, 1, 0, 0, 1);
	}
	public static void playbuszhier()
	{
		soudpool.play(soudmap.get(6), 1, 1, 0, 0, 1);
	}
	public static void playbuszhiwei()
	{
		soudpool.play(soudmap.get(7), 1, 1, 0, 0, 1);
	}
	public static void playbushuogezi()
	{
		soudpool.play(soudmap.get(8), 1, 1, 0, 0, 1);
	}
	public static void playbushuotang()
	{
		soudpool.play(soudmap.get(9), 1, 1, 0, 0, 1);
	}
	public static void playbuspayout()
	{
		soudpool.play(soudmap.get(10), 1, 1, 0, 0, 1);
	}
	public static void playbusfinish()
	{
		soudpool.play(soudmap.get(11), 1, 1, 0, 0, 1);
	}
	public static void playwelcomeegg()
	{
		soudpool.play(soudmap.get(12), 1, 1, 0, 0, 1);
	}
}
