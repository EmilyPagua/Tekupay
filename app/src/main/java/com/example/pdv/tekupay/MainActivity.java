package com.example.pdv.tekupay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.pdv.tekupay.PinActivity.sendData;
import com.example.pdv.tekupay.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

@SuppressLint("NewApi")

public class MainActivity extends Activity implements View.OnClickListener, Runnable {

	public Handler handler = new Handler();
	public static TextView textView1;
	public static TextView textView2;
	private ViewSwitcher switcher;
	public Button mainNext = null;
	public static EditText logText;
	public static String reciveData = "";
	public static String aflString = "";
	public static String mainResponse = "";
	public static String valuesAmmountPressed = "";
	public static String valuesIDPressed = "";
	public static String cardType = "";
	public static String ammount = "";
	public static String street = "";
	public static String state = "";
	public static String country = "";
	public static String transactionDateTime = "";
	public static String transactionDate = "";
	public static String transactionTime = "";
	public static String logError = "";
	public String[] records;
	public static int aflPadding = 0;
	public static int aipPadding = 0;
	public static MainActivity mainContext;
	public static boolean deviceConnected = false;
	public static boolean cardInserted = false;
	public static boolean notEnteTwiceDisonnected = true;
	public static boolean notEnteTwiceConnected = true;
	public static boolean slotStatus = false; 
	
	private static final String TAG = "PDVMovil";
	
	private static final String twoBytesTags[] = {"5F57","9F01","9F40","9F02","9F04","9F03","9F3A","9F06","9F09","9F34","9F22","9F1E","9F15","9F16","9F39","9F33","9F1A","9F1B","9F1C","9F1D","9F35","5F2A","5F36","9F3C","9F3D","9F41","9F21","9F37"};
	private static final String twoBytesLength[] = {"1","6","5","6","4","6","4","5-16","2","3","1","8","2","15","1","3","2","4","8","1-8","1","2","1","2","1","2-4","3","4"};
	private static final String oneByteTags[] = {"81","8A","95","98","9A","9B","9C"};
	private static final String oneByteLength[] = {"4","2","5","20","3","2","1"};
	public static tagObject[] tags4 = new tagObject[twoBytesTags.length];
	public static tagObject[] tags2 = new tagObject[oneByteTags.length];
	public static tvrObject[] TVR;
	public static tsiObject[] TSI;
	public static cvmObject[] CVM;
	
	public Intent personalIDIntent  = null;
	
	//AIP
	private boolean sdaSupported = false;
	private boolean ddaSupported = false;
	private boolean cardholderVerification = false;
	private boolean terminalRiskManagement = false;
	private boolean issuerAuthentication = false;
	private boolean cdaSupported = false;
	
	//APPLICATION USAGE CONTROL
	private boolean domesticCashTransaction = false;
	private boolean internationalCashTransaction = false;
	private boolean domesticGoods = false;
	private boolean internationalGoods = false;
	private boolean domesticServices = false;
	private boolean internationalServices = false;
	private boolean ATM = false;
	private boolean otherThanATM = false;
	private boolean domesticCashback = false;
	private boolean internationalCashback = false;
	
	//CARDHOLDER VERIFICATION CONDITION 03
	private boolean failCVMSupport = true;
	private boolean plaintextPinICCSupport = true;
	private boolean encipheredPinOnlineSupport = true;
	private boolean plaintextPinICCandSignatureSupport = true;
	private boolean encipheredPinICCSupport = true;
	private boolean encipheredPinICCandSignatureSupport = true;
	private boolean signaturePaperSupport = true;
	private boolean noCVMRequiredSupport = true;
	//CARDHOLDER VERIFICATION BYTE 1
	private final String failCVM = "000000";
	private final String plaintextPinICC = "000001";
	private final String encipheredPinOnline = "000010";
	private final String plaintextPinICCandSignature = "000011";
	private final String encipheredPinICC = "000100";
	private final String encipheredPinICCandSignature = "000101";
	private final String signaturePaper = "011110";
	private final String noCVMRequired = "011111";
	//CARDHOLDER VERIFICATION BYTE 2
	private static final String always = "00";
	private static final String transactionsWithCashOrCashback = "01";
	private static final String transactionsWithoutCashOrCashback = "02";
	private static final String alwaysIfSupported = "03";
	private static final String transactionsWithCash = "04";
	private static final String transactionsWithCashback = "05";
	//CARDHOLDER VERIFICATION TRANSACTION TYPE
	private boolean transactionWithCash = true;
	private boolean transactionWithCashback = true;
	
	
	public static final byte CCID_ICC_PRESENT_ACTIVE = 0x00;
	public static final byte CCID_ICC_PRESENT_INACTIVE = 0x01;
	public static final byte CCID_ICC_ABSENT = 0x02;
	public static final byte CCID_ICC_STATUS_MASK = 0x03;
	private Button asButton;
	private Button iapButton;
	private Button prButton;
	private Button radButton;
	private Button cvButton;
	private Button trmButton;
	private Button taaButton;
	private ProgressBar progressBar = null;
	private static UsbManager mUsbManager;
	private static UsbDevice mDevice;
	public static UsbDeviceConnection mConnection;
	private static UsbEndpoint mEndpointIntr;
	private static UsbEndpoint mEndpointIn;
	private static UsbEndpoint mEndpointOut;
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
		
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, final Intent intent) {
		String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						setDevice(device);
					} 
					else {
						Log.d(TAG, "Permission denied for device " + device);
					}
				}
			} 
			else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				new Thread(new Runnable() {
					public void run() {
						while(deviceConnected == false){
							setDevice(mDevice);
						}
					}
				}).start();
			} 
			else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				deviceConnected = false;
		    	try {
		    		Thread.sleep(300);
					if (mConnection != null) {
						mConnection.close();
						mConnection = null;
					}
		    	} 
		    	catch (InterruptedException e) {}
		    	//System.exit(0);
		    	Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
		    	mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainIntent);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_main);
		
		switcher = (ViewSwitcher) findViewById(R.id.profileSwitcher);
		
		asButton = (Button) findViewById(R.id.as);
		asButton.setOnClickListener(this);
		iapButton = (Button) findViewById(R.id.iap);
		iapButton.setOnClickListener(this);
		prButton = (Button) findViewById(R.id.pr);
		prButton.setOnClickListener(this);
		radButton = (Button) findViewById(R.id.rad);
		radButton.setOnClickListener(this);
		cvButton = (Button) findViewById(R.id.cv);
		cvButton.setOnClickListener(this);
		trmButton = (Button) findViewById(R.id.trm);
		trmButton.setOnClickListener(this);
		taaButton = (Button) findViewById(R.id.taa);
		taaButton.setOnClickListener(this);
		
		textView1 = (TextView) findViewById(R.id.statusBlanco); 
		logText = (EditText) findViewById(R.id.logText);
		
		deviceConnected = false;
		
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		registerReceiver(mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
		registerReceiver(mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));
		registerReceiver(mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
						
		UncaughtExceptionHandler mUEHandler = new UncaughtExceptionHandler() {

	        @Override
	        public void uncaughtException(Thread t, Throwable e) {
	        	logError = "";
		   	    
                for (int i=0; i < 2; i++)
                	toastNotification("La aplicaci�n debe cerrarse debido a un error. Por favor intente nuevamente");
	            
	            try{
	                Thread.sleep(4000); // Let the Toast display before app will get shutdown
	                try {
		                PrintWriter pw = new PrintWriter(new OutputStreamWriter(openFileOutput("logPDV.txt", 0)));
		                e.printStackTrace(pw);
		                pw.flush();
		                pw.close();
		                
		                InputStream instream = openFileInput("logPDV.txt");
		                if (instream != null) {      
		                    InputStreamReader inputreader = new InputStreamReader(instream);
		                    BufferedReader buffreader = new BufferedReader(inputreader);
		                     
		                    String line=null;
		                    
		                    while ((line = buffreader.readLine()) != null) {      
		                    	logError += line;
		                    }
		                } 
						Log.d(TAG, "got log " + logError);
	        
		                
		            } catch (FileNotFoundException e1) {
		                // do nothing
		            } catch (IOException e2) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                
	            }
	            catch (InterruptedException e3){
	            	// do nothing
	            }
	            
            	new sendErrorLog().execute((Void) null);
	        }
	    };

	    Thread.setDefaultUncaughtExceptionHandler(mUEHandler);
		
		//4 Bytes Tags Object
		for(int i = 0; i < twoBytesTags.length; i++){
			if(twoBytesLength[i].length() > 2){
				String[] splits = twoBytesLength[i].split("-");
				tags4[i] = new tagObject(twoBytesTags[i], splits[0], splits[1], "");
			}
			else
				tags4[i] = new tagObject(twoBytesTags[i], twoBytesLength[i], twoBytesLength[i], "");
		}
		//Initialize some 4 bytes tags
		for(int i = 0; i < twoBytesTags.length; i++){
			if(tags4[i].tag.equals("9F1A")){
				tags4[i].value = "0937";
			}
			if(tags4[i].tag.equals("5F2A")){
				tags4[i].value = "0937";
			}
		}
		
		//2 Bytes Tags Object
		for(int i = 0; i < oneByteTags.length; i++){
			if(oneByteLength[i].length() > 2){
				String[] splits = oneByteLength[i].split("-");
				tags2[i] = new tagObject(oneByteTags[i], splits[0], splits[1], "");
			}
			else
				tags2[i] = new tagObject(oneByteTags[i], oneByteLength[i], oneByteLength[i], "");
		}
		
		//Start
    	try {
    		Thread.sleep(800);
			PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this,0, new Intent(ACTION_USB_PERMISSION), 0);
			HashMap<String, UsbDevice> map = mUsbManager.getDeviceList();
			for (UsbDevice device : map.values()) {
				mDevice = device;
				mUsbManager.requestPermission(device, mPermissionIntent);
			}
    	} 
    	catch (InterruptedException e) {
    				
    	}
    	
    	//switcher.showNext();
		//Intent result = new Intent(this, SignatureActivity.class);
    	//startActivityForResult(result, 0);

    	//heapDataObject = new tagObject[500];
		//heapCounter = 0; 
		//tlvObject.processTLV("702E57104110186901028130D1803201179334130000009F1F10313739333330303431333030303030300000000000009000");
		//startActivity(new Intent(this, PinActivity.class));
    	
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    	
		mainContext = MainActivity.this;
    	new waitForCard().execute((Void) null);	
    	//new geolocation().execute((Void) null);			
    	
    	numpadFunctions();

		if(personalIDIntent == null){
			personalIDIntent = new Intent(this, PersonalIDActivity.class);
		} 

    	final Button mainButtonBack = (Button) findViewById(R.id.mainButtonBack);
    	mainButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		Intent paymentIntent = new Intent(MainActivity.this, MainTabActivity.class);
        		startActivity(paymentIntent);
            }
        });

    	final Button mainDevConnectedButtonBack = (Button) findViewById(R.id.mainDevConnectedButtonBack);
    	mainDevConnectedButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
        			Intent paymentIntent = new Intent(MainActivity.this, MainTabActivity.class);
        			startActivity(paymentIntent);
        		}
            }
        });

    	mainNext = (Button) findViewById(R.id.mainButtonNext);
    	mainNext.setEnabled(false);
    	mainNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted){
        			Date now = new Date();
        	    	SimpleDateFormat formatDateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        	    	SimpleDateFormat formatDate = new SimpleDateFormat("MMdd");
        	    	SimpleDateFormat formatTime = new SimpleDateFormat("hhmmss");
        	    	transactionDateTime = formatDateTime.format(now);
        	    	transactionDate = formatDate.format(now);
        	    	transactionTime = formatTime.format(now);
        	    	startActivityForResult(personalIDIntent, 0);
        		}
            }
        });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(resultCode)
	    {
	    case 1:
	        setResult(1);
	        finish();
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {		
		//CharSequence userText = savedState.getCharSequence("savedText");
	}
	
	@Override
	public void onBackPressed() {
		Intent paymentIntent = new Intent(MainActivity.this, MainTabActivity.class);
		startActivity(paymentIntent);
		/*Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);*/
	}
	
	@Override
	public void run() {
		UsbRequest request = new UsbRequest();
		request.initialize(mConnection, mEndpointIntr);
		byte status = -1;
		ByteBuffer buffer = ByteBuffer.allocate(mEndpointIntr.getMaxPacketSize());
		while (true) {
			request.queue(buffer, mEndpointIntr.getMaxPacketSize());
			//Wait for status event
			if (mConnection != null) {
				if (mConnection.requestWait() == request) {
					byte newStatus = buffer.get(1);
					//MainActivity.this.textView1.setText(newStatus);
					//if(newStatus < 0){
						//mConnection.close();
					//}
					request.getClientData();
					if (newStatus != status) {
						//Log.d(TAG, "got status " + newStatus);
						//logText.append("Got status "+newStatus+" \n\n");
						status = newStatus;
					}
				}
			}
			else{
				try {
					Thread.sleep(100);
				} 
				catch (InterruptedException e) {
				
				}
			}
		}
	}
	
	@Override
	public void onPause() {
		if(!deviceConnected)
			close();
    	final TextView inputAmmount = (TextView) findViewById(R.id.inputAmmount);
    	ammount = inputAmmount.getText().toString();
		super.onPause();
	}
	@Override
	protected void onStop() {
		if(!deviceConnected)
			close();
		super.onStop();
	}
	@Override
	public void onResume() {
		if(valuesAmmountPressed.length() > 0)
    		mainNext.setEnabled(true);
		super.onResume();
	}
	@Override
	public void onDestroy() {
		close();
		super.onDestroy();
	}
	
	public void close() {
    	try {
    		Thread.sleep(100);
			if (mConnection != null) {
				mConnection.close();
				mConnection = null;
			}
			//Intent paymentIntent = new Intent(MainActivity.this, MainTabActivity.class);
			//startActivity(paymentIntent);
			System.exit(0);
    	} 
    	catch (InterruptedException e) {
    				
    	}
	}
	
	private void setDevice(UsbDevice device) {
		if (device == null) {
			mConnection = null;
			return;
		}

		switch(device.getDeviceClass()){
		
			case UsbConstants.USB_CLASS_APP_SPEC:
	
			case UsbConstants.USB_CLASS_AUDIO:
	
			case UsbConstants.USB_CLASS_CDC_DATA:
	
			case UsbConstants.USB_CLASS_COMM:
	
			case UsbConstants.USB_CLASS_CONTENT_SEC:
	
			case UsbConstants.USB_CLASS_CSCID:
	
			case UsbConstants.USB_CLASS_HID:
	
			case UsbConstants.USB_CLASS_HUB:
	
			case UsbConstants.USB_CLASS_MASS_STORAGE:
	
			case UsbConstants.USB_CLASS_MISC:
	
			case UsbConstants.USB_CLASS_PER_INTERFACE:
	
			case UsbConstants.USB_CLASS_PHYSICA:
	
			case UsbConstants.USB_CLASS_PRINTER:
	
			case UsbConstants.USB_CLASS_STILL_IMAGE:
	
			case UsbConstants.USB_CLASS_VENDOR_SPEC:
	
			case UsbConstants.USB_CLASS_VIDEO:
	
			case UsbConstants.USB_CLASS_WIRELESS_CONTROLLER:

		}
		mDevice = device;
		if (device != null) {
			UsbDeviceConnection connection = mUsbManager.openDevice(device);
			if (connection != null){ // && connection.claimInterface(intf, true)) {
				//logText.append("open SUCCESS\n\n");
				mConnection = connection;
				{ 
					UsbInterface ui = device.getInterface(0);
					for(int j = 0;j < ui.getEndpointCount();j++){
						UsbEndpoint endPoint = ui.getEndpoint(j);
						switch(endPoint.getType()){
					     case UsbConstants.USB_ENDPOINT_XFER_BULK:
					    	 if(endPoint.getDirection() == UsbConstants.USB_DIR_IN){
					    		 //logText.append("Dispositivo conectado\n\n");
					    		 deviceConnected = true;
								 mEndpointIn = endPoint;
								 switcher.showNext();
					    	 } 
					    	 else {
								 deviceConnected = false;
								 mEndpointOut = endPoint;
					    	 }
					    	 break;
					    	 
					     case UsbConstants.USB_ENDPOINT_XFER_CONTROL:
					    	 break;
					    	 
					     case UsbConstants.USB_ENDPOINT_XFER_INT:
					    	 mEndpointIntr = endPoint;
						     if (connection.claimInterface(ui, true)) 
						     {
							 /*  if(thread == null){
							    thread = new Thread(this);
								  thread.start();
							  }*/
						     }
					    	 break;
					     case UsbConstants.USB_ENDPOINT_XFER_ISOC:
					    	 break;
						}
					}
				}
			} 
			else {
				//logText.append("open FAIL\n\n");
				close();
			}
		}
	}
	
	UsbInterface getUsbInterface(UsbEndpoint endPoinst){
		for(int i = 0;i < mDevice.getInterfaceCount();i++){
			UsbInterface usbInterface = mDevice.getInterface(i);
			for(int j = 0;j < usbInterface.getEndpointCount();j++){
				if(usbInterface.getEndpoint(j) == endPoinst){
					return usbInterface;
				}
			}
		}
		return null;
	}
	
	public boolean CheckSlotStatus(){   
		byte cmdGetSlot[]={0x65,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		while(mEndpointIn != null){
			byte[] buffer = new byte[mEndpointIn.getMaxPacketSize()];
			sendCmd(cmdGetSlot);
			receiveData(buffer);
			if((buffer[7] & CCID_ICC_STATUS_MASK)==CCID_ICC_PRESENT_ACTIVE){
					return true;
			}
			if((buffer[7] & CCID_ICC_STATUS_MASK)==CCID_ICC_PRESENT_INACTIVE){
					return true;
			}
			if((buffer[7] & CCID_ICC_STATUS_MASK)==CCID_ICC_ABSENT){
					return false;
			}
		}
		return true;
	}
	public int PowerOn(byte[]atr){
		int reslen=0;
		byte cmd[]={0x62,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		byte[] buffer = new byte[mEndpointIn.getMaxPacketSize()];
		sendCmd(cmd);
		receiveData(buffer);
		if((byte)buffer[0]==(byte)0x80)
		{
			reslen=buffer[1];
			int i;
			for(i=0;i<reslen;i++)
			{
				atr[i]=buffer[i+10];
			}
		}
		return reslen;
	}
	
	public boolean PowerOff(){
		byte cmdGetSlot[]={0x63,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		byte[] buffer = new byte[mEndpointIn.getMaxPacketSize()];
		sendCmd(cmdGetSlot);
		receiveData(buffer);
		return true;
	}
	
	public void sendCmd(byte[] buffer) {
		if(mConnection != null && mEndpointOut != null){
			synchronized (this) {
				if(mConnection.claimInterface(getUsbInterface(mEndpointOut), true)){
					mConnection.bulkTransfer(mEndpointOut, buffer, buffer.length, mEndpointOut.getInterval());
				}
			}
		}
	}
    
	public int receiveData(byte[] buffer) {
		if (mDevice != null && mEndpointIn != null && mConnection != null) {
			synchronized (this) {
				//byte[] buffer = new byte[mEndpointIn.getMaxPacketSize()];
				if(mConnection.claimInterface(getUsbInterface(mEndpointIn), true)){
					int size = mConnection.bulkTransfer(mEndpointIn, buffer, buffer.length, mEndpointIn.getInterval());
					if (size > 0) {
						buffer.clone();
						return size;
					}
				}
			}
		}
		return 0;
	}
	
	public int Transmit(int len,byte[]apdu,byte[]res){
		byte cmd[]=new byte[10+len];
		int reslen=0;
		byte[] firstBuffer = new byte[mEndpointIn.getMaxPacketSize()];
		byte[] secondBuffer = new byte[mEndpointIn.getMaxPacketSize()];
		byte[] thirdBuffer = new byte[mEndpointIn.getMaxPacketSize()];
		byte[] fourthBuffer = new byte[mEndpointIn.getMaxPacketSize()];
		cmd[0] = 0x6F; /* XfrBlock */
		cmd[1] = (byte)(len & 0xFF);
		cmd[2] = (byte)((len >> 8) & 0xFF);
		cmd[3] = (byte)((len >> 16) & 0xFF);
		cmd[4] = (byte)((len >> 24) & 0xFF);
		cmd[5] = 00;	/* slot number */
		cmd[6] = 00;
		cmd[7] = 0x00;//bBWI;	/* extend block waiting timeout */
		cmd[8] = 0x00;//(byte)(rx_length & 0xFF);	/* Expected length, in character mode only */
		cmd[9] = 0x00;//(rx_length >> 8) & 0xFF;
		for(int i=0;i<apdu.length;i++)
		{
			cmd[i+10]=apdu[i];
		}
		
		sendCmd(cmd);
		receiveData(firstBuffer);
		reciveData = conversionObject.bytesToHexString(firstBuffer);
		
		int bufferLength = Integer.parseInt(String.valueOf(reciveData.substring(2,4)), 16);
		
		if(bufferLength >= 54){
			receiveData(secondBuffer);
		}
		if(bufferLength >= 118){
			receiveData(thirdBuffer);
		}
		if(bufferLength >= 182){
			receiveData(fourthBuffer);
		}
		
		if((byte)firstBuffer[0]==(byte)0x80){
			reslen=bufferLength;
			for(int i=0;i<reslen;i++)
			{
				if(i < 54)
					res[i]=firstBuffer[i+10];
				if(i >= 54 && i < 118)
					res[i]=secondBuffer[i-54];
				if(i >= 118 && i < 182)
					res[i]=thirdBuffer[i-118];
				if(i >= 182)
					res[i]=fourthBuffer[i-182];
			}
		}
		return reslen;
	}
	
	public void connectCard(){
		byte atr[] = new byte[33];
		int atrlen=0;
		int i;
		atrlen=PowerOn(atr);
		byte[] result=new byte[atrlen];
		for(i=0;i<atrlen;i++)
			result[i]=atr[i];
	}
	
	public void disconnectCard(){
		PowerOff();
	}
	
	public boolean getResponse61(byte length){
		byte res[] = new byte[512];
		byte[] apdu = {(byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) length};
		int reslen=Transmit(apdu.length,apdu,res);
		
		byte[] result=new byte[reslen];
		for(int i=0;i<reslen;i++)
			result[i]=(byte)res[i];
		
		reciveData = conversionObject.bytesToHexString(result);
		
		if(result[reslen-2]==(byte)0x90||result[reslen-1]==(byte)0x00){
			mainResponse = reciveData;
			return true;
		}
		return false;
	}
	
	public boolean getResponse6C(byte[] apdu, byte length){
		byte res[] = new byte[512];
		apdu[apdu.length-1] = (byte) length;
		int reslen=Transmit(apdu.length,apdu,res);
		
		byte[] result=new byte[reslen];
		for(int i=0;i<reslen;i++)
			result[i]=(byte)res[i];
		
		reciveData = conversionObject.bytesToHexString(result);
		
		if(result[reslen-2]==(byte)0x90||result[reslen-1]==(byte)0x00){
			mainResponse = reciveData;
			return true;
		}
		return false;
	}
	
	public void onClick(View v) {
		
		if (v == asButton) { 
			emv_as();
		}
		
		else if (v == iapButton) {
			emv_iap();
		}
		
		else if (v == radButton) {
			emv_rad();
		}
		
		else if (v == prButton) {
			emv_pr();
		}
		
		else if (v == cvButton) {
			emv_cv();
		}
		
		else if (v == trmButton) {
			emv_trm();
		}
		
		else if (v == taaButton) {
			emv_taa();
		}
	}
	
	public boolean Mastercard(){
		byte res[] = new byte[512];
		byte[] apdu = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x07, 
					(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x10, (byte) 0x10};
		
		int reslen=Transmit(apdu.length,apdu,res);
		
		boolean response = false;
		byte[] result=new byte[reslen];
		
		for(int i=0;i<reslen;i++)
			result[i]=(byte)res[i];
		
		reciveData = conversionObject.bytesToHexString(result);
		
		try {
			if(result[0]==(byte)0x61)
				response = getResponse61((byte)result[1]); 
			
			else if(result[0]==(byte)0x6C)
				response = getResponse6C(apdu, (byte)result[1]); 
			
			else if(result[reslen-2]==(byte)0x90||result[reslen-1]==(byte)0x00){
				mainResponse = reciveData;
				response = true;
			}
			
	    } catch (ArrayIndexOutOfBoundsException e) {
	        //toastNotification("Array index out of bounds.");
	    }
		
		if(response)
			return true;
		
		return false;		
	}
	
	public boolean Visa(){  
		byte res[] = new byte[512];
		byte[] apdu = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x07, 
					(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10};
		
		int reslen=Transmit(apdu.length,apdu,res);
		
		boolean response = false;
		byte[] result=new byte[reslen];
		
		for(int i=0;i<reslen;i++)
			result[i]=(byte)res[i];
		
		reciveData = conversionObject.bytesToHexString(result);
		
		try {
			if(result[0]==(byte)0x61)
				response = getResponse61((byte)result[1]); 
			
			else if(result[0]==(byte)0x6C)
				response = getResponse6C(apdu, (byte)result[1]);
			
			else if(result[reslen-2]==(byte)0x90||result[reslen-1]==(byte)0x00){
				mainResponse = reciveData;
				response = true;
			}
			
	    } catch (ArrayIndexOutOfBoundsException e) {
	        //toastNotification("Array index out of bounds.");
	    }
		
		if(response)
			return true;
		
		return false;
	}
	
	public boolean VisaElectron(){  
		byte res[] = new byte[512];
		byte[] apdu = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x07, 
					(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x20, (byte) 0x10};
		
		int reslen=Transmit(apdu.length,apdu,res);
		
		boolean response = false;
		byte[] result=new byte[reslen];
		
		for(int i=0;i<reslen;i++)
			result[i]=(byte)res[i];
		
		reciveData = conversionObject.bytesToHexString(result);

		try {
			if(result[0]==(byte)0x61)
				response = getResponse61((byte)result[1]); 
			
			else if(result[0]==(byte)0x6C)
				response = getResponse6C(apdu, (byte)result[1]);
			
			else if(result[reslen-2]==(byte)0x90||result[reslen-1]==(byte)0x00){
				mainResponse = reciveData;
				response = true;
			}
			
	    } catch (ArrayIndexOutOfBoundsException e) {
	        //toastNotification("Array index out of bounds.");
	    }
		
		if(response)
			return true;
		
		return false;
	}
	
	public boolean Maestro(){  
		byte res[] = new byte[512];
		byte[] apdu = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x07, 
					(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x30, (byte) 0x60};
		
		int reslen=Transmit(apdu.length,apdu,res);
		
		boolean response = false;
		byte[] result=new byte[reslen];
		
		for(int i=0;i<reslen;i++)
			result[i]=(byte)res[i];
		
		reciveData = conversionObject.bytesToHexString(result);
		
		try {
			if(result[0]==(byte)0x61)
				response = getResponse61((byte)result[1]); 
			
			else if(result[0]==(byte)0x6C)
				response = getResponse6C(apdu, (byte)result[1]);
			
			else if(result[reslen-2]==(byte)0x90||result[reslen-1]==(byte)0x00){
				mainResponse = reciveData;
				response = true;
			}
			
	    } catch (ArrayIndexOutOfBoundsException e) {
	        //toastNotification("Array index out of bounds.");
	    }
		
		if(response)
			return true;
		
		return false;
	}
	
	public void checkForCard(){
		waitForCard wfc = new waitForCard();
		readCard rc = new readCard();
		
		if(deviceConnected){
			slotStatus = CheckSlotStatus();
			if(slotStatus && notEnteTwiceConnected){
				if(cardInserted == false)
					connectCard();
				
				cardInserted = true;
				notEnteTwiceDisonnected = true;
				notEnteTwiceConnected = false;
				
				rc.execute((Void) null);

		    	/*try {
		    		  Process process = Runtime.getRuntime().exec("logcat -d");
		    		  BufferedReader bufferedReader = new BufferedReader(
		    		  new InputStreamReader(process.getInputStream()));

		    		  StringBuilder log=new StringBuilder();
		    		  String line = "";
		    		  while ((line = bufferedReader.readLine()) != null) {
		    		    log.append(line);
		    		  }
		    		  logError += log.toString();
		    	} 
		    	catch (IOException e) {}
		    	
            	new sendErrorLog().execute((Void) null);*/
				
				/*emv_as();
				emv_iap();
				emv_rad();
				emv_pr();
				emv_cv();*/

				//MainActivity.this.textView1.setText("Ingrese Monto");
			}
			else if (!slotStatus && notEnteTwiceDisonnected){
		    	final ImageView card = (ImageView) findViewById(R.id.cardType);
			    card.setImageResource(R.drawable.none);
			    
				MainActivity.this.textView1.setText("Por favor inserte la tarjeta");
				//MainActivity.this.textView2.setText("");
				
				TextView input = (TextView) findViewById(R.id.inputAmmount); 
				input.setText("0,00");

		    	Button mainNext = (Button) findViewById(R.id.mainButtonNext);
		    	mainNext.setEnabled(false);
				//logText.setText("");
            	
				rc.cancel(true);
				
				disconnectCard();
				reinitializeVariables();
				
			    trimCache(this);
			}
		}
		
		wfc.execute((Void) null);
	}
	
	static void reinitializeVariables(){
		tlvObject.heapDataObject = new tagObject[50];
		tlvObject.heapCounter = 0;
		
    	cardType = "";
		mainResponse = "";
		valuesAmmountPressed = "";
		cardInserted = false;
		notEnteTwiceDisonnected = false;
		notEnteTwiceConnected = true;
	}
	
	private void toastNotification(final String text){
		new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
	}
	
	private void failedTransaction(){
		MainActivity.this.textView1.setText("Por favor re-inserte la tarjeta");
		MainActivity.this.textView2.setText("");
		//logText.setText("");
		disconnectCard();
		reinitializeVariables();
	}
	
	public class readCard extends AsyncTask<Void, Void, Void> {
		boolean insertedCorrectly = false;

		@Override
		protected void onPreExecute(){
			final TextView input = (TextView) findViewById(R.id.inputAmmount); 
			input.setText("0,00");
			MainActivity.this.textView1.setText("Leyendo Tarjeta");
	        progressBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			if(emv_as()){
				emv_iap();
				emv_rad();
				emv_pr();
				emv_cv();
				insertedCorrectly = true;
			}
			else{
				insertedCorrectly = false;
			}
			return null;
		}

		@Override
	    protected void onProgressUpdate(Void... params) {
	    }

		@Override
		protected void onPostExecute(Void params) {
			if(insertedCorrectly == false){
				new AlertDialog.Builder(MainActivity.this)
				.setCancelable(false)
			    .setTitle("Informaci�n")
			    .setMessage("Por favor verifique que haya insertado la tarjeta correctamente.")
			    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
						MainActivity.this.textView1.setText("Por favor inserte la tarjeta");
				        progressBar.setVisibility(View.GONE);
			        	checkForCard();
			        }
			     })
			     .show();
			}
			else{
		    	final ImageView card = (ImageView) findViewById(R.id.cardType);
		    	if(cardType.equals("mastercard"))
		    		card.setImageResource(R.drawable.mastercard);
		    	if(cardType.equals("visa"))
		    		card.setImageResource(R.drawable.visa);
		    	if(cardType.equals("visa_electron"))
		    		card.setImageResource(R.drawable.visa_electron);
		    	if(cardType.equals("maestro"))
		    		card.setImageResource(R.drawable.maestro);
		    	
				MainActivity.this.textView1.setText("");
		        progressBar.setVisibility(View.GONE);
			}
		}

		@Override
		protected void onCancelled() {
		}
	}
	
	public class waitForCard extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute(){
			// TODO: register the new account here.
		}
		
		@Override
		protected Void doInBackground(Void... params) {
	        //DELAY TO CHECK IF CARD INSERTED
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
	    protected void onProgressUpdate(Void... params) {
	    }

		@Override
		protected void onPostExecute(Void params) {
			checkForCard();
		}

		@Override
		protected void onCancelled() {
		}
	}
	
	public class geolocation extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			//GET LOCATION
	    	LocationManager lm = null;
	    	Location location = null;
	    	int tryCounter = 0;
			while(location == null || tryCounter <= 10){
		    	lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		    	location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		    	if(location != null){
			    	double latitude = location.getLatitude();
			    	double longitude = location.getLongitude();
			    	Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
			        try {
			            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			            Address obj = addresses.get(0);
			            street = obj.getAddressLine(0);
			            state = obj.getSubAdminArea();
			            country = obj.getAdminArea();
						
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
		    	}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tryCounter++;
			}
			return null;
		}
	}
	
	private boolean emv_as(){
		//APPLICATION SELECTION
		//logText.append("- APPLICATION SELECTION\n\n");
		//final ImageView card = (ImageView) findViewById(R.id.cardType);
		
		if(Mastercard()){
			//MainActivity.this.textView2.setText("Mastercard");
			//card.setImageResource(R.drawable.mastercard);
			//logText.append(mainResponse);
			//logText.append("\n\n");
	    	cardType = "mastercard";
	    	return true;
		}
		
		else if(Visa()){
			//MainActivity.this.textView2.setText("Visa");
			//card.setImageResource(R.drawable.visa);
			//logText.append(mainResponse);
			//logText.append("\n\n");
	    	cardType = "visa";
	    	return true;
		}
		
		else if(VisaElectron()){
			//MainActivity.this.textView2.setText("Visa Electron");
			//card.setImageResource(R.drawable.visa_electron);
			//logText.append(mainResponse);
			//logText.append("\n\n");
	    	cardType = "visa_electron";
	    	return true;
		}
		
		else if(Maestro()){
			//MainActivity.this.textView2.setText("Maestro");
			//card.setImageResource(R.drawable.maestro);
			//logText.append(mainResponse);
			//logText.append("\n\n");
	    	cardType = "maestro";
	    	return true;
		}
		else{
			//logText.append(mainResponse);
			//logText.append("\n\n");
	    	return false;
		}
	}
	
	private void emv_iap(){
		//INITIATE APPLICATION PROCESSING
		//logText.append("- INITIATE APPLICATION PROCESSING\n\n");
		
		tlvObject.processTLV(mainResponse);
		
		String pdol = tlvObject.findTLV("9F38");
		
		String Tag[] = new String[5];
		String LengthTag[] = new String[5];
		int currPos = 0;
		int tagCounter = 0;
		int pdolLength = pdol.length();
		while(pdolLength > 0){
	        Tag[tagCounter] = pdol.substring(currPos, currPos+4);
	        currPos += 4;
	        LengthTag[tagCounter] = pdol.substring(currPos, currPos+2);
	        currPos += 2;
        	tagCounter++;
        	
	        if(pdolLength <= currPos || tagCounter > Tag.length)
	        	break;
		}
		int totalTagLength = 0;
		for(int i = 0; i < tagCounter; i++){
			totalTagLength += Integer.parseInt(LengthTag[i]);
		}	

		String gpo = "80A80000" + conversionObject.intToHexStr(totalTagLength + 2) + "83" + conversionObject.intToHexStr(totalTagLength);

		for(int i = 0; i < tagCounter; i++){
			for(int j = 0; j < twoBytesTags.length; j++){
				if(Tag[i].equals(tags4[j].tag)){
					if(Integer.parseInt(LengthTag[i], 16) >= Integer.parseInt(tags4[j].minLength) && Integer.parseInt(LengthTag[i], 16) <= Integer.parseInt(tags4[j].maxLength))
						gpo += tags4[j].value;
					else{
						if(Integer.parseInt(LengthTag[i], 16) < Integer.parseInt(tags4[j].maxLength)){ //Si la longitud enviada por el ICC es m�s corta
							if (tags4[j].value.matches("[0-9]+")) {
						        String value = tags4[j].value.substring(tags4[j].value.length() - Integer.parseInt(LengthTag[i], 16)*2, tags4[j].value.length()); 
						        gpo += value;
							}
							else{
						        String value = tags4[j].value.substring(0, Integer.parseInt(LengthTag[i], 16)*2); 
						        gpo += value;
							}
						}
						if(Integer.parseInt(LengthTag[i], 16) > Integer.parseInt(tags4[j].maxLength)){ //Si la longitud enviada por el ICC es m�s larga
							if (tags4[j].value.matches("[0-9]+")) {
								for(int k = 0; k < Integer.parseInt(LengthTag[i], 16)*2 - Integer.parseInt(tags4[j].maxLength)*2; k++)
									gpo += "0";
								gpo += tags4[j].value;
							}
							else if (tags4[j].value.matches("[0-9]+[F]+")) {
								gpo += tags4[j].value;
								for(int k = Integer.parseInt(tags4[j].maxLength)*2; k < Integer.parseInt(LengthTag[i], 16)*2; k++)
									gpo += "F";
							}
							else{
								gpo += tags4[j].value;
								for(int k = Integer.parseInt(tags4[j].maxLength)*2; k < Integer.parseInt(LengthTag[i], 16)*2; k++)
									gpo += "0";
							}
						}
					}
					break;
				}
				
				else if(j == twoBytesTags.length-1){ //Si el tag no se encuentra
					for(int k = 0; k < Integer.parseInt(LengthTag[i], 16)*2; k++){
						gpo += "0";
					}
				}
			}
		}
		
		gpo += "00";
		
		//logText.append("- GPO COMMAND\n");
		//logText.append(gpo);
		//logText.append("\n\n");
		
		byte apdu[] = conversionObject.hexStringToByteArray(gpo);
		byte res[] = new byte[512];
		int reslen=Transmit(apdu.length,apdu,res);
		
		boolean response = false;
		byte[] result=new byte[reslen];
		
		for(int i=0;i<reslen;i++)
			result[i]=(byte)res[i];
		
		try {
			if(result[0]==(byte)0x61)
				response = getResponse61((byte)result[1]); 
			
			if(result[0]==(byte)0x6C)
				response = getResponse6C(apdu, (byte)result[1]);  //ADD 9000 validation in case is shorter 
	    } catch (ArrayIndexOutOfBoundsException e) {
	        //toastNotification("Array index out of bounds.");
	    }
		
		//GET PROCESSING OPTIONS RESPONSE
		if(response){
			//logText.append("- GET PROCESSING OPTIONS\n");
			//logText.append(mainResponse);
			//logText.append("\n\n");
			
			String gpoResponseFormat = mainResponse.substring(0, 2);
			String gpoLengthString = mainResponse.substring(2, 4);
			int gpoLength = Integer.parseInt(gpoLengthString, 16);
			
			//logText.append("- GET PROCESSING OPTIONS RESPONSE FORMAT\n");
			//logText.append(gpoResponseFormat);
			//logText.append("\n\n");
			
			//logText.append("- LENGTH\n");
			//logText.append(gpoLengthString);
			//logText.append("\n\n");
			
			if(gpoResponseFormat.equals("80")){
				aipPadding = 0;
				aflPadding = 0;
			}
			
			if(gpoResponseFormat.equals("77")){
				aipPadding = 4;
				aflPadding = 8;
			}
				
			//AIP
			String aipString = mainResponse.substring(4 + aipPadding, 6 + aipPadding); //Son 2 Bytes, pero agarramos 1 porque los de la derecha son RFU
			String aipBin =  conversionObject.hexToBin(aipString);

			//logText.append("- AIP\n");
			//logText.append("Hex -> " + aipString + " / Binary -> " +aipBin);
			//logText.append("\n\n");
				
			for(int i = 0; i < aipBin.length(); i++){
				String bit = aipBin.substring(i, i+1);
				if(i == 1){
					if(bit.equals("1"))
						sdaSupported = true;
					else
						sdaSupported = false;
				}
				if(i == 2){
					if(bit.equals("1"))
						ddaSupported = true;
					else
						ddaSupported = false;
				}
				if(i == 3){
					if(bit.equals("1"))
						cardholderVerification = true;
					else
						cardholderVerification = false;
				}
				if(i == 4){
					if(bit.equals("1"))
						terminalRiskManagement = true;
					else
						terminalRiskManagement = false;
				}
				if(i == 5){
					if(bit.equals("1"))
						issuerAuthentication = true;
					else
						issuerAuthentication = false;
				}
				if(i == 7){
					if(bit.equals("1"))
						cdaSupported = true;
					else
						cdaSupported = false;
				}
			}

			//logText.append("- CARD FEATURES\n");
			//logText.append("   � SDA -> " + String.valueOf(sdaSupported) + "\n");
			//logText.append("   � DDA -> " + String.valueOf(ddaSupported) + "\n");
			//logText.append("   � CARDHOLDER VERIFICATION -> " + String.valueOf(cardholderVerification) + "\n");
			//logText.append("   � TERMINAL RISK MANAGEMENT -> " + String.valueOf(terminalRiskManagement) + "\n");					
			//logText.append("   � ISSUER AUTHENTICATION -> " + String.valueOf(issuerAuthentication) + "\n");
			//logText.append("   � CDA -> " + String.valueOf(cdaSupported) + "\n");
			//logText.append("\n\n");
				
			//AFL
			aflString = mainResponse.substring(8 + aflPadding, (gpoLength*2)+4);

			//logText.append("- AFL\n");
			//logText.append(String.valueOf(aflString));
			//logText.append("\n\n");
		}
	}
	
	private void emv_rad(){
		//READ APPLICATION DATA
		//logText.append("- READ APPLICATION DATA\n\n");
		
		//READ RECORDS
		//SPLIT AFL
		records = new String[aflString.length()/8];
		for(int i = 0; i < aflString.length(); i+=8){
			records[i/8] = aflString.substring(i, i+8);
		}
		
		//READ RECORDS
		//GET SFI
		
		for(int i = 0; i < aflString.length()/8; i++){
			String recordBinSfi =  conversionObject.hexToBin(records[i].substring(0, 2));
			String sfi = recordBinSfi.substring(0, 5);
			sfi += "100";
			
			int currRecord = Integer.parseInt(records[i].substring(2, 4), 16);
			for(int j = Integer.parseInt(records[i].substring(2, 4), 16); j <= Integer.parseInt(records[i].substring(4, 6), 16); j++){
				String readRecord = "00B2";
				readRecord += conversionObject.intToHexStr(currRecord);
				readRecord += conversionObject.intToHexStr(Integer.parseInt(sfi, 2));
				readRecord += "00";
				readRecord = readRecord.toUpperCase();
				
				byte recordsApdu[] = conversionObject.hexStringToByteArray(readRecord);
				byte res[] = new byte[512];
				int reslen=Transmit(recordsApdu.length,recordsApdu,res);
					
				boolean response = false;
				byte result[] = new byte[reslen];
					
				for(int k = 0; k < reslen; k++)
					result[k]=(byte)res[k];
				
				try {
					if(result[0]==(byte)0x61)
						response = getResponse61((byte)result[1]); 
					
					if(result[0]==(byte)0x6C)
						response = getResponse6C(recordsApdu, (byte)result[1]);  //ADD 9000 validation in case is shorter 
			    } catch (ArrayIndexOutOfBoundsException e) {
			        //toastNotification("Array index out of bounds.");
			    }
					
				if(response){
					//logText.append("- READ SFI "+conversionObject.intToHexStr(Integer.parseInt(sfi, 2))+" / RECORD NUMBER "+conversionObject.intToHexStr(currRecord)+"\n");
					//logText.append(mainResponse);
					//logText.append("\n\n");
					tlvObject.processTLV(mainResponse);
				}
				
				currRecord++;
			}
		}
		//logText.append("\n");
	}
	
	private void emv_pr(){
		//PROCESSING RESTRICTIONS
		//logText.append("- PROCESSING RESTRICTIONS\n\n");
		
		TVR = new tvrObject[5];
		TSI = new tsiObject[2];
		for(int i = 0; i < 5; i++){
			TVR[i] = new tvrObject();
			TVR[i].setTVR("0", "0", "0", "0", "0", "0", "0", "0");
			if(i <= 1){
				TSI[i] = new tsiObject();
				TSI[i].setTSI("0", "0", "0", "0", "0", "0", "0", "0");
			}
		}
		
		//APPLICATION VERSION VALIDATION
		// PREGUNTAR
		
		//CHECK MANDATORY TAGS
		String aplicationExpirationDate = tlvObject.findTLV("5F24");
		String PAN = tlvObject.findTLV("5A");
		String CDOL1 = tlvObject.findTLV("8C");
		String CDOL2 = tlvObject.findTLV("8D");
		
		if(aplicationExpirationDate.equals("") || PAN.equals("") || CDOL1.equals("") || CDOL2.equals("")){
			TVR[0].byte6 = "1";
			//toastNotification("Chip Inv�lido - Tag obligatorio no est� presente.");
			//failedTransaction();
		}
		
		if(cardholderVerification){
			String CVM = tlvObject.findTLV("8E");
			if(CVM.equals("")){
				TVR[0].byte6 = "1";
				//toastNotification("Chip Inv�lido - Tag obligatorio no est� presente, CVM.");
				//failedTransaction();
			}
		}
		
		//SDA AND DDA
		String cerAuthorityPublicKeyIndex = tlvObject.findTLV("8F");
		String issuerPublicKeyCertificate = tlvObject.findTLV("90");
		String issuerPublicKeyExponent = tlvObject.findTLV("9F32");
		String signedStaticApplicationData = tlvObject.findTLV("93"); //ONLY SDA
		
		//ONLY DDA
		String publicKeyCertificate = tlvObject.findTLV("9F46");
		String publicKeyExponent = tlvObject.findTLV("9F47");
		
		if(sdaSupported){
			if(cerAuthorityPublicKeyIndex.equals("") || issuerPublicKeyCertificate.equals("") || issuerPublicKeyExponent.equals("") || signedStaticApplicationData.equals("")){
				TVR[0].byte7 = "1";
			}
		}
		
		if(ddaSupported || cdaSupported){
			if(cerAuthorityPublicKeyIndex.equals("") || issuerPublicKeyCertificate.equals("") || issuerPublicKeyExponent.equals("") || publicKeyCertificate.equals("") || publicKeyExponent.equals("")){
				if(ddaSupported)
					TVR[0].byte4 = "1";
				if(cdaSupported)
					TVR[0].byte3 = "1";
			}
		}

		//APPLICATION USAGE CONTROL
		String ApplicationUsageControl = tlvObject.findTLV("9F07");
		if(!ApplicationUsageControl.equals("")){
			String ApplicationUsageControlBin =  conversionObject.hexToBin(ApplicationUsageControl);
	
			//logText.append("- APPLICATION USAGE CONTROL\n");
			//logText.append("Hex -> " + ApplicationUsageControl + " / Binary -> " +ApplicationUsageControlBin);
			//logText.append("\n\n");
			
			for(int i = 0; i < ApplicationUsageControlBin.length(); i++){
				String bit = ApplicationUsageControlBin.substring(i, i+1);
				if(i == 0){
					if(bit.equals("1"))
						domesticCashTransaction = true;
					else
						domesticCashTransaction = false;
				}
				if(i == 1){
					if(bit.equals("1"))
						internationalCashTransaction = true;
					else
						internationalCashTransaction = false;
				}
				if(i == 2){
					if(bit.equals("1"))
						domesticGoods = true;
					else
						domesticGoods = false;
				}
				if(i == 3){
					if(bit.equals("1"))
						internationalGoods = true;
					else
						internationalGoods = false;
				}
				if(i == 4){
					if(bit.equals("1"))
						domesticServices = true;
					else
						domesticServices = false;
				}
				if(i == 5){
					if(bit.equals("1"))
						internationalServices = true;
					else
						internationalServices = false;
				}
				if(i == 5){
					if(bit.equals("1"))
						ATM = true;
					else
						ATM = false;
				}
				if(i == 7){
					if(bit.equals("1"))
						otherThanATM = true;
					else
						otherThanATM = false;
				}
				if(i == 8){
					if(bit.equals("1"))
						domesticCashback = true;
					else
						domesticCashback = false;
				}
				if(i == 9){
					if(bit.equals("1"))
						internationalCashback = true;
					else
						internationalCashback = false;
				}
			}
	
			//logText.append("- VALID APPLICATION FEATURES\n");
			//logText.append("   � DOMESTIC CASH TRANSACTION -> " + String.valueOf(domesticCashTransaction) + "\n");
			//logText.append("   � INTERNATIONAL CASH TRANSACTION -> " + String.valueOf(internationalCashTransaction) + "\n");
			//logText.append("   � DOMESTIC GOODS -> " + String.valueOf(domesticGoods) + "\n");
			//logText.append("   � INTERNATIONAL GOODS -> " + String.valueOf(internationalGoods) + "\n");
			//logText.append("   � DOMESTIC SERVICES -> " + String.valueOf(domesticServices) + "\n");
			//logText.append("   � INTERNATIONAL SERVICES -> " + String.valueOf(internationalServices) + "\n");					
			//logText.append("   � ATM -> " + String.valueOf(ATM) + "\n");
			//logText.append("   � OTHER THAN ATM -> " + String.valueOf(otherThanATM) + "\n");
			//logText.append("   � DOMESTIC CASHBACK ALLOWED -> " + String.valueOf(domesticCashback) + "\n");
			//logText.append("   � INTERNATIONAL CASHBACK ALLOWED -> " + String.valueOf(internationalCashback) + "\n");
			//logText.append("\n\n");
			
			// HAY QUE DEFINIR COMO ESTABLECEREMOS EL TIPO DE TRANSACCION
			// PREGUNTAR
			String transactionType = "cash_transaction";
			
			//CHECK IF THE CARD'S "OTHER THAN ATM" OPTION IS AVAILABLE
			if(otherThanATM){
				//COMPARE TERMINAL COUNTRY CODE WITH ISSUER COUNTRY CODE
				String terminalCountryCode = null;
				for(int i = 0; i < twoBytesTags.length; i++){
					if(tags4[i].tag.equals("9F1A")){
						terminalCountryCode = tags4[i].value;
						break;
					}
				}
				String issuerCountryCode = tlvObject.findTLV("5F28");
				if(terminalCountryCode.equals(issuerCountryCode)){
					if(transactionType.equals("cash_transaction")){
						if(!domesticCashTransaction){
							TVR[1].byte5 = "1";
						}
					}
					else if(transactionType.equals("purchase_goods")){
						if(!domesticGoods){
							TVR[1].byte5 = "1";
						}
					}
					else if(transactionType.equals("purchase_services")){
						if(!domesticServices){
							TVR[1].byte5 = "1";
						}
					}
					else if(transactionType.equals("cashback_ammounts")){
						if(!domesticCashback){
							TVR[1].byte5 = "1";
						}
					}
				}
				else{
					if(transactionType.equals("cash_transaction")){
						if(!internationalCashTransaction){
							TVR[1].byte5 = "1";
						}
					}
					else if(transactionType.equals("purchase_goods")){
						if(!internationalGoods){
							TVR[1].byte5 = "1";
						}
					}
					else if(transactionType.equals("purchase_services")){
						if(!internationalServices){
							TVR[1].byte5 = "1";
						}
					}
					else if(transactionType.equals("cashback_ammounts")){
						if(!internationalCashback){
							TVR[1].byte5 = "1";
						}
					}
				}
				String applicationEffectiveDate = tlvObject.findTLV("5F25");
				String applicationExpirationDate = tlvObject.findTLV("5F24");
				
				String effectiveDateString = ""+applicationEffectiveDate.substring(2, 4)+"/"+applicationEffectiveDate.substring(4, 6)+"/"+applicationEffectiveDate.substring(0, 2)+"";
				String expirationDateString = ""+applicationExpirationDate.substring(2, 4)+"/"+applicationExpirationDate.substring(4, 6)+"/"+applicationExpirationDate.substring(0, 2)+"";
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
			    
			    Date iccEffectiveDate = new Date();
			    Date iccExpirationDate = new Date();
			    Date terminalDate = new Date();
			    try {
			    	iccEffectiveDate = dateFormat.parse(effectiveDateString);
			    	iccExpirationDate = dateFormat.parse(expirationDateString);
			    } catch (ParseException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			    }
			    
			    if (iccEffectiveDate.after(terminalDate)) {
					TVR[1].byte6 = "1";
			    }
			    
			    if (iccExpirationDate.before(terminalDate)) {
					TVR[1].byte7 = "1";
			    }
			}
			else{
				toastNotification("Transacci�n f�llida. Esta tarjeta s�lo se puede usar en cajeros.");
				failedTransaction();
			}
		}
		else{
			//logText.append("- APPLICATION USAGE CONTROL NOT PRESENT\n");
		}
	}
	
	private void emv_cv(){
		//PROCESSING RESTRICTIONS
		//logText.append("- CARDHOLDER VERIFICATION METHODS\n\n");
		
		String cvmString = tlvObject.findTLV("8E");
		if(cardholderVerification){
			if(!cvmString.equals("")){
				if(cvmString.length() > 16){
					cvmString = cvmString.substring(16, cvmString.length());
					if(cvmString.length() % 2 != 0){
						toastNotification("Transacci�n f�llida. CVM Rules impar.");
						failedTransaction();
					}
						
					//logText.append("- CVM SUPPORTED \n   � "+cvmString+"\n\n");
		
					CVM = new cvmObject[cvmString.length()/4];
					for(int i = 0; i < cvmString.length(); i+=4){
						String cvmDescription = cvmString.substring(i, i+2);
						String cvmCondition = cvmString.substring(i+2, i+4);
						CVM[i/4] = new cvmObject();
						CVM[i/4].setCVM(cvmDescription, cvmCondition, "0", ""); 
					}
					
					for(int i = 0; i < cvmString.length()/4; i++){
						String cvmDescriptionBin =  conversionObject.hexToBin(CVM[i].description);
						
						if(failCVM.equals(cvmDescriptionBin.substring(2, cvmDescriptionBin.length())))
							CVM[i].setCVM(CVM[i].description, CVM[i].condition, cvmDescriptionBin.substring(1,2), "Fail CVM"); 
							
						else if(plaintextPinICC.equals(cvmDescriptionBin.substring(2, cvmDescriptionBin.length())))
							CVM[i].setCVM(CVM[i].description, CVM[i].condition, cvmDescriptionBin.substring(1,2), "Plaintext PIN verified by ICC"); 
						
						else if(encipheredPinOnline.equals(cvmDescriptionBin.substring(2, cvmDescriptionBin.length())))
							CVM[i].setCVM(CVM[i].description, CVM[i].condition, cvmDescriptionBin.substring(1,2), "Eciphered PIN verified Online"); 
						
						else if(plaintextPinICCandSignature.equals(cvmDescriptionBin.substring(2, cvmDescriptionBin.length())))
							CVM[i].setCVM(CVM[i].description, CVM[i].condition, cvmDescriptionBin.substring(1,2), "Plaintext PIN verified by ICC and Signature on paper"); 
						
						else if(encipheredPinICC.equals(cvmDescriptionBin.substring(2, cvmDescriptionBin.length())))
							CVM[i].setCVM(CVM[i].description, CVM[i].condition, cvmDescriptionBin.substring(1,2), "Enciphered PIN verified by ICC"); 
						
						else if(encipheredPinICCandSignature.equals(cvmDescriptionBin.substring(2, cvmDescriptionBin.length())))
							CVM[i].setCVM(CVM[i].description, CVM[i].condition, cvmDescriptionBin.substring(1,2), "Enciphered PIN verified by ICC and Signature on paper"); 
						
						else if(signaturePaper.equals(cvmDescriptionBin.substring(2, cvmDescriptionBin.length())))
							CVM[i].setCVM(CVM[i].description, CVM[i].condition, cvmDescriptionBin.substring(1,2), "Signature on paper"); 
						
						else if(noCVMRequired.equals(cvmDescriptionBin.substring(2, cvmDescriptionBin.length())))
							CVM[i].setCVM(CVM[i].description, CVM[i].condition, cvmDescriptionBin.substring(1,2), "No CVM Required"); 
					}
					
					for(int i = 0; i < cvmString.length()/4; i++){
						if(transactionsWithCashOrCashback.equals(CVM[i].condition)){
							if(!transactionWithCash && !transactionWithCashback)
								TVR[2].byte8 = "1"; //BYPASS THE RULE DELETE TVR
						}
						if(transactionsWithoutCashOrCashback.equals(CVM[i].condition)){
							if(transactionWithCash || transactionWithCashback)
								TVR[2].byte8 = "1"; //BYPASS THE RULE DELETE TVR
						}
							
						if(!always.equals(CVM[i].condition) && !transactionsWithCashOrCashback.equals(CVM[i].condition) && !transactionsWithoutCashOrCashback.equals(CVM[i].condition) && !alwaysIfSupported.equals(CVM[i].condition) && !transactionsWithCash.equals(CVM[i].condition) && !transactionsWithCashback.equals(CVM[i].condition))
							TVR[2].byte8 = "1"; //BYPASS THE RULE DELETE TVR
					}

					//logText.append("- CARDHOLDER VERIFICATION FEATURES\n");
					
					for(int i = 0; i < cvmString.length()/4; i++){
						//logText.append("   � "+CVM[i].shortName+" -> If fails try next one? "+CVM[i].b7+"\n");
					}
					
					//logText.append("\n");
				}
			}
		}
	}
	
	private void emv_trm(){
		//TERMINAL RISK MANAGEMENT
		//logText.append("- TERMINAL RISK MANAGEMENT\n\n");

	}
	
	private void emv_taa(){
		//TERMINAL ACTION ANALYSIS
		//logText.append("- TERMINAL ACTION ANALYSIS\n\n");
		
		TSI[0].byte7 = "1"; //CARDHOLDER VERIFICATION WAS PERFORMED - CHECK IF WHEN CVM FAILS THIS WOULD BE STILL 1
		TVR[2].byte3 = "1"; //ONLINE PIN ENTERED
		TVR[3].byte4 = "1"; //MERCHANT FORCED TRANSACTION ONLINE
		
		String CDOL1 = tlvObject.findTLV("8C");
		String CDOL2 = tlvObject.findTLV("8D");
		
		//logText.append("- CDOL1: \n"+CDOL1+"\n\n");
		//logText.append("- CDOL2: \n"+CDOL2+"\n\n");
		
		String ammount = ""; 						//9F02 Amount (Numerico)
		String ammountCashback = ""; 				//9F03 Amount, Other (Numerico)
		String terminalCountryCode = ""; 			//9F1A Terminal Country Code
		String hexTVR = ""; 						//95 Terminal Verification Result
		String transactionCurrencyCode = ""; 		//5F2A Transaction Currency Code
		String transactionDate = "" ;				//9A Transaction Date
		String transactionType = ""; 				//9C Transaction Type
		String unpredictableNumber = ""; 			//9F37 Unpredictable Number
		String applicationInterchangeProfile = ""; 	//82 Application Interchange Profile
		String applicationTransactionCounter = ""; 	//9F36 Application Transaction Counter
		
		String tagsCDOL[] = {"9F02","9F03","9F1A","95","5F2A","9A","9C","9F37","82","9F36"};
		tagObject[] cdolDataObject = new tagObject[70];
		int cdolCounter = 0;
		String tag = "";
		String tagLength = "";
		if(CDOL1.length() > 0){
			for(int i = 0; i < CDOL1.length(); i+=2){
				boolean isTag = false;
				String tagType = CDOL1.substring(i+1, i+2);
				if(tagType.equals("F")){
					tag = CDOL1.substring(i, i+4);
					i += 2;
				}
				else
					tag = CDOL1.substring(i, i+2);
				
				for(int j = 0; j < tagsCDOL.length; j++){
					if(tagsCDOL[j].equals(tag))
						isTag = true;
				}
					
				if(!isTag)
					break;
				
				else{
					i += 2;
					tagLength = CDOL1.substring(i, i+2);

					//logText.append("TAG: "+tag+" VALUE: "+tagLength+"\n\n");
					
					cdolDataObject[cdolCounter] = new tagObject();
					cdolDataObject[cdolCounter].tlvTagObject(tag, tagLength);
					cdolCounter++;
				}
			}
			
			int lengthData = 0; //Requested Data Length
			for(int i = 0; i < cdolCounter; i++){
				lengthData += Integer.parseInt(cdolDataObject[i].valueTLV, 16);
			}
			
			String hexLength = Integer.toHexString(lengthData);
			hexLength = hexLength.toUpperCase();
			String ARQC = "80AE8000"+hexLength; 
			
			for(int i = 0; i < cdolCounter; i++){
				if(cdolDataObject[i].tagTLV.equals("9F02")){
					ammount = "000000000000";
					ARQC += ammount;
				}
				if(cdolDataObject[i].tagTLV.equals("9F03")){
					ammountCashback = "000000000000";
					ARQC += ammountCashback;
				}
				if(cdolDataObject[i].tagTLV.equals("9F1A")){
					for(int j = 0; j < twoBytesTags.length; j++){
						if(tags4[j].tag.equals("9F1A")){
							terminalCountryCode = tags4[j].value;
							break;
						}
					}
					ARQC += terminalCountryCode;
				}
				if(cdolDataObject[i].tagTLV.equals("95")){
					String bin = "";
					for(int j = 0; j < 5; j++){
						bin = TVR[j].byte8 + TVR[j].byte7 + TVR[j].byte6 + TVR[j].byte5;
						hexTVR += Long.toHexString(Long.parseLong(bin, 2));
						bin = TVR[j].byte4 + TVR[j].byte3 + TVR[j].byte2 + TVR[j].byte1;
						hexTVR += Long.toHexString(Long.parseLong(bin, 2));
					}
					ARQC += hexTVR;
				}
				if(cdolDataObject[i].tagTLV.equals("5F2A")){
					for(int j = 0; j < twoBytesTags.length; j++){
						if(tags4[j].tag.equals("5F2A")){
							transactionCurrencyCode = tags4[j].value;
							break;
						}
					}
					ARQC += transactionCurrencyCode;
				}
				if(cdolDataObject[i].tagTLV.equals("9A")){
        			Date now = new Date();
        	    	SimpleDateFormat formatDate = new SimpleDateFormat("ddMMyy");
        	    	transactionDate = formatDate.format(now);
					ARQC += transactionDate;
				}
				if(cdolDataObject[i].tagTLV.equals("9C")){
					transactionType = "00";
					ARQC += transactionType;
				}
				if(cdolDataObject[i].tagTLV.equals("9F37")){
					Random rnd = new Random();
					int n = 00000000 + rnd.nextInt(99999999);
					unpredictableNumber = String.valueOf(n);
					ARQC += unpredictableNumber;
				}
				if(cdolDataObject[i].tagTLV.equals("82")){
					
				}
				if(cdolDataObject[i].tagTLV.equals("9F36")){
					
				}
			}
			
			byte arqcApdu[] = conversionObject.hexStringToByteArray(ARQC);
			byte res[] = new byte[512];
			int reslen=Transmit(arqcApdu.length,arqcApdu,res);
				
			boolean response = false;
			byte result[] = new byte[reslen];
				
			for(int k = 0; k < reslen; k++)
				result[k]=(byte)res[k];
			
			reciveData = conversionObject.bytesToHexString(result);
			//logText.append("RESULT: -"+reciveData+"-\n\n");
			
			try {
				if(result[0]==(byte)0x61)
					response = getResponse61((byte)result[1]); 
				
				if(result[0]==(byte)0x6C)
					response = getResponse6C(arqcApdu, (byte)result[1]);
				
				else if(result[reslen-2]==(byte)0x90||result[reslen-1]==(byte)0x00){
					mainResponse = reciveData;
					response = true;
				}
		    } catch (ArrayIndexOutOfBoundsException e) {
		        //toastNotification("Array index out of bounds.");
		    }	
			
			if(response){
				//logText.append("RESPONSE: "+mainResponse+"\n\n");
			}
		}
	}
	
	private double ammountProcessing(String value){
		if(value.length() == 1)
			value = "0.0"+value;
		else if(value.length() == 2)
			value = "0."+value;
		else
			value = value.substring(0, value.length()-2) + "." + value.substring(value.length()-2, value.length());
			
		double returnValue = Double.parseDouble(value);
		
		return returnValue;
	}
	
	private void numpadFunctions(){
		final TextView input = (TextView) findViewById(R.id.inputAmmount); 
		final TextView montoPeq = (TextView) findViewById(R.id.monto); 
		final DecimalFormat df = new DecimalFormat("###,##0.00");
		
    	final LinearLayout numpad_1 = (LinearLayout) findViewById(R.id.numpad_1);
    	numpad_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "1";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_2 = (LinearLayout) findViewById(R.id.numpad_2);
    	numpad_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "2";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_3 = (LinearLayout) findViewById(R.id.numpad_3);
    	numpad_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "3";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_4 = (LinearLayout) findViewById(R.id.numpad_4);
    	numpad_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "4";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_5 = (LinearLayout) findViewById(R.id.numpad_5);
    	numpad_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "5";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_6 = (LinearLayout) findViewById(R.id.numpad_6);
    	numpad_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "6";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_7 = (LinearLayout) findViewById(R.id.numpad_7);
    	numpad_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "7";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_8 = (LinearLayout) findViewById(R.id.numpad_8);
    	numpad_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "8";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_9 = (LinearLayout) findViewById(R.id.numpad_9);
    	numpad_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "9";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_00 = (LinearLayout) findViewById(R.id.numpad_00);
    	numpad_00.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "00";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_0 = (LinearLayout) findViewById(R.id.numpad_0);
    	numpad_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted && valuesAmmountPressed.length() != 0 && valuesAmmountPressed.length() < 8){
        			valuesAmmountPressed += "0";
        			double result = ammountProcessing(valuesAmmountPressed);
        			input.setText(df.format(result));
        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
    		    	mainNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_X = (LinearLayout) findViewById(R.id.numpad_X);
    	numpad_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(cardInserted){
	            	if(valuesAmmountPressed.length() > 1){
	            		valuesAmmountPressed = valuesAmmountPressed.substring(0, valuesAmmountPressed.length()-1);
	        			double result = ammountProcessing(valuesAmmountPressed);
	        			input.setText(df.format(result));
	        			montoPeq.setText("Monto: "+df.format(result)+" Bs");
	            	}
	            	else{
	    		    	mainNext.setEnabled(false);
	            		valuesAmmountPressed = "";
	            		input.setText(df.format(0.00));
	            	}
        		}
            }
        });
	}
	
	public class sendErrorLog extends AsyncTask<Void, Integer, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
		  	    HttpClient httpClientData = new DefaultHttpClient();
		   	    HttpPost httpPostData = new HttpPost("http://www.okosolutions.com/Tato/report.php");
	   	    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	   	    	nameValuePairs.add(new BasicNameValuePair("ENTRADA", logError));
	   	    	httpPostData.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	   	    	httpClientData.execute(httpPostData);
	   	    } 
		   	catch (ClientProtocolException e3) {
			    toastNotification("No se puede contactar al host de errores");
		    } 
		   	catch (IOException e4) {
			    toastNotification("No se puede contactar al host de errores");
		    }
			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			System.exit(0);
		}
	}


	public static void trimCache(Context context) {
	    try {
	        File dir = context.getCacheDir();
	        if (dir != null && dir.isDirectory()) {
	            deleteDir(dir);
	
	        }
	    } catch (Exception e) {
	        // TODO: handle exception
	    }
	}
	
	
	public static boolean deleteDir(File dir) {
	    if (dir != null && dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	
	    return dir.delete();
	}
}




