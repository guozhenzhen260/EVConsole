package com.easivend.app.maintain;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_ClassAdapter;
import com.easivend.common.Vmc_ProductAdapter;
import com.easivend.model.Tb_vmc_class;
import com.easivend.model.Tb_vmc_product;
import com.example.evconsole.R;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class GoodsManager extends TabActivity 
{
	private TabHost mytabhost = null;
	Intent intent = null;// 创建Intent对象
	String strInfo="";
	private final static int REQUEST_CODE=1,REQCLASS_CODE=2;//声明请求标识
	private int[] layres=new int[]{R.id.tab_class,R.id.tab_product};//内嵌布局文件的id
	private ListView lvinfo;// 创建ListView对象
	private Uri uri=null;	
	private String imgDir=null;
	//定义显示的内容包装
    private List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
    private SimpleAdapter simpleada = null;//进行数据的转换操作
	private String[] proclassID = null;//用来分离出类型编号
	private String[] proclassName = null;//用来分离出类型名称
	private String[] imgDirs=null;
	private EditText edtclassid=null,edtclassname=null,edtfindProduct=null;
	private ImageView imgclassname=null;
	private Button btnclassname=null,btnclassadd=null,btnclassupdate=null,btnclassdel=null,btnclassexit=null;// 创建Button对象“退出”
	private Button btnproadd=null,btnprodel=null,btnprodelselect=null,btnproexit=null;	
	// 定义商品列表
	Vmc_ProductAdapter productAdapter=null;
    private GridView gvProduct=null;
    private TextView txtproidValue=null,txtpronameValue=null;
    private String datasort="shoudong";
    
    		
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.goodsmanager);// 设置布局文件
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		this.mytabhost = super.getTabHost();//取得TabHost对象
        LayoutInflater.from(this).inflate(R.layout.goodsmanage, this.mytabhost.getTabContentView(),true);
        //增加Tab的组件
        TabSpec myTabclass=this.mytabhost.newTabSpec("tab0");
    	myTabclass.setIndicator("商品分类设置");
    	myTabclass.setContent(this.layres[0]);
    	this.mytabhost.addTab(myTabclass); 
    	
    	TabSpec myTabproduct=this.mytabhost.newTabSpec("tab1");
    	myTabproduct.setIndicator("商品设置");
    	myTabproduct.setContent(this.layres[1]);
    	this.mytabhost.addTab(myTabproduct); 
    	
    	//===============
    	//商品分类设置页面
    	//===============
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    	final String date=df.format(new Date());
    	imgclassname = (ImageView) findViewById(R.id.imgclassname);
    	//选择图片
    	btnclassname=(Button) findViewById(R.id.btnclassname);
    	btnclassname.setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();  
               /* 开启Pictures画面Type设定为image */  
               intent.setType("image/*");  
               /* 使用Intent.ACTION_GET_CONTENT这个Action */  
               intent.setAction(Intent.ACTION_GET_CONTENT);   
               /* 取得相片后返回本画面 */  
               startActivityForResult(intent, REQCLASS_CODE);
			}
		}); 
    	// 为ListView添加项单击事件
    	lvinfo = (ListView) findViewById(R.id.lvclass);// 获取布局文件中的ListView组件
    	lvinfo.setOnItemClickListener(new OnItemClickListener() 
    	{
            // 覆写onItemClick方法
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	edtclassid.setText(proclassID[position]);
                edtclassname.setText(proclassName[position]);
                imgDir=imgDirs[position];
                if(imgDir!=null)
                {	                
	                /*为什么图片一定要转化为 Bitmap格式的！！ */
	    	        Bitmap bitmap = ToolClass.getLoacalBitmap(imgDir); //从本地取图片(在cdcard中获取)  //
	    	        imgclassname.setImageBitmap(bitmap);// 设置图像的二进制值
                }
                else
                {
                	imgclassname.setImageResource(R.drawable.wutupian);	
				}
            }
        });
    	showInfo();// 调用自定义方法显示商品分类信息
    	edtclassid = (EditText) findViewById(R.id.edtclassid);
    	edtclassname = (EditText) findViewById(R.id.edtclassname);
    	//添加
    	btnclassadd = (Button) findViewById(R.id.btnclassadd);
    	btnclassadd.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0)
		    {
		    	String strclassid = edtclassid.getText().toString();
		    	String strclassname = edtclassname.getText().toString();
		    	if ((strclassid.isEmpty()!=true)&&(strclassname.isEmpty()!=true))
		    	{
		    		try 
		    		{
		    			// 创建InaccountDAO对象
			        	vmc_classDAO classDAO = new vmc_classDAO(GoodsManager.this);
			            // 创建Tb_inaccount对象
			        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(strclassid, strclassname,date,imgDir);
			        	classDAO.add(tb_vmc_class);// 添加收入信息
			        	ToolClass.addOptLog(GoodsManager.this,0,"添加类别:"+strclassid+strclassname);
			        	// 弹出信息提示
			            Toast.makeText(GoodsManager.this, "〖新增类别〗数据添加成功！", Toast.LENGTH_SHORT).show();
			            
					} catch (Exception e)
					{
						// TODO: handle exception
						ToolClass.failToast("类别添加失败！");	
					}			    		
		            showInfo();
		        } 
		        else
		        {
		        	ToolClass.failToast("请输入类别编号和名称！");	
		        }
		    }
		});
    	//修改
    	btnclassupdate = (Button) findViewById(R.id.btnclassupdate);
    	btnclassupdate.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0)
		    {
		    	String strclassid = edtclassid.getText().toString();
		    	String strclassname = edtclassname.getText().toString();
		    	if ((strclassid.isEmpty()!=true)&&(strclassname.isEmpty()!=true))
		    	{
		        	// 创建InaccountDAO对象
		        	vmc_classDAO classDAO = new vmc_classDAO(GoodsManager.this);
		            // 创建Tb_inaccount对象
		        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(strclassid, strclassname,date,imgDir);
		        	classDAO.update(tb_vmc_class);// 修改
		        	ToolClass.addOptLog(GoodsManager.this,1,"修改类别:"+strclassid+strclassname);
		            // 弹出信息提示
		            Toast.makeText(GoodsManager.this, "〖修改类别〗成功！", Toast.LENGTH_SHORT).show();
		            showInfo();
		        } 
		        else
		        {
		        	ToolClass.failToast("请输入类别编号和名称！");	
		        }
		    }
		});
    	//删除
    	btnclassdel = (Button) findViewById(R.id.btnclassdel);
    	btnclassdel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0)
		    {
		    	final String strclassid = edtclassid.getText().toString();
		    	final String strclassname = edtclassname.getText().toString();
		    	if ((strclassid.isEmpty()!=true)&&(strclassname.isEmpty()!=true))
		    	{
			    	//创建警告对话框
			    	Dialog alert=new AlertDialog.Builder(GoodsManager.this)
			    		.setTitle("对话框")//标题
			    		.setMessage("您确定要删除该商品类别吗？")//表示对话框中得内容
			    		.setIcon(R.drawable.ic_launcher)//设置logo
			    		.setPositiveButton("删除", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
			    			{				
				    				@Override
				    				public void onClick(DialogInterface dialog, int which) 
				    				{
				    		        	// 创建InaccountDAO对象
				    		        	vmc_classDAO classDAO = new vmc_classDAO(GoodsManager.this);
				    		            // 创建Tb_inaccount对象
				    		        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(strclassid, strclassname,date,imgDir);
				    		        	classDAO.detele(tb_vmc_class);// 修改
				    		        	ToolClass.addOptLog(GoodsManager.this,2,"删除类别:"+strclassid+strclassname);
				    		            // 弹出信息提示
				    		            Toast.makeText(GoodsManager.this, "〖删除类别〗成功！", Toast.LENGTH_SHORT).show();
				    		            showInfo();
				    		        }
			    		      }
			    			)		    		        
		    		        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
		    		        	{			
		    						@Override
		    						public void onClick(DialogInterface dialog, int which) 
		    						{
		    							// TODO Auto-generated method stub				
		    						}
		    		        	}
		    		        )
		    		        .create();//创建一个对话框
		    		        alert.show();//显示对话框
		    	}			    	 
		        else
		        {
		        	ToolClass.failToast("请输入类别编号和名称！");	
		        }
		    }
		});
    	//退出
    	btnclassexit = (Button) findViewById(R.id.btnclassexit);
    	btnclassexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		        finish();
		    }
		});
    	
    	//===============
    	//商品设置页面
    	//===============
    	txtproidValue = (TextView) findViewById(R.id.txtproidValue);
    	txtpronameValue = (TextView) findViewById(R.id.txtpronameValue);    	  	
    	gvProduct = (GridView) findViewById(R.id.gvProduct);// 获取布局文件中的gvInfo组件
    	// 商品表中的所有商品信息补充到商品数据结构数组中  
    	VmcProductThread vmcProductThread=new VmcProductThread();
    	vmcProductThread.execute();
    	gvProduct.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) { 
				// TODO Auto-generated method stub
				String productID[]=productAdapter.getProductID();
				String productName[]=productAdapter.getProductName();
				strInfo = productID[arg2];// 记录收入信息               
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+strInfo,"log.txt");
				txtproidValue.setText(strInfo);
				txtpronameValue.setText(productName[arg2]);
				//跳转页面
				intent = new Intent();
		    	intent.setClass(GoodsManager.this, GoodsProSet.class);// 使用AddInaccount窗口初始化Intent
                intent.putExtra("proID", strInfo);
		    	startActivityForResult(intent, REQUEST_CODE);// 打开AddInaccount	
			}// 为GridView设置项单击事件
    		
    	});
    	//添加
    	btnproadd = (Button) findViewById(R.id.btnproadd);
    	btnproadd.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0)
		    {
		    	 intent = new Intent();
		    	 intent.setClass(GoodsManager.this, GoodsProSet.class);// 使用AddInaccount窗口初始化Intent
                 intent.putExtra("proID", "");
		    	 startActivityForResult(intent, REQUEST_CODE);// 打开AddInaccount	
		    }
		});    	
    	//删除
    	btnprodel = (Button) findViewById(R.id.btnprodel);
    	btnprodel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0)
		    {
		    	if (txtproidValue.getText().toString().isEmpty()!=true)
		    	{
			    	//创建警告对话框
			    	Dialog alert=new AlertDialog.Builder(GoodsManager.this)
			    		.setTitle("对话框")//标题
			    		.setMessage("您确定要删除该商品吗？")//表示对话框中得内容
			    		.setIcon(R.drawable.ic_launcher)//设置logo
			    		.setPositiveButton("删除", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
			    			{				
				    				@Override
				    				public void onClick(DialogInterface dialog, int which) 
				    				{
				    					// TODO Auto-generated method stub	
				    					// 创建InaccountDAO对象
						    			vmc_productDAO productDAO = new vmc_productDAO(GoodsManager.this);
							            //创建Tb_inaccount对象
						    			Tb_vmc_product tb_vmc_product = new Tb_vmc_product(strInfo, "","",0,
						    					0,0,date,date,"","","",0,0);				    			
						    			productDAO.detele(tb_vmc_product);// 添加商品信息
						    			productAdapter.showProInfo(GoodsManager.this,"",datasort,""); 
										ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(),productAdapter.getProcount(), GoodsManager.this);
						    			gvProduct.setAdapter(adapter);// 为GridView设置数据源
						    			ToolClass.addOptLog(GoodsManager.this,2,"删除商品:"+strInfo);
						    			// 弹出信息提示
				    		            Toast.makeText(GoodsManager.this, "〖删除商品〗成功！", Toast.LENGTH_SHORT).show();
				    				}
			    		      }
			    			)		    		        
		    		        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
		    		        	{			
		    						@Override
		    						public void onClick(DialogInterface dialog, int which) 
		    						{
		    							// TODO Auto-generated method stub				
		    						}
		    		        	}
		    		        )
		    		        .create();//创建一个对话框
		    		        alert.show();//显示对话框
		    	}
		    	else
		        {
		    		ToolClass.failToast("请选择需要删除的商品！");	
		        }
		    }
		});
    	//删除按键配置
    	btnprodelselect = (Button) findViewById(R.id.btnprodelselect);
    	btnprodelselect.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0)
		    {
		    	
		    	//创建警告对话框
		    	Dialog alert=new AlertDialog.Builder(GoodsManager.this)
		    		.setTitle("对话框")//标题
		    		.setMessage("您确定要全部清空选货按键配置信息吗？")//表示对话框中得内容
		    		.setIcon(R.drawable.ic_launcher)//设置logo
		    		.setPositiveButton("清除", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
		    			{				
			    				@Override
			    				public void onClick(DialogInterface dialog, int which) 
			    				{
			    					// TODO Auto-generated method stub	
			    					ToolClass.DelSelectFile();
					    			ToolClass.addOptLog(GoodsManager.this,2,"清空选货按键配置");
					    			// 弹出信息提示
			    		            Toast.makeText(GoodsManager.this, "〖清空选货按键配置〗成功！", Toast.LENGTH_SHORT).show();
			    				}
		    		      }
		    			)		    		        
	    		        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
	    		        	{			
	    						@Override
	    						public void onClick(DialogInterface dialog, int which) 
	    						{
	    							// TODO Auto-generated method stub				
	    						}
	    		        	}
	    		        )
	    		        .create();//创建一个对话框
	    		        alert.show();//显示对话框		    	
		    }
		});    	
    	//退出
    	btnproexit = (Button) findViewById(R.id.btnproexit);
    	btnproexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		        finish();
		    }
		});    	     	
    	//动态设置控件高度
    	//
    	DisplayMetrics  dm = new DisplayMetrics();  
        //取得窗口属性  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        //窗口的宽度  
        int screenWidth = dm.widthPixels;          
        //窗口高度  
        int screenHeight = dm.heightPixels;      
        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<屏幕"+screenWidth
				+"],["+screenHeight+"]","log.txt");	
		
        //横屏
		if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
		{
			//商品的高
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gvProduct.getLayoutParams(); // 取控件mGrid当前的布局参数
	    	linearParams.height =  screenHeight-252;// 当控件的高强制设成75象素
	    	gvProduct.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
	    	//商品分类的高
	    	LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) lvinfo.getLayoutParams(); // 取控件mGrid当前的布局参数
	    	linearParams2.height =  screenHeight-700;// 当控件的高强制设成75象素
	    	lvinfo.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
		}
		//竖屏
		else
		{
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gvProduct.getLayoutParams(); // 取控件mGrid当前的布局参数
	    	linearParams.height =  screenHeight-700;// 当控件的高强制设成75象素
	    	gvProduct.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
	    	LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) lvinfo.getLayoutParams(); // 取控件mGrid当前的布局参数
	    	linearParams2.height =  screenHeight-700;// 当控件的高强制设成75象素
	    	lvinfo.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
		}
		
	}
	//===============
	//商品分类设置页面
	//===============
	// 显示商品分类信息
	private void showInfo() 
	{
		VmcClassThread vmcClassThread=new VmcClassThread();
		vmcClassThread.execute();
	}
	
	//****************
	//异步线程，用于查询记录
	//****************
	private class VmcClassThread extends AsyncTask<Void,Void,Vmc_ClassAdapter>
	{

		@Override
		protected Vmc_ClassAdapter doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Vmc_ClassAdapter vmc_classAdapter=new Vmc_ClassAdapter();
		    String[] strInfos = vmc_classAdapter.showListInfo(GoodsManager.this);
			return vmc_classAdapter;
		}

		@Override
		protected void onPostExecute(Vmc_ClassAdapter vmc_classAdapter) {
			// TODO Auto-generated method stub
			imgDirs=vmc_classAdapter.getProImage();
		    proclassID=vmc_classAdapter.getProclassID();
			proclassName=vmc_classAdapter.getProclassName();
			int x=0;
			GoodsManager.this.listMap.clear();
			for(x=0;x<proclassID.length;x++)
			{
			  	Map<String,String> map = new HashMap<String,String>();//定义Map集合，保存每一行数据
			   	map.put("proclassID", proclassID[x]);
		    	map.put("proclassName", proclassName[x]);	    	
		    	GoodsManager.this.listMap.add(map);//保存数据行
			}
			//将这个构架加载到data_list中
			GoodsManager.this.simpleada = new SimpleAdapter(GoodsManager.this,GoodsManager.this.listMap,R.layout.goodsclasslist,
			    		new String[]{"proclassID","proclassName"},//Map中的key名称
			    		new int[]{R.id.txtclassID,R.id.txtclassName});
			GoodsManager.this.lvinfo.setAdapter(GoodsManager.this.simpleada);
		}
				
	}
	
	@Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();// 实现基类中的方法
        showInfo();// 重新显示
    }
	//===============
	//商品设置页面
	//===============	
	//****************
	//异步线程，用于查询记录
	//****************
	private class VmcProductThread extends AsyncTask<Void,Void,Vmc_ProductAdapter>
	{

		@Override
		protected Vmc_ProductAdapter doInBackground(Void... params) {
			// TODO Auto-generated method stub
			productAdapter=new Vmc_ProductAdapter();
	    	productAdapter.showProInfo(GoodsManager.this,"",datasort,""); 
			return productAdapter;
		}

		@Override
		protected void onPostExecute(Vmc_ProductAdapter productAdapter) {
			// TODO Auto-generated method stub
			ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(),productAdapter.getProcount(), GoodsManager.this);// 创建pictureAdapter对象
	    	gvProduct.setAdapter(adapter);// 为GridView设置数据源	    	
		}
				
	}
	//接收GoodsProSet返回信息
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==REQUEST_CODE)
		{
			if(resultCode==GoodsManager.RESULT_OK)
			{
				Bundle bundle=data.getExtras();
				String str=bundle.getString("back");
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品ret="+str,"log.txt");
				productAdapter.showProInfo(GoodsManager.this,"",datasort,""); 
				ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(),productAdapter.getProcount(), GoodsManager.this);// 创建pictureAdapter对象
		    	gvProduct.setAdapter(adapter);// 为GridView设置数据源
			}			
		}
		else if(requestCode==REQCLASS_CODE)
		{
			if(resultCode==GoodsManager.RESULT_OK)
			{
				 uri = data.getData();  
		         ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<uri="+ uri.toString(),"log.txt");  
		         ContentResolver cr = this.getContentResolver();  
		         try {  
		             Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));  
		             /* 将Bitmap设定到ImageView */  
		             imgclassname.setImageBitmap(bitmap);  
		             imgDir=ToolClass.getRealFilePath(GoodsManager.this,uri);
		         } catch (FileNotFoundException e) {  
		             Log.e("Exception", e.getMessage(),e);  
		         }  
			}			
		}
	}
	
	@Override
	protected void onDestroy() {
    	//退出时，返回intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}
	
}
