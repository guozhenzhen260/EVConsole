package com.easivend.app.maintain;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.easivend.dao.vmc_machinesetDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.common.MachineExpanseListAdapter;
import com.easivend.common.ShowSortAdapter;
import com.easivend.common.ToolClass;
import com.easivend.model.Tb_vmc_machineset;
import com.easivend.model.Tb_vmc_product;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TimePicker;


public class ParamManager extends TabActivity 
{
	private TabHost mytabhost = null;
	private int[] layres=new int[]{R.id.tab_machine,R.id.tab_device,R.id.tab_run};//��Ƕ�����ļ���id
	private static final int TIME_DIALOG_ID = 0;// ����ʱ��Ի�����
	private EditText edtdevID=null,edtdevhCode=null,edtmainPwd=null,edtrstTime=null,edtrstDay=null;
	private Switch switchisNet = null,switchisbuyCar = null,switchlanguage = null,switchbaozhiProduct = null,switchemptyProduct = null,switchamount = null,switchcard = null,
			switchzhifubaofaca = null,switchzhifubaoer = null,switchweixing = null,switchprinter = null;    
	private RadioGroup grpisfenClass=null, grpliebiaoKuan=null;
	private RadioButton hunradiobtn=null,typeradiobtn=null,guiradiobtn=null,smallradiobtn=null,normalradiobtn=null,bigradiobtn=null;
	private Spinner spinparamsort=null;
	private Button btnmachineSave=null,btnmachineexit=null,btndeviceSave=null,btndeviceexit=null,btnamount=null,btncard=null,btnzhifubaofaca=null,
			btnzhifubaoer=null,btnweixing=null,btnprinter=null;
	private int isfenClass=0,liebiaoKuan=0;
	private int proSortType=6;
	//�����йصĶ���
	private ShowSortAdapter showSortAdapter=null;
    private ArrayAdapter<String> arrayadapter = null;
	private int mHour=0,mMinute=0;
	
	//�����Ƶ�ֵ
	public static String runValue[][]=new String[][]{{"","","",""},{"","",""},
		{"",""},{"",""},{"",""}};
	private ExpandableListView emachinelistview = null;	
	private ImageView ivmachineLogo=null;
	private Button btnmachineImg=null,btnmachinerunSave=null,btnmachinerunCancel=null;
	private Uri uri=null;
	private String imgDir=null;
	private Tb_vmc_machineset tb_vmc_machineset=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.parammanage);		
		this.mytabhost = super.getTabHost();//ȡ��TabHost����
        LayoutInflater.from(this).inflate(R.layout.parammanage, this.mytabhost.getTabContentView(),true);
        //����Tab�����
        TabSpec myTabmachine=this.mytabhost.newTabSpec("tab0");
        myTabmachine.setIndicator("������������");
        myTabmachine.setContent(this.layres[0]);
    	this.mytabhost.addTab(myTabmachine); 
    	
    	TabSpec myTabdevice=this.mytabhost.newTabSpec("tab1");
    	myTabdevice.setIndicator("�豸��������");
    	myTabdevice.setContent(this.layres[1]);
    	this.mytabhost.addTab(myTabdevice); 
    	
    	TabSpec myTabrun=this.mytabhost.newTabSpec("tab2");
    	myTabrun.setIndicator("���в�������");
    	myTabrun.setContent(this.layres[2]);
    	this.mytabhost.addTab(myTabrun); 
    	
    	//===========================
    	//������������
    	//===========================
    	edtdevID = (EditText) findViewById(R.id.edtdevID);
    	edtdevhCode = (EditText) findViewById(R.id.edtdevhCode);
    	switchisNet = (Switch)findViewById(R.id.switchisNet); //��ȡ���ؼ�  
    	this.grpisfenClass = (RadioGroup) super.findViewById(R.id.grpisfenClass);
	    this.hunradiobtn = (RadioButton) super.findViewById(R.id.hunradiobtn);
	    this.typeradiobtn = (RadioButton) super.findViewById(R.id.typeradiobtn);	
	    this.guiradiobtn = (RadioButton) super.findViewById(R.id.guiradiobtn);		    
	    this.grpisfenClass.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 
				if(hunradiobtn.getId()==checkedId)
				{
					isfenClass=0;
				}
				//
				else if(typeradiobtn.getId()==checkedId)
				{
					isfenClass=1;
				}
				else if(guiradiobtn.getId()==checkedId)
				{
					isfenClass=2;
				}
			}
		});
	    switchisbuyCar = (Switch)findViewById(R.id.switchisbuyCar);    	
	    this.grpliebiaoKuan = (RadioGroup) super.findViewById(R.id.grpliebiaoKuan);
	    this.smallradiobtn = (RadioButton) super.findViewById(R.id.smallradiobtn);
	    this.normalradiobtn = (RadioButton) super.findViewById(R.id.normalradiobtn);	
	    this.bigradiobtn = (RadioButton) super.findViewById(R.id.bigradiobtn);	
	    this.grpliebiaoKuan.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 
				if(smallradiobtn.getId()==checkedId)
				{
					liebiaoKuan=0;
				}
				//
				else if(normalradiobtn.getId()==checkedId)
				{
					liebiaoKuan=1;
				}
				else if(bigradiobtn.getId()==checkedId)
				{
					liebiaoKuan=2;
				}
			}
		});
	    edtmainPwd = (EditText) findViewById(R.id.edtmainPwd);
	    switchlanguage = (Switch)findViewById(R.id.switchlanguage);
    	edtrstTime = (EditText) findViewById(R.id.edtrstTime);// ��ȡʱ���ı���
    	edtrstTime.setOnClickListener(new OnClickListener() {// Ϊʱ���ı������õ��������¼�
            @Override
            public void onClick(View arg0) {
                showDialog(TIME_DIALOG_ID);// ��ʾ����ѡ��Ի���
            }
        });
    	edtrstDay = (EditText) findViewById(R.id.edtrstDay);
    	switchbaozhiProduct = (Switch)findViewById(R.id.switchbaozhiProduct);
    	switchemptyProduct = (Switch)findViewById(R.id.switchemptyProduct);
    	//����
    	this.spinparamsort = (Spinner) super.findViewById(R.id.spinparamsort); 
    	showSortAdapter=new ShowSortAdapter();    	
	    arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, showSortAdapter.getDataSortName());
	    spinparamsort.setAdapter(arrayadapter);// ΪListView�б���������Դ
    	spinparamsort.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			//��ѡ��ı�ʱ����
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				proSortType=arg2;				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		}); 
    	loadmachineparam();
    	btnmachineSave = (Button) findViewById(R.id.btnmachineSave);
    	btnmachineSave.setOnClickListener(new OnClickListener() {// Ϊ�˳���ť���ü����¼�
		    @Override
		    public void onClick(View arg0) {
		        saveparam();
		    }
		});
    	btnmachineexit = (Button) findViewById(R.id.btnmachineexit);
    	btnmachineexit.setOnClickListener(new OnClickListener() {// Ϊ�˳���ť���ü����¼�
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
    	
    	
    	
    	
    	
    	//===========================
    	//�豸��������ҳ��
    	//===========================
    	switchamount = (Switch)findViewById(R.id.switchamount);
    	switchamount.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnamount.setEnabled(isChecked);	
			}  
            
            
        }); 
    	switchcard = (Switch)findViewById(R.id.switchcard);
    	switchcard.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btncard.setEnabled(isChecked);	
			}  
            
            
        });
    	switchzhifubaofaca = (Switch)findViewById(R.id.switchzhifubaofaca);
    	switchzhifubaofaca.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnzhifubaofaca.setEnabled(isChecked);	
			}  
            
            
        });
    	switchzhifubaoer = (Switch)findViewById(R.id.switchzhifubaoer);
    	switchzhifubaoer.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnzhifubaoer.setEnabled(isChecked);	
			}  
            
            
        });
    	switchweixing = (Switch)findViewById(R.id.switchweixing);
    	switchweixing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnweixing.setEnabled(isChecked);	
			}  
            
            
        });
    	switchprinter = (Switch)findViewById(R.id.switchprinter);
    	switchprinter.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnprinter.setEnabled(isChecked);	
			}  
            
            
        });
    	    	    	
    	btndeviceSave = (Button) findViewById(R.id.btndeviceSave);
    	btndeviceSave.setOnClickListener(new OnClickListener() {// Ϊ�˳���ť���ü����¼�
		    @Override
		    public void onClick(View arg0) {
		    	saveparam();
		    }
		});
    	btndeviceexit = (Button) findViewById(R.id.btndeviceexit);
    	btndeviceexit.setOnClickListener(new OnClickListener() {// Ϊ�˳���ť���ü����¼�
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
    	btnamount = (Button) findViewById(R.id.btnamount);
    	btnamount.setOnClickListener(new OnClickListener() {// Ϊ�˳���ť���ü����¼�
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = new Intent(ParamManager.this, AddInaccount.class);// ʹ��AddInaccount���ڳ�ʼ��Intent
		        startActivity(intent);// ��AddInaccount
		    }
		});
    	
    	btncard = (Button) findViewById(R.id.btncard);
    	btnzhifubaofaca = (Button) findViewById(R.id.btnzhifubaofaca);
    	btnzhifubaofaca.setOnClickListener(new OnClickListener() {// Ϊ�˳���ť���ü����¼�
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = new Intent(ParamManager.this, ZhifubaoTest.class);// ʹ��AddInaccount���ڳ�ʼ��Intent
		    	intent.putExtra("id", edtdevhCode.getText().toString());
		    	startActivity(intent);// ��AddInaccount
		    }
		});
    	btnzhifubaoer = (Button) findViewById(R.id.btnzhifubaoer);
    	btnzhifubaoer.setOnClickListener(new OnClickListener() {// Ϊ�˳���ť���ü����¼�
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = new Intent(ParamManager.this, ZhifubaoTest.class);// ʹ��AddInaccount���ڳ�ʼ��Intent
		        intent.putExtra("id", edtdevhCode.getText().toString());
		    	startActivity(intent);// ��AddInaccount
		    }
		});
    	btnweixing = (Button) findViewById(R.id.btnweixing);
    	btnweixing.setOnClickListener(new OnClickListener() {// Ϊ�˳���ť���ü����¼�
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = new Intent(ParamManager.this, WeixingTest.class);// ʹ��AddInaccount���ڳ�ʼ��Intent
		    	intent.putExtra("id", edtdevhCode.getText().toString());
		    	startActivity(intent);// ��AddInaccount
		    }
		});
    	btnprinter = (Button) findViewById(R.id.btnprinter);
    	loaddeviceparam();
    	
    	
    	//===========================
    	//���в�������
    	//===========================
    	this.ivmachineLogo = (ImageView) findViewById(R.id.ivmachineLogo);
    	this.emachinelistview = (ExpandableListView) super.findViewById(R.id.emachinelistview);
    	loadrunparam();
    	MachineExpanseListAdapter adapter = new MachineExpanseListAdapter(this,ParamManager.runValue);
        this.emachinelistview.setAdapter(adapter);
        
        this.emachinelistview.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				//Toast.makeText(ParamManager.this, "��ѡ��,group="+groupPosition+",child="+childPosition, Toast.LENGTH_LONG).show();
				if((groupPosition==0&&childPosition==0)||(groupPosition==0&&childPosition==2)||(groupPosition==1&&childPosition==0))
				{	
					emachineListviewSet(1,groupPosition,childPosition);				
				}
				else 
				{
					emachineListviewSet(2,groupPosition,childPosition);	
				}
				return false;
			}
		});
        //ѡ��ͼƬ
        btnmachineImg = (Button)findViewById(R.id.btnmachineImg);  
        btnmachineImg.setOnClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				// TODO Auto-generated method stub
  				Intent intent = new Intent();  
                 /* ����Pictures����Type�趨Ϊimage */  
                 intent.setType("image/*");  
                 /* ʹ��Intent.ACTION_GET_CONTENT���Action */  
                 intent.setAction(Intent.ACTION_GET_CONTENT);   
                 /* ȡ����Ƭ�󷵻ر����� */  
                 startActivityForResult(intent, 1);
  			}
  		}); 
        btnmachinerunSave = (Button)findViewById(R.id.btnmachinerunSave);  
  		btnmachinerunSave.setOnClickListener(new OnClickListener() {// Ϊ���水ť���ü����¼�
		    @Override
		    public void onClick(View arg0) 
		    {
		    	saverunparam();
		    }
		});
        btnmachinerunCancel = (Button)findViewById(R.id.btnmachinerunCancel);  
        btnmachinerunCancel.setOnClickListener(new OnClickListener() {// Ϊ�˳���ť���ü����¼�
		    @Override
		    public void onClick(View arg0) 
		    {
		    	finish();
		    }
		});
        
	}
	//===========================
	//������������
	//===========================
		
	@Override
    protected Dialog onCreateDialog(int id) {// ��дonCreateDialog����

        switch (id) {
        case TIME_DIALOG_ID:// ��������ѡ��Ի���
            return new TimePickerDialog(this, mDateSetListener, mHour, mMinute, false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener mDateSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			mHour=hourOfDay;
			mMinute=minute;
			updateDisplay(); 
		}
        
    };
    private void updateDisplay() 
    {
        // ��ʾ���õ�ʱ��
    	edtrstTime.setText(new StringBuilder().append(mHour).append(":").append(mMinute));
    }
    
    private void saveparam()
    {
    	String devID = edtdevID.getText().toString();
    	String devhCode = edtdevhCode.getText().toString();
    	int isNet = (switchisNet.isChecked()==true)?1:0;
    	int isbuyCar = (switchisbuyCar.isChecked()==true)?1:0;
    	String mainPwd= edtmainPwd.getText().toString();
    	int amount = (switchamount.isChecked()==true)?1:0;
    	int card = (switchcard.isChecked()==true)?1:0;
    	int zhifubaofaca = (switchzhifubaofaca.isChecked()==true)?1:0;
    	int zhifubaoer = (switchzhifubaoer.isChecked()==true)?1:0;
    	int weixing = (switchweixing.isChecked()==true)?1:0;
    	int printer = (switchprinter.isChecked()==true)?1:0;
    	int language = (switchlanguage.isChecked()==true)?1:0;
    	String rstTime = edtrstTime.getText().toString();
    	int rstDay = 0;
    	if(edtrstDay.getText().toString().isEmpty()!=true)
    		rstDay = Integer.parseInt(edtrstDay.getText().toString());
    	int baozhiProduct = (switchbaozhiProduct.isChecked()==true)?1:0;
    	int emptyProduct = (switchemptyProduct.isChecked()==true)?1:0;
    	if ((devID.isEmpty()!=true)&&(devhCode.isEmpty()!=true)    				    			
    		)
    	{
    		try 
    		{
    			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<����devID="+devID+" devhCode="+devhCode+" isNet="
    					+isNet+" isfenClass="+isfenClass+" isbuyCar="+isbuyCar+" liebiaoKuan="+liebiaoKuan+" mainPwd="
    					+mainPwd+" amount="+amount+" card="+card+" zhifubaofaca="+zhifubaofaca+" zhifubaoer="+zhifubaoer
    					+" weixing="+weixing+" printer="+printer+" language="+language
    					+" rstTime="+rstTime+" rstDay="+rstDay+" baozhiProduct="+baozhiProduct
    					+" emptyProduct="+emptyProduct+" proSortType="+proSortType);
    			// ����InaccountDAO����
    			vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ParamManager.this);
	            //����Tb_inaccount���� 
    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(devID, devhCode, isNet,isfenClass, 
    					isbuyCar,liebiaoKuan,mainPwd,amount,card,zhifubaofaca,zhifubaoer,weixing,printer,language,rstTime,rstDay,
    					baozhiProduct,emptyProduct, proSortType);
    			parameterDAO.add(tb_vmc_system_parameter);    			
	        	// ������Ϣ��ʾ
	            Toast.makeText(ParamManager.this, "�������ӳɹ���", Toast.LENGTH_SHORT).show();	            
	            
    		} catch (Exception e)
			{
				// TODO: handle exception
				Toast.makeText(ParamManager.this, "��������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}		    		
            
        } 
        else
        {
            Toast.makeText(ParamManager.this, "����д��ɫ���֣�", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadmachineparam()
    {
    	vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ParamManager.this);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
	    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<����devID="+tb_inaccount.getDevID().toString()+" devhCode="+tb_inaccount.getDevhCode().toString()+" isNet="
					+tb_inaccount.getIsNet()+" isfenClass="+tb_inaccount.getIsfenClass()+" isbuyCar="+tb_inaccount.getIsbuyCar()+" liebiaoKuan="+tb_inaccount.getLiebiaoKuan()+" mainPwd="
					+tb_inaccount.getMainPwd()+" amount="+tb_inaccount.getAmount()+" card="+tb_inaccount.getCard()+" zhifubaofaca="+tb_inaccount.getZhifubaofaca()+" zhifubaoer="+tb_inaccount.getZhifubaoer()
					+" weixing="+tb_inaccount.getWeixing()+" printer="+tb_inaccount.getPrinter()+" language="+tb_inaccount.getLanguage()
					+" rstTime="+tb_inaccount.getRstTime().toString()+" rstDay="+tb_inaccount.getRstDay()+" baozhiProduct="+tb_inaccount.getBaozhiProduct()
					+" emptyProduct="+tb_inaccount.getEmptyProduct()+" proSortType="+tb_inaccount.getProSortType()); 
		    edtdevID.setText(tb_inaccount.getDevID().toString());
		    edtdevhCode.setText(tb_inaccount.getDevhCode().toString());	  
		    //switchisNet.setChecked(true);
		    switchisNet.setChecked((tb_inaccount.getIsNet()==1)?true:false);
		    switch(tb_inaccount.getIsfenClass())
		    {
		    	case 0:
		    		hunradiobtn.setChecked(true);				
		    		break;
		    	case 1:
		    		typeradiobtn.setChecked(true);				
		    		break;
		    	case 2:
		    		guiradiobtn.setChecked(true);
		    		break;
		    }
		    switchisbuyCar.setChecked((tb_inaccount.getIsbuyCar()==1)?true:false);
		    switch(tb_inaccount.getLiebiaoKuan())
		    {
		    	case 0:
		    		smallradiobtn.setChecked(true);						
		    		break;
		    	case 1:
		    		normalradiobtn.setChecked(true);										
		    		break;
		    	case 2:
		    		bigradiobtn.setChecked(true);		
		    		break;
		    }
		    edtmainPwd.setText(tb_inaccount.getMainPwd().toString());
		    switchlanguage.setChecked((tb_inaccount.getLanguage()==1)?true:false);
		    edtrstTime.setText(tb_inaccount.getRstTime().toString());
		    edtrstDay.setText(String.valueOf(tb_inaccount.getRstDay()));
		    switchbaozhiProduct.setChecked((tb_inaccount.getBaozhiProduct()==1)?true:false);
		    switchemptyProduct.setChecked((tb_inaccount.getEmptyProduct()==1)?true:false);
		    //����������Ĭ��ֵ
		    spinparamsort.setSelection(tb_inaccount.getProSortType());
    	}
	}
    
    
    //===========================
	//�豸��������ҳ��
	//===========================
    private void loaddeviceparam()
    {
    	vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ParamManager.this);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
	    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<����devID="+tb_inaccount.getDevID().toString()+" devhCode="+tb_inaccount.getDevhCode().toString()+" isNet="
					+tb_inaccount.getIsNet()+" isfenClass="+tb_inaccount.getIsfenClass()+" isbuyCar="+tb_inaccount.getIsbuyCar()+" liebiaoKuan="+tb_inaccount.getLiebiaoKuan()+" mainPwd="
					+tb_inaccount.getMainPwd()+" amount="+tb_inaccount.getAmount()+" card="+tb_inaccount.getCard()+" zhifubaofaca="+tb_inaccount.getZhifubaofaca()+" zhifubaoer="+tb_inaccount.getZhifubaoer()
					+" weixing="+tb_inaccount.getWeixing()+" printer="+tb_inaccount.getPrinter()+" language="+tb_inaccount.getLanguage()
					+" rstTime="+tb_inaccount.getRstTime().toString()+" rstDay="+tb_inaccount.getRstDay()+" baozhiProduct="+tb_inaccount.getBaozhiProduct()
					+" emptyProduct="+tb_inaccount.getEmptyProduct()+" proSortType="+tb_inaccount.getProSortType()); 
		    
		    switchamount.setChecked((tb_inaccount.getAmount()==1)?true:false);
		    switchcard.setChecked((tb_inaccount.getCard()==1)?true:false);
		    switchzhifubaofaca.setChecked((tb_inaccount.getZhifubaofaca()==1)?true:false);
		    switchzhifubaoer.setChecked((tb_inaccount.getZhifubaoer()==1)?true:false);
		    switchweixing.setChecked((tb_inaccount.getWeixing()==1)?true:false);
		    switchprinter.setChecked((tb_inaccount.getPrinter()==1)?true:false);
    	}
	}
    
    //===========================
  	//���в�������ҳ��
  	//===========================
    @Override  
	//ѡȡͼƬ����ֵ
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if (resultCode == RESULT_OK) {  
	         uri = data.getData();  
	         ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<uri="+ uri.toString());  
	         ContentResolver cr = this.getContentResolver();  
	         try {  
	             Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));  
	             /* ��Bitmap�趨��ImageView */  
	             ivmachineLogo.setImageBitmap(bitmap);  
	             imgDir=ToolClass.getRealFilePath(ParamManager.this,uri);
	         } catch (FileNotFoundException e) {  
	             Log.e("Exception", e.getMessage(),e);  
	         }  
	     }  
    	super.onActivityResult(requestCode, resultCode, data);  
    }
    //�������в�������
    private void loadrunparam()
    {
    	vmc_machinesetDAO machinesetDAO = new vmc_machinesetDAO(ParamManager.this);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	tb_vmc_machineset = machinesetDAO.find();
    	
    	if(tb_vmc_machineset!=null)
    	{
//	    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<����devID="+tb_inaccount.getDevID().toString()+" devhCode="+tb_inaccount.getDevhCode().toString()+" isNet="
//					+tb_inaccount.getIsNet()+" isfenClass="+tb_inaccount.getIsfenClass()+" isbuyCar="+tb_inaccount.getIsbuyCar()+" liebiaoKuan="+tb_inaccount.getLiebiaoKuan()+" mainPwd="
//					+tb_inaccount.getMainPwd()+" amount="+tb_inaccount.getAmount()+" card="+tb_inaccount.getCard()+" zhifubaofaca="+tb_inaccount.getZhifubaofaca()+" zhifubaoer="+tb_inaccount.getZhifubaoer()
//					+" weixing="+tb_inaccount.getWeixing()+" printer="+tb_inaccount.getPrinter()+" language="+tb_inaccount.getLanguage()
//					+" rstTime="+tb_inaccount.getRstTime().toString()+" rstDay="+tb_inaccount.getRstDay()+" baozhiProduct="+tb_inaccount.getBaozhiProduct()
//					+" emptyProduct="+tb_inaccount.getEmptyProduct()+" proSortType="+tb_inaccount.getProSortType()); 
    		imgDir=tb_vmc_machineset.getLogoStr();
    		if(imgDir!=null)
    		{
	    		/*ΪʲôͼƬһ��Ҫת��Ϊ Bitmap��ʽ�ģ��� */
		        Bitmap bitmap = ToolClass.getLoacalBitmap(imgDir); //�ӱ���ȡͼƬ(��cdcard�л�ȡ)  //
		        ivmachineLogo.setImageBitmap(bitmap);// ����ͼ��Ķ�����ֵ
    		}
    		ParamManager.runValue[0][0]=String.valueOf(tb_vmc_machineset.getAudioWork());
    		ParamManager.runValue[0][1]="��ʼʱ��:"+tb_vmc_machineset.getAudioWorkstart()+"  ����ʱ��:"+tb_vmc_machineset.getAudioWorkend();
    		ParamManager.runValue[0][2]=String.valueOf(tb_vmc_machineset.getAudioSun());
    		ParamManager.runValue[0][3]="��ʼʱ��:"+tb_vmc_machineset.getAudioSunstart()+"  ����ʱ��:"+tb_vmc_machineset.getAudioSunend();
    		
    		ParamManager.runValue[1][0]=String.valueOf(tb_vmc_machineset.getTempWork());
    		ParamManager.runValue[1][1]="��ʼʱ��:"+tb_vmc_machineset.getTempWorkstart()+"  ����ʱ��:"+tb_vmc_machineset.getTempWorkend();
    		ParamManager.runValue[1][2]="��ʼʱ��:"+tb_vmc_machineset.getTempSunstart()+"  ����ʱ��:"+tb_vmc_machineset.getTempSunend();
    		
    		ParamManager.runValue[2][0]="��ʼʱ��:"+tb_vmc_machineset.getLigntWorkstart()+"  ����ʱ��:"+tb_vmc_machineset.getLigntWorkend();
    		ParamManager.runValue[2][1]="��ʼʱ��:"+tb_vmc_machineset.getLigntSunstart()+"  ����ʱ��:"+tb_vmc_machineset.getLigntSunend();
    		
    		ParamManager.runValue[3][0]="��ʼʱ��:"+tb_vmc_machineset.getColdWorkstart()+"  ����ʱ��:"+tb_vmc_machineset.getColdWorkend();
    		ParamManager.runValue[3][1]="��ʼʱ��:"+tb_vmc_machineset.getColdSunstart()+"  ����ʱ��:"+tb_vmc_machineset.getColdSunend();
    		
    		ParamManager.runValue[4][0]="��ʼʱ��:"+tb_vmc_machineset.getChouWorkstart()+"  ����ʱ��:"+tb_vmc_machineset.getChouWorkend();
    		ParamManager.runValue[4][1]="��ʼʱ��:"+tb_vmc_machineset.getChouSunstart()+"  ����ʱ��:"+tb_vmc_machineset.getChouSunend();
    	}
	}
    //dialogtype==1������ֵѡ���,2����ʱ��ѡ���
    private void emachineListviewSet(int dialogtype,final int groupPosition,final int childPosition)
    {    	
    	View myview=null;    	
		// TODO Auto-generated method stub
		LayoutInflater factory = LayoutInflater.from(ParamManager.this);
		if(dialogtype==1)//��ֵѡ���
		{
			myview=factory.inflate(R.layout.selectinteger, null);
			final EditText dialoginte=(EditText) myview.findViewById(R.id.dialoginte);
			
			Dialog dialog = new AlertDialog.Builder(ParamManager.this)
			.setTitle("����")
			.setPositiveButton("����", new DialogInterface.OnClickListener() 	
			{
					
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					//Toast.makeText(ParamManager.this, "��ֵ="+dialoginte.getText().toString(), Toast.LENGTH_LONG).show();
					updaterunparam(groupPosition,childPosition,Integer.parseInt(dialoginte.getText().toString()),"","");
				}
			})
			.setNegativeButton("ȡ��",  new DialogInterface.OnClickListener()//ȡ����ť���������ü����¼�
	    	{			
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub				
				}
	    	})
			.setView(myview)//���ｫ�Ի��򲼾��ļ����뵽�Ի�����
			.create();
			dialog.show();			
		}
		else if(dialogtype==2)//ʱ��ѡ���
		{
			myview=factory.inflate(R.layout.selecttimepick, null);
			final TimePicker diaStartTime=(TimePicker) myview.findViewById(R.id.diaStartTime);
			final TimePicker diaEndTime=(TimePicker) myview.findViewById(R.id.diaEndTime);
			
			Dialog dialog = new AlertDialog.Builder(ParamManager.this)
			.setTitle("����")
			.setPositiveButton("����", new DialogInterface.OnClickListener() 	
			{
					
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub					
					//Toast.makeText(ParamManager.this, "��ʼʱ��="+diaStartTime.getCurrentHour().toString()+":"+diaStartTime.getCurrentMinute().toString()
					//		+"����ʱ��="+diaEndTime.getCurrentHour().toString()+":"+diaEndTime.getCurrentMinute().toString(), Toast.LENGTH_LONG).show();
					updaterunparam(groupPosition,childPosition,0,diaStartTime.getCurrentHour().toString()+":"+diaStartTime.getCurrentMinute().toString(),diaEndTime.getCurrentHour().toString()+":"+diaEndTime.getCurrentMinute().toString());
				}
			})
			.setNegativeButton("ȡ��",  new DialogInterface.OnClickListener()//ȡ����ť���������ü����¼�
	    	{			
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub				
				}
	    	})
			.setView(myview)//���ｫ�Ի��򲼾��ļ����뵽�Ի�����
			.create();
			dialog.show();	
		}
			
			 	
    }
    //�������в�������
    private void updaterunparam(int groupPosition,int childPosition,int value,String StartTime,String EndTime)
    {
    	
    	if(groupPosition==0)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setAudioWork(value);
    			ParamManager.runValue[groupPosition][childPosition]=String.valueOf(value);
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setAudioWorkstart(StartTime);
    			tb_vmc_machineset.setAudioWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    		else if(childPosition==2)
    		{
    			tb_vmc_machineset.setAudioSun(value);
    			ParamManager.runValue[groupPosition][childPosition]=String.valueOf(value);
    		}
    		else if(childPosition==3)
    		{
    			tb_vmc_machineset.setAudioSunstart(StartTime);
    			tb_vmc_machineset.setAudioSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    	}
    	else if(groupPosition==1)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setTempWork(value);
    			ParamManager.runValue[groupPosition][childPosition]=String.valueOf(value);
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setTempWorkstart(StartTime);
    			tb_vmc_machineset.setTempWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    		else if(childPosition==2)
    		{
    			tb_vmc_machineset.setTempSunstart(StartTime);
    			tb_vmc_machineset.setTempSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    	}
    	else if(groupPosition==2)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setLigntWorkstart(StartTime);
    			tb_vmc_machineset.setLigntWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setLigntSunstart(StartTime);
    			tb_vmc_machineset.setLigntSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    	}
    	else if(groupPosition==3)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setColdWorkstart(StartTime);
    			tb_vmc_machineset.setColdWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setColdSunstart(StartTime);
    			tb_vmc_machineset.setColdSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    	}
    	else if(groupPosition==4)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setChouWorkstart(StartTime);
    			tb_vmc_machineset.setChouWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setChouSunstart(StartTime);
    			tb_vmc_machineset.setChouSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]="��ʼʱ��:"+StartTime+"  ����ʱ��:"+EndTime;
    		}
    	}  
    	MachineExpanseListAdapter adapter = new MachineExpanseListAdapter(this,ParamManager.runValue);
        this.emachinelistview.setAdapter(adapter);
	}
    //�������в�������
    private void saverunparam()
    {
    	vmc_machinesetDAO machinesetDAO = new vmc_machinesetDAO(ParamManager.this);// ����InaccountDAO����
    	tb_vmc_machineset.setLogoStr(imgDir);
    	try {			
	    	machinesetDAO.add(tb_vmc_machineset);    
	    	// ������Ϣ��ʾ
	        Toast.makeText(ParamManager.this, "���ݸ��³ɹ���", Toast.LENGTH_SHORT).show();	            
        
		} catch (Exception e)
		{
			// TODO: handle exception
			Toast.makeText(ParamManager.this, "��������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
		}	    		    	
	}
    @Override
	protected void onDestroy() {
    	//�˳�ʱ������intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}
}

