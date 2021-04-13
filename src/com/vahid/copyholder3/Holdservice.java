package com.vahid.copyholder3;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Holdservice extends Service {
   
	//Declare
	int sdk = android.os.Build.VERSION.SDK_INT;
	private static final String TAG = "copHoldService";
	private database db;
	String sclipholder, sclipholder2;
	android.content.ClipboardManager clipboard;
	@SuppressWarnings("deprecation")
	android.text.ClipboardManager clipboard2;
	OnPrimaryClipChangedListener listener;
	boolean isrunning;
	
	 @Override
	   public void onCreate() {
	       Log.i(TAG, "Service onCreate...!");
	   }
   
   @SuppressLint("NewApi") @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
	   db = new database(this);
	   db.useable();
	   isrunning = true;
	   Log.i(TAG, "Service onStartCommand");
	   try {
			if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
				new Thread(new Runnable() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						Log.i(TAG, "Service Thread2 started");
						clipboard2 = (android.text.ClipboardManager)
					    		getSystemService(Context.CLIPBOARD_SERVICE);
						db.open();
						Log.i(TAG, "Service clipboard checking...!");
						while(isrunning){
							sclipholder2 = clipboard2.getText().toString();
							if(!db.SearchData(clipboard2.getText().toString())){
								sclipholder2 = clipboard2.getText().toString();
								db.AddCopData("copytable", sclipholder2);
								Log.i(TAG, "clipboard has been changed...!");
								Notify("متن جدید اضافه شد", sclipholder2);
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
							    
			} else {
				clipboard = (android.content.ClipboardManager)
						getSystemService(Context.CLIPBOARD_SERVICE);
				Log.i(TAG, "Service Thread befor clipchange00...!");
				sclipholder = clipboard.getText().toString();
				Log.i(TAG, "Service Thread befor clipchange11...!");
				listener = new OnPrimaryClipChangedListener(){

					@Override
					public void onPrimaryClipChanged() {
						Log.i(TAG, "Service PrimaryClipChanged...!");
						db.open();
						if(!(db.SearchData(clipboard.getText().toString()))){
							sclipholder = clipboard.getText().toString();
							db.AddCopData("copytable", sclipholder);
							//MainActivity.loadcopdata(db);
							Log.i(TAG, "Service new clip added to database...!");
							Notify("متن جدید اضافه شد", sclipholder);
						}
						db.close();
								
						}
					
				};
				clipboard.addPrimaryClipChangedListener(listener);
						
			}
		}catch (Exception e) {
		}
	   Toast.makeText(getApplicationContext(), "کپی نگهدار فعال شد.", Toast.LENGTH_LONG).show();
	   return Service.START_STICKY;
   }
   
   @Override
   public IBinder onBind(Intent arg0) {
       Log.i(TAG, "Service onBind...!");
       return null;
   }
   
   @SuppressLint("NewApi") @Override
   public void onDestroy() {
	   super.onDestroy();
	   if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) 
		   isrunning = false;
	   else
		   clipboard.removePrimaryClipChangedListener(listener);
       Log.i(TAG, "Service onDestroy...!");
       Toast.makeText(getApplicationContext(), "کپی نگهدار غیر فعال شد.", Toast.LENGTH_LONG).show();
   }
   
   private void Notify(String notificationTitle, String notificationMessage){
	      NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	      Notification notification = new Notification(R.drawable.ic_launcher,"متن جدید اضافه شد",
	    		  System.currentTimeMillis());
	      notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL ;
	      
	      Intent notificationIntent = new Intent(this,MainActivity.class);
	      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
	      
	      notification.setLatestEventInfo(Holdservice.this, notificationTitle,notificationMessage, pendingIntent);
	      notificationManager.notify(9999, notification);
	   }
}