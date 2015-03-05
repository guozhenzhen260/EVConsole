package com.example.evconsole;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ShowSortAdapter;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_ClassAdapter;
import com.easivend.common.Vmc_ProductAdapter;
import com.easivend.model.Tb_vmc_class;
import com.easivend.model.Tb_vmc_product;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rasterizer;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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
	private final static int REQUEST_CODE=1;//声明请求标识
	private int[] layres=new int[]{R.id.tab_class,R.id.tab_product};//内嵌布局文件的id
	private ListView lvinfo;// 创建ListView对象
	private EditText edtclassid=null,edtclassname=null,edtfindProduct=null;
	private Button btnclassadd=null,btnclassupdate=null,btnclassdel=null,btnclassexit=null;// 创建Button对象“退出”
	private Button btnproadd=null,btnproupdate=null,btnprodel=null,btnproexit=null;
	// 定义商品列表
	Vmc_ProductAdapter productAdapter=null;
    private GridView gvProduct=null;
    //排序有关的定义
    private ShowSortAdapter showSortAdapter=null;
    private ArrayAdapter<String> arrayadapter = null;
    private Spinner spinprodsort=null;
    private String datasort="shoudong";
    private Button btnsortup=null,btnsortdown=null;
    		
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.goodsmanager);// 设置布局文件
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
    	// 为ListView添加项单击事件
    	lvinfo = (ListView) findViewById(R.id.lvclass);// 获取布局文件中的ListView组件
    	lvinfo.setOnItemClickListener(new OnItemClickListener() 
    	{
            // 覆写onItemClick方法
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strInfoclass = String.valueOf(((TextView) view).getText());// 记录收入信息
                String strid = strInfoclass.substring(0, strInfoclass.indexOf('<'));// 从收入信息中截取收入编号
                String strname = strInfoclass.substring(strInfoclass.indexOf('>')+1);// 从收入信息中截取收入编号
                edtclassid.setText(strid);
                edtclassname.setText(strname);
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
			        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(strclassid, strclassname,date);
			        	classDAO.add(tb_vmc_class);// 添加收入信息
			        	// 弹出信息提示
			            Toast.makeText(GoodsManager.this, "〖新增类别〗数据添加成功！", Toast.LENGTH_SHORT).show();
			            
					} catch (Exception e)
					{
						// TODO: handle exception
						Toast.makeText(GoodsManager.this, "类别添加失败！", Toast.LENGTH_SHORT).show();
					}			    		
		            showInfo();
		        } 
		        else
		        {
		            Toast.makeText(GoodsManager.this, "请输入类别编号和名称！", Toast.LENGTH_SHORT).show();
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
		        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(strclassid, strclassname,date);
		        	classDAO.update(tb_vmc_class);// 修改
		            // 弹出信息提示
		            Toast.makeText(GoodsManager.this, "〖修改类别〗成功！", Toast.LENGTH_SHORT).show();
		            showInfo();
		        } 
		        else
		        {
		            Toast.makeText(GoodsManager.this, "请输入类别编号和名称！", Toast.LENGTH_SHORT).show();
		        }
		    }
		});
    	//删除
    	btnclassdel = (Button) findViewById(R.id.btnclassdel);
    	btnclassdel.setOnClickListener(new OnClickListener() {
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
		        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(strclassid, strclassname,date);
		        	classDAO.detele(tb_vmc_class);// 修改
		            // 弹出信息提示
		            Toast.makeText(GoodsManager.this, "〖删除类别〗成功！", Toast.LENGTH_SHORT).show();
		            showInfo();
		        } 
		        else
		        {
		            Toast.makeText(GoodsManager.this, "请输入类别编号和名称！", Toast.LENGTH_SHORT).show();
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
    	// 商品表中的所有商品信息补充到商品数据结构数组中
    	productAdapter=new Vmc_ProductAdapter();
    	productAdapter.showProInfo(this,"",datasort);     	
    	gvProduct = (GridView) findViewById(R.id.gvProduct);// 获取布局文件中的gvInfo组件
    	ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(), this);// 创建pictureAdapter对象
    	gvProduct.setAdapter(adapter);// 为GridView设置数据源
    	gvProduct.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String productID[]=productAdapter.getProductID();
				strInfo = productID[arg2];// 记录收入信息               
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+strInfo);
//				intent = new Intent();
//		    	intent.setClass(GoodsManager.this, GoodsProSet.class);// 使用AddInaccount窗口初始化Intent
//                intent.putExtra("proID", strInfo);
//		    	startActivityForResult(intent, REQUEST_CODE);// 打开AddInaccount	
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
    	//修改
    	btnproupdate = (Button) findViewById(R.id.btnproupdate);
    	btnproupdate.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0)
		    {
		    	 intent = new Intent();
		    	 intent.setClass(GoodsManager.this, GoodsProSet.class);// 使用AddInaccount窗口初始化Intent
                 intent.putExtra("proID", strInfo);
		    	 startActivityForResult(intent, REQUEST_CODE);// 打开AddInaccount	
		    }
		});
    	//删除
    	btnprodel = (Button) findViewById(R.id.btnprodel);
    	btnprodel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0)
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
					    			productAdapter.showProInfo(GoodsManager.this,"",datasort); 
									ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(), GoodsManager.this);
					    			gvProduct.setAdapter(adapter);// 为GridView设置数据源
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
    	//检索
    	edtfindProduct = (EditText) findViewById(R.id.edtfindProduct);
    	edtfindProduct.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub				
				productAdapter.showProInfo(GoodsManager.this,edtfindProduct.getText().toString(),datasort); 
				ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(), GoodsManager.this);// 创建pictureAdapter对象
		    	gvProduct.setAdapter(adapter);// 为GridView设置数据源				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		}); 
    	btnsortup = (Button) findViewById(R.id.btnsortup);
    	btnsortdown = (Button) findViewById(R.id.btnsortdown);
    	//排序
    	this.spinprodsort = (Spinner) super.findViewById(R.id.spinprodsort);
    	// 使用字符串数组初始化ArrayAdapter对象
    	showSortAdapter=new ShowSortAdapter();    	
	    arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, showSortAdapter.getDataSortName());
	    spinprodsort.setAdapter(arrayadapter);// 为ListView列表设置数据源
    	spinprodsort.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			//当选项改变时触发
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				List<String> dataSortID=showSortAdapter.getDataSortID();
				datasort=dataSortID.get(arg2);
				productAdapter.showProInfo(GoodsManager.this,edtfindProduct.getText().toString(),datasort); 
				ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(), GoodsManager.this);// 创建pictureAdapter对象
		    	gvProduct.setAdapter(adapter);// 为GridView设置数据源
		    	if(datasort.equals("shoudong"))
		    	{
		    		btnsortup.setEnabled(true);
		    		btnsortdown.setEnabled(true);
		    	}
		    	else
		    	{
		    		btnsortup.setEnabled(false);
		    		btnsortdown.setEnabled(false);
		    	}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	//上移排序序号
    	btnsortup.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0)
		    {
		    	if(strInfo.isEmpty()!=true)
		    	{
			    	// 创建InaccountDAO对象
	    			vmc_productDAO productDAO = new vmc_productDAO(GoodsManager.this);
		            //创建Tb_inaccount对象
	    			Tb_vmc_product tb_vmc_product = new Tb_vmc_product(strInfo, "","",0,
	    					0,0,date,date,"","","",0,0);				    			
	    			productDAO.sortupdown(tb_vmc_product,1);// 添加商品信息
	    			productAdapter.showProInfo(GoodsManager.this,"",datasort); 
					ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(), GoodsManager.this);
	    			gvProduct.setAdapter(adapter);// 为GridView设置数据源
		    	}
		    }
		});
    	//下移排序序号
    	btnsortdown.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0)
		    {
		    	if(strInfo.isEmpty()!=true)
		    	{
			    	// 创建InaccountDAO对象
	    			vmc_productDAO productDAO = new vmc_productDAO(GoodsManager.this);
		            //创建Tb_inaccount对象
	    			Tb_vmc_product tb_vmc_product = new Tb_vmc_product(strInfo, "","",0,
	    					0,0,date,date,"","","",0,0);				    			
	    			productDAO.sortupdown(tb_vmc_product,2);// 添加商品信息
	    			productAdapter.showProInfo(GoodsManager.this,"",datasort); 
					ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(), GoodsManager.this);
	    			gvProduct.setAdapter(adapter);// 为GridView设置数据源
		    	}
		    }
		});
	}
	//===============
	//商品分类设置页面
	//===============
	// 显示商品分类信息
	private void showInfo() 
	{
	    ArrayAdapter<String> arrayAdapter = null;// 创建ArrayAdapter对象
	    Vmc_ClassAdapter vmc_classAdapter=new Vmc_ClassAdapter();
	    String[] strInfos = vmc_classAdapter.showListInfo(GoodsManager.this);
	    // 使用字符串数组初始化ArrayAdapter对象
	    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strInfos);
	    lvinfo.setAdapter(arrayAdapter);// 为ListView列表设置数据源
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
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品ret="+str);
				productAdapter.showProInfo(GoodsManager.this,"",datasort); 
				ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(), GoodsManager.this);// 创建pictureAdapter对象
		    	gvProduct.setAdapter(adapter);// 为GridView设置数据源
			}			
		}
	}
	
	
}
