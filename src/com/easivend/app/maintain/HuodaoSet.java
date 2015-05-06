package com.easivend.app.maintain;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_ProductAdapter;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.model.Tb_vmc_column;
import com.easivend.model.Tb_vmc_product;
import com.example.evconsole.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HuodaoSet extends Activity 
{
	private ImageView ivhuoProID=null;
	private Button btnhuoProID=null,btnhuoaddSave=null,btnhuodelSave=null,btnhuoexit=null;
	private TextView txthuoCabID=null,txthuoColID=null,txthuoProID=null,txthuoProName=null,txthuomarketPrice=null,
			txthuosalesPrice=null,txthuocolumnStatus=null,txthuoshelfLife=null,txthuolasttime=null,txthuoIslast=null;	
	private EditText edthuopathCount=null,edthuopathRemain=null;
	private String huoID=null,cabID=null,temphuostatus=null,huostatus=null,productID=null,imgDir=null;;
	private View popview=null;
	private PopupWindow popWin=null;
	private GridView gvselectProduct=null;
	private Button btnselectexit=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.huoset);// 设置布局文件
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    	final String date=df.format(new Date());
		ivhuoProID = (ImageView) findViewById(R.id.ivhuoProID);
		txthuoCabID = (TextView) findViewById(R.id.txthuoCabID);
		txthuoColID = (TextView) findViewById(R.id.txthuoColID);
		txthuoProID = (TextView) findViewById(R.id.txthuoProID);
		txthuoProName = (TextView) findViewById(R.id.txthuoProName);
		txthuomarketPrice = (TextView) findViewById(R.id.txthuomarketPrice);
		txthuosalesPrice = (TextView) findViewById(R.id.txthuosalesPrice);
		txthuocolumnStatus = (TextView) findViewById(R.id.txthuocolumnStatus);
		txthuoshelfLife = (TextView) findViewById(R.id.txthuoshelfLife);
		txthuolasttime = (TextView) findViewById(R.id.txthuolasttime);
		txthuoIslast = (TextView) findViewById(R.id.txthuoIslast);
		edthuopathCount = (EditText) findViewById(R.id.edthuopathCount);	
		edthuopathCount.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub				
				updatehuodaostatus();				
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
		edthuopathRemain = (EditText) findViewById(R.id.edthuopathRemain);	
		edthuopathRemain.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub				
				updatehuodaostatus();			
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
		//从商品页面中取得锁选中的商品
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		huoID=bundle.getString("huoID");
		cabID=bundle.getString("cabID");
		temphuostatus=bundle.getString("huoStatus");		
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品huoID="+huoID+"cabID="+cabID+"status="+temphuostatus);
		txthuoCabID.setText(cabID);
		txthuoColID.setText(huoID);
		//如果该货道有绑定商品ID有存在则刷新页面为修改商品的页面
		updateHuodao(cabID,huoID);
		
		//修改货道状态参数		
		updatehuodaostatus();
		//选择绑定的商品
		btnhuoProID = (Button) findViewById(R.id.btnhuoProID);
		btnhuoProID.setOnClickListener(new OnClickListenerpop());
		//退出
		btnhuoexit = (Button) findViewById(R.id.btnhuoexit);
		btnhuoexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
		//保存
		btnhuoaddSave = (Button) findViewById(R.id.btnhuoaddSave);
		btnhuoaddSave.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) 
		    {
		    	int pathCount= 0;
		    	int pathRemain= 0;
		    	if(edthuopathCount.getText().toString().isEmpty()!=true)
		    		pathCount= Integer.parseInt(edthuopathCount.getText().toString());
		    	if(edthuopathRemain.getText().toString().isEmpty()!=true)
		    		pathRemain= Integer.parseInt(edthuopathRemain.getText().toString());
		    	
		    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道cabineID="+cabID+" columnID="+huoID+" productID="
    					+productID+" pathCount="+pathCount+" pathRemain="+pathRemain+" columnStatus="+huostatus);
    			
		    	if ((productID.isEmpty()!=true)&&(edthuopathCount.getText().toString().isEmpty()!=true)
		    			&&(edthuopathRemain.getText().toString().isEmpty()!=true)
		    			&&(pathCount>0)
		    		)
		    	{
		    		try 
		    		{
		    			// 创建InaccountDAO对象
		    			vmc_columnDAO columnDAO = new vmc_columnDAO(HuodaoSet.this);
			            //创建Tb_inaccount对象
		    			Tb_vmc_column tb_vmc_column = new Tb_vmc_column(cabID, huoID,productID,pathCount,
		    					pathRemain,Integer.parseInt(huostatus),date);
		    			
		    			columnDAO.addorupdate(tb_vmc_column);// 添加商品信息
		    			
			        	// 弹出信息提示
			            Toast.makeText(HuodaoSet.this, "〖新增商品〗数据添加成功！", Toast.LENGTH_SHORT).show();
			            //退出时，返回intent
			            Intent intent=new Intent();
			            intent.putExtra("back", "ok");
			            setResult(GoodsManager.RESULT_OK,intent);
			            finish();
//			            
		    		} catch (Exception e)
					{
						// TODO: handle exception
						Toast.makeText(HuodaoSet.this, "配置货道失败！", Toast.LENGTH_LONG).show();
					}		    		
		            
		        } 
		        else
		        {
		            Toast.makeText(HuodaoSet.this, "请填完参数！", Toast.LENGTH_LONG).show();
		        }
		    }
		});
		//删除绑定的商品
		btnhuodelSave = (Button) findViewById(R.id.btnhuodelSave);
		btnhuodelSave.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0)
		    {
		    	//创建警告对话框
		    	Dialog alert=new AlertDialog.Builder(HuodaoSet.this)
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
			    					vmc_columnDAO columnDAO = new vmc_columnDAO(HuodaoSet.this);
						            //创建Tb_inaccount对象
			    					Tb_vmc_column tb_vmc_column = new Tb_vmc_column(cabID, huoID,productID,0,
					    					0,Integer.parseInt(huostatus),date);				    			
			    					columnDAO.detele(tb_vmc_column);// 删除货道信息	
			    					// 弹出信息提示
						            Toast.makeText(HuodaoSet.this, "〖新增商品〗数据添加成功！", Toast.LENGTH_SHORT).show();
						            //退出时，返回intent
						            Intent intent=new Intent();
						            intent.putExtra("back", "ok");
						            setResult(GoodsManager.RESULT_OK,intent);
						            finish();
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
	}
	//弹出选择绑定的商品页面
	private class OnClickListenerpop implements OnClickListener
    {

		@Override
		public void onClick(View arg0) 
		{
			// TODO Auto-generated method stub
			LayoutInflater inflater=LayoutInflater.from(HuodaoSet.this);
			//找到了了布局文件中的view
			HuodaoSet.this.popview = inflater.inflate(R.layout.goodsselect, null);
			//新建弹出菜单实例，使用布局文件中的view,长600,宽800，有焦距
			HuodaoSet.this.popWin = new PopupWindow(HuodaoSet.this.popview,300,800,true);
			//开始处理popWin中的控件
			HuodaoSet.this.gvselectProduct = (GridView)HuodaoSet.this.popview.findViewById(R.id.gvselectProduct);
			HuodaoSet.this.btnselectexit = (Button)HuodaoSet.this.popview.findViewById(R.id.btnselectexit);
			// 商品表中的所有商品信息补充到商品数据结构数组中
			final Vmc_ProductAdapter productAdapter=new Vmc_ProductAdapter();
	    	productAdapter.showProInfo(HuodaoSet.this,"","shoudong",""); 
	    	ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(), HuodaoSet.this);// 创建pictureAdapter对象
	    	gvselectProduct.setAdapter(adapter);// 为GridView设置数据源
			
			//按下popwindow的图片按钮
			HuodaoSet.this.gvselectProduct.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					String strInfo[]=productAdapter.getProductID();
					productID = strInfo[arg2];// 记录收入信息               
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+productID);
					HuodaoSet.this.popWin.dismiss();
					updateProduct(productID);
				}// 为GridView设置项单击事件
	    		
	    	});
			//按下返回按钮
			HuodaoSet.this.btnselectexit.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					HuodaoSet.this.popWin.dismiss();
				}
				
			});
			//弹出窗体
			HuodaoSet.this.popWin.showAtLocation(HuodaoSet.this.btnhuoProID, Gravity.CENTER, 0, 0);
		}
    	
    }
	//导入该货道信息
	private void updateHuodao(String cabID,String huoID)
	{
		// 创建InaccountDAO对象
		vmc_columnDAO columnDAO = new vmc_columnDAO(HuodaoSet.this);
		Tb_vmc_column tb_vmc_column = columnDAO.find(cabID,huoID);// 添加商品信息
		if(tb_vmc_column!=null)
		{
			edthuopathCount.setText(String.valueOf(tb_vmc_column.getPathCount()));
			edthuopathRemain.setText(String.valueOf(tb_vmc_column.getPathRemain()));
			txthuolasttime.setText(tb_vmc_column.getLasttime());
			HuodaoSet.this.productID=tb_vmc_column.getProductID();
			updateProduct(HuodaoSet.this.productID);//更新商品信息到页面中
		}		
	}
	//更新商品信息到页面中
	private void updateProduct(String productID)
	{
		vmc_productDAO productDAO = new vmc_productDAO(HuodaoSet.this);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
	    Tb_vmc_product tb_inaccount = productDAO.find(productID);
	    imgDir=tb_inaccount.getAttBatch1().toString();
	    /*为什么图片一定要转化为 Bitmap格式的！！ */
        Bitmap bitmap = ToolClass.getLoacalBitmap(imgDir); //从本地取图片(在cdcard中获取)  //
        ivhuoProID.setImageBitmap(bitmap);// 设置图像的二进制值
                
		txthuoProID.setText(tb_inaccount.getProductID().toString());
		txthuoProName.setText(tb_inaccount.getProductName().toString());
		txthuomarketPrice.setText(String.valueOf(tb_inaccount.getMarketPrice()));
		txthuosalesPrice.setText(String.valueOf(tb_inaccount.getSalesPrice()));
		txthuoshelfLife.setText(String.valueOf(tb_inaccount.getShelfLife()));		
	}
	//更新货道信息
	private void updatehuodaostatus()
	{
		if(temphuostatus.equals("1")==true)//货道故障
			huostatus="2";
		else if(temphuostatus.equals("0")==true)//货道正常
		{
			int tempCount= 0;
	    	int tempRemain= 0;
	    	if(edthuopathCount.getText().toString().isEmpty()!=true)
	    		tempCount= Integer.parseInt(edthuopathCount.getText().toString());
	    	if(edthuopathRemain.getText().toString().isEmpty()!=true)
	    		tempRemain= Integer.parseInt(edthuopathRemain.getText().toString());
	    	
			if ((edthuopathCount.getText().toString().isEmpty()!=true)
	    			&&(edthuopathRemain.getText().toString().isEmpty()!=true)
	    			&&(tempCount>0)&&(tempRemain>0)
	    		)//正常
	    		huostatus="1";
	    	else                             //缺货
	    		huostatus="3";	
		}
		
		if(huostatus.equals("1")==true)
			txthuocolumnStatus.setText("正常");
		else if(huostatus.equals("2")==true)
			txthuocolumnStatus.setText("故障");
		else if(huostatus.equals("3")==true)
			txthuocolumnStatus.setText("缺货");
	}
}
