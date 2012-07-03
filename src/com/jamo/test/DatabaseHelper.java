/*
 * File: DataBaseHelper.java
 * Author: Wei Ma
 * Date: 3/14/2011 
 * Modified 4/14/2011 Solved the problem of idkey table not found on some devices.
 */

package com.jamo.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "jamoDB.sqlite";
	public static final String TABLE_NAME = "idKey";
	private static final int DATABASE_VERSION = 1;
	
	//The Android's default system path of your application database.
    public static String DATABASE_PATH = "/data/data/com.jamo.test/databases/";
 
    private SQLiteDatabase myDataBase; 
    private final Context myContext;
    
  //Columns in the staff database
  	public static final String _ID = "_id";
  	public static final String LASTNAME = "lastName";
  	public static final String FIRSTNAME = "firstName";
  	public static final String OFFICELOC = "officeLoc";
  	public static final String EXTENSION = "extension";
  	public static final String CELLNUM = "cellNum";
  	public static final String HOMENUM = "homeNum";
  	public static int keyidnumber = 1;
  	public static int lastNameColumn = 1;
  	public static int firstNameColumn = 1;
  	public static int officeLocColumn = 1;
  	public static int extensionColumn = 1;
  	public static int cellNumColumn = 1;
  	public static int homeNumColumn = 1;
    
  	public static String[] FROM = {_ID, LASTNAME, FIRSTNAME, OFFICELOC, EXTENSION, CELLNUM, HOMENUM};
  	
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
		createDataBase();
	}
 
	/**
     * Creates a empty database on the system.
     * */
    public void createDataBase() {
    	try{
	    	boolean dbExist = checkDataBase();
	    	SQLiteDatabase db_read = null;
	 
	    	if(dbExist){
	    		try{
	    			copyDataBase();
	    		}catch(IOException e){
	    			throw new Error("Error copying database");
	    		}
	    		
	    	}else{
	 
	    		//By calling this method and empty database will be created into the default system path
	               //of your application so we are gonna be able to overwrite that database with our database.
	        	db_read = this.getReadableDatabase();
	        	db_read.close();
	 
	        	try {
	 
	    			copyDataBase();
	 
	    		} catch (IOException e) {
	 
	        		throw new Error("Error copying database");
	 
	        	}
	    	} 
	    
		    try {
		    	 
		 		openDataBase();
		 		Cursor cursor = myDataBase.query(TABLE_NAME, FROM, "_id = 1", null, null, null, null);
		 		keyidnumber = cursor.getColumnIndexOrThrow(_ID);
		 		lastNameColumn = cursor.getColumnIndexOrThrow(LASTNAME);
		 		firstNameColumn = cursor.getColumnIndexOrThrow(FIRSTNAME);
		 		officeLocColumn = cursor.getColumnIndexOrThrow(OFFICELOC);
		 		extensionColumn = cursor.getColumnIndexOrThrow(EXTENSION);
		 		cellNumColumn = cursor.getColumnIndexOrThrow(CELLNUM);
		 		homeNumColumn = cursor.getColumnIndexOrThrow(HOMENUM);
		 		
		 	}catch(SQLException sqle){
		 
		 		throw sqle;
		 	}
	 
	 	}finally{}
	    return;
    }
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DATABASE_PATH + DATABASE_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null,  SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
    
    private void copyDataBase() throws IOException{
    	 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        try{
    	 // Path to the just created empty db
    	 String outFileName = DATABASE_PATH + DATABASE_NAME;
 
    	 //Open the empty db as the output stream
    	 OutputStream myOutput = new FileOutputStream(outFileName);
 
    	 //transfer bytes from the inputfile to the outputfile
    	 byte[] buffer = new byte[1024];
    	 int length;
    	 while ((length = myInput.read(buffer))>0){
    	 	myOutput.write(buffer, 0, length);
    	 }
 
    	 //Close the streams
    	 myOutput.flush();
    	 myOutput.close();
    	 myInput.close();
        }
        catch(IOException e){
        	
        }
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DATABASE_PATH + DATABASE_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
    @Override
	public void onCreate(SQLiteDatabase db) {
    	 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
	
	public ArrayList<String> getAllItems(int spinner)
    {
		String[] index = {FIRSTNAME, LASTNAME, OFFICELOC, EXTENSION, CELLNUM, HOMENUM};
		int column = 0;
		Boolean empty = true;
		ArrayList<String> tempStr = new ArrayList<String>();
		int i = 0;
		if(spinner == 1)
		{
			Cursor cursor = this.myDataBase.query(TABLE_NAME, new String[] {index[spinner]}, null, null, null, null, null);
			 
	        if(cursor.getCount() >0)
	        {
	            while (cursor.moveToNext())
	            {
	            	 Boolean repeat = false;
	            	 int j = 0;
	            	 while(j < i)
	            	 {
	            		 if(tempStr.get(j).equals(cursor.getString(cursor.getColumnIndex(index[spinner]))))
	            		 {
	            			 repeat = true;
	            		 }
	            		 j++;
	            	 }
	            	 if(!repeat)
	            	 {
	            		 tempStr.add(cursor.getString(cursor.getColumnIndex(index[spinner])));
	            		 i++;
	            	 }
	             }
	            empty = false;
	        }
		}
		else{
			while (column <= 5)
			{
		        Cursor cursor = this.myDataBase.query(TABLE_NAME, new String[] {index[column]}, null, null, null, null, null);
		 
		        if(cursor.getCount() >0)
		        {
		            while (cursor.moveToNext())
		            {
		            	 Boolean repeat = false;
		            	 int j = 0;
		            	 while(j < i)
		            	 {
		            		 if(tempStr.get(j).equals(cursor.getString(cursor.getColumnIndex(index[column]))))
		            		 {
		            			 repeat = true;
		            		 }
		            		 j++;
		            	 }
		            	 if(!repeat)
		            	 {
		            		 tempStr.add(cursor.getString(cursor.getColumnIndex(index[column])));
		            		 i++;
		            	 }
		             }
		            empty = false;
		        }
		    column++;
		    }
		}
		if(!empty)
		{
			return tempStr;
		}
		else
		{
		    return new ArrayList<String>();
		}
    }
}
