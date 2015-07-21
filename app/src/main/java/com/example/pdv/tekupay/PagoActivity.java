package com.example.pdv.tekupay;

import android.os.Bundle;
import android.provider.BaseColumns;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.pdv.tekupay.R;

public class PagoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pago);
		

		
	    Button reversarButton = (Button) findViewById(R.id.continuarTransaccionButton);
	    reversarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		setProgressBarIndeterminateVisibility(true); 
        		ProgressDialog progressDialog = ProgressDialog.show(PagoActivity.this,"Cargando", "Espere por favor..."); 
    			Intent mainIntent = new Intent(PagoActivity.this, MainActivity.class);
    			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(mainIntent);
            }
        }); 
	} 

}
