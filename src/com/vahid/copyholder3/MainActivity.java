package com.vahid.copyholder3;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.vahid.copyholder3.R;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	//Declare
	public static ArrayList<String> al;
	static ArrayAdapter<String> aa;
	private database db;
	String clipholder, clipholder2, longPressedItem;
	android.content.ClipboardManager clipboard;
	android.text.ClipboardManager clipboard2;
	Timer mytimer;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		db = new database(this);
		db.useable();
		al=new ArrayList<String>();
		ListView list = (ListView) findViewById(R.id.listView1);
		EditText search = (EditText) findViewById(R.id.editText1);
		aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al);
		list.setAdapter(aa);
		db.open();
		loadcopdata(db);
		db.close();
		search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				MainActivity.this.aa.getFilter().filter(cs);
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		registerForContextMenu(list);
		
		try {
			int sdk = android.os.Build.VERSION.SDK_INT;
			if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			    clipboard2 = (android.text.ClipboardManager)
			    		getSystemService(Context.CLIPBOARD_SERVICE);
			    clipholder2 = clipboard2.getText().toString();
				mytimer = new Timer();
				mytimer.schedule(new TimerTask() {
					
					@Override
					public void run() {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									db.open();
									if(!db.SearchData(clipboard2.getText().toString())){
										clipholder2 = clipboard2.getText().toString();
										db.AddCopData("copytable", clipholder2);
										loadcopdata(db);
										Notify("متن جدید اضافه شد", clipholder2);
									}
									db.close();
								}
							});
					}
				}, 5000,2000);
			   
							    
			} else {
			    clipboard = (android.content.ClipboardManager)
			    		getSystemService(Context.CLIPBOARD_SERVICE);
			    clipholder = clipboard.getText().toString();
			    clipboard.addPrimaryClipChangedListener(new OnPrimaryClipChangedListener() {
					
					@Override
					public void onPrimaryClipChanged() {
						db.open();
						if(!(db.SearchData(clipboard.getText().toString()))){
							clipholder = clipboard.getText().toString();
							db.AddCopData("copytable", clipholder);
							loadcopdata(db);
							Notify("متن جدید اضافه شد", clipholder);
						}
						db.close();
						
					}
				});
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
		if (v.getId()==R.id.listView1) {
    	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
    		menu.setHeaderTitle(aa.getItem(info.position));
    		String[] menuItems = getResources().getStringArray(R.array.menu); 
    		for (int i = 0; i<menuItems.length; i++) {
    			menu.add(Menu.NONE, i, i, menuItems[i]);
			}
    	}
	}
	
	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	@Override
    public boolean onContextItemSelected(MenuItem item) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	    int menuItemIndex = item.getItemId();
	    longPressedItem = aa.getItem(info.position).toString();
	    switch (menuItemIndex) {
		case 0:	//edit
			Intent i = new Intent(MainActivity.this,Edit.class);
			i.putExtra("copdata", longPressedItem);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
			
		case 1:	//delete
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		      alertDialogBuilder.setMessage("آیا مطمئنید این متن پاک شود؟");
		      
		      alertDialogBuilder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
		         @Override
		         public void onClick(DialogInterface arg0, int arg1) {
		        	 db.open();
		 			if(db.RemoveCopData(longPressedItem))
		 			{
		 				loadcopdata(db);
		 				Toast.makeText(getApplicationContext(), "پاک شد", Toast.LENGTH_SHORT).show();
		 			}else
		 				Toast.makeText(getApplicationContext(), "پاک نشد! دوباره تلاش کنید", Toast.LENGTH_SHORT).show();
		 			db.close();		         }
		      });
		      
		      alertDialogBuilder.setNegativeButton("خیر",new DialogInterface.OnClickListener() {
		         @Override
		         public void onClick(DialogInterface dialog, int which) {
		            dialog.cancel();
		         }
		      });
		      
		      AlertDialog alertDialog = alertDialogBuilder.create();
		      alertDialog.show();
			break;
			
		case 2:	//copy
			try {
				int sdk = android.os.Build.VERSION.SDK_INT;
				if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
				    clipboard2.setText(longPressedItem);
				    Toast.makeText(getApplicationContext(), "کپی شد", Toast.LENGTH_SHORT).show();	
				    
				} else {				   
				    clipboard.setText(longPressedItem);				
				    Toast.makeText(getApplicationContext(), "کپی شد", Toast.LENGTH_SHORT).show();
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
			break;
			
		case 3:	//share
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
		    sharingIntent.setType("text/plain");
		    //String shareBody = "Here is the share content body";
		    //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
		    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, longPressedItem);
		    startActivity(Intent.createChooser(sharingIntent, "اشتراک گذاری با:"));
			break;
			
		default:
			break;
		}
    	return true;
    }
	
	public static void loadcopdata(database db){
		al.clear();
		db.open();
		db.TransferCopData("copytable");
		db.close();
		aa.notifyDataSetChanged();
		
	}
	
	private void Notify(String notificationTitle, String notificationMessage){
	      NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	      @SuppressWarnings("deprecation")
	      
	      Notification notification = new Notification(R.drawable.ic_launcher,"متن جدید اضافه شد",
	    		  System.currentTimeMillis());
	      notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL ;
	      
	      Intent notificationIntent = new Intent(this,MainActivity.class);
	      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
	      
	      notification.setLatestEventInfo(MainActivity.this, notificationTitle,notificationMessage, pendingIntent);
	      notificationManager.notify(9999, notification);
	   }
	
}
