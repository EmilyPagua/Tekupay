package com.example.pdv.tekupay;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
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
import android.widget.TextView;

import com.example.pdv.tekupay.R;

public class ConsultaActivity extends ListActivity {
	 
	public static EventDataSQLHelper eventsData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consulta);

		ImageView okImage = (ImageView) findViewById(R.id.okImage);
		okImage.setAlpha(130);
		ImageView undoImage = (ImageView) findViewById(R.id.undoImage);
		undoImage.setAlpha(130);
		ImageView cancelImage = (ImageView) findViewById(R.id.cancelImage);
		cancelImage.setAlpha(130);
		
		eventsData = new EventDataSQLHelper(this);
	    Cursor cursor = getEvents();
	    showEvents(cursor);
		
	    Button atrasConsultarButton = (Button) findViewById(R.id.atrasConsultarButton);
	    atrasConsultarButton.setOnClickListener(new View.OnClickListener() {
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
		
        ArrayList<Transacciones> transacciones = new ArrayList<Transacciones>();
	    while (cursor.moveToNext()) {
	    	long id = cursor.getLong(0);
	    	if(cursor.getInt(7) == 0)
	    		transacciones.add(new Transacciones(cursor.getString(1), cursor.getString(4), cursor.getString(2)+"  ", cursor.getString(3), cursor.getInt(5), cursor.getString(6), cursor.getInt(7), cursor.getString(8)));
	    }    
        TransaccionesAdapter adaptador = new TransaccionesAdapter(ConsultaActivity.this, transacciones);
        setListAdapter(adaptador);
	}

	private Cursor getEvents() {
	    SQLiteDatabase db = eventsData.getReadableDatabase();
	    Cursor cursor = db.query(EventDataSQLHelper.TABLE, null, null, null, null, null, null);
	    
	    startManagingCursor(cursor);
	    return cursor;
	}

}
