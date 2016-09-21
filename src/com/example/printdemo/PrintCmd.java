package com.example.printdemo;
//package com.example.comdemo;
//
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//
//import android.util.Log;
//import cn.jelly.qrcode.util.QRCodeInfo;
//
//@SuppressWarnings("unused")
//public abstract class PrintCmd {
//	private static String hexString = "0123456789ABCDEF";
//	
//	/** ======================================================================= */
//
//	/* START================常用打印接口说明==============
//	 * --------【说明：无@return 返回统一返回 byte[];其他返回类型，准参�?�@return实际类型�? */
//	
//	/**
//	 * 3.1 设置指令模式
//	 *     描述：设置打印机指令模式
//	 * @param iMode 2 EPIC模式�?3 EPOS模式
//	 */
//	public static byte[] SetCommmandmode(int iMode) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x79;
//		if ((iMode == 2) || (iMode == 3))
//			bCmd[iIndex++] = (byte) iMode;
//		else
//			bCmd[iIndex++] = 3;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.2 清理缓存
//	 *     描述：清理缓存，清除之前设置的参�?
//	 */
//	public static byte[] SetClean(){
//		byte[] bCmd = new byte[2];
//		int iIndex = 0;
//		bCmd[iIndex++]= 0x1B;
//		bCmd[iIndex++]= 0x40;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.3 设定行间�?
//	 * @param iLinespace 行间距，取�??0-127，单�?0.125mm
//	 */
//	public static byte[] SetLinespace(int iLinespace) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x33;
//		if (iLinespace > 127) {
//			bCmd[iIndex++] = 127;
//		} else
//			bCmd[iIndex++] = (byte) iLinespace;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.4 设置字符间距 
//	 * @param iSpace 字符间距，取�?0-64，单�?0.125mm
//	 */
//	public static byte[] SetSpacechar(int iSpace) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x20;
//		if (iSpace > 64) {
//			bCmd[iIndex++] = 64;
//		} else
//			bCmd[iIndex++] = (byte) iSpace;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.5 设置汉字间距
//	 * @param iChsleftspace  汉字左空，取�?0-64，单�?0.125mm
//	 * @param iChsrightspace 汉字右空，取�?0-64，单�?0.125mm
//	 */
//	public static byte[] SetSpacechinese(int iChsleftspace, int iChsrightspace) {
//		byte[] bCmd = new byte[4];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1C;
//		bCmd[iIndex++] = 0x53;
//		if (iChsleftspace > 64) {
//			bCmd[iIndex++] = 64;
//		} else
//			bCmd[iIndex++] = (byte) iChsleftspace;
//
//		if (iChsrightspace > 64) {
//			bCmd[iIndex++] = 64;
//		} else
//			bCmd[iIndex++] = (byte) iChsrightspace;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.6 设置左边�?
//	 * @param iLeftspace 取�??0-576，单�?0.125mm
//	 */
//	public static byte[] SetLeftmargin(int iLeftspace) {
//		byte[] bCmd = new byte[4];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1D;
//		bCmd[iIndex++] = 0x4C;
//		if (iLeftspace > 576) {
//			bCmd[iIndex++] = 0;
//			bCmd[iIndex++] = 0;
//		} else {
//			bCmd[iIndex++] = (byte) (iLeftspace % 256);
//			bCmd[iIndex++] = (byte) (iLeftspace / 256);
//		}
//		return bCmd;
//	}
//	
//	/**
//	 * 3.7 设置黑标切纸偏移�?
//	 * @param iOffset 偏移量，取�??0-1600
//	 */
//	public static byte[] SetMarkoffsetcut(int iOffset){
//		byte[] bCmd = new byte[6];
//		int iIndex = 0;
//		bCmd[iIndex++]=0x13; 
//		bCmd[iIndex++]=0x74; 
//		bCmd[iIndex++]=0x33; 
//		bCmd[iIndex++]=0x78; 
//		if(iOffset > 1600){
//			iOffset = 1600;
//		}else{
//			bCmd[iIndex++]=(byte) (iOffset>>8); 
////			bCmd[iIndex++]=(byte) (iOffset * 256); 
//			bCmd[iIndex++]=(byte) iOffset; 
//		}
//		return bCmd;
//	}
//	
//	/**
//	 * 3.8 设置黑标打印进纸偏移�?
//	 * @param iOffset 偏移量，取�?? 0-1600
//	 */
//	public static byte[] SetMarkoffsetprint(int iOffset){
//		byte[] bCmd = new byte[6];
//		int iIndex=0; 
//		bCmd[iIndex++]=0x13; 
//		bCmd[iIndex++]=0x74; 
//		bCmd[iIndex++]=0x11; 
//		bCmd[iIndex++]=0x78; 
//		if(iOffset > 1600){
//			iOffset = 1600;
//		}else{
//			bCmd[iIndex++]=(byte) (iOffset>>8); 
//			bCmd[iIndex++]=(byte) iOffset; 
//		}
//		return bCmd;
//	}
//	
//	/**
//	 * 3.9 设置汉字放大
//	 * @param iHeight      倍高     0 无效  1 有效
//	 * @param iWidth       倍宽     0 无效  1 有效
//	 * @param iUnderline   下划�?   0 无效  1 有效
//	 * @param iChinesetype 汉字字形   0: 24*24  1: 16*16
//	 */
//	public static byte[] SetSizechinese(int iHeight, int iWidth,
//			int iUnderline, int iChinesetype) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		int height = iHeight;
//		int width = iWidth;
//		int underline = iUnderline;
//		if (height > 1)
//			height = 1;
//		
//		if (iWidth > 1)
//			width = 1;
//		
//		if (iUnderline > 1)
//			underline = 1;
//		
//		int iSize = height * 0x08 + width * 0x04 + underline * 0x80
//				+ iChinesetype * 0x01;
//
//		bCmd[iIndex++] = 0x1C;
//		bCmd[iIndex++] = 0x21;
//		bCmd[iIndex++] = (byte) iSize;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.10 设置字符放大
//	 * @param iHeight         倍高     0 无效  1 有效
//	 * @param iWidth          倍宽     0 无效  1 有效
//	 * @param iUnderline      下划�?   0 无效  1 有效
//	 * @param int iAsciitype  ASCII字形   0: 12*24  1: 9*17
//	 */
//	public static byte[] SetSizechar(int iHeight,int iWidth,int iUnderline,int iAsciitype){
//		byte[] bCmd = new byte[3];
//		int iIndex=0;		       
//		int height = iHeight;
//		int width = iWidth;
//		int underline = iUnderline;
//		int asciitype = iAsciitype;
//		if(height>1) 
//			height = 1; 
//        if(iWidth>1) 
//			width = 1; 
//		if(underline>1) 
//			underline = 1; 
//		if(asciitype>1) 
//			asciitype = 1;
//		
//		int iSize = height*0x10+width*0x20+underline*0x80+asciitype*0x01;
//		bCmd[iIndex++]=0x1B;
//		bCmd[iIndex++]=0x21;
//		bCmd[iIndex++]=(byte) iSize;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.11 设置文本放大
//	 * @param iWidth 宽度�?1-8�?
//	 * @param iHeight 高度�?1-8�?
//	 */
//	public static byte[] SetSizetext(int iWidth, int iHeight) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		int height = iHeight;
//		int width = iWidth;
//
//		if (height > 8)
//			height = 8;
//		if (width > 8)
//			width = 8;
//		
//		int iSize = iHeight + iWidth * 0x10;
//		bCmd[iIndex++] = 0x1D;
//		bCmd[iIndex++] = 0x21;
//		bCmd[iIndex++] = (byte) iSize;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.12 设置字符对齐
//	 * @param iAlignment 0 左对齐，1 居中�?2 右对�?
//	 */
//	public static byte[] SetAlignment(int iAlignment) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x61;
//		if (iAlignment > 2)
//			bCmd[iIndex++] = 2;
//		else
//			bCmd[iIndex++] = (byte) iAlignment;
//		return bCmd;
//	}
//
//	/**
//	 * 3.13 设置字体加粗
//	 * @param iBold  0 不加�?,1 加粗
//	 */
//	public static byte[] SetBold(int iBold) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++]= 0x1B;
//		bCmd[iIndex++]= 0x47;// 0x47 都可以加�?
//		if(iBold != 1)
//			bCmd[iIndex++]= 0;
//		else
//			bCmd[iIndex++]= 1;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.14 设置字体旋转
//	 * @param iRotate 0 解除旋转,1 顺时针度旋转90°
//	 */
//	public static byte[] SetRotate(int iRotate){
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x56;
//		if (iRotate != 1)
//			bCmd[iIndex++] = 0;
//		else
//			bCmd[iIndex++] = 1;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.15 设置字体方向
//	 * @param iDirection 0 左至右，1 旋转180�?
//	 */
//	public static byte[] SetDirection(int iDirection){
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x7B;
//		if(iDirection != 1)
//			bCmd[iIndex++] = 0;
//		else{
//			bCmd[iIndex++] = 1;
//		}
//		return bCmd;
//	}
//	
//	/**
//	 * 3.16 设定反白
//	 * @param iWhite  0  取消反白�?1 设置反白
//	 */
//	public static byte[] SetWhitemodel(int iWhite) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1D;
//		bCmd[iIndex++] = 0x42;
//		if (iWhite != 1)
//			bCmd[iIndex++] = 0;
//		else
//			bCmd[iIndex++] = 1;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.17 设定斜体
//	 * @param iItalic  0 取消斜体�?1 设置斜体
//	 */
//	public static byte[] SetItalic(int iItalic) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x25;
//		if(iItalic == 1)
//			bCmd[iIndex++] = 0x47;
//		else
//			bCmd[iIndex++] = 0x48; 
//		return bCmd;
//	}
//	
//	/**
//	 * 3.18 设定下划�? 
//	 * @param underline 0  无， 1 �?个点下划线，2 两个点下划线 ；其他无�?
//	 *      描述：设置下划线（字符，ASCII 都有效）
//	 */
//	public static byte[] SetUnderline(int underline) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x2D;
//		if (underline > 2)
//			bCmd[iIndex++] = 2;
//		else
//			bCmd[iIndex++] = (byte) underline;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.19 设置汉字模式
//	 * @param mode 0 进入汉字模式�?1 �?出汉字模�?
//	 *      描述：设置汉字模式有无效
//	 */
//	public static byte[] SetReadZKmode(int mode) {
//		byte[] bCmd = new byte[2];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1C;
//		if (mode == 1)
//			bCmd[iIndex++] = 0x2E;
//		else
//			bCmd[iIndex++] = 0x26;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.20 设置水平制表位置
//	 * @param bHTseat 水平制表的位�?,从小到大,单位�?个ASCII字符,不能�?0
//	 * @param iLength 水平制表的位置数据的个数
//	 */
//	public static byte[] SetHTseat(byte[] bHTseat, int iLength) {
//		byte[] bCmd = new byte[35];
//		int iIndex = 0;
//		int x, length;
//		if (iLength > 32)
//			length = 32;
//		else
//			length = iLength;
//
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x44;
//		for (x = 0; x < length; x++) {
//			bCmd[iIndex++] = bHTseat[x];
//		}
//		bCmd[iIndex++] = 0x00;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.21 设置区域国家和代码页
//	 * @param country  区域国家 0   美国   1	法国      2	德国           3  英国    4  丹麦 I
//							   5   瑞典   6	意大�?  7	西班�? I  8  日本    9  挪威  10 丹麦 II
//	 * @param CPnumber 代码�?             0  PC437[美国欧洲标准]     1 	 PC737    2	PC775 
//	 *                 3   PC850   4	 PC852   5	PC855     6	 PC857    7	PC858   8  PC860   9  PC862
//	 *                 10  PC863  11	 PC864  12	PC865    13	 PC866   14	PC1251 15 PC1252  16  PC1253
//	 *                 17  PC1254 18	 PC1255 19	PC1256   20	 PC1257  21	PC928  22 Hebrew old
//	 *                 23  IINTEL CHAR      18	Katakana 25    特殊符号00-1F  26	SPACE PAGE
//	 */
//	public static byte[] SetCodepage(int country, int CPnumber) {
//		byte[] bCmd = new byte[6];
//		int iIndex = 0;
//
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x52;
//		if (country < 11)
//			bCmd[iIndex++] = (byte) country;
//		else
//			bCmd[iIndex++] = 0x00;
//
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x74;
//		if (CPnumber < 27)
//			bCmd[iIndex++] = (byte) CPnumber;
//		else
//			bCmd[iIndex++] = 0x00;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.22	设置NV位图【已通过测试�?
//	 * @param iNums    位图数量(单个文件�?�?64K，所有文件最�?192K)
//	 * @param strPath  图像文件路径（若只有文件名则使用当前路径�?
//	 * 				      若指定全路径则使用指定的路径），以�??;”分隔，个数�?和iNums参数�?�?
//	 *      描述：设置NV bmp位图
//	 */
//	public static byte[] SetNvbmp(int iNums, String strPath) {
//		QRCodeInfo qrCodeInfo = new QRCodeInfo();
//		byte[] b_send = qrCodeInfo.SetNvbmp(iNums, strPath);
//		if(b_send.length != 0)
//			return b_send;
//		else
//			return null;
//	}
//	
//	/**
//	 * 3.23 打印自检�?
//	 */
//	public static byte[] PrintSelfcheck(){
//		byte[] bCmd = new byte[7];
//		int iIndex=0;  
//		bCmd[iIndex++]=0x1D;
//		bCmd[iIndex++]=0x28;   
//		bCmd[iIndex++]=0x41; 	
//		bCmd[iIndex++]=0x02; 	
//		bCmd[iIndex++]=0x00; 	
//		bCmd[iIndex++]=0x00; 	
//		bCmd[iIndex++]=0x02; 		
//		return bCmd;
//	}
//	
//	/**
//	 * 3.24 打印走纸  
//	 * @param iLine 走纸行数 
//	 * 描述：走纸，单位字符�?
//	 */
//	public static byte[] PrintFeedline(int iLine) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++]= 0x1B;
//		bCmd[iIndex++]= 0x64;
//		bCmd[iIndex++]= (byte) iLine;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.25 打印字符�?
//	 * @param strData 打印的字符串内容
//	 * @param iImme   是否加换行指�?0x0a�? 0 加换行指令，1 不加换行指令
//	 * @throws UnsupportedEncodingException
//	 *     描述: 打印字符串，字符集为GB2312 入口参数：strData 打印的字符串内容，出口参�?:byte[]数组
//	 */
//	public static byte[] PrintString(String strData ,int iImme) 
//			throws UnsupportedEncodingException {
//		// 字符串转换为byte[]数组
//		byte[] strAarry = strData.getBytes("GB2312");
//		int iLen = strAarry.length;
//		byte[] bCmd = new byte[iLen + 1];
//		System.arraycopy(strAarry, 0, bCmd, 0, strAarry.length-1);
//		if (iImme != 1)
//			bCmd[iLen++] = 0x0A;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.26 打印并换�?
//	 * 描述：打印内容并换行，无打印内容的时候走1空白�?
//	 */
//	public static byte[] PrintChargeRow(){
//		byte[] bCmd = new byte[2];
//		int iIndex = 0;		 
//		bCmd[iIndex++] = 0x0A;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.27 打印细走�?
//	 * @param Lnumber  范围 0-250
//	 */
//	public static byte[] PrintFeedDot(int Lnumber) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x4A;
//		if (Lnumber > 250)
//			bCmd[iIndex++] = (byte) 250;
//		else
//			bCmd[iIndex++] = (byte) Lnumber;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.28 执行到下�?个水平制表位�?
//	 *     描述：执行到下一个水平制表位�?
//	 */
//	public static byte[] PrintNextHT(){
//		byte[] bCmd = new byte[1];
//		int iIndex=0;		 
//		bCmd[iIndex++]=0x09;
//		return bCmd;
//	}
//	
//	/**
//	 * 3.29 打印切纸
//	 * @param iMode 0 全切�?1半切
//	 */
//	public static byte[] PrintCutpaper(int iMode) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		if (iMode != 1) {
//			bCmd[iIndex++]= 0x1B;
//			bCmd[iIndex++]= 0x69;
//		} else {
//			bCmd[iIndex++]= 0x1B;
//			bCmd[iIndex++]= 0x6D;
//		}
//		bCmd[iIndex++]= (byte) iMode;
//		return bCmd;
//	}
//	
//	/*
//	 * 3.30 �?测黑标[之前有使用过]
//	 *     描述：黑标模式下�?测黑标，停止在黑标位�?
//	 */
//	public static byte[] PrintMarkposition(){
//		byte[] bCmd = new byte[1];
//		int iIndex=0;	 
//		bCmd[iIndex++]=0x0C; 
//		return bCmd;
//	}
//	
//	/*
//	 * 3.31 �?测黑标进纸到打印位置
//	 *     描述：黑标模式下�?测黑标并进纸到打印位置（偏移量打印影响走纸距离）
//	 */
//	public static byte[] PrintMarkpositionprint(){ 
//		byte[] bCmd = new byte[2];
//		int iIndex=0;	 
//		bCmd[iIndex++]=0x1B;
//		bCmd[iIndex++]=0x0C; 
//		return bCmd;
//	}
//	
//	/**
//	 * 3.32 �?测黑标进纸到切纸位置
//	 *     描述：黑标模式下�?测黑标并进纸到切纸位置（偏移量切纸影响走纸距离）
//	 */
//	public static byte[] PrintMarkpositioncut(){
//		byte[] bCmd = new byte[2];
//		int iIndex=0;	 
//		bCmd[iIndex++]=0x1D;
//		bCmd[iIndex++]=0x0C; 
//		return bCmd;
//	}
//	
//	/**
//	 * 3.33 打印黑标切纸
//	 * @param iMode  0 �?测黑标全切，1 不检测黑标半�?
//	 */
//	public static byte[] PrintMarkcutpaper(int iMode) {
//		byte[] bCmd = new byte[4];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1D;
//		bCmd[iIndex++] = 0x56;
//		if (iMode == 0) {
//			bCmd[iIndex++] = 0x42;
//			bCmd[iIndex++] = 0x0;
//		} else {
//			bCmd[iIndex++] = 0x1;
//		}
//		return bCmd;
//	}
//	
//	/**
//	 * 3.34 打印QR�? 【利用jar包进行代码整理为新调用接口�??
//	 * @param strData  内容
//	 * @param iLmargin 左边距，取�??0-27 单位mm
//	 * @param iMside   单位长度，即QR码大小，取�??1-8，（有些打印机型只支�?1-4�?
//	 * @param iRound   环绕模式�?0环绕（混排，有些机型不支持）�?1立即打印（不混排�?
//	 * 注意：必须调用MsPrintSDK.jar包QRCodeInfo类设置相关参数，进行获取返回数组，完成二维码QRCode打印打印
//	 */
//	public static byte[] PrintQrcode(String strData,int iLmargin,int iMside,int iRound){
//		byte[] bCmd = new byte[50];
//		QRCodeInfo codeInfo = new QRCodeInfo();
//		codeInfo.setlMargin(iLmargin);
//		codeInfo.setmSide(iMside);
//		bCmd = codeInfo.GetQRBCode(strData, iRound);
//		if(bCmd.length != 0)
//			return bCmd;
//		else
//			return null;
//	}
//	
//	/*
//	 * 3.35 打印剩下QR 码�??----废弃不用----�?
//	 *     描述：QR混排打印时�?�，打印剩下QR码�??
//	 */
////	public static byte[] PrintRemainQR(){
////		byte[] bCmd = new byte[2];
////		int iIndex=0;		 
////		bCmd[iIndex++]=0x13;
////		bCmd[iIndex++]=0x0C;
////		return bCmd;
////	}
//	
//	/**
//	 * 3.36 打印PDF417�? 
//	 * @param iDotwidth    宽度，取�?0-255
//	 * @param iDotheight   高度，取�?0-255
//	 * @param iDatarows    行数
//	 * @param iDatacolumns 列数
//	 * @param strData      条码内容，若使用字符串模式，应包含结尾符�?
//	 *                     若使用字节流模式，长度应补齐为字节单位，未使用的位补�?0 
//	 *                     1d 6b 4C 0A 30 31 32 33 34 35 36 37 38 39 00
//	 */
//	public static byte[] PrintPdf417(int iDotwidth, int iDotheight,
//			int iDatarows, int iDatacolumns, String strData) {
//		int width1, height1, length1; // 条码的宽度�?�高度�?�长�?
//		if ((iDotwidth < 2) || (iDotwidth > 6)) {
//			width1 = 2;
//		} else {
//			width1 = iDotwidth; // 宽度
//		}
//		height1 = iDotheight;   // 高度
//		length1 = strData.length();// strData长度
//		byte[] bCmd = new byte[128];
//		int iIndex;
//		iIndex = 0;
//		bCmd[iIndex++] = 0x1d;
//		bCmd[iIndex++] = 0x77;
//		bCmd[iIndex++] = (byte) width1;
//		bCmd[iIndex++] = 0x1d;
//		bCmd[iIndex++] = 0x68;
//		bCmd[iIndex++] = (byte) height1;// '1');
//
//		iIndex = 0;
//		bCmd[iIndex++] = 0x1d;
//		bCmd[iIndex++] = 0x6b;
//		bCmd[iIndex++] = 0x4c;
//		bCmd[iIndex++] = (byte) iDatarows;
//		bCmd[iIndex++] = (byte) iDatacolumns;
//		bCmd[iIndex++] = (byte) length1;
//		
//		byte[] str = new byte[length1];
//		str = strData.getBytes();
//		for (int i = 0; i < length1; i++) {
//			bCmd[iIndex++] = str[i];
//		}
//		return bCmd;
//	}
//
//	/**
//	 * 3.37 打印�?维条�?
//	 * @param iWidth    条码宽度，取�?2-6 单位0.125mm
//	 * @param iHeight   条码高度，取�?1-255 单位0.125mm
//	 * @param iHrisize  条码显示字符字型0 12*24 1 9*17
//	 * @param iHriseat  条码显示字符位置0 无�??1 上�?? 2 下�??3 上下
//	 * @param iCodetype 条码的类型（UPC-A 0,UPC-E 1,EAN13 2,EAN8 3, CODE39 4,
//	 *                            ITF 5,CODABAR 6,Standard EAN13 7, 
//	 *                            Standard EAN8 8,CODE93 9,CODE128 10)
//	 * @param strData  条码内容
//	 */
//	public static byte[] Print1Dbar(int iWidth, int iHeight, int iHrisize,
//			int iHriseat, int iCodetype, String strData) {
//		byte[] bCmd = new byte[64];
//		int iIndex = 0;
//		int length = 0;
//		int width = iWidth;
//		int height = iHeight;
//		int codetype = iCodetype; // 条码类型
//		if ((width < 2) || (width > 6))
//			width = 2;
//		if ((height < 24) || (height > 250))
//			height = 24;
//		if (codetype > 10)
//			codetype = 10;
//		bCmd[iIndex++] = 0x1d;
//		bCmd[iIndex++] = 0x77;
//		bCmd[iIndex++] = (byte) width;
//		bCmd[iIndex++] = 0x1d;
//		bCmd[iIndex++] = 0x68;
//		bCmd[iIndex++] = (byte) height;
//
//		bCmd[iIndex++] = 0x1d;
//		bCmd[iIndex++] = 0x66;
//		if (iHrisize > 1)
//			bCmd[iIndex++] = 0;
//		else
//			bCmd[iIndex++] = (byte) iHrisize;
//		bCmd[iIndex++] = 0x1d;
//		bCmd[iIndex++] = 0x48;
//		if (iHriseat > 3)
//			bCmd[iIndex++] = 0;
//		else
//			bCmd[iIndex++] = (byte) iHriseat;
//		bCmd[iIndex++] = 0x1d;
//
//		bCmd[iIndex++] = 0x6b;
//
//		byte[] strAarry = strData.getBytes();
//		length = strAarry.length;
//
//		int i = 0;
//		switch (codetype) {
//		case 0:// UPC-A
//			bCmd[iIndex++] = 0x00;
//			if (length < 11)
//				return bCmd;
//			for (i = 0; i < 11; i++) {
//				if ((strAarry[i] < 48) || (strAarry[i] > 57))
//					return bCmd;
//			}
//			for (i = 0; i < 11; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			bCmd[iIndex++] = 0x00;
//			break;
//		case 1:// UPC-E
//			bCmd[iIndex++] = 0x01;
//			if (length < 11)
//				return bCmd;
//			if (strAarry[0] != 0x30)
//				return bCmd;
//			for (i = 1; i < 11; i++) {
//				if ((strAarry[i] < 48) || (strAarry[i] > 57))
//					return bCmd;
//			}
//			for (i = 0; i < 11; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			bCmd[iIndex++] = 0x00;
//			break;
//		case 2:// EAN13
//			bCmd[iIndex++] = 0x02;
//			if (length < 12)
//				return bCmd;
//			for (i = 0; i < 12; i++) {
//				if ((strAarry[i] < 48) || (strAarry[i] > 57))
//					return bCmd;
//			}
//			for (i = 0; i < 12; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			bCmd[iIndex++] = 0x00;
//			break;
//		case 3:// EAN8 (7 8)
//			bCmd[iIndex++] = 0x03;
//			if (length < 7)
//				return bCmd;
//			for (i = 0; i < 7; i++) {
//				if ((strAarry[i] < 48) || (strAarry[i] > 57))
//					return bCmd;
//			}
//			for (i = 0; i < 7; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			bCmd[iIndex++] = 0x00;
//			break;
//		case 4:// CODE39
//			bCmd[iIndex++] = 0x04;
//			for (i = 0; i < length; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			bCmd[iIndex++] = 0x00;
//			break;
//		case 5:// ITF
//			bCmd[iIndex++] = 0x05;
//			for (i = 0; i < length; i++) {
//				if ((strAarry[i] < 48) || (strAarry[i] > 57))
//					return bCmd;
//			}
//			if (length % 2 == 1)
//				length = length - 1;
//			for (i = 0; i < length; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			bCmd[iIndex++] = 0x00;
//			break;
//		case 6:// CODABAR
//			bCmd[iIndex++] = 0x06;
//			for (i = 0; i < length; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			bCmd[iIndex++] = 0x00;
//			break;
//		case 7:// Standard EAN13 (12 13)
//			bCmd[iIndex++] = 0x07;
//			if (length < 12)
//				return bCmd;
//			for (i = 0; i < 12; i++) {
//				if ((strAarry[i] < 48) || (strAarry[i] > 57))
//					return bCmd;
//			}
//			for (i = 0; i < 12; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			bCmd[iIndex++] = 0x00;
//			break;
//		case 8:// Standard EAN8 (7 8)
//			bCmd[iIndex++] = 0x08;
//			if (length < 7)
//				return bCmd;
//			for (i = 0; i < 7; i++) {
//				if ((strAarry[i] < 48) || (strAarry[i] > 57))
//					return bCmd;
//			}
//			for (i = 0; i < 7; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			bCmd[iIndex++] = 0x00;
//			break;
//		case 9:// CODE93
//			bCmd[iIndex++] = 72;
//			bCmd[iIndex++] = (byte) length;
//			for (i = 0; i < length; i++) {
//				bCmd[iIndex++] = strAarry[i];
//			}
//			break;
//		case 10:// CODE128
//			bCmd[iIndex++] = 73;
//			for (i = 0; i < length; i++) {
//				if ((strAarry[i] < 48) || (strAarry[i] > 57))
//					break;
//			}
//			if (i == length) {
//				if (length % 2 == 1)
//					bCmd[iIndex++] = (byte) (length / 2 + 1 + 4);
//				else
//					bCmd[iIndex++] = (byte) (length / 2 + 2);
//				bCmd[iIndex++] = 123;
//				bCmd[iIndex++] = 67;
//				for (i = 0; i < length; i++) {
//					if ((i + 1) >= length) {
//						bCmd[iIndex++] = 123;
//						bCmd[iIndex++] = 66;
//						bCmd[iIndex++] = strAarry[i];
//						i++;
//					} else {
//						bCmd[iIndex++] = (byte) ((strAarry[i] - 0x30) * 10 + (strAarry[i + 1] - 0x30));
//						i++;
//					}
//				}
//			} else {
//				bCmd[iIndex++] = (byte) (length + 2);
//				bCmd[iIndex++] = 123;
//				bCmd[iIndex++] = 66;
//				for (i = 0; i < length; i++) {
//					bCmd[iIndex++] = strAarry[i];
//				}
//			}
////			for (i = 0; i < length; i++) {
////				bCmd[iIndex++] = strAarry[i];
////			}
//			break;
//		default:
//			break;
//		}
//		return bCmd;
//	}
//	
//	/*
//	 *  3.38 打印磁盘BMP文件�?----废弃不用----�?
//	 *  描述：打印磁盘BMP文件，仅支持单色BMP文件
//	 */
//	
////	public static byte[] PrintDiskbmpfile(String strPath){
////		return null;
////	}
//	
//	/**
//	 * 3.39 打印NV BMP 文件  【可用，已�?�过测试�?
//	 * @param iNvindex  NV位图索引
//	 * @param iMode     48 普�?��??49 倍宽�?50 倍高�?51 倍宽倍高(4倍大�?)
//	 *  描述：打印NV BMP文件，仅支持单色BMP文件
//	 */
//	public static byte[] PrintNvbmp(int iNvindex, int iMode) {
//		byte[] bCmd = new byte[4];
//		int iIndex = 0;
//		int iValue = iMode;
//		if (iMode < 48)
//			iValue = 48;
//		if (iMode > 51)
//			iValue = 51;
//
//		bCmd[iIndex++] = 0x1C;
//		bCmd[iIndex++] = 0x70;
//		bCmd[iIndex++] = (byte) iNvindex;
//		bCmd[iIndex++] = (byte) iValue;
//		return bCmd;
//	}
//	
//	/**
//	 * 获取NVbmp图片byte[]数据 【可用，已�?�过测试�?
//	 * @param strPath
//	 * @return byte[]
//	 */
//	public static byte[] GetBMPImageByte(String strPath) {
//		QRCodeInfo codeInfo = new QRCodeInfo();
//		byte[] bQRcode = codeInfo.getBMPImageByte(strPath);
//		int cnt = bQRcode.length;
//		if (cnt == 0) {
//			return null;
//		}
//		byte[] bCmd = new byte[cnt];
//		for (int iIndex = 0; iIndex < cnt; iIndex++)
//			bCmd[iIndex] = bQRcode[iIndex];
//		return bCmd;
//	}
//	
//	/*
//	 * 3.40 转发指令
//	 * @param bCmd     指令
//	 * @param iLength  指令长度
//	 *  描述：将指令原样转给打印�?
//	 */
//	public static byte[] PrintTransmit(String strCmd,int iLength){
//		iLength = strCmd.length();
//		byte[] bCmd = new byte[iLength];
//		bCmd = strCmd.getBytes();
//		return bCmd;
//	}
//	
//	/*
//	 * 3.41 转发指令[接收返回值]
//	 * @param strCmd  指令
//	 * @param iLength 指令长度
//	 * @param bRecv   接收数据的地�?
//	 * @param iRelen  接收数据的长�?
//	 *        描述：将指令原样转给打印机，并接收返回�??
//	 */
//	public static byte[] GetTransmit(String bCmd,int iLength,String bRecv,int iRelen){
//		int iIndex = 0;
//		iLength = bCmd.length();
//		iRelen = iLength; // 赋�??
//		byte[] b_Send = new byte[iLength];// 发�?�数�?
//		byte[] b_Recv = new byte[iRelen]; // 接收数组
//		b_Send = bCmd.getBytes();
//		b_Recv = bRecv.getBytes();
//		if(b_Send != null){
//			for (int i = 0; i < iLength; i++) {
//				b_Recv[iIndex++] = b_Send[i];
//			}
//		}
//		return b_Recv;
//	}	
//	
//	/**
//	 * 3.42 获取打印机状�?
//	 * @return     0 打印机正�? �?1 打印机未连接或未上电�?2 打印机和调用库不匹配 
//	 *             3 打印头打�? �?4 切刀未复�? �?5 打印头过�? �?6 黑标错误 �?7 纸尽 �?8 纸将�?
//	 */
//	public static byte[] GetStatus(){
//		try{ 
//			byte[] b_send = new byte[12];
//			int iIndex=0;
//			// 01 判断打印机是否连接正�?
//			b_send[iIndex++] = 0x10;
//			b_send[iIndex++] = 0x04;
//			b_send[iIndex++] = 0x01;
//			// 02 判断打印�? 机头打开 
//			b_send[iIndex++] = 0x10;
//			b_send[iIndex++] = 0x04;
//			b_send[iIndex++] = 0x02;
//			// 03 判断打印�? 切刀   打印头温�?
//			b_send[iIndex++] = 0x10;
//			b_send[iIndex++] = 0x04;
//			b_send[iIndex++] = 0x03;
//			// 04 判断打印机纸�?  纸将�?
//			b_send[iIndex++] = 0x10;
//			b_send[iIndex++] = 0x04;
//			b_send[iIndex++] = 0x04;
//			return b_send;
//	 	}catch(Exception e){
//	 		e.printStackTrace();
//	 	}
//		return null;
//	}
//	/**
//	 * �?测打印机状�??
//	 * @param  b_recv
//	 * @return int
//	 */
//	public static int CheckStatus(byte[] b_recv) {
//		// 01 判断打印机是否连接正�?
//		if ((b_recv[0] & 0x16) != 0x16)
//			return 2;
//		// 02 判断打印�? 机头打开
//		if ((b_recv[1] & 0x04) == 0x04)
//			return 3;
//		// 03 判断打印�? 切刀 打印头温�?
//		if ((b_recv[2] & 0x08) == 0x08)
//			return 4;
//		if ((b_recv[2] & 0x40) == 0x40)
//			return 5;
//		if ((b_recv[2] & 0x20) == 0x20)
//			return 6;
//		// 04 判断打印机纸�? 纸将�?
//		if ((b_recv[3] & 0x60) == 0x60)
//			return 7;
//		if ((b_recv[3] & 0x0C) == 0x0C)
//			return 8;
//		else
//			return 0;
//	}
//	
//	/**
//	 * 3.43 获取打印机特殊功能状�?
//	 * 【描述：获取打印机特殊功能状态，仅�?�用于D347部分机型�?
//	 * 返回值： 0 打印机正常�??  1 打印机未连接或未上电�?        2 打印机和调用库不匹配
//               3 当前使用打印机无特殊功能�? 4 容纸器错误�??5 堵纸
//               6 卡纸�?            7 拽纸�?           8 出纸传感器有�?
//	 * @return int
//	 */
//	public static byte[] GetStatusspecial() {
//		try {
//			byte[] b_recv = new byte[6];
//			int iIndex = 0;
//			// 01 判断打印机是否链接正�?
//			b_recv[iIndex++] = 0x10;
//			b_recv[iIndex++] = 0x04;
//			b_recv[iIndex++] = 0x01;
//			//05 判断打印机特殊功�?  容纸器错�? 堵纸  卡纸  拽纸  出纸传感器有�?   
//			b_recv[iIndex++] = 0x10;
//			b_recv[iIndex++] = 0x04;
//			b_recv[iIndex++] = 0x05;
//			return b_recv;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static int CheckStatusspecial(byte[] b_recv) {
////		if ((b_recv[0] & 0x16) != 0x16) 
////			return 2;
////		if ((b_recv[1] & 0x80) == 0x80) 
////			return 4;
////		if ((b_recv[1] & 0x01) == 0x01) 
////			return 5;
////		if ((b_recv[1] & 0x08) == 0x08) 
////			return 6;
////		if ((b_recv[1] & 0x02) == 0x02)
////			return 7;
////		if ((b_recv[1] & 0x04) == 0x04)
////			return 8;
////		else 
//			return 0;
//	}
//	
//	/*
//	 * 3.44 获取打印机信�?
//	 * @param iFstype  信息类型�? 1 打印头型号ID�?2 类型ID�?       3 软件版本�?
//	 *                           4 生产厂商信息�?5 打印机型号�??6 支持的中文编码格�?
//	 * @param bFiddata 返回信息
//	 * @param iFidlen  返回信息长度
//	 */
//	public static byte[] GetProductinformation(int iFstype, byte[] bFiddata,
//			int iFidlen) {
//		try {
//			byte[] b_send = new byte[3];
//			byte[] b_recv = new byte[128];
//			int iIndex = 0;
//			b_send[iIndex++] = 0x1D;
//			b_send[iIndex++] = 0x49;
//			b_send[iIndex++] = (byte) iFstype;
//			for (iIndex = 0; iIndex < b_send.length; iIndex++)
//				b_recv[iIndex++] = b_send[iIndex];
//			
//			for (iIndex = 0; iIndex < 32; iIndex++)
//				bFiddata[iIndex] = b_recv[iIndex];
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return bFiddata;
//	}
//	
//	/*
//	 * 3.45 获取�?发包信息
//	 * @param bInfodata 返回信息
//	 *        描述：获取开发包版本等信�?
//	 */
//	public static byte[] GetSDKinformation(String bInfodata){
//		byte[] b_infodata = bInfodata.getBytes();
//		byte[] b_info = new byte[10];
//		int len = b_info.length;
//		try{
//	        System.arraycopy(b_infodata, 0, b_info, 0, len);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return b_info;
//	}
//
//	/**
//	 * 3.46 设置右边�?
//	 * @param iRightspace
//	 *            [范围] 0 �? n �? 255 
//	 *            [描述] 设置字符右侧的间距为n 个水平点距�?? 在�?�宽模式下，字符右侧间距是正�?
//	 *            值的两�?�；当字符被放大时，字符右侧间距被放大同样的倍数�?
//	 */
//	public static byte[] SetRightmargin(int iRightspace) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1B;
//		bCmd[iIndex++] = 0x20;
//		bCmd[iIndex++] = (byte) iRightspace;
//		return bCmd;
//	}
//	/**
//	 * 3.47 设置条码对齐方式
//	 * @param iAlign 0 左对�? �?1 居中对齐�?2 右对�? 
//	 * 	   描述：打印条形码时，根据iAlign可�?��?�进行条码对�?
//	 */
//	public static byte[] Set1DBarCodeAlign(int iAlign) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x1D;
//		bCmd[iIndex++] = 0x50;
//		bCmd[iIndex++] = (byte) iAlign;
//		return bCmd;
//	}
//	/* END  ================常用打印接口说明============== */
//	
//	// *************************************************************
//	
//	/* START================定制类打印接口说�?(EP800)============== 
//	 * --------【说明：无@return 返回统一返回 byte[];其他返回类型，准参�?�@return实际类型�?
//	 */
//	/**
//	 * 4.1 设置指令模式
//	 *     描述：设置打印机指令模式
//	 * @param iMode 2 EPIC模式�?3 EPOS模式
//	 */
//	public static byte[] SetCommandmode(int iMode){
//		return SetCommmandmode(iMode);
//	}
//	
//	/*
//	 * 4.2 设置旋转打印模式 �?----未�?�过测试----�? 
//	 *     描述：设置进入旋转打印模�?
//	 */
//	public static int SetRotation_Intomode(){
//		int iRet = 1;
//		byte[] bCmd = new byte[2];
//		int iIndex=0;
//		bCmd[iIndex++]=0x13;
//		bCmd[iIndex++]=0x44;
//		if(bCmd != null && bCmd.length > 1)
//			iRet = 1;
//		else
//			iRet = 0;
//		return iRet;
//	}
//	
//	/*
//	 * 4.3 打印旋转模式数据
//	 *    描述：打印进入旋转模式后保存的数据并�?出旋转模式并默认EPOS指令模式
//	 */
//	public static byte[] PrintRotation_Data(){
//		byte[] bCmd = new byte[1];
//		int iIndex=0;
//		bCmd[iIndex++]=0x0b;
//		return bCmd;
//	}
//	
//	/**
//	 * 4.4 发�?�旋转模式数�? 
//	 * @param strData 文本数据
//	 * @param iImme   换行�?0 不换行�??1 换行
//	 *     描述：旋转模式下传文本数�?
//	 */
//	public static byte[] PrintRotation_Sendtext(String strData, int iImme) {
//		byte[] bCmd = new byte[1];
//		int iLen = strData.length();
//		byte[] strString = new byte[iLen + 1];
//		// memcpy(strString,strData,iLen);
//		System.arraycopy(strString, 0, bCmd, 0, iLen);
//		if (iImme != 1)
//			strString[iLen++] = 0x0A;
//		// 获取变量，置空操�?
//		{
//			strData = System.getenv(new String(strString));
//			strData = "";
//			strString = null;
//		}
//		return bCmd;
//	}
//	
//	/**
//	 * 4.5 发�?�旋转模式条�?
//	 * @param leftspace  条码左边距，单位mm
//	 * @param iWidth     条码宽度，取�?2-6 单位0.125mm
//	 * @param iHeight    条码高度，取�?1-255 单位0.125mm
//	 * @param iCodetype  条码的类�? �?* UPC-A 0,* UPC-E 1,* EAN13 2,* EAN8 3,
//	 *                                 CODE39 4,* ITF 5,* CODABAR 6,* Standard EAN13 7,
//	 *                                 Standard EAN8 8,* CODE93 9,* CODE128 10)
//	 * @param strData    条码内容
//	 * 	            描述：旋转模式下传条码数�?
//	 */
//	public static byte[] PrintRotation_Sendcode(int leftspace, int iWidth,
//			int iHeight, int iCodetype, byte[] strData) {
//		try {
//			byte[] bCmd = new byte[64];
//			int iIndex = 0;
//			int length = 0;
//			int Codetype = 2;
//			bCmd[iIndex++] = 0x1B;
//			bCmd[iIndex++] = 0x62;
//
//			if (leftspace < 72)
//				bCmd[iIndex++] = (byte) leftspace;
//			else
//				bCmd[iIndex++] = 0;
//
//			if ((iWidth >= 2) && (iWidth <= 6))
//				bCmd[iIndex++] = (byte) iWidth;
//			else
//				bCmd[iIndex++] = 2;
//
//			if ((iHeight >= 1) && (iHeight <= 10))
//				bCmd[iIndex++] = (byte) iHeight;
//			else
//				bCmd[iIndex++] = 1;
//
//			if ((iCodetype <= 8) || (iCodetype == 12))
//				Codetype = iCodetype;
//			else
//				Codetype = 2;
//
//			length = strData.length;
//			if (length < 2)
//				return bCmd;
//			int i = 0;
//			switch (Codetype) {
//			case 0:// ITF
//				bCmd[iIndex++] = 0x00;
//				for (i = 0; i < length; i++) {
//					if ((strData[i] < 48) || (strData[i] > 57))
//						return bCmd;
//				}
//				if (length % 2 == 1)
//					length = length - 1;
//				for (i = 0; i < length; i++) {
//					bCmd[iIndex++] = strData[i];
//				}
//				bCmd[iIndex++] = 0x03;
//				break;
//			case 1:// CODE39
//				bCmd[iIndex++] = 0x01;
//				for (i = 0; i < length; i++) {
//					bCmd[iIndex++] = strData[i];
//				}
//				bCmd[iIndex++] = 0x03;
//				break;
//
//			case 2:// CODE128
//				bCmd[iIndex++] = 0x02;
//				for (i = 0; i < length; i++) {
//					if ((strData[i] < 48) || (strData[i] > 57))
//						break;
//				}
//				if (i == length) {
//					// if(length%2) bCmd[iIndex++] = length/2 + 1 + 2;
//					// else bCmd[iIndex++] = length/2 + 1;
//					bCmd[iIndex++] = (byte) 137;
//					for (i = 0; i < length; i++) {
//						if ((i + 1) >= length) {
//							bCmd[iIndex++] = (byte) 136;
//							bCmd[iIndex++] = strData[i];
//							i++;
//						} else {
//							bCmd[iIndex++] = (byte) ((strData[i] - 0x30) * 10 + (strData[i + 1] - 0x30));
//							i++;
//						}
//					}
//				} else {
//					// bCmd[iIndex++]=length+1;
//					bCmd[iIndex++] = (byte) 136;
//					for (i = 0; i < length; i++) {
//						bCmd[iIndex++] = strData[i];
//					}
//				}
//				bCmd[iIndex++] = 0x03;
//				break;
//
//			case 3:// UPC-A
//				bCmd[iIndex++] = 0x03;
//				if (length < 11)
//					return bCmd;
//				for (i = 0; i < 11; i++) {
//					if ((strData[i] < 48) || (strData[i] > 57))
//						return bCmd;
//				}
//				for (i = 0; i < 11; i++) {
//					bCmd[iIndex++] = strData[i];
//				}
//				bCmd[iIndex++] = 0x03;
//				break;
//
//			case 4:// EAN13
//				bCmd[iIndex++] = 0x04;
//				if (length < 12)
//					return bCmd;
//				for (i = 0; i < 12; i++) {
//					if ((strData[i] < 48) || (strData[i] > 57))
//						return bCmd;
//				}
//				for (i = 0; i < 12; i++) {
//					bCmd[iIndex++] = strData[i];
//				}
//				bCmd[iIndex++] = 0x03;
//				break;
//
//			case 5:// UPC-E
//				bCmd[iIndex++] = 0x05;
//				if (length < 11)
//					return bCmd;
//				if (strData[0] != 0x30)
//					return bCmd;
//				for (i = 1; i < 11; i++) {
//					if ((strData[i] < 48) || (strData[i] > 57))
//						return bCmd;
//				}
//				for (i = 0; i < 11; i++) {
//					bCmd[iIndex++] = strData[i];
//				}
//				bCmd[iIndex++] = 0x03;
//				break;
//
//			case 6:// EAN8 (7 8)
//				bCmd[iIndex++] = 0x06;
//				if (length < 7)
//					return bCmd;
//				for (i = 0; i < 7; i++) {
//					if ((strData[i] < 48) || (strData[i] > 57))
//						return bCmd;
//				}
//				for (i = 0; i < 7; i++) {
//					bCmd[iIndex++] = strData[i];
//				}
//				bCmd[iIndex++] = 0x03;
//				break;
//
//			case 7:// CODE93
//				bCmd[iIndex++] = 0x07;
//				for (i = 0; i < length; i++) {
//					bCmd[iIndex++] = strData[i];
//				}
//				bCmd[iIndex++] = 0x03;
//				break;
//
//			case 8:// CODABAR
//				bCmd[iIndex++] = 0x08;
//				for (i = 0; i < length; i++) {
//					bCmd[iIndex++] = strData[i];
//				}
//				bCmd[iIndex++] = 0x03;
//				break;
//
//			case 12:// DrawCODEEAN14
//				bCmd[iIndex++] = 0x0C;
//				if (length % 2 == 1)
//					length = length - 1;
//				if (length > 14)
//					return bCmd;
//				for (i = 0; i < length; i++) {
//					if ((strData[i] < 48) || (strData[i] > 57))
//						return bCmd;
//				}
//				for (i = 0; i < length; i++) {
//					bCmd[iIndex++] = strData[i];
//				}
//				bCmd[iIndex++] = 0x03;
//				bCmd[iIndex++] = 0x00;
//				break;
//			default:
//				break;
//			}
//			return bCmd;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	/**
//	 * 4.6 发�?�旋转模式换�?
//	 *     描述：旋转模式下换行
//	 */
//	public static byte[] PrintRotation_Changeline(){
//		byte[] bCmd = new byte[1];
//		int iIndex = 0;
//		bCmd[iIndex++]=0x0a;
//		return bCmd;
//	}
//	
//	/**
//	 * 4.7 发�?�旋转模式左边距
//	 * @param iLeftspace 左边距，单位mm
//	 *     描述：设置旋转模式下左边�?
//	 */
//	public static byte[] SetRotation_Leftspace(int iLeftspace) {
//		byte[] bCmd = new byte[3];
//		int iIndex = 0;
//		bCmd[iIndex++] = 0x13;
//		bCmd[iIndex++] = 0x76;
//		if (iLeftspace < 72)
//			bCmd[iIndex++] = (byte) iLeftspace;
//		else
//			bCmd[iIndex++] = 0x00;
//		return bCmd;
//	}
//	
//	/**
//	 * 4.8 设置打印机ID或名�?
//	 * @param strIDorNAME  打印机ID或名�?
//	 * @return int
//	 */
//	public static byte[] SetPrintIDorName(byte[] strIDorNAME) {
//		byte[] b_send = new byte[64];
//		int iIndex;
//		int length, i;
//		length = strIDorNAME.length;
//		if ((length == 0) || (length > 30))
//			return b_send;
//		iIndex = 0;
//		b_send[iIndex++] = 0x13;
//		b_send[iIndex++] = 0x75;
//		b_send[iIndex++] = (byte) length;
//		for (i = 0; i < length; i++) {
//			b_send[iIndex++] = strIDorNAME[i];
//		}
//		return b_send;
//	}
//	
//	/**
//	 * 4.9 获取打印机ID或名�? 
//	 * @param strIDorNAME  打印机ID或名�?
//	 */
//	public static byte[] GetPrintIDorName(byte[] strIDorNAME) {
//		int iIndex = 0;
//		byte[] b_send = new byte[2];
//		byte[] b_recv = new byte[128];
//
//		b_send[iIndex++] = 0x13;
//		b_send[iIndex++] = 0x76;
//		System.arraycopy(b_send, iIndex, b_recv, 0, 32);// b_send拷贝到b_recv数组中去
//		for (iIndex = 0; iIndex < 32; iIndex++) {
//			strIDorNAME[iIndex] = b_recv[iIndex];
//		}
//		return strIDorNAME;
//	}
//	
//	/* END================定制类打印接口说�?(EP800)============== */
//	
//	
//	
//	/** ========================Start【HexUtils之前用到的函数�??========================= */
//	public static String encodeCN(String data) {
//		byte[] bytes;
//		try {
//			bytes = data.getBytes("gbk");
//			StringBuilder sb = new StringBuilder(bytes.length * 2);
//			for (int i = 0; i < bytes.length; i++) {
//				sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
//				sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
//				// sb.append(" ");
//			}
//			return sb.toString();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//
//	public static String encodeStr(String data) {
//		String result = "";
//		byte[] b;
//		try {
//			b = data.getBytes("gbk");
//			for (int i = 0; i < b.length; i++) {
//				result += Integer.toString((b[i] & 0xff) + 0x100, 16)
//						.substring(1);
//				// result += " ";
//			}
//			return result;
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//
//	public static boolean isCN(String data) {
//		boolean flag = false;
//		String regex = "^[\u4e00-\u9fa5]*$";
////		String regex = "^[�?-龥]*$";
//		if (data.matches(regex)) {
//			flag = true;
//		}
//		return flag;
//	}
//	
//	public static String getHexResult(String targetStr) {
//		StringBuilder hexStr = new StringBuilder();
//		int len = targetStr.length();
//		if (len > 0) {
//			for (int i = 0; i < len; i++) {
//				char tempStr = targetStr.charAt(i);
//				String data = String.valueOf(tempStr);
//				if (isCN(data)) {
//					hexStr.append(encodeCN(data));
//				} else {
//					hexStr.append(encodeStr(data));
//				}
//			}
//		}
//		return hexStr.toString();
//	}
//	/*
//	 * 未转换为十六进制字节的字符串
//	 * @param paramString
//	 * @return byte[]
//	 */
//	public static byte[] hexStr2Bytesnoenter(String paramString) {
//		String[] paramStr = paramString.split(" ");
//		byte[] arrayOfByte = new byte[paramStr.length];
//		
//		for (int j = 0; j < paramStr.length; j++) {
//			arrayOfByte[j] = Integer.decode("0x" + paramStr[j]).byteValue();
//		}
//		return arrayOfByte;
//	}
//	
//	// 读数�?
//	private static byte[] PCTOread(byte[] b_send,int iIndex,byte[] b_recv,int iReLen)
//	{	
//		iIndex = 0;
//		System.arraycopy(b_send, 0, b_recv, 0, b_send.length);
//		byte[] bCmd = new byte[iReLen];
//		for(int i = 0; i < iReLen; i++){
//			bCmd[iIndex++] = b_recv[i];
//		}
//		return bCmd; 
//	}
//	
//	// 写数�?
//	private static byte[] PCTOwrite(byte[] bSend, int iLen){
//		int iIndex = 0;
//		int iPrintLen = 0;
//		iPrintLen = bSend.length;
//		byte[] bCmd = new byte[iPrintLen];
//
//		for (int i = 0; i < iPrintLen; i++) {
//			bCmd[iIndex++] = bSend[i];
//		}
//		return bCmd;
//	}
//	
//	/** ==================================End=================================== */
//
//}
