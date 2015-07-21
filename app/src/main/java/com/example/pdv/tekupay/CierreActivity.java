package com.example.pdv.tekupay;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.provider.BaseColumns;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pdv.tekupay.R;

public class CierreActivity extends Activity {
	 
	public static EventDataSQLHelper eventsData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cierre);
		
		eventsData = new EventDataSQLHelper(this);
	    Cursor cursor = getEvents();
	    showEvents(cursor);
		
	    Button reversarButton = (Button) findViewById(R.id.cierreButton);
	    reversarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	new AlertDialog.Builder(CierreActivity.this)
           	    .setTitle("Reversar Transacción")
           	    .setMessage("Está seguro que desea realizar el cierre?")
           	    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
           	        public void onClick(DialogInterface dialog, int which) { 
           	        	long id = 0;
           	        	Cursor cursor = getEvents();
           	        	while (cursor.moveToNext()) {
	           		    	id = cursor.getLong(0);
		           	 	    SQLiteDatabase db = ConsultaActivity.eventsData.getWritableDatabase();
		           	 	    ContentValues values = new ContentValues();
		           	 	    values.put(EventDataSQLHelper.CIERRE, 1);
		           	 	    db.update(EventDataSQLHelper.TABLE, values, BaseColumns._ID+"="+id, null); 
           	        	}
           	        	eventsData = new EventDataSQLHelper(CierreActivity.this);
           	        	cursor = getEvents();
           	        	showEvents(cursor);
           	        }
           	     })
           	    .setNegativeButton("No", new DialogInterface.OnClickListener() {
           	        public void onClick(DialogInterface dialog, int which) {  
	           			MainTabActivity.mTabHost.setCurrentTab(0); 
           	        }
           	     })
           	     .show();
            }
        });
	} 
	@Override
	public void onResume() {
		eventsData = new EventDataSQLHelper(this);
	    Cursor cursor = getEvents();
	    showEvents(cursor);
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
	    eventsData.close();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}

	private void showEvents(Cursor cursor) {  
		
        ArrayList<Transacciones> transaccionesMaestro = new ArrayList<Transacciones>();
        ArrayList<Transacciones> transaccionesMastercard = new ArrayList<Transacciones>();
        ArrayList<Transacciones> transaccionesVisa = new ArrayList<Transacciones>();
        
        Float totalMaestro = (float) 0.0;
        Float totalMastercard = (float) 0.0;
        Float totalVisa = (float) 0.0;
        Float totalGeneral = (float) 0.0;
        
	    while (cursor.moveToNext()) {
	    	long id = cursor.getLong(0);
	    	if(cursor.getString(6).equals("maestro")){
	    		if(cursor.getInt(7) == 0){
	    			transaccionesMaestro.add(new Transacciones(cursor.getString(1), cursor.getString(4), cursor.getString(2)+"  ", cursor.getString(3), cursor.getInt(5), cursor.getString(6), cursor.getInt(7), cursor.getString(8)));
		    		if(cursor.getInt(5) == 0){
			    		String monto = cursor.getString(4);
			    		monto = monto.replace('.', '.');
			    		monto = monto.replace(',', '.');
			    		Float montoMaestro = Float.valueOf(monto);
			    		totalMaestro += montoMaestro;
		    		}
	    		}
	    	}
	    	if(cursor.getString(6).equals("mastercard")){
	    		if(cursor.getInt(7) == 0){
	    			transaccionesMastercard.add(new Transacciones(cursor.getString(1), cursor.getString(4), cursor.getString(2)+"  ", cursor.getString(3), cursor.getInt(5), cursor.getString(6), cursor.getInt(7), cursor.getString(8)));
		    		if(cursor.getInt(5) == 0){
			    		String monto = cursor.getString(4);
			    		monto = monto.replace('.', '.');
			    		monto = monto.replace(',', '.');
			    		Float montoMastercard = Float.valueOf(monto);
			    		totalMastercard += montoMastercard;
		    		}
	    		}
	    	}
	    	if(cursor.getString(6).equals("visa")){
	    		if(cursor.getInt(7) == 0){
	    			transaccionesVisa.add(new Transacciones(cursor.getString(1), cursor.getString(4), cursor.getString(2)+"  ", cursor.getString(3), cursor.getInt(5), cursor.getString(6), cursor.getInt(7), cursor.getString(8)));
		    		if(cursor.getInt(5) == 0){
			    		String monto = cursor.getString(4);
			    		monto = monto.replace(".", ""); 
			    		monto = monto.replace(",", ".");
			    		Float montoVisa = Float.valueOf(monto);
			    		totalVisa += montoVisa;
		    		}
	    		}
	    	}
	    }    
        CierreAdapter adaptadorMaestro = new CierreAdapter(CierreActivity.this, transaccionesMaestro);
        CierreAdapter adaptadorMastercard = new CierreAdapter(CierreActivity.this, transaccionesMastercard);
        CierreAdapter adaptadorVisa = new CierreAdapter(CierreActivity.this, transaccionesVisa);
        
        final ListView maestro = (ListView) findViewById(R.id.maestroList);
        final ListView mastercard = (ListView) findViewById(R.id.mastercardList);
        final ListView visa = (ListView) findViewById(R.id.visaList);

        maestro.setAdapter(adaptadorMaestro);
        mastercard.setAdapter(adaptadorMastercard);
        visa.setAdapter(adaptadorVisa);
        
		final DecimalFormat df = new DecimalFormat("###,##0.00");
        
        if(transaccionesMaestro.isEmpty()){
        	findViewById(R.id.noPoseeMaestro).setVisibility(View.VISIBLE);
        	findViewById(R.id.maestroTotalLayout).setVisibility(View.GONE);
        }
        else{
            TextView maestroTotal = (TextView) findViewById(R.id.maestroTotal);
            maestroTotal.setText(df.format(Double.parseDouble(String.valueOf(totalMaestro))));
        }
        if(transaccionesMastercard.isEmpty()){
        	findViewById(R.id.noPoseeMastercard).setVisibility(View.VISIBLE);
        	findViewById(R.id.mastercardTotalLayout).setVisibility(View.GONE);
        }	
        else{
            TextView mastercardTotal = (TextView) findViewById(R.id.mastercardTotal);
            mastercardTotal.setText(df.format(Double.parseDouble(String.valueOf(totalMastercard))));
        }
        if(transaccionesVisa.isEmpty()){
        	findViewById(R.id.noPoseeVisa).setVisibility(View.VISIBLE);
        	findViewById(R.id.visaTotalLayout).setVisibility(View.GONE);
        }
        else{
            TextView visaTotal = (TextView) findViewById(R.id.visaTotal);
            visaTotal.setText(df.format(Double.parseDouble(String.valueOf(totalVisa))));
        }
        
        if(transaccionesMaestro.isEmpty() && transaccionesMastercard.isEmpty() && transaccionesVisa.isEmpty()){
        	Button cierreButton = (Button) findViewById(R.id.cierreButton);
        	cierreButton.setEnabled(false);
        }
        
        totalGeneral = totalMaestro + totalMastercard + totalVisa;
        TextView total = (TextView) findViewById(R.id.totalGeneral);
        total.setText(df.format(Double.parseDouble(String.valueOf(totalGeneral))));
        
        maestro.postDelayed(new Runnable() {
	     	public void run() {
	        	Utility.setListViewHeightBasedOnChildren(maestro);
	     	}
      	}, 0);
        
        mastercard.postDelayed(new Runnable() {
	     	public void run() {
	        	Utility.setListViewHeightBasedOnChildren(mastercard);
	     	}
      	}, 0);
        
        visa.postDelayed(new Runnable() {
	     	public void run() {
	        	Utility.setListViewHeightBasedOnChildren(visa);
	     	}
      	}, 0);
	}

	private Cursor getEvents() {
	    SQLiteDatabase db = eventsData.getReadableDatabase();
	    Cursor cursor = db.query(EventDataSQLHelper.TABLE, null, null, null, null, null, null);
	    
	    startManagingCursor(cursor);
	    return cursor;
	}

}
