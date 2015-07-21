package com.example.pdv.tekupay;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.pdv.tekupay.PersonalIDActivity.waitForCard;
import com.example.pdv.tekupay.R;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")

public class ResultActivity extends Activity implements Runnable {

	String resultado;
	EventDataSQLHelper eventsData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
    	
    	final Button enviarCorreoButton = (Button) findViewById(R.id.enviarCorreoButton);
    	enviarCorreoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
        			new AlertDialog.Builder(ResultActivity.this)
        		    .setTitle("Información")
        		    .setMessage("Correo enviado con exito. Por favor retire la tarjeta.")
        		    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        		        public void onClick(DialogInterface dialog, int which) { 
                			Intent mainIntent = new Intent(ResultActivity.this, MainTabActivity.class);
                			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                			startActivity(mainIntent);
        		        }
        		     })
        		     .show();
        			//setResult(1);
        			//finish();
        		}
            }
        });
    	
    	final Button finalizarResultButton = (Button) findViewById(R.id.finalizarResultButton);
    	finalizarResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
        			new AlertDialog.Builder(ResultActivity.this)
        		    .setTitle("Informaci�n")
        		    .setMessage("Por favor retire la tarjeta.")
        		    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        		        public void onClick(DialogInterface dialog, int which) { 
                			Intent mainIntent = new Intent(ResultActivity.this, MainTabActivity.class);
                			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                			startActivity(mainIntent);
        		        }
        		     })
        		     .show();
        			//setResult(1);
        			//finish();
        		}
            }
        });

    	transactionInfo();
	}
	
	@Override
	public void onBackPressed() {
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}
	
	private void transactionInfo(){ 
		if(MainActivity.cardType.equals("maestro")){
			if(PinActivity.response == true)
				resultado = "Aprobada.";
			else
				resultado = "Rechazada.";
		}
		else{
			if(SignatureActivity.response == true)
				resultado = "Aprobada.";
			else
				resultado = "Rechazada.";
		}
		
    	final ImageView resultadoImagen = (ImageView) findViewById(R.id.resultadoImagen); 
    	if(resultado.equals("Aprobada."))
    		resultadoImagen.setImageResource(R.drawable.ok);
    	else if(resultado.equals("Rechazada."))
    		resultadoImagen.setImageResource(R.drawable.undo);
    	resultadoImagen.setAlpha(130);

    	final TextView resultadoElement = (TextView) findViewById(R.id.resultado);
    	resultadoElement.setText(resultado);
		
		String nombreHex = tlvObject.findTLV("5F20");
		String tarjeta = tlvObject.findTLV("5A");
		String fecha = "";
		
    	Date now = new Date();
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm");
    	fecha = format.format(now);
		
        StringBuilder nombre = new StringBuilder();
        for (int i = 0; i < nombreHex.length(); i+=2) {
            String str = nombreHex.substring(i, i+2);
            nombre.append((char)Integer.parseInt(str, 16));
        }  

    	final TextView ubicacion = (TextView) findViewById(R.id.ubicacion);
    	//ubicacion.setText(MainActivity.street+" - "+MainActivity.state+", "+MainActivity.country);  
    	ubicacion.setText("Caracas Distrito Federal");  

    	final TextView tipo = (TextView) findViewById(R.id.fecha);
    	tipo.setText(fecha);

    	final TextView cedula = (TextView) findViewById(R.id.cedula);
    	cedula.setText("Cedula: "+PersonalIDActivity.identification);

    	final TextView monto = (TextView) findViewById(R.id.monto);
    	monto.setText("Monto: "+MainActivity.ammount+" Bs");
    	
    	addEvent(tarjeta, MainActivity.ammount);
	} 

	private void addEvent(String pan, String monto) {
		Date now = new Date();
    	SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    	SimpleDateFormat formatTime = new SimpleDateFormat("kk:mm");
    	String date = formatDate.format(now);
    	String hora = formatTime.format(now);

		eventsData = new EventDataSQLHelper(this);
	    SQLiteDatabase db = eventsData.getWritableDatabase();
	    ContentValues values = new ContentValues();
	    values.put(EventDataSQLHelper.PAN, pan);
	    values.put(EventDataSQLHelper.FECHA, date);
	    values.put(EventDataSQLHelper.HORA, hora);
	    values.put(EventDataSQLHelper.MONTO, monto);
	    
    	if(resultado.equals("Aprobada."))
    	    values.put(EventDataSQLHelper.STATUS, 0);
    	else if(resultado.equals("Rechazada."))
    	    values.put(EventDataSQLHelper.STATUS, 1);
	    
    	if(MainActivity.cardType.equals("maestro"))
    	    values.put(EventDataSQLHelper.TIPO_TARJETA, "maestro");
    	else if(MainActivity.cardType.equals("mastercard"))
    	    values.put(EventDataSQLHelper.TIPO_TARJETA, "mastercard");
    	else if(MainActivity.cardType.equals("visa"))
    	    values.put(EventDataSQLHelper.TIPO_TARJETA, "visa");
    	else if(MainActivity.cardType.equals("visa_electron"))
    	    values.put(EventDataSQLHelper.TIPO_TARJETA, "visa_electron");

	    values.put(EventDataSQLHelper.CIERRE, 0);

	    long randomNumber = (long) Math.floor(Math.random() * 9999999L) + 1000000L;
	    values.put(EventDataSQLHelper.REFERENCIA, String.valueOf(randomNumber));
	    
	    db.insert(EventDataSQLHelper.TABLE, null, values);

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
    	new waitForCard().execute((Void) null);
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
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	
	public void checkForCard(){
		if(MainActivity.deviceConnected){
			if (!MainActivity.slotStatus){
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
}
