package com.example.pdv.tekupay;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdv.tekupay.R;

public class SignatureActivity extends Activity { 
 
    LinearLayout mContent;
    signature mSignature;
    public static String tempDir;
    public static String entryData;
	public static boolean response = false;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;
    View mView;
    File mypath;
    File directory;
    
    public static Button signatureButtonNext;
    public static Button signatureButtonBack;
 
    private String uniqueId;
    private String filepath = "MyFileStorage";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signature);
        
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        directory = cw.getDir(filepath, Context.MODE_PRIVATE);
 
        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
        current = uniqueId + ".jpg";
        mypath= new File(directory, current);
 
        mContent = (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mView = mContent;
        
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        
        signatureButtonNext = (Button) findViewById(R.id.signatureButtonNext);
       	signatureButtonBack = (Button) findViewById(R.id.signatureButtonBack);
    	
    	signatureButtonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
                    mSignature.clear();
                    signatureButtonNext.setEnabled(false);
        			onBackPressed();
        		}
            }
        });

    	signatureButtonNext.setEnabled(false);
    	signatureButtonNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
        		if(MainActivity.cardInserted){
                    mView.setDrawingCacheEnabled(true);
                    mSignature.save(mView);
                    Bundle b = new Bundle();
                    b.putString("status", "done");
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    setResult(RESULT_OK,intent);   
                    //finish();
        		}
            }
        });

    	final TextView cedula = (TextView) findViewById(R.id.cedula);
    	cedula.setText("Cedula: "+PersonalIDActivity.identification);

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
		super.onPause();
	}
	@Override
	protected void onStop() {
        super.onStop();
	}
	@Override
	public void onResume() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

    	final TextView cedula = (TextView) findViewById(R.id.cedula);
    	cedula.setText("Cedula: "+PersonalIDActivity.identification);

    	final TextView monto = (TextView) findViewById(R.id.monto);
    	monto.setText("Monto: "+MainActivity.ammount+" Bs");
        
		checkForCard();
        super.onResume();
	}
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
 
    private String getTodaysDate() { 
        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) + 
        ((c.get(Calendar.MONTH) + 1) * 100) + 
        (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:",String.valueOf(todaysDate));
        return(String.valueOf(todaysDate));
    }
 
    private String getCurrentTime() {
        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) + 
        (c.get(Calendar.MINUTE) * 100) + 
        (c.get(Calendar.SECOND));
        Log.w("TIME:",String.valueOf(currentTime));
        return(String.valueOf(currentTime));
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
	
	public class sendImage extends AsyncTask<Void, Integer, Void> {
		String responseString;
		
		@Override
		protected void onPreExecute(){
	        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
	        progressBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
            Bitmap bitmapOrg = BitmapFactory.decodeFile(mypath.toString());
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte [] ba = bao.toByteArray();
            String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
   	    	nameValuePairs.add(new BasicNameValuePair("entrada", entryData));
            nameValuePairs.add(new BasicNameValuePair("firma",ba1));
            try{
            	HttpClient httpClientSignature = new DefaultHttpClient();
            	HttpPost httpPostSignature = new HttpPost("http://www.okovenezuela.com:8080/dispatcher/servlet/dispatcher");
            	httpPostSignature.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            	HttpResponse response = httpClientSignature.execute(httpPostSignature);
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
            
			//DELETE IMAGE AFTER UPLOAD
            mypath.delete();
            response = true;
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			//TODO
	    }

		@Override
		protected void onPostExecute(Void params) {
	        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
	        progressBar.setVisibility(View.GONE);
	        
	        responseString = responseString.replaceAll("\\s+", ""); 
	        
   	    	if(responseString.equals("001")) 
   	    		response =  true; 
   	    	else
   	    		response = false;

	        /*new AlertDialog.Builder(SignatureActivity.this)
	   	     .setTitle("Delete entry")
	   	     .setMessage(responseString+"-"+String.valueOf(response))
	   	     .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	   	         public void onClick(DialogInterface dialog, int which) { 
	   	             // continue with delete
	   	         }
	   	      })
	   	     .setNegativeButton("No", new DialogInterface.OnClickListener() {
	   	         public void onClick(DialogInterface dialog, int which) { 
	   	             // do nothing
	   	         }
	   	      })
	   	      .show();*/
	        
			if(response){
				Intent ResultIntent = new Intent(SignatureActivity.this, ResultActivity.class);
				startActivityForResult(ResultIntent, 0);
			}
			else{
				Intent ResultIntent = new Intent(SignatureActivity.this, ResultActivity.class);
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
 
    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
 
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();
 
        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }
 
        public void save(View v) {
            if(mBitmap == null){
                mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);;
            }
            Canvas canvas = new Canvas(mBitmap);
            try{
                FileOutputStream mFileOutStream = new FileOutputStream(mypath);
                v.draw(canvas); 
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, mFileOutStream); 
                mFileOutStream.flush();
                mFileOutStream.close();
                
    			entryData = PinActivity.joinMandatoryData(); 
       	    	
       	    	/*new AlertDialog.Builder(SignatureActivity.this)
    	   	     .setTitle("Delete entry")
    	   	     .setMessage(entryData)
    	   	     .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	   	         public void onClick(DialogInterface dialog, int which) { 
    	   	             // continue with delete
    	   	         }
    	   	      })
    	   	     .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	   	         public void onClick(DialogInterface dialog, int which) { 
    	   	             // do nothing
    	   	         }
    	   	      })
    	   	      .show();*/
    			
            	new sendImage().execute((Void) null);
            }
            catch(Exception e) { 
                Log.v("log_tag", e.toString()); 
            } 
        }
 
        public void clear() {
            path.reset();
            invalidate();
        }
 
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
 
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            
            signatureButtonNext.setEnabled(true);
 
            switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                path.moveTo(eventX, eventY);
	                lastTouchX = eventX;
	                lastTouchY = eventY;
	                return true;
	 
	            case MotionEvent.ACTION_MOVE:
	 
	            case MotionEvent.ACTION_UP:
	 
	                resetDirtyRect(eventX, eventY);
	                int historySize = event.getHistorySize();
	                for (int i = 0; i < historySize; i++) 
	                {
	                    float historicalX = event.getHistoricalX(i);
	                    float historicalY = event.getHistoricalY(i);
	                    expandDirtyRect(historicalX, historicalY);
	                    path.lineTo(historicalX, historicalY);
	                }
	                path.lineTo(eventX, eventY);
	                break;
	 
	            default:
	                debug("Ignored touch event: " + event.toString());
	                return false;
            }
 
            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
 
            lastTouchX = eventX;
            lastTouchY = eventY;
 
            return true;
        }
 
        private void debug(String string){
        	
        }
 
        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } 
            
            else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }
 
            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } 
            else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }
 
        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}