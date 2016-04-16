package com.easivend.app.maintain;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.easivend.dao.vmc_productDAO;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_ClassAdapter;
import com.easivend.model.Tb_vmc_product;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsProSet extends Activity 
{
	private Uri uri=null;
	private String proID=null,imgDir=null;
	private ImageView ivProduct=null;
	private Button btnImg=null,btnaddProSave=null,btnaddProexit=null;
	private EditText edtproductID=null,edtproductName=null,edtmarketPrice=null,edtsalesPrice=null,
			edtshelfLife=null;
	private WebView webproductDesc=null;
	private TextView onloadTime=null;	
	private String[] proclassID = null;// 定义字符串数组，用来存储商品id信息
	private Spinner spinproductclassID=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goodsset);// 设置布局文件
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    	final String date=df.format(new Date());
		ivProduct = (ImageView) findViewById(R.id.ivProduct);
		onloadTime = (TextView) findViewById(R.id.onloadTime);
		edtproductID = (EditText) findViewById(R.id.edtproductID);		
		edtproductName = (EditText) findViewById(R.id.edtproductName);
		edtmarketPrice = (EditText) findViewById(R.id.edtmarketPrice);		
		edtsalesPrice = (EditText) findViewById(R.id.edtsalesPrice);
		edtshelfLife = (EditText) findViewById(R.id.edtshelfLife);		
		webproductDesc = (WebView) findViewById(R.id.webproductDesc);
		this.spinproductclassID = (Spinner) super.findViewById(R.id.spinproductclassID);
		
		showInfo();//显示下拉列表
		//从商品页面中取得锁选中的商品
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		proID=bundle.getString("proID");
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+proID,"log.txt");
		//如果商品ID有存在则刷新页面为修改商品的页面
		if(proID.isEmpty()!=true)
		{
			vmc_productDAO productDAO = new vmc_productDAO(GoodsProSet.this);// 创建InaccountDAO对象
		    // 获取所有收入信息，并存储到List泛型集合中
		    Tb_vmc_product tb_inaccount = productDAO.find(proID);
		    imgDir=tb_inaccount.getAttBatch1().toString();
		    /*为什么图片一定要转化为 Bitmap格式的！！ */
	        Bitmap bitmap = ToolClass.getLoacalBitmap(imgDir); //从本地取图片(在cdcard中获取)  //
	        ivProduct.setImageBitmap(bitmap);// 设置图像的二进制值
	        
		    edtproductID.setText(tb_inaccount.getProductID().toString());
		    edtproductName.setText(tb_inaccount.getProductName().toString());
		    edtmarketPrice.setText(String.valueOf(tb_inaccount.getMarketPrice()));
		    edtsalesPrice.setText(String.valueOf(tb_inaccount.getSalesPrice()));
		    edtshelfLife.setText(String.valueOf(tb_inaccount.getShelfLife()));
		    //得到商品描述
		    WebSettings settings = webproductDesc.getSettings();
		    settings.setSupportZoom(true);
		    settings.setTextSize(WebSettings.TextSize.LARGEST);
		    webproductDesc.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
		    webproductDesc.loadDataWithBaseURL(null,tb_inaccount.getProductDesc().toString(), "text/html; charset=UTF-8","utf-8", null);//这种写法可以正确中文解码
			
		    onloadTime.setText(tb_inaccount.getOnloadTime().toString());
		    //设置下拉框默认值
		    String classID=productDAO.findclass(proID);
		    int position=0;
		    for(int i=0;i<proclassID.length;i++)
		    {
		    	if(classID.equals(proclassID[i]))
		    	{
		    		position=i;
		    		break;
		    	}
		    }
		    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品classID="+classID+",pos="+position,"log.txt");
		    spinproductclassID.setSelection(position);	   
		}
		
		//选择图片
		btnImg = (Button)findViewById(R.id.btnImg);  
		btnImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();  
               /* 开启Pictures画面Type设定为image */  
               intent.setType("image/*");  
               /* 使用Intent.ACTION_GET_CONTENT这个Action */  
               intent.setAction(Intent.ACTION_GET_CONTENT);   
               /* 取得相片后返回本画面 */  
               startActivityForResult(intent, 1);
			}
		}); 
		
		
		//保存
		btnaddProSave = (Button) findViewById(R.id.btnaddProSave);
		btnaddProSave.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) 
		    {
		    	String productID = edtproductID.getText().toString();
		    	String productName = edtproductName.getText().toString();
		    	float marketPrice = Float.parseFloat(edtmarketPrice.getText().toString());
		    	float salesPrice = Float.parseFloat(edtsalesPrice.getText().toString());
		    	int shelfLife= 0;
		    	if(edtshelfLife.getText().toString().isEmpty()!=true)
		    		shelfLife = Integer.parseInt(edtshelfLife.getText().toString());		    	
		    	String productDesc = "";
		    	//商品类别
		    	String strInfo= spinproductclassID.getSelectedItem().toString();
		    	String classID= strInfo.substring(0, strInfo.indexOf('<'));// 从收入信息中截取收入编号
		    	String attBatch1=imgDir;
		    	String attBatch2="";
		    	String attBatch3="";		    	    	
		    	if ((productID.isEmpty()!=true)&&(productName.isEmpty()!=true)
		    			&&(edtmarketPrice.getText().toString().isEmpty()!=true)		    				    			
		    		)
		    	{
		    		try 
		    		{
		    			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+productID+" productName="+productName+" marketPrice="
		    					+marketPrice+" salesPrice="+salesPrice+" shelfLife="+shelfLife+" productDesc="+productDesc+" attBatch1="
		    					+attBatch1+" attBatch2="+attBatch2+" attBatch3="+attBatch3+" classID="+classID,"log.txt");
		    			// 创建InaccountDAO对象
		    			vmc_productDAO productDAO = new vmc_productDAO(GoodsProSet.this);
			            //创建Tb_inaccount对象
		    			Tb_vmc_product tb_vmc_product = new Tb_vmc_product(productID, productName,productDesc,marketPrice,
		    					salesPrice,shelfLife,date,date,attBatch1,attBatch2,attBatch3,0,0);
		    			if(proID.isEmpty()==true)
		    			{
		    				productDAO.add(tb_vmc_product,classID);// 添加商品信息
		    				ToolClass.addOptLog(GoodsProSet.this,0,"添加商品:"+productID+productName);
		    			}
		    			else 
		    			{	
		    				productDAO.update(tb_vmc_product,classID);// 修改商品信息
		    				ToolClass.addOptLog(GoodsProSet.this,1,"修改商品"+productID+productName);
						}
			        	// 弹出信息提示
			            Toast.makeText(GoodsProSet.this, "〖新增商品〗数据添加成功！", Toast.LENGTH_SHORT).show();
			            //退出时，返回intent
			            Intent intent=new Intent();
			            intent.putExtra("back", "ok");
			            setResult(GoodsManager.RESULT_OK,intent);
			            finish();
			            
		    		} catch (Exception e)
					{
						// TODO: handle exception
						Toast.makeText(GoodsProSet.this, "类别商品失败！", Toast.LENGTH_SHORT).show();
					}		    		
		            
		        } 
		        else
		        {
		            Toast.makeText(GoodsProSet.this, "请填写红色部分！", Toast.LENGTH_SHORT).show();
		        }
		    }
		});
		//退出
		btnaddProexit = (Button) findViewById(R.id.btnaddProexit);
		btnaddProexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	//退出时，返回intent
	            Intent intent=new Intent();
	            intent.putExtra("back", "none");
	            setResult(GoodsManager.RESULT_CANCELED,intent);
		        finish();
		    }
		});
		
	}
	
	// 显示商品分类信息
	private void showInfo() 
	{	  
		ArrayAdapter<String> arrayAdapter = null;// 创建ArrayAdapter对象 
		Vmc_ClassAdapter vmc_classAdapter=new Vmc_ClassAdapter();
	    String[] strInfos = vmc_classAdapter.showSpinInfo(GoodsProSet.this);	    
	    // 使用字符串数组初始化ArrayAdapter对象
	    arrayAdapter = new ArrayAdapter<String>(this, R.layout.viewspinner, strInfos);
	    spinproductclassID.setAdapter(arrayAdapter);// 为ListView列表设置数据源
	    proclassID=vmc_classAdapter.getProclassID();
	}
	@Override  
	//选取图片返回值
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{  
	     if (resultCode == RESULT_OK) {  
	         uri = data.getData();  
	         ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<uri="+ uri.toString(),"log.txt");  
	         ContentResolver cr = this.getContentResolver();  
	         try {  
	             Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));  
	             /* 将Bitmap设定到ImageView */  
	             ivProduct.setImageBitmap(bitmap);  
	             imgDir=ToolClass.getRealFilePath(GoodsProSet.this,uri);
	         } catch (FileNotFoundException e) {  
	             Log.e("Exception", e.getMessage(),e);  
	         }  
	     }  
	     super.onActivityResult(requestCode, resultCode, data);  
	 }  
	  
	 
	
}
