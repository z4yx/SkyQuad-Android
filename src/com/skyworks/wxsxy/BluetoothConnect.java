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
        // Button ����  
        btnSearch = (Button) this.findViewById(R.id.btnSearch);  
        btnSearch.setOnClickListener(new ClickEvent());  
        btnExit = (Button) this.findViewById(R.id.btnExit);  
        btnExit.setOnClickListener(new ClickEvent());  
        btnSwitch=(Button)this.findViewById(R.id.btnSwitch);
        btnSwitch.setOnClickListener(new ClickEvent()); 
  
//        // ListView��������Դ ������  
        lvBTDevices = (ListView) this.findViewById(R.id.lvDevices);  
        adtDevices = new ArrayAdapter<String>(this,  
                android.R.layout.simple_list_item_1, lstDevices);  
        lvBTDevices.setAdapter(adtDevices);  
        lvBTDevices.setOnItemClickListener(new ItemClickEvent());  
  
       btAdapt = BluetoothAdapter.getDefaultAdapter();// ��ʼ��������������  
//  
//        // ========================================================  
//        // modified by hg 
//        /* 
//         * if (btAdapt.getState() == BluetoothAdapter.STATE_OFF)// ��ȡ����״̬����ʾ 
//         * tbtnSwitch.setChecked(false); else if (btAdapt.getState() == 
//         * BluetoothAdapter.STATE_ON) tbtnSwitch.setChecked(true); 
//         */  
       if (btAdapt.isEnabled()) {  
            btnSwitch.setText("�ر�����");
        } else {
            btnSwitch.setText("������");
        }  
        // ============================================================  
//        // ע��Receiver����ȡ�����豸��صĽ��  
        IntentFilter intent = new IntentFilter();  
        intent.addAction(BluetoothDevice.ACTION_FOUND);// ��BroadcastReceiver��ȡ���������  
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
  
            // ��ʾ�����յ�����Ϣ����ϸ��  
            for (int i = 0; i < lstName.length; i++) {  
                String keyName = lstName[i].toString();  
                Log.e(keyName, String.valueOf(b.get(keyName)));  
            }  
            BluetoothDevice device = null;  
            // �����豸ʱ��ȡ���豸��MAC��ַ  
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {  
                device = intent  
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {  
                    String str = "δ���|" + device.getName() + "|"  
                            + device.getAddress();  
                    if (lstDevices.indexOf(str) == -1)// ��ֹ�ظ����  
                        lstDevices.add(str); // ��ȡ�豸���ƺ�mac��ַ  
                    adtDevices.notifyDataSetChanged();  
                }  
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){  
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                switch (device.getBondState()) {  
                case BluetoothDevice.BOND_BONDING:  
                    Log.d("BlueToothTestActivity", "�������......");  
                    break;  
                case BluetoothDevice.BOND_BONDED:  
                    Log.d("BlueToothTestActivity", "������");  
                    startBloothcarActivity(device);
                    break;  
                case BluetoothDevice.BOND_NONE:  
                    Log.d("BlueToothTestActivity", "ȡ�����");  
                default:  
                    break;  
                }  
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            	//���������߹ر�������˲�䣬��������Ϊ�����á�
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
                    //���÷��䷽������BluetoothDevice.createBond(BluetoothDevice remoteDevice);  
                    Method createBondMethod = BluetoothDevice.class  
                            .getMethod("createBond");  
                    Log.d("BlueToothTestActivity", "��ʼ���");  
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
     * ==========����С�����ƽ���===========
     * ��BluetoothDevice����BloothcarActivity
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
            if (v == btnSearch)// ���������豸����BroadcastReceiver��ʾ���  
            {  
                if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// ���������û����  
                    Toast.makeText(BluetoothConnect.this, "���ȴ�����", 1000)  
                            .show();  
                    return;  
                }  
                if (btAdapt.isDiscovering())  
                    btAdapt.cancelDiscovery();  
                lstDevices.clear();  
                Object[] lstDevice = btAdapt.getBondedDevices().toArray();  
                for (int i = 0; i < lstDevice.length; i++) {  
                    BluetoothDevice device = (BluetoothDevice) lstDevice[i];  
                    String str = "�����|" + device.getName() + "|"  
                            + device.getAddress();  
                    lstDevices.add(str); // ��ȡ�豸���ƺ�mac��ַ  
                    adtDevices.notifyDataSetChanged();  
                }  
                setTitle("����������ַ��" + btAdapt.getAddress());  
                btAdapt.startDiscovery();  
            } else if (v == btnSwitch) {// ������������/�ر�  
                if (btnSwitch.getText().toString().equals("������")){
                	btnSwitch.setText("�ر�����");
//                	btnSwitch.setEnabled(false);
                    btAdapt.enable();  
                    
                }
                else if (btnSwitch.getText().toString().equals("�ر�����")) { 
                	btnSwitch.setText("������");
//                	btnSwitch.setEnabled(false);
                    btAdapt.disable();  
                    
                }
            }  else if (v == btnExit) { //�˳���ť
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