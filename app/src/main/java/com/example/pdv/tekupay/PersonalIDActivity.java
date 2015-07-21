package com.example.pdv.tekupay;

import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdv.tekupay.R;

@SuppressLint("NewApi")

public class PersonalIDActivity extends Activity implements Runnable {

	public Handler handler = new Handler();
	public TextView textView1;
	public String reciveData = "";
	public String mainResponse = "";
	public String aflString = "";
	public String[] records;
	public static String identification = "";

	public static Button personalIDButtonNext = null;
	
	public static final byte CCID_ICC_PRESENT_ACTIVE = 0x00;
	public static final byte CCID_ICC_PRESENT_INACTIVE = 0x01;
	public static final byte CCID_ICC_ABSENT = 0x02;
	public static final byte CCID_ICC_STATUS_MASK = 0x03;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_personal_id);
	    
    	new waitForCard().execute((Void) null);	
    	
    	numpadFunctions();

    	final Button personalIDButtonBack = (Button) findViewById(R.id.personalIDButtonBack);
    	personalIDButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
        			onBackPressed();
        		}
            }
        }); 
		
    	personalIDButtonNext = (Button) findViewById(R.id.personalIDButtonNext);
		personalIDButtonNext.setEnabled(false);
    	personalIDButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
        			if(MainActivity.cardType.equals("maestro")){
        		    	Intent pinIntent = new Intent(PersonalIDActivity.this, PinActivity.class);
        		    	startActivityForResult(pinIntent, 0);
        			}
        			else{
        		    	Intent signatureIntent = new Intent(PersonalIDActivity.this, SignatureActivity.class);
        		    	startActivityForResult(signatureIntent, 0);
        			}
        		} 
            }
        });

    	final TextView monto = (TextView) findViewById(R.id.monto);
    	monto.setText("Monto: "+MainActivity.ammount+" Bs"); 
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
    	final TextView inputAmmount = (TextView) findViewById(R.id.inputID);
    	identification = inputAmmount.getText().toString();
		super.onPause();
	}
	@Override
	protected void onStop() {
        super.onStop();
	}
	@Override
	public void onResume() {
		if(MainActivity.valuesIDPressed.length() > 0)
			personalIDButtonNext.setEnabled(true);
		
		checkForCard();
		
		final TextView input = (TextView) findViewById(R.id.inputID); 
    	final TextView cedula = (TextView) findViewById(R.id.cedula);
		final DecimalFormat df = new DecimalFormat("###,###");
		MainActivity.valuesIDPressed = MainActivity.valuesIDPressed;
		if(MainActivity.valuesIDPressed.length() > 0){
			double result = Double.parseDouble(MainActivity.valuesIDPressed);
			input.setText(df.format(result));
			cedula.setText("Cedula: "+df.format(result));
		} 

    	final TextView monto = (TextView) findViewById(R.id.monto);
    	monto.setText("Monto: "+MainActivity.ammount+" Bs"); 
        super.onResume();
	}
	@Override
	public void onDestroy() {
        super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {		
		//CharSequence userText = savedState.getCharSequence("savedText");
	}
	
	public static void close() {
    	try {
    		Thread.sleep(1000);
			if (MainActivity.mConnection != null) {
				MainActivity.mConnection.close();
				MainActivity.mConnection = null;
			}
			System.exit(0);
    	} 
    	catch (InterruptedException e) {
    				
    	}
	}
	
	public void checkForCard(){
		if(MainActivity.deviceConnected){
			if(MainActivity.slotStatus){ 

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
			else if (!MainActivity.slotStatus){
		    	final ImageView card = (ImageView) findViewById(R.id.cardType);
			    card.setImageResource(R.drawable.none);
				
				final TextView input = (TextView) findViewById(R.id.inputID); 
				input.setText("");
				//DESTRUIR ESTA ACTIVITY E IRSE A LA ACTIVIDAD MAIN
				
				MainActivity.valuesIDPressed = "";
				
				finish();
			}
		}
		
    	new waitForCard().execute((Void) null);
	}
	
	public class waitForCard extends AsyncTask<Void, Void, Void> {

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
	
	private void numpadFunctions(){
		final TextView input = (TextView) findViewById(R.id.inputID); 
		final TextView cedPeq = (TextView) findViewById(R.id.cedula); 
		final DecimalFormat df = new DecimalFormat("###,###");
		
    	final LinearLayout numpad_1 = (LinearLayout) findViewById(R.id.numpad_1);
    	numpad_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "1";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_2 = (LinearLayout) findViewById(R.id.numpad_2);
    	numpad_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "2";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_3 = (LinearLayout) findViewById(R.id.numpad_3);
    	numpad_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "3";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_4 = (LinearLayout) findViewById(R.id.numpad_4);
    	numpad_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "4";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_5 = (LinearLayout) findViewById(R.id.numpad_5);
    	numpad_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "5";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_6 = (LinearLayout) findViewById(R.id.numpad_6);
    	numpad_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "6";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_7 = (LinearLayout) findViewById(R.id.numpad_7);
    	numpad_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "7";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_8 = (LinearLayout) findViewById(R.id.numpad_8);
    	numpad_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "8";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_9 = (LinearLayout) findViewById(R.id.numpad_9);
    	numpad_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "9";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_0 = (LinearLayout) findViewById(R.id.numpad_0);
    	numpad_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted && MainActivity.valuesIDPressed.length() != 0 && MainActivity.valuesIDPressed.length() < 8){
        			MainActivity.valuesIDPressed += "0";
        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
        			input.setText(df.format(result));
        			cedPeq.setText("Cedula: "+df.format(result));
        			personalIDButtonNext.setEnabled(true);
        		}
            }
        });
    	final LinearLayout numpad_X = (LinearLayout) findViewById(R.id.numpad_X);
    	numpad_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
	            	if(MainActivity.valuesIDPressed.length() > 1){
	            		MainActivity.valuesIDPressed = MainActivity.valuesIDPressed.substring(0, MainActivity.valuesIDPressed.length()-1);
	        			double result = Double.parseDouble(MainActivity.valuesIDPressed);
	        			input.setText(df.format(result));
	        			cedPeq.setText("Cedula: "+df.format(result));
	            	}
	            	else{
	        			personalIDButtonNext.setEnabled(false);
	        			MainActivity.valuesIDPressed = "";
	            		input.setText("");
	        			cedPeq.setText("Cedula:");
	            	}
        		}
            }
        });
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}





