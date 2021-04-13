package com.vahid.copyholder3;

import com.vahid.copyholder3.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Edit extends ActionBarActivity {
	
	private database db;
	String copdata;
	EditText edittext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		db = new database(this);
		db.useable();
		edittext = (EditText) findViewById(R.id.editText1);
		Button save = (Button) findViewById(R.id.btnsave);
		Button cancel = (Button) findViewById(R.id.btncancel);
		copdata = getIntent().getExtras().getString("copdata");
		
		edittext.setText(copdata);
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				db.open();
				db.UpdateCopData("copytable", copdata, edittext.getText().toString());
				MainActivity.loadcopdata(db);
				db.close();
				Intent j = new Intent(Edit.this,MainActivity.class);
				j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(j);
				
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent k = new Intent(Edit.this,MainActivity.class);
				k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(k);
				
			}
		});
	}

	
}
