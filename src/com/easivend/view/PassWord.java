package com.easivend.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PassWord extends Activity 
{
	private BluetoothAdapter blueadapter=null;  
    private DeviceReceiver mydevice=new DeviceReceiver();  
    private List<String> deviceList=new ArrayList<String>();  
    private ListView deviceListview;  
    private Button btserch,btn_msg_send,btn_end; 
    private EditText editMsgView;
    private ArrayAdapter<String> adapter;  
    private boolean hasregister=false; 
    private BluetoothServerSocket mserverSocket = null;  
    private ServerThread startServerThread = null; 
    private readThread mreadThread = null; 
    private BluetoothSocket socket = null;  
    /* 一些常量，代表服务器的名称 */  
    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp"; 
    private boolean isfile=false;
    ArrayList<Uri> uris = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password);// 设置布局文件
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		
		setView();//初始化控件  
		setBluetooth(); //1.打开蓝牙设备
	}
	
	private void setView()
	{  
		editMsgView=(EditText)findViewById(R.id.editMsgView);  
        deviceListview=(ListView)findViewById(R.id.devicelist);  
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);  
        deviceListview.setAdapter(adapter);          
        btserch=(Button)findViewById(R.id.start_seach);  
        btserch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(blueadapter.isDiscovering()){  
//	                blueadapter.cancelDiscovery();  
//	                btserch.setText("扫一扫1");  
	            }else{  
	                findAvalibleDevice(); //遍历已经存在的设备 
	                blueadapter.startDiscovery();//2.搜索蓝牙设备
	                btserch.setText("正在扫描，请稍后");  
	            }  
			}
		}); 
        //单击选中单个运行日志文件蓝牙上传
        deviceListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				   
		            final String msg = deviceList.get(pos);  
		              
		            if(blueadapter!=null&&blueadapter.isDiscovering())
		            {  
//		                blueadapter.cancelDiscovery();  
//		                btserch.setText("扫一扫2");  
		            }  
		            else 
		            {
		            	File f = new File(msg);
				          //调用android分享窗口
				          Intent intent = new Intent();
				          intent.setAction(Intent.ACTION_SEND);
				          intent.setType("application/octet-stream");
				          intent.setClassName("com.android.bluetooth" , "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
				      	  intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));				          
				          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				          startActivity(intent);
				          isfile=true;
					}	              
		          
			}
		});
        //确认并且退出
        btn_msg_send = (Button) findViewById(R.id.btn_msg_send);
        btn_msg_send.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
  		    @Override
  		    public void onClick(View arg0) {
  		        //退出时，返回intent
	            Intent intent=new Intent();
	            intent.putExtra("pwd", editMsgView.getText().toString());
	            setResult(PassWord.RESULT_OK,intent);
  		    	finish();
  		    }
  		}); 
    } 
	/** 
     * Setting Up Bluetooth 
     * //1.打开蓝牙设备
     */  
    private void setBluetooth(){  
         blueadapter=BluetoothAdapter.getDefaultAdapter();  
           
            if(blueadapter!=null){  //Device support Bluetooth  
                //确认开启蓝牙  
                if(!blueadapter.isEnabled()){  
                    //请求用户开启  
                    Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
                    startActivityForResult(intent, RESULT_FIRST_USER);  
                    //使蓝牙设备可见，方便配对  （默认打开120秒，可以将时间延长至最多300秒）
                    Intent in=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);  
                    //0代表永久可见，数值代表可见时间，以s为单位
                    in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);  
                    startActivity(in);  
                    //直接开启，不经过提示  
                    blueadapter.enable(); 
                    //显示本机蓝牙地址
                    deviceList.add("本机"+blueadapter.getName()+'\n'+blueadapter.getAddress()); 
                }  
            }  
            else{   //Device does not support Bluetooth  
                  
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);  
                dialog.setTitle("没有蓝牙设备");  
                dialog.setMessage("你的设备不支持蓝牙");  
                  
                dialog.setNegativeButton("cancel",  
                        new DialogInterface.OnClickListener() {  
                            @Override  
                            public void onClick(DialogInterface dialog, int which) {  
                                  
                            }  
                        });  
                dialog.show();  
            }  
    }
    
  
    /** 
     * Finding Devices 
     */  
    private void findAvalibleDevice(){  
        //获取可配对蓝牙设备  
        Set<BluetoothDevice> device=blueadapter.getBondedDevices();  
          
        if(blueadapter!=null&&blueadapter.isDiscovering()){  
            deviceList.clear();  
            adapter.notifyDataSetChanged();  
        }  
        if(device.size()>0){ //存在已经配对过的蓝牙设备,遍历显示  
            for(Iterator<BluetoothDevice> it=device.iterator();it.hasNext();){  
                BluetoothDevice btd=it.next();  
                deviceList.add(btd.getName()+'\n'+btd.getAddress());  
                adapter.notifyDataSetChanged();  
            }  
        }else{  //不存在已经配对过的蓝牙设备,给出提示  
            deviceList.add("No can be matched to use bluetooth");  
            adapter.notifyDataSetChanged();  
        }  
    } 
    
    /** 
     * 4.蓝牙搜索状态广播监听 
     * @author Andy 
     * 
     */  
    private class DeviceReceiver extends BroadcastReceiver{  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action =intent.getAction();  
            if(BluetoothDevice.ACTION_FOUND.equals(action)){    //搜索到新设备  
                BluetoothDevice btd=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                //搜索没有配过对的蓝牙设备  
                 if (btd.getBondState() != BluetoothDevice.BOND_BONDED) {  
                     deviceList.add(btd.getName()+'\n'+btd.getAddress());  
                     adapter.notifyDataSetChanged();  
                 }  
            }  
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {   //搜索结束  
                   if(isfile==false)
                   {
	                    if (deviceListview.getCount() == 0) {  
	                        deviceList.add("No can be matched to use bluetooth");  
	                        adapter.notifyDataSetChanged();  
	                    }  
	                    btserch.setText("扫一扫3");  
	                    startServerThread = new ServerThread();  
	                    startServerThread.start();  
	                    BluetoothMsg.isOpen = true; 
                   }
            }             
        }     
    } 
    //3.注册蓝牙接收广播  
	/*startDiscovery()方法是一个异步方法，调用后会立即返回。该方法会进行对其他蓝牙设备的搜索，该过程会持续12秒。该方法调用后，搜索过程实际上是在一个System Service中进行的，所以可以调用cancelDiscovery()方法来停止搜索（该方法可以在未执行discovery请求时调用）。
	请求Discovery后，系统开始搜索蓝牙设备，在这个过程中，系统会发送以下三个广播：
    ACTION_DISCOVERY_START：开始搜索
	ACTION_DISCOVERY_FINISHED：搜索结束
	ACTION_FOUND：找到设备，这个Intent中包含两个extra fields：EXTRA_DEVICE和EXTRA_CLASS，分别包含BluetooDevice和BluetoothClass。
	我们可以自己注册相应的BroadcastReceiver来接收响应的广播，以便实现某些功能
	*/
    @Override  
    protected void onStart() 
    { 
        if(!hasregister)
        {  
            hasregister=true;  
            IntentFilter filterStart=new IntentFilter(BluetoothDevice.ACTION_FOUND);      
            IntentFilter filterEnd=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  
            registerReceiver(mydevice, filterStart);  
            registerReceiver(mydevice, filterEnd);  
        }         
        super.onStart();  
    }  
    
    //主ui显示分线程发送来的提示信息
  	private Handler LinkDetectedHandler = new Handler() {  
          @Override  
          public void handleMessage(Message msg) {  
              //Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();  
              if(((String)msg.obj).equals("getfile"))  
              {  
            	  deviceList.add("准备发送文件...");  
            	  listFiles(); 
              }  
              else if(((String)msg.obj).equals("getfilezip"))  
              {  
            	  deviceList.add("准备发送压缩包...");  
            	  zipFiles();
              }
              else  
              {  
                  deviceList.add((String)msg.obj);                    
              }  
              adapter.notifyDataSetChanged();  
              deviceListview.setSelection(deviceList.size() - 1);  
          }  
      };  
  	  //5.开启读取通道服务器  
      private class ServerThread extends Thread {   
          @Override  
          public void run() {  
                        
              try {  
                  /* 创建一个蓝牙服务器  
                   * 参数分别：服务器名称、UUID  
                   * 首先通过调用listenUsingRfcommWithServiceRecord(String, UUID)方法来获
                   * 取bluetoothserversocket对象，参数string代表了该服务的名称，UUID代
                   * 表了和客户端连接的一个标识（128位格式的字符串ID，相当于pin码），
                   * UUID必须双方匹配才可以建立连接。其次调用accept（）方法来监听可能
                   * 到来的连接请求，当监听到以后，返回一个连接上的蓝牙套接
                   * 字bluetoothsocket。 */   
                  mserverSocket = blueadapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,  
                          UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));         
                    
                  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<wait cilent connect...","log.txt");  
                  Message msg = new Message();  
                  msg.obj = "请稍候，正在等待客户端的连接...";  
                  msg.what = 0;  
                  LinkDetectedHandler.sendMessage(msg);  
                    
                  /* 接受客户端的连接请求 */  
                  socket = mserverSocket.accept();  
                  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<accept success !","log.txt");  
                  Message msg2 = new Message();  
                  String info = "客户端已经连接上！可以发送信息。";  
                  msg2.obj = info;  
                  msg.what = 0;  
                  LinkDetectedHandler.sendMessage(msg2);  
                  //启动接受数据  
                  mreadThread = new readThread();  
                  mreadThread.start();  
              } catch (IOException e) {  
                  e.printStackTrace();  
              }  
          }  
      };  
      /* 7.停止读取通道服务器 */  
      private void shutdownServer() {  
          new Thread() {  
              @Override  
              public void run() {  
                  if(startServerThread != null)  
                  {  
                      startServerThread.interrupt();  
                      startServerThread = null;  
                  }  
                  if(mreadThread != null)  
                  {  
                      mreadThread.interrupt();  
                      mreadThread = null;  
                  }                 
                  try
                  {                     
                      if(socket != null)  
                      {  
                          socket.close();  
                          socket = null;  
                      }  
                      if (mserverSocket != null)  
                      {  
                          mserverSocket.close();/* 关闭服务器 */  
                          mserverSocket = null;  
                      }  
                  } catch (IOException e) {  
                      ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<mserverSocket.close()","log.txt"); 
                  }  
              };  
          }.start();  
      }
      //6.开启读取数据线程  
      private class readThread extends Thread {   
          @Override  
          public void run() {  
                
              byte[] buffer = new byte[1024];  
              int bytes;  
              InputStream mmInStream = null;  
                
              try {  
                  mmInStream = socket.getInputStream();  
              } catch (IOException e1) {  
                  // TODO Auto-generated catch block  
                  e1.printStackTrace();  
              }     
              while (true) {  
                  try {  
                      // Read from the InputStream  
                      if( (bytes = mmInStream.read(buffer)) > 0 )  
                      {  
                          byte[] buf_data = new byte[bytes];  
                          for(int i=0; i<bytes; i++)  
                          {  
                              buf_data[i] = buffer[i];  
                          }  
                          String s = new String(buf_data); 
                          if(s.equals("getfile")||s.equals("getfilezip"))  
                          {  
	                            Message msg = new Message();  
	                            msg.obj = s;  
	                            msg.what = 1;  
	                            LinkDetectedHandler.sendMessage(msg);
                          }
                          else 
                          {
                        	  //退出时，返回intent
              	              Intent intent=new Intent();
              	              intent.putExtra("pwd", s);
              	              setResult(PassWord.RESULT_OK,intent);    		      
              	              finish();
						  }                          
                      }  
                  } catch (IOException e) {  
                      try {  
                          mmInStream.close();  
                      } catch (IOException e1) {  
                          // TODO Auto-generated catch block  
                          e1.printStackTrace();  
                      }  
                      break;  
                  }  
              }  
          }  
      } 
         
    //8.结束页面
    @Override  
    protected void onDestroy() 
    {  
    	shutdownServer();//结束服务线程
    	//解除监听绑定
        if(blueadapter!=null&&blueadapter.isDiscovering())
        {  
            blueadapter.cancelDiscovery();  
        }  
        if(hasregister)
        {  
            hasregister=false;  
            unregisterReceiver(mydevice);  
        }  
        //直接关闭蓝牙设备
        blueadapter.disable();  
        super.onDestroy();  
    } 
   
    /* 蓝牙传送全部运行日志文件 */  
    private void listFiles() 
    {  
    	//遍历这个文件夹里的所有文件
		File file = new File(ToolClass.ReadLogFile());
		File[] files = file.listFiles();
		if (files.length > 0) 
		{  
			uris=new ArrayList<Uri>();
			for (int i = 0; i < files.length; i++) 
			{
			  if(!files[i].isDirectory())
			  {		
				  deviceList.add(files[i].toString());				  	
				  uris.add(Uri.fromFile(new File(files[i].toString())));
			  }
			}
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND_MULTIPLE);
			intent.setType("application/octet-stream");
			intent.setClassName("com.android.bluetooth" , "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
			intent.putExtra(Intent.EXTRA_STREAM, uris);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			isfile=true;
		}
		
		
    }
    
    /* 蓝牙传送全部运行日志的压缩文件 */  
    private void zipFiles() 
    {  
    	//遍历这个文件夹里的所有文件
		String srcFileString=ToolClass.ReadLogFile();
		String zipFileString=ToolClass.getEV_DIR()+File.separator+"logzip.zip";
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<srcFileString="+srcFileString+" zipFileString="+zipFileString,"log.txt"); 
		try {
			XZip.ZipFolder(srcFileString, zipFileString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File f = new File(zipFileString);
        //调用android分享窗口
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("application/octet-stream");
        intent.setClassName("com.android.bluetooth" , "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
    	intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));				          
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        isfile=true;
		
    }
	
}
