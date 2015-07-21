package com.example.pdv.tekupay;

import java.util.ArrayList;

import android.os.Bundle; 
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.pdv.tekupay.R;

public class OperacionesActivity extends ActivityGroup { 
	
    public static OperacionesActivity self;
    private ArrayList<View> history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	    this.history = new ArrayList<View>();
        self = this;
		setContentView(R.layout.activity_operaciones);
		
		LinearLayout backgroundImage = (LinearLayout)findViewById(R.id.backgroundReverso);
		backgroundImage.getBackground().setAlpha(50);	
		
		Button button_consultar_operaciones = (Button) findViewById(R.id.button_consultar_operaciones);
		button_consultar_operaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   
				Intent consultaIntent = new Intent(OperacionesActivity.this, ConsultaActivity.class); 
				replaceContentView("Consulta", consultaIntent);
            }
        });
		
		Button button_reversar_operaciones = (Button) findViewById(R.id.button_reversar_operaciones);
		button_reversar_operaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  
				Intent consultaIntent = new Intent(OperacionesActivity.this, ReversoActivity.class); 
				consultaIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				replaceContentView("Reverso", consultaIntent); 
            }
        });
	}  
	public void replaceContentView(String id, Intent newIntent) {  
	    this.history.add(getWindow().findViewById(R.id.operacionesContent));
		View view = getLocalActivityManager().startActivity(id, newIntent).getDecorView();  
	    this.history.add(view);
		this.setContentView(view);
	}   
	
	public void back()
	{
	    if(history.size() > 1){ 
	        history.remove(history.size()-1);  
	        View view = history.get(history.size()-1);
	        Activity activity = (Activity) view.getContext();  
	        setContentView(view);
	    }
	    else{ 
	        finish(); 
	    }
	}
}
