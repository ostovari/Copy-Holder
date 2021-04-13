package com.vahid.copyholder3;

import java.util.ArrayList;
import com.vahid.copyholder3.R;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {
	
	//Declare
	int sdk = android.os.Build.VERSION.SDK_INT;
	private static final String TAG = "copHoldMain";
	public static ArrayList<String> al;
	static ArrayAdapter<String> aa;
	private database db;
	String longPressedItem;
	static boolean isRunning = false;
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
				MainActivity.aa.getFilter().filter(cs);
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});

		registerForContextMenu(list);

		ToggleButton tglb = (ToggleButton) findViewById(R.id.toggleButton1);
		tglb.setChecked(isRunning);
	
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
				if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
					android.text.ClipboardManager clipboard2 = (android.text.ClipboardManager)
				    		getSystemService(Context.CLIPBOARD_SERVICE);
				    clipboard2.setText(longPressedItem);
				    Toast.makeText(getApplicationContext(), "کپی شد", Toast.LENGTH_SHORT).show();	
				    
				} else {
					android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
							getSystemService(Context.CLIPBOARD_SERVICE);
				    clipboard.setText(longPressedItem);				
				    Toast.makeText(getApplicationContext(), "کپی شد", Toast.LENGTH_SHORT).show();
				}
			}catch (Exception e) {
				Log.e(TAG, "error on copy...!");
				Toast.makeText(getApplicationContext(), "کپی نشد! لطفا دوباره تلاش کنید", Toast.LENGTH_SHORT).show();
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
	
	public void serciveHandle(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    if (on) {
	    	isRunning = true;
	    	startService(new Intent(getBaseContext(), Holdservice.class));
	    } else {
	    	isRunning = false;
	    	stopService(new Intent(getBaseContext(), Holdservice.class));
	    }
	}
	
}
