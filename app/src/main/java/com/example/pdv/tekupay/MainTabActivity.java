package com.example.pdv.tekupay;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.example.pdv.tekupay.R;


public class MainTabActivity extends TabActivity {
	
	public static TabHost mTabHost;
	public static int tabDisplayed = 0;

	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        
        /*boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun){
        	new AlertDialog.Builder(this).setTitle("First Run").setMessage("This only pops up once").setNeutralButton("OK", null).show();

        	getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        		.edit()
        	 	.putBoolean("firstrun", false)
        	   	 .commit();
      	}*/
        
        setupTabHost();

		setupTab(new TextView(this), "Transacciones");
		setupTab(new TextView(this), "Operaciones");
		setupTab(new TextView(this), "Cierre");
		setupTab(new TextView(this), "Info");
		
		mTabHost.setCurrentTab(tabDisplayed);
		
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
		    @Override
		    public void onTabChanged(String tabId) {
		        Log.i("Tabs", "Tab #: " + tabId);
		        if(tabId == "Transacciones"){
        			//Intent mainIntent = new Intent(MainTabActivity.this, MainActivity.class);
        			//mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			//startActivity(mainIntent);
		        }
		        if(tabId == "Operaciones"){
		        	
		        }
		        if(tabId == "Cierre"){
		        	
		        }
		        if(tabId == "Info"){
		        	
		        }
		    }
		});
    }
    
	private void setupTab(final View view, final String tag) {
		 
		if (tag == "Transacciones"){  
			View tabview = createTabView(mTabHost.getContext(), tag);
			Intent intent = new Intent(getApplicationContext(), PagoActivity.class);
	        TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabHost.TabContentFactory()
	        {
	            public View createTabContent(String tag)
	            {
	                return view;
	            }
	        });
	        setContent.setContent(intent);
	        mTabHost.addTab(setContent);
		}
		else if (tag == "Operaciones"){
			View tabview = createTabView(mTabHost.getContext(), tag);
			Intent intent = new Intent(getApplicationContext(), OperacionesActivity.class);
	        TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabHost.TabContentFactory()
	        {
	            public View createTabContent(String tag)
	            {
	                return view;
	            }
	        });
	        setContent.setContent(intent);
	        mTabHost.addTab(setContent);
		}
		else if (tag == "Cierre"){
			View tabview = createTabView(mTabHost.getContext(), tag);
			Intent intent = new Intent(getApplicationContext(), CierreActivity.class);
	        TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabHost.TabContentFactory()
	        {
	            public View createTabContent(String tag)
	            {
	                return view;
	            }
	        });
	        setContent.setContent(intent);
	        mTabHost.addTab(setContent);
		}
		else if (tag == "Info"){
			View tabview = createTabView(mTabHost.getContext(), tag);
			Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
	        TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabHost.TabContentFactory()
	        {
	            public View createTabContent(String tag)
	            {
	                return view;
	            }
	        });
	        setContent.setContent(intent);
	        mTabHost.addTab(setContent);
		}

	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		ImageView tab_image = (ImageView) view.findViewById(R.id.tab_image);
		if (text == "Transacciones")
			tab_image.setImageResource(R.drawable.transacciones);
		else if (text == "Operaciones")
			tab_image.setImageResource(R.drawable.operaciones);
		else if (text == "Cierre")
			tab_image.setImageResource(R.drawable.cierre);	
		else if (text == "Info")
			tab_image.setImageResource(R.drawable.info);	
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
}




