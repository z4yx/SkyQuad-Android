package com.skyworks.wxsxy;

import java.io.IOException;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import com.skyworks.wxsxy.R.id;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import android.content.res.Resources;/*______________________________________________________________*/
import android.graphics.Bitmap;/*______________________________________________________________*/
import android.graphics.drawable.Drawable;/*______________________________________________________________*/
import android.graphics.drawable.BitmapDrawable;/*______________________________________________________________*/

@SuppressLint("NewApi")
public class Control extends Activity implements Runnable {

	// ң������ֵ��Χ
	final int MIN_VAL = 1108;
	final int MAX_VAL = 1933;

	/*
	 * 80 80
	 * 
	 * 40 360 440 760
	 * 
	 * 400 400
	 */

	/* ______________________________________________________________ */
	private Bitmap map1, map2, map3, map4;
	private BitmapDrawable bd1, bd2, bd3, bd4;
	/* ______________________________________________________________ */
	private BluetoothSocket btSocket;
	private BluetoothDevice btDev;
	private UUID uuid;
	private final String TAG = "BloothcarActivity";
	private boolean D = true;
	private SeekBar s1, s2, s3, s4;
	private Button btnF, btnB, btnL, btnR, btn1;
	private boolean T = false;
	short Youmen = 0, Pitch = 1500, Roll = 1500, Yaw = 1500, Pdata = 114,
			Idata = 1, Ddata = 47, sum;
	private TextView tv;
	MyView myView;
	private boolean thread_running;
	private Thread thread;

	/**
	 * ���Ի�ȡԶ���豸������socket
	 */
	private void connect() {
		btDev = this.getIntent().getExtras().getParcelable("BluetoothDevice");
		if (D)
			Log.e(TAG, "BluetoothDevice�����ɹ�");
		uuid = UUID.fromString(this.getIntent().getExtras().getString("UUID"));
		thread_running = true;
		thread = new Thread(this);
		thread.start();// �����߳�,��ʼ���Ӻͷ�������
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_control);

		btn1 = (Button) findViewById(R.id.button1);
		// tv = (TextView) findViewById(R.id.tv);
		LinearLayout layout = (LinearLayout) findViewById(R.id.widget0);
		myView = new MyView(this);
		myView.invalidate();
		layout.addView(myView);
		/* ______________________________________________________________ */
		Resources res = getResources();
		Drawable drawable1 = res.getDrawable(R.drawable.point);
		Drawable drawable2 = res.getDrawable(R.drawable.point);

		bd1 = (BitmapDrawable) drawable1;
		map1 = bd1.getBitmap();
		bd2 = (BitmapDrawable) drawable2;
		map2 = bd2.getBitmap();
		/* ______________________________________________________________ */

		btn1.setOnClickListener(ClickEvent);

		// s1=(SeekBar) findViewById(R.id.seekBar1);
		// s2=(SeekBar) findViewById(R.id.seekBar2);
		// s3=(SeekBar) findViewById(R.id.seekBar3);
		// s4=(SeekBar) findViewById(R.id.seekBar4);
		// s1.setMax(300);s1.setProgress(114);
		// s2.setMax(10);s2.setProgress(1);
		// s3.setMax(100);s3.setProgress(47);
		// s4.setMax(1000);s4.setProgress(500);
		//
		// s1.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		// {
		// //��дOnSeeBarChangeListener����������
		// //��һ��ʱOnStartTrackingTouch,�ڽ��ȿ�ʼ�ı�ʱִ��
		// public void onStartTrackingTouch(SeekBar seekBar)
		// {
		//
		// }
		//
		// //��������onStopTrackingTouch,��ֹͣ�϶�ʱִ��
		// public void onStopTrackingTouch(SeekBar seekBar)
		// {
		// Pdata= (short) seekBar.getProgress();
		// }
		//
		// public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// Pdata=(short) arg1;
		// }
		//
		// });
		//
		// s2.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		// {
		// //��дOnSeeBarChangeListener����������
		// //��һ��ʱOnStartTrackingTouch,�ڽ��ȿ�ʼ�ı�ʱִ��
		// public void onStartTrackingTouch(SeekBar seekBar) {
		// // TODO Auto-generated method stub
		//
		// }
		// //��������onStopTrackingTouch,��ֹͣ�϶�ʱִ��
		//
		// public void onStopTrackingTouch(SeekBar seekBar) {
		// Idata= (short) seekBar.getProgress();
		// }
		// public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2){
		// Idata=(short) arg1;
		// }
		// });
		//
		// s3.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		// {
		// //��дOnSeeBarChangeListener����������
		// //��һ��ʱOnStartTrackingTouch,�ڽ��ȿ�ʼ�ı�ʱִ��
		//
		// public void onStartTrackingTouch(SeekBar seekBar) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// //��������onStopTrackingTouch,��ֹͣ�϶�ʱִ��
		//
		// public void onStopTrackingTouch(SeekBar seekBar) {
		// Ddata= (short) seekBar.getProgress();
		// }
		// public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// Ddata=(short) arg1;// TODO Auto-generated method stub
		// }
		// });
		//
		// s4.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		// {
		// //��дOnSeeBarChangeListener����������
		// //��һ��ʱOnStartTrackingTouch,�ڽ��ȿ�ʼ�ı�ʱִ��
		//
		// public void onStartTrackingTouch(SeekBar seekBar) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// //��������onStopTrackingTouch,��ֹͣ�϶�ʱִ��
		//
		// public void onStopTrackingTouch(SeekBar seekBar) {
		// Yaw= (short) (seekBar.getProgress()+1000);
		// }
		// public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// Yaw=(short) (arg1+1000);// TODO Auto-generated method stub
		// }
		// });

		connect();
	}

	private void do_exit() {
		// Intent intent = new Intent();
		// intent.setClass(Control.this, BluetoothConnect.class);
		// startActivity(intent);

		thread_running = false;
		thread.interrupt();
		// Toast.makeText(this, "���ڶϿ�...", Toast.LENGTH_SHORT).show();

		while (thread.isAlive())
			;
		thread=null;

		try {
			if (btSocket != null)
				btSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		Log.i("onBackPressed", "do_exit");
		do_exit();
		// super.onBackPressed();
	}
	
	

	@Override
	protected void onDestroy() {
		try {

			if(thread!=null){
				thread_running=false;
				thread.interrupt();
				thread=null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onDestroy();
	}



	private OnClickListener ClickEvent = new OnClickListener() {
		public void onClick(View v1) {
			switch (v1.getId()) {
			case id.button1:
				do_exit();
				break;
			}
		}
	};

	//
	// private void write(BluetoothSocket btSocket, String str) {
	// OutputStream out;
	// try {
	// out = btSocket.getOutputStream();
	// out.write(str.getBytes(Charset.forName("GBK")));
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// private void write1(BluetoothSocket btSocket, short i) {
	// OutputStream out;
	// try {
	// out = btSocket.getOutputStream();
	// out.write((i >> 8) & 0x00FF);
	// out.write((i & 0x00FF));
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	private void write1(BluetoothSocket btSocket, byte[] b) {
		OutputStream out;
		try {
			out = btSocket.getOutputStream();
			out.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void sendingDataLoop() {
		while (btSocket != null && thread_running) {
			// write(btSocket, "SW");
			// write1(btSocket, Youmen);
			// write1(btSocket, Pitch);
			// write1(btSocket, Roll);
			// write1(btSocket, Yaw);
			// write1(btSocket, Pdata);
			// write1(btSocket, Idata);
			// write1(btSocket, Ddata);
			StringBuilder d = new StringBuilder();
			d.append("fun");
			d.append('-');
			d.append(Yaw);
			d.append('-');
			d.append(Pitch);
			d.append('-');
			d.append(Youmen);
			d.append('-');
			d.append(Roll);
			d.append('-');
			d.append(MAX_VAL);
			d.append('-');
			d.append(1200);
			d.append('-');
			d.append(MAX_VAL);
			d.append("\r\n");

			// sum = (short) (Youmen + Pitch + Roll + Yaw + Pdata + Idata +
			// Ddata);
			// write1(btSocket, sum);
			Log.v("send", d.toString());
			write1(btSocket, d.toString().getBytes());
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private void initParam() {
		Youmen=MIN_VAL;
		Pitch=Roll=Yaw=(MAX_VAL+MIN_VAL)/2;
	}

	public void run() {

		while (thread_running) {
			try {
				btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
				if (D)
					Log.e(TAG, "��ʼ����...");
				initParam();
				btSocket.connect();
				if (D)
					Log.e(TAG, "���ӳɹ�");
				sendingDataLoop();
				break;
			} catch (IOException e) {
				/* ========to do list======== */
				// ����дһ�������򣬽����ִ�����в���ѯ���û��Ƿ��������ӡ�

				if (D)
					Log.e(TAG, e.toString());
				try {
					btSocket.close();
					if (D)
						Log.e(TAG, "����ʧ�ܣ�socket���Զ��ر�");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					if (D)
						Log.e(TAG, "����ʧ�ܣ�socket�ر�ʧ��");
					e1.printStackTrace();
				}
			}
		}
		// TODO Auto-generated method stub

	}

	// ʵ��onTouchEvent����
	public boolean onTouchEvent(MotionEvent event) {
		int pointerCount = event.getPointerCount();
		short xTemp = 0, yTemp = 0;
		for (short i = 0; i < pointerCount; i++) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				xTemp = (short) event.getX(i);
				yTemp = (short) event.getY(i);

				break;
			case MotionEvent.ACTION_MOVE:
				xTemp = (short) event.getX(i);
				yTemp = (short) event.getY(i);
				break;
			case MotionEvent.ACTION_UP:
				myView.x2 = 600;
				myView.y2 = 240;
				myView.x = 200;
				// myView.y = 400;
				break;
			}
			if ((xTemp > 40) && (xTemp < 360) && (yTemp > 80) && (yTemp < 400)) {
				myView.x = xTemp;
				myView.y = yTemp;
			}
			if ((xTemp > 440) && (xTemp < 760) && (yTemp > 80) && (yTemp < 400)) {
				myView.x2 = xTemp;
				myView.y2 = yTemp;
			}
		}
		// if(myView.x<150 || myView.x>450||myView.y<50 || myView.y>350)
		// {
		// myView.x=300;
		// myView.y=200;
		// }
		// if(myView.x2<0 || myView.x2>100||myView.y2<50 || myView.y2>350)
		// {
		// myView.x2=50;
		// myView.y2=350;
		// }
		myView.postInvalidate();

//		Log.v("coord", "" + myView.x + " " + myView.y + " " + myView.x2 + " "
//				+ myView.y2);

		Roll = (short) ((myView.x2 - 440) / 320.0 * (MAX_VAL - MIN_VAL) + MIN_VAL);
		Pitch = (short) ((myView.y2 - 80) / 320.0 * (MAX_VAL - MIN_VAL) + MIN_VAL);
		Youmen = (short) ((400 - myView.y) / 320.0 * (MAX_VAL - MIN_VAL) + MIN_VAL);
		Yaw = (short) ((myView.x - 40) / 320.0 * (MAX_VAL - MIN_VAL) + MIN_VAL);
		// showXY(event.getX(), event.getY());
		return super.onTouchEvent(event);
	}

	// // ��ȡ�����꣬�����ж�
	// private void showXY(float x, float y) {
	// tv.setText("����x��" + x + " y��" + y);
	//
	// }
	class MyView extends View {

		short x = 200;
		short y = 400;
		short x2 = 600;
		short y2 = 240;

		public MyView(Context context) {
			super(context);

		}

		@Override
		protected void onDraw(Canvas canvas) {
			/* ______________________________________________________________ */
			canvas.drawBitmap(map1, x - 38, y - 110, null);
			canvas.drawBitmap(map2, x2 - 38, y2 - 110, null);
			super.onDraw(canvas);
			/* ______________________________________________________________ */
		}
	}
}
