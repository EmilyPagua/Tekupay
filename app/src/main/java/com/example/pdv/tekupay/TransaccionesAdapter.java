package com.example.pdv.tekupay;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pdv.tekupay.R;

public class TransaccionesAdapter extends BaseAdapter {
	 
	private ArrayList<Transacciones> listadoTransaccciones;
	private LayoutInflater lInflater;
	 
	public TransaccionesAdapter(Context context, ArrayList<Transacciones> transacciones) {
		this.lInflater = LayoutInflater.from(context);
		this.listadoTransaccciones = transacciones;
	}

	@Override
	public int getCount() {
		return listadoTransaccciones.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listadoTransaccciones.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ContenedorView contenedor = null;
	      
		if (arg1 == null){
	    	arg1 = lInflater.inflate(R.layout.list_consulta, null);
	    	contenedor = new ContenedorView();
	     	contenedor.pan = (TextView) arg1.findViewById(R.id.pan);
	      	contenedor.monto = (TextView) arg1.findViewById(R.id.monto);
	      	contenedor.fecha = (TextView) arg1.findViewById(R.id.fecha);
	       	contenedor.hora = (TextView) arg1.findViewById(R.id.hora);
	       	contenedor.referencia = (TextView) arg1.findViewById(R.id.referencia);
	       	contenedor.transactionStatus = (ImageView) arg1.findViewById(R.id.transactionStatus);
	       	arg1.setTag(contenedor);
	 	} 
		else
	     	contenedor = (ContenedorView) arg1.getTag();
	 
		Transacciones transacciones = (Transacciones) getItem(arg0);
		String pan = transacciones.getPan();
		String resPan = "";
		int panLen = pan.length();
		pan = pan.substring(panLen-4, panLen);
		for(int i = 0; i < panLen-4; i++){
			resPan += "x";
		}
		pan = resPan+pan;
	  	contenedor.pan.setText(pan);
	   	contenedor.monto.setText(transacciones.getMonto());
	   	contenedor.fecha.setText(transacciones.getFecha());
	   	contenedor.hora.setText(transacciones.getHora());
	   	contenedor.referencia.setText("Ref."+transacciones.getReferencia());
	   	if(transacciones.getTransactionStatus() == 0)
	   		contenedor.transactionStatus.setImageResource(R.drawable.ok);
	   	if(transacciones.getTransactionStatus() == 1)
	   		contenedor.transactionStatus.setImageResource(R.drawable.cancel);
	   	if(transacciones.getTransactionStatus() == 2)
	   		contenedor.transactionStatus.setImageResource(R.drawable.cancel);
	   	contenedor.transactionStatus.setAlpha(130);
	   	
	 	View dividerColor = (View) arg1.findViewById(R.id.dividerColor);
	   	if(transacciones.getTransactionStatus() == 0)
		 	dividerColor.setBackgroundColor(Color.parseColor("#259200"));
	   	if(transacciones.getTransactionStatus() == 1)
		 	dividerColor.setBackgroundColor(Color.parseColor("#FE9300"));
	   	if(transacciones.getTransactionStatus() == 2)
		 	dividerColor.setBackgroundColor(Color.parseColor("#E12200"));
	   	dividerColor.getBackground().setAlpha(130);
	    return arg1;
	}
	 
	class ContenedorView{
	 	TextView pan;
	 	TextView monto; 
	 	TextView fecha; 
	 	TextView hora; 
	 	TextView referencia; 
	 	ImageView transactionStatus;
	} 
}
