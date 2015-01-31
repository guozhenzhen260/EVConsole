package com.example.evconsole;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.easivend.dao.vmc_classDAO;
import com.easivend.model.Tb_vmc_class;



import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class GoodsManager extends TabActivity 
{
	private TabHost mytabhost = null;
	private int[] layres=new int[]{R.id.tab_class,R.id.tab_product};//内嵌布局文件的id
	private ListView lvinfo;// 创建ListView对象
	private EditText edtclassid=null,edtclassname=null;
	private Button btnclassadd=null,btnclassupdate=null,btnclassdel=null,btnclassexit=null;// 创建Button对象“退出”
	// 定义字符串数组，存储系统功能
    private String[] proID = new String[9];
    private String[] proImage = new String[9];
    private String[] promarket = new String[9];
    private String[] prosales = new String[9];
    private GridView gvProduct=null;
    
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
                String strInfo = String.valueOf(((TextView) view).getText());// 记录收入信息
                String strid = strInfo.substring(0, strInfo.indexOf('<'));// 从收入信息中截取收入编号
                String strname = strInfo.substring(strInfo.indexOf('>')+1);// 从收入信息中截取收入编号
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
    	proImage[0]="/sdcard/productimage/449.jpg";
    	proImage[1]="/sdcard/productimage/chaomiandawng.jpg";
    	proImage[2]="/sdcard/productimage/niurouganbanmian.jpg";
    	proImage[3]="/sdcard/productimage/P1070588.jpg";
    	proImage[4]="/sdcard/productimage/P1070589.jpg";
    	proImage[5]="/sdcard/productimage/shimianbafang.jpg";
    	proImage[6]="/sdcard/productimage/xiangnaroujiang.jpg";
    	proImage[7]="/sdcard/productimage/xuebi500ml.jpg";
    	proImage[8]="/sdcard/productimage/yibao.jpg";
    	proID[0]="449.jpg";
    	proID[1]="chaomiandawng.jpg";
    	proID[2]="niurouganbanmian.jpg";
    	proID[3]="P1070588.jpg";
    	proID[4]="P1070589.jpg";
    	proID[5]="shimianbafang.jpg";
    	proID[6]="xiangnaroujiang.jpg";
    	proID[7]="xuebi500ml.jpg";
    	proID[8]="yibao.jpg";
    	promarket[0]="20";
    	promarket[1]="21";
    	promarket[2]="22";
    	promarket[3]="23";
    	promarket[4]="24";
    	promarket[5]="25";
    	promarket[6]="26";
    	promarket[7]="27";
    	promarket[8]="28";
    	prosales[0]="10";
    	prosales[1]="11";
    	prosales[2]="12";
    	prosales[3]="13";
    	prosales[4]="14";
    	prosales[5]="15";
    	prosales[6]="16";
    	prosales[7]="17";
    	prosales[8]="18";
    	
    	gvProduct = (GridView) findViewById(R.id.gvProduct);// 获取布局文件中的gvInfo组件
    	ProPictureAdapter adapter = new ProPictureAdapter(proID,promarket,prosales,proImage, this);// 创建pictureAdapter对象
    	gvProduct.setAdapter(adapter);// 为GridView设置数据源
        
	}
	//===============
	//商品分类设置页面
	//===============
	// 显示商品分类信息
	private void showInfo() 
	{
	    String[] strInfos = null;// 定义字符串数组，用来存储收入信息
	    ArrayAdapter<String> arrayAdapter = null;// 创建ArrayAdapter对象
	    vmc_classDAO inaccountinfo = new vmc_classDAO(GoodsManager.this);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
	    List<Tb_vmc_class> listinfos = inaccountinfo.getScrollData(0, (int) inaccountinfo.getCount());
	    strInfos = new String[listinfos.size()];// 设置字符串数组的长度
	    int m = 0;// 定义一个开始标识
	    // 遍历List泛型集合
	    for (Tb_vmc_class tb_inaccount : listinfos) 
	    {
	        // 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
	        strInfos[m] = tb_inaccount.getClassID() + "<---|--->" + tb_inaccount.getClassName();
	        m++;// 标识加1
	    }
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
}

//===============
//商品设置页面
//===============
class ProViewHolder {// 创建ProViewHolder类存放控件集合

    public TextView proID;// 创建商品ID和名称
    public ImageView image;// 创建ImageView对象
    public TextView promarket;// 创建商品原价
    public TextView prosales;// 创建商品销售价
}

class ProPicture {// 创建ProPicture类

    private String proID;// 定义字符串，表示图像标题
    private String proImage;//图像位置
    private String promarket;//原价
    private String prosales;//现价
	public ProPicture(String proID, String promarket, String prosales,String proImage) {
		super();
		this.proID = proID;
		this.promarket = promarket;
		this.prosales = prosales;
		this.proImage = proImage;
		
	}
	public String getTitle() {
		return proID;
	}
	public void setTitle(String proID) {
		this.proID = proID;
	}
	public String getProImage() {
		return proImage;
	}
	public void setProImage(String proImage) {
		this.proImage = proImage;
	}
	public String getPromarket() {
		return promarket;
	}
	public void setPromarket(String promarket) {
		this.promarket = promarket;
	}
	public String getProsales() {
		return prosales;
	}
	public void setProsales(String prosales) {
		this.prosales = prosales;
	}
    
}

class ProPictureAdapter extends BaseAdapter {// 创建基于BaseAdapter的子类

    private LayoutInflater inflater;// 创建LayoutInflater对象
    private List<ProPicture> pictures;// 创建List泛型集合

    // 为类创建构造函数
    public ProPictureAdapter(String[] proID, String[] promarket,String[] prosales,String[] proImage, Context context) {
        super();
        pictures = new ArrayList<ProPicture>();// 初始化泛型集合对象
        inflater = LayoutInflater.from(context);// 初始化LayoutInflater对象
        for (int i = 0; i < proImage.length; i++)// 遍历图像数组
        {
            ProPicture picture = new ProPicture(proID[i],promarket[i],prosales[i], proImage[i]);// 使用标题和图像生成ProPicture对象
            pictures.add(picture);// 将Picture对象添加到泛型集合中
        }
    }

    @Override
    public int getCount() {// 获取泛型集合的长度
        if (null != pictures) {// 如果泛型集合不为空
            return pictures.size();// 返回泛型长度
        } else {
            return 0;// 返回0
        }
    }

    @Override
    public Object getItem(int arg0) {
        return pictures.get(arg0);// 获取泛型集合指定索引处的项
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;// 返回泛型集合的索引
    }
    
    /**
     * 加载本地图片
     * @param url
     * @return
     */
     public Bitmap getLoacalBitmap(String url) {
          try {
               FileInputStream fis = new FileInputStream(url);
               return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片        

            } catch (FileNotFoundException e) {
               e.printStackTrace();
               return null;
          }
     }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ProViewHolder viewHolder;// 创建ProViewHolder对象
        if (arg1 == null) {// 判断图像标识是否为空

            arg1 = inflater.inflate(R.layout.productgv, null);// 设置图像标识
            viewHolder = new ProViewHolder();// 初始化ProViewHolder对象
            viewHolder.proID = (TextView) arg1.findViewById(R.id.proID);// 设置图像标题
            viewHolder.image = (ImageView) arg1.findViewById(R.id.proImage);// 设置图像的二进制值
            viewHolder.promarket = (TextView) arg1.findViewById(R.id.promarket);// 设置图像标题
            viewHolder.prosales = (TextView) arg1.findViewById(R.id.prosales);// 设置图像标题
            
            arg1.setTag(viewHolder);// 设置提示
        } 
        else
        {
            viewHolder = (ProViewHolder) arg1.getTag();// 设置提示
        }
        
        viewHolder.proID.setText(pictures.get(arg0).getTitle());// 设置图像ID
        viewHolder.promarket.setText("原价:"+pictures.get(arg0).getPromarket());// 设置图像原价
        viewHolder.prosales.setText("现价:"+pictures.get(arg0).getProsales());// 设置图像原价
        /*为什么图片一定要转化为 Bitmap格式的！！ */
        Bitmap bitmap = getLoacalBitmap(pictures.get(arg0).getProImage()); //从本地取图片(在cdcard中获取)  //
        viewHolder.image.setImageBitmap(bitmap);// 设置图像的二进制值
        return arg1;// 返回图像标识
    }
}


