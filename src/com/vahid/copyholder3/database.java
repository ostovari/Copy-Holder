package com.vahid.copyholder3;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class database extends SQLiteOpenHelper {

    public final String   path = "data/data/com.vahid.copyholder3/databases/";
    public final String   Name = "copydata";
    public SQLiteDatabase mydb;
    private final Context mycontext;


    public database(Context context) {
        super(context, "copydata", null, 1);
        mycontext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase arg0) {

    }


    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }


    public void useable() {
        boolean checkdb = checkdb();
        if (checkdb) {

        } else {
            this.getReadableDatabase();
            try {
                copydatabase();
            }
            catch (IOException e) {	
            
            }
        }
    }


    public void open() {
        mydb = SQLiteDatabase.openDatabase(path + Name, null, SQLiteDatabase.OPEN_READWRITE);
    }


    @Override
    public void close() {
        mydb.close();
    }


    public boolean checkdb() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(path + Name, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLException e) {

        }
        return db != null ? true : false;
    }


    public void copydatabase() throws IOException {

        OutputStream myOutput = new FileOutputStream(path + Name);
        byte[] buffer = new byte[1024];
        int lenght;
        InputStream myInput = mycontext.getAssets().open(Name);
        while ((lenght = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, lenght);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }
    public void TransferCopData (String table){
    	
    	android.database.Cursor cursor = mydb.rawQuery("SELECT cdata FROM "+table , null);
    	if (cursor.moveToLast()) {
    	    do {
    	      MainActivity.al.add(cursor.getString(0));                 
    	    } while (cursor.moveToPrevious());
    	}
    }
    
    public void AddCopData(String table, String clipholder) {
		ContentValues cv = new ContentValues();
		cv.put("cdata", clipholder);
		mydb.insert(table, null, cv);
		
	}
    
    public boolean RemoveCopData(String data) {
		return mydb.delete("copytable", "cdata =? ", new String[] { data }) > 0;
		
	}
    
    public boolean SearchData(String data) {
    	android.database.Cursor cursor = mydb.rawQuery("SELECT cdata FROM copytable WHERE cdata =?", new String[] { data });
    	boolean hasObject = false;
        if(cursor.moveToFirst()){
            hasObject = true;
        }
        return hasObject;
		
	}
    
    public boolean UpdateCopData(String table, String data, String newdata) {
    	ContentValues cv = new ContentValues();
		cv.put("cdata", newdata);
		return mydb.update(table, cv, "cdata =? ", new String[] { data }) > 0;
    	
    }

    
}
