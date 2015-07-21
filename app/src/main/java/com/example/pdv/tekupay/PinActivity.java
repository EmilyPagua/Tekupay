package com.example.pdv.tekupay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.pdv.tekupay.PersonalIDActivity.waitForCard;
import com.example.pdv.tekupay.SignatureActivity.sendImage;
import com.example.pdv.tekupay.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PinActivity extends Activity implements Runnable {

	public static String valuesPinPressed = "";
	public static boolean response = false;
	public static String entryData = "";
	public static ArrayList<String> number;
	public static Button pinButtonNext = null;
	public Random randomGenerator = new Random();
	PinActivity pinContext = null;
	ProgressBar progressBar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin);
	    
    	new waitForCard().execute((Void) null);	
    	
    	numpadFunctions();
    	
    	pinContext = PinActivity.this;

    	final Button pinButtonBack = (Button) findViewById(R.id.pinButtonBack);
    	pinButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
        			onBackPressed();
        		}
            }
        });
		
    	pinButtonNext = (Button) findViewById(R.id.pinButtonNext);
    	pinButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
        			entryData = joinMandatoryData();
	            	new sendData().execute((Void) null);
        		}
            }
        });

    	final TextView monto = (TextView) findViewById(R.id.monto);
    	monto.setText("Monto: "+MainActivity.ammount+" Bs"); 
    	final TextView cedula = (TextView) findViewById(R.id.cedula);
    	cedula.setText("Cedula: "+PersonalIDActivity.identification); 

    	final ImageView card = (ImageView) findViewById(R.id.cardType);
    	if(MainActivity.cardType == "mastercard")
    		card.setImageResource(R.drawable.mastercard);
    	if(MainActivity.cardType == "visa")
    		card.setImageResource(R.drawable.visa);
    	if(MainActivity.cardType == "visa_electron")
    		card.setImageResource(R.drawable.visa_electron);
    	if(MainActivity.cardType == "maestro")
    		card.setImageResource(R.drawable.maestro);
    	if(MainActivity.cardType == "")
    		card.setImageResource(R.drawable.none);
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
	public void onPause() {
		super.onPause();
	}
	@Override
	protected void onStop() {
        super.onStop();
	}
	@Override
	public void onResume() {
		
		randomNumpad();
		
    	final TextView montoPin = (TextView) findViewById(R.id.monto);
    	montoPin.setText(MainActivity.ammount);
    	
		final TextView input = (TextView) findViewById(R.id.inputPIN);
		input.setText(""); 
		
		valuesPinPressed = "";

    	final TextView monto = (TextView) findViewById(R.id.monto);
    	monto.setText("Monto: "+MainActivity.ammount+" Bs"); 
    	final TextView cedula = (TextView) findViewById(R.id.cedula);
    	cedula.setText("Cedula: "+PersonalIDActivity.identification); 

    	final ImageView card = (ImageView) findViewById(R.id.cardType);
    	if(MainActivity.cardType == "mastercard")
    		card.setImageResource(R.drawable.mastercard);
    	if(MainActivity.cardType == "visa")
    		card.setImageResource(R.drawable.visa);
    	if(MainActivity.cardType == "visa_electron")
    		card.setImageResource(R.drawable.visa_electron);
    	if(MainActivity.cardType == "maestro")
    		card.setImageResource(R.drawable.maestro);
    	if(MainActivity.cardType == "")
    		card.setImageResource(R.drawable.none);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
		
        if(valuesPinPressed.length() < 4)
        	pinButtonNext.setEnabled(false);
    	
		//valuesPinPressed = "";
		checkForCard();
        super.onResume();
	}
	@Override
	public void onDestroy() {
        super.onDestroy();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public static String joinMandatoryData(){
		Date now = new Date();
    	SimpleDateFormat format = new SimpleDateFormat("MMddhhmmss");
    	
		String PAN = tlvObject.findTLV("5A");
		String transactionAmmount = MainActivity.ammount;
    	String transmisionDateTime = format.format(now);
		String transactionTime = MainActivity.transactionTime;
		String transactionDate = MainActivity.transactionDate;
		String expirationDate = tlvObject.findTLV("5F24").substring(2,6); 
		String track2 = tlvObject.findTLV("57");
		if(track2.length() >= 38)
			track2 = track2.substring(0, 37);
		String terminalSerial = "0002";   
		String transactionCurrencyCode = "";
		for(int i = 0; i < MainActivity.tags4.length; i++){ 
			if(MainActivity.tags4[i].tag.equals("9F1A")){
				transactionCurrencyCode = MainActivity.tags4[i].value;
			}
		}
		String PIN = "00000000";
		if(MainActivity.cardType.equals("maestro"))
			PIN = "0000"+valuesPinPressed; 
		String location = "Locatel,"+MainActivity.state+","+MainActivity.country;
		String aditional = ""; 
		String POSConditions = "0000000000";
		
		return PAN+";"+transactionAmmount+";"+transmisionDateTime+";"+transactionTime+";"+transactionDate+";"+expirationDate+";"
				+track2+";"+terminalSerial+";"+location+";"+aditional+";"+transactionCurrencyCode+";"+PIN+";"+POSConditions;
	}
	
	private void checkForCard(){
		if(MainActivity.deviceConnected){
			if (!MainActivity.slotStatus){
				finish();
			}
		}
    	new waitForCard().execute((Void) null);
	}
	
	private class waitForCard extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute(){
			// TODO: register the new account here.
		}
		
		@Override
		protected Void doInBackground(Void... params) {
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
	
	public class sendData extends AsyncTask<Void, Integer, Void> {
		String responseString;
		@Override
		protected void onPreExecute(){
	        progressBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
	   	    try {
		  	    HttpClient httpClientData = new DefaultHttpClient();
		   	    HttpPost httpPostData = new HttpPost("http://www.okovenezuela.com:8080/dispatcher/servlet/dispatcher");
	            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	            nameValuePairs.add(new BasicNameValuePair("entrada",entryData));
	   	    	nameValuePairs.add(new BasicNameValuePair("firma", ""));
	   	    	
		   	    httpPostData.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	   	    	HttpResponse response = httpClientData.execute(httpPostData); 
	   	    	HttpEntity entity = response.getEntity();
	   	    	responseString = EntityUtils.toString(entity);
	   	    } 
		   	catch (ClientProtocolException e) {
			    //toastNotification("No se puede contactar al host");
			    response =  false;
			    return null;
		    } 
		   	catch (IOException e) {
			    //toastNotification("No se puede contactar al host");
			    response =  false;
			    return null;
		    }
	   	    
            response = true;
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			//TODO
	    }

		@Override
		protected void onPostExecute(Void params) {

   	    	if(responseString.equals("001")) 
   	    		response =  true; 
   	    	else
   	    		response = false;
   	    	
			if(response){
				Intent ResultIntent = new Intent(PinActivity.this, ResultActivity.class);
				startActivityForResult(ResultIntent, 0);
			}
			else{
				Intent ResultIntent = new Intent(PinActivity.this, ResultActivity.class);
				startActivityForResult(ResultIntent, 0);
			}
		}

		@Override
		protected void onCancelled() {
		}
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
	
	private void randomNumpad(){
    	final TextView numpad_0 = (TextView) findViewById(R.id.numpadText_0);
    	final TextView numpad_1 = (TextView) findViewById(R.id.numpadText_1);
    	final TextView numpad_2 = (TextView) findViewById(R.id.numpadText_2);
    	final TextView numpad_3 = (TextView) findViewById(R.id.numpadText_3);
    	final TextView numpad_4 = (TextView) findViewById(R.id.numpadText_4);
    	final TextView numpad_5 = (TextView) findViewById(R.id.numpadText_5);
    	final TextView numpad_6 = (TextView) findViewById(R.id.numpadText_6);
    	final TextView numpad_7 = (TextView) findViewById(R.id.numpadText_7);
    	final TextView numpad_8 = (TextView) findViewById(R.id.numpadText_8);
    	final TextView numpad_9 = (TextView) findViewById(R.id.numpadText_9);
    	
		number = new ArrayList<String>();
		for (int i = 0; i <= 9; ++i) 
			number.add(String.valueOf(i));
		Collections.shuffle(number);
		
    	numpad_0.setText(number.get(0));
    	numpad_1.setText(number.get(1));
    	numpad_2.setText(number.get(2));
    	numpad_3.setText(number.get(3));
    	numpad_4.setText(number.get(4));
    	numpad_5.setText(number.get(5));
    	numpad_6.setText(number.get(6));
    	numpad_7.setText(number.get(7));
    	numpad_8.setText(number.get(8));
    	numpad_9.setText(number.get(9));
	}
	
	private void numpadFunctions(){
		final TextView input = (TextView) findViewById(R.id.inputPIN);
    	final Button pinButtonNext = (Button) findViewById(R.id.pinButtonNext);
		
    	final LinearLayout numpad_1 = (LinearLayout) findViewById(R.id.numpad_1);
    	numpad_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(1);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_2 = (LinearLayout) findViewById(R.id.numpad_2);
    	numpad_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(2);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_3 = (LinearLayout) findViewById(R.id.numpad_3);
    	numpad_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(3);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_4 = (LinearLayout) findViewById(R.id.numpad_4);
    	numpad_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(4);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_5 = (LinearLayout) findViewById(R.id.numpad_5);
    	numpad_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(5);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_6 = (LinearLayout) findViewById(R.id.numpad_6);
    	numpad_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(6);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_7 = (LinearLayout) findViewById(R.id.numpad_7);
    	numpad_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(7);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_8 = (LinearLayout) findViewById(R.id.numpad_8);
    	numpad_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(8);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_9 = (LinearLayout) findViewById(R.id.numpad_9);
    	numpad_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(9);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_0 = (LinearLayout) findViewById(R.id.numpad_0);
    	numpad_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && valuesPinPressed.length() < 4){
        			valuesPinPressed += number.get(0);
        			randomNumpad();
        			input.append("*");
        		}
        		if(valuesPinPressed.length() == 4)
        			pinButtonNext.setEnabled(true);
            }
        });
    	final LinearLayout numpad_X = (LinearLayout) findViewById(R.id.numpad_X);
    	numpad_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
        			pinButtonNext.setEnabled(false);
	            	if(valuesPinPressed.length() > 1){
	            		valuesPinPressed = valuesPinPressed.substring(0, valuesPinPressed.length()-1);
	            		input.setText("");
	            		for(int i = 0; i < valuesPinPressed.length(); i++)
	            			input.append("*");
	            	}
	            	else{
	            		valuesPinPressed = "";
	            		input.setText("");
	            	}
        		}
            }
        });
	}
}
