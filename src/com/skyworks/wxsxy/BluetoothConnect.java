package com.skyworks.wxsxy;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class BluetoothConnect extends Activity {
	
    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";  
    Button btnSearch, btnExit,btnSwitch;  
    //ToggleButton tbtnSwitch;  
    ListView lvBTDevices;  
    ArrayAdapter<String> adtDevices;  
    List<String> lstDevices = new ArrayList<String>();  
    BluetoothAdapter btAdapt;  
    public static BluetoothSocket btSocket;  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_bluetoothconnect);  
        // Button 设置  
        btnSearch = (Button) this.findViewById(R.id.btnSearch);  
        btnSearch.setOnClickListener(new ClickEvent());  
        btnExit = (Button) this.findViewById(R.id.btnExit);  
        btnExit.setOnClickListener(new ClickEvent());  
        btnSwitch=(Button)this.findViewById(R.id.btnSwitch);
        btnSwitch.setOnClickListener(new ClickEvent()); 
  
//        // ListView及其数据源 适配器  
        lvBTDevices = (ListView) this.findViewById(R.id.lvDevices);  
        adtDevices = new ArrayAdapter<String>(this,  
                android.R.layout.simple_list_item_1, lstDevices);  
        lvBTDevices.setAdapter(adtDevices);  
        lvBTDevices.setOnItemClickListener(new ItemClickEvent());  
  
       btAdapt = BluetoothAdapter.getDefaultAdapter();// 初始化本机蓝牙功能  
//  
//        // ========================================================  
//        // modified by hg 
//        /* 
//         * if (btAdapt.getState() == BluetoothAdapter.STATE_OFF)// 读取蓝牙状态并显示 
//         * tbtnSwitch.setChecked(false); else if (btAdapt.getState() == 
//         * BluetoothAdapter.STATE_ON) tbtnSwitch.setChecked(true); 
//         */  
       if (btAdapt.isEnabled()) {  
            btnSwitch.setText("关闭蓝牙");
        } else {
            btnSwitch.setText("打开蓝牙");
        }  
        // ============================================================  
//        // 注册Receiver来获取蓝牙设备相关的结果  
        IntentFilter intent = new IntentFilter();  
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果  
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);  
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);  
        registerReceiver(searchDevices, intent);  
    }  
  
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {  
  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            Bundle b = intent.getExtras();  
            Object[] lstName = b.keySet().toArray();  
  
            // 显示所有收到的消息及其细节  
            for (int i = 0; i < lstName.length; i++) {  
                String keyName = lstName[i].toString();  
                Log.e(keyName, String.valueOf(b.get(keyName)));  
            }  
            BluetoothDevice device = null;  
            // 搜索设备时，取得设备的MAC地址  
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {  
                device = intent  
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {  
                    String str = "未配对|" + device.getName() + "|"  
                            + device.getAddress();  
                    if (lstDevices.indexOf(str) == -1)// 防止重复添加  
                        lstDevices.add(str); // 获取设备名称和mac地址  
                    adtDevices.notifyDataSetChanged();  
                }  
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){  
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                switch (device.getBondState()) {  
                case BluetoothDevice.BOND_BONDING:  
                    Log.d("BlueToothTestActivity", "正在配对......");  
                    break;  
                case BluetoothDevice.BOND_BONDED:  
                    Log.d("BlueToothTestActivity", "完成配对");  
                    startBloothcarActivity(device);
                    break;  
                case BluetoothDevice.BOND_NONE:  
                    Log.d("BlueToothTestActivity", "取消配对");  
                default:  
                    break;  
                }  
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            	//打开蓝牙或者关闭蓝牙的瞬间，将开关置为不可用。
				if(!btnSwitch.isEnabled())btnSwitch.setEnabled(true); 
			}
              
        }  
    };  
  
    @Override  
    protected void onDestroy() {  
        this.unregisterReceiver(searchDevices);  
        super.onDestroy();  
        android.os.Process.killProcess(android.os.Process.myPid());  
    }  
  
    class ItemClickEvent implements AdapterView.OnItemClickListener {  

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                long arg3) {  
            if(btAdapt.isDiscovering())btAdapt.cancelDiscovery();  
            String str = lstDevices.get(arg2);  
            String[] values = str.split("\\|");  
            String address = values[2];  
            Log.e("address", values[2]);  
            BluetoothDevice btDev = btAdapt.getRemoteDevice(address);  
            try {  
                Boolean returnValue = false;  
                if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {  
                    //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);  
                    Method createBondMethod = BluetoothDevice.class  
                            .getMethod("createBond");  
                    Log.d("BlueToothTestActivity", "开始配对");  
                    returnValue = (Boolean) createBondMethod.invoke(btDev);  
                      
                }else if(btDev.getBondState() == BluetoothDevice.BOND_BONDED){  
                	startBloothcarActivity(btDev);  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
  
        }  
  
    }  
    /**
     * ==========启动小车控制界面===========
     * 将BluetoothDevice传给BloothcarActivity
     */
    private void startBloothcarActivity(BluetoothDevice btDev) {  
        Intent intent = new Intent();
        intent.setClass(BluetoothConnect.this,Control.class);
        intent.putExtra("BluetoothDevice", btDev);
        intent.putExtra("UUID", SPP_UUID);
        startActivity(intent);
        //finish();
    }  
  
  
    class ClickEvent implements View.OnClickListener {  
     
        public void onClick(View v) {  
            if (v == btnSearch)// 搜索蓝牙设备，在BroadcastReceiver显示结果  
            {  
                if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启  
                    Toast.makeText(BluetoothConnect.this, "请先打开蓝牙", 1000)  
                            .show();  
                    return;  
                }  
                if (btAdapt.isDiscovering())  
                    btAdapt.cancelDiscovery();  
                lstDevices.clear();  
                Object[] lstDevice = btAdapt.getBondedDevices().toArray();  
                for (int i = 0; i < lstDevice.length; i++) {  
                    BluetoothDevice device = (BluetoothDevice) lstDevice[i];  
                    String str = "已配对|" + device.getName() + "|"  
                            + device.getAddress();  
                    lstDevices.add(str); // 获取设备名称和mac地址  
                    adtDevices.notifyDataSetChanged();  
                }  
                setTitle("本机蓝牙地址：" + btAdapt.getAddress());  
                btAdapt.startDiscovery();  
            } else if (v == btnSwitch) {// 本机蓝牙启动/关闭  
                if (btnSwitch.getText().toString().equals("打开蓝牙")){
                	btnSwitch.setText("关闭蓝牙");
//                	btnSwitch.setEnabled(false);
                    btAdapt.enable();  
                    
                }
                else if (btnSwitch.getText().toString().equals("关闭蓝牙")) { 
                	btnSwitch.setText("打开蓝牙");
//                	btnSwitch.setEnabled(false);
                    btAdapt.disable();  
                    
                }
            }  else if (v == btnExit) { //退出按钮
                try {  
                    if (btSocket != null)  
                        btSocket.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
                finish();  
            }  
        }  
    }  
}  