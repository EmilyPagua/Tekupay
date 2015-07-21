package com.example.pdv.tekupay;

import java.util.ArrayList;

import com.example.pdv.tekupay.PinActivity.sendData;
import com.example.pdv.tekupay.R;

import android.os.Bundle;
import android.provider.BaseColumns;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog; 
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReversoActivity extends Activity {
	
	public static EventDataSQLHelper eventsData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reverso);
		
		LinearLayout backgroundImage = (LinearLayout)findViewById(R.id.backgroundReverso);
		backgroundImage.getBackground().setAlpha(50);

		eventsData = new EventDataSQLHelper(this);
	    Cursor cursor = getEvents();
	    showEvents(cursor);
		
	    Button reversarButton = (Button) findViewById(R.id.reversarButton);
	    reversarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	new AlertDialog.Builder(ReversoActivity.this)
           	    .setTitle("Reversar Transacción")
           	    .setMessage("Esta seguro que desea reversar esta transacción?")
           	    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
           	        public void onClick(DialogInterface dialog, int which) { 
           	        	long id = 0;
           	        	Cursor cursor = getEvents();
	           	         if (cursor.moveToLast()) 
	           		    	id = cursor.getLong(0);
	           	 	    SQLiteDatabase db = ConsultaActivity.eventsData.getWritableDatabase();
	           	 	    ContentValues values = new ContentValues();
	           	 	    values.put(EventDataSQLHelper.STATUS, 2);
	           	 	    db.update(EventDataSQLHelper.TABLE, values, BaseColumns._ID+"="+id, null); 
	           			MainTabActivity.mTabHost.setCurrentTab(0); 
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
		
	    Button atrasReversarButton = (Button) findViewById(R.id.atrasReversarButton);
	    atrasReversarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   
            	OperacionesActivity.self.back();  
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

	private void showEvents(Cursor cursor) { 
	    if (cursor.moveToLast()) {
	    	long id = cursor.getLong(0);
	    	if(cursor.getInt(5) == 0 && cursor.getInt(7) == 0){
	    		String pan = cursor.getString(1);
	    		String resPan = ""; 
	    		int panLen = pan.length();
	    		pan = pan.substring(panLen-4, panLen);
	    		for(int i = 0; i < panLen-4; i++){
	    			resPan += "x";
	    		}
	    		pan = resPan+pan;
		        TextView panTV = (TextView)findViewById(R.id.panReverso);
		        panTV.setText(pan);
		        TextView monto = (TextView)findViewById(R.id.montoReverso);
		        monto.setText("Bs."+cursor.getString(4));
		        TextView reverso = (TextView)findViewById(R.id.referenciaReverso);
		        reverso.setText("Ref."+cursor.getString(8));
		        TextView fecha = (TextView)findViewById(R.id.fechaReverso);
		        fecha.setText(cursor.getString(2));
		        TextView hora = (TextView)findViewById(R.id.horaReverso);
		        hora.setText(cursor.getString(3));
	    	}
	    	else{
	    		findViewById(R.id.reversoLayout).setVisibility(View.GONE);
	    		findViewById(R.id.reversoMensajeLayout).setVisibility(View.VISIBLE);
		        TextView reverso = (TextView)findViewById(R.id.reversoMensaje);
		        if(cursor.getInt(5) == 1)
		        	reverso.setText("La ultima transacción fue una transacción fallida y por tanto no puede reversarse.");
		        else if(cursor.getInt(5) == 2)
			        reverso.setText("La ultima transacción ya fue reversada.");
		        else if(cursor.getInt(7) == 1)
			        reverso.setText("Una vez realizado el cierre no puede reversar transacciones previas al mismo.");
	    	} 
	    }
	    else{
    		findViewById(R.id.reversoLayout).setVisibility(View.GONE);
    		findViewById(R.id.reversoMensajeLayout).setVisibility(View.VISIBLE);
	        TextView reverso = (TextView)findViewById(R.id.reversoMensaje);
	        reverso.setText("Usted no posee transacciones.");
	    }
	}

	private Cursor getEvents() {
	    SQLiteDatabase db = eventsData.getReadableDatabase();
	    Cursor cursor = db.query(EventDataSQLHelper.TABLE, null, null, null, null, null, null);
	    
	    startManagingCursor(cursor);
	    return cursor;
	}

}
