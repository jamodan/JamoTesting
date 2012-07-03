package com.jamo.test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StaffActivity extends Activity{
	
	private AutoCompleteTextView userInput=null;
	
	private DatabaseHelper myDbHelper;
	
	public Spinner spinner = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.staff);
		
		myDbHelper = new DatabaseHelper(this);
		
		spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.staff_fields, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

		userInput = (AutoCompleteTextView) findViewById(R.id.seachInput);
        userInput.setHintTextColor(-16777216);
        
        ArrayList<String> allItems = myDbHelper.getAllItems(spinner.getSelectedItemPosition());
        ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(this, R.layout.list_item, allItems);
        //ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(this, R.layout.list_item, myDbHelper.getAllItems(spinner.getSelectedItemPosition()));
        userInput.setThreshold(1);
	    userInput.setAdapter(adapter0);
        userInput.setOnKeyListener(new OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                        	//Toast.makeText(StaffActivity.this, "Enter was pressed", Toast.LENGTH_SHORT).show();
                        	search(userInput);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        
        
        
        final Button searchButton = (Button) findViewById(R.id.searchStaff);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Perform action on click
            	search(userInput);
            }
        });
        
        final Button exitButton = (Button) findViewById(R.id.exit3);
        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Perform action on click
            	//keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            	myDbHelper.close();
            	finish();
            }
        });  
	}
	
	public void search(EditText userInput){
		int numResults = 0;
		String returnFields = "_id,lastName,firstName,officeLoc,extension,cellNum,homeNum";
		String searchText = getStringFromUser(userInput);
		String searchColumn = findColumn(spinner.getSelectedItemPosition());
	
		String myPath = DatabaseHelper.DATABASE_PATH + DatabaseHelper.DATABASE_NAME;
		SQLiteDatabase staffDb = SQLiteDatabase.openDatabase(myPath, null,  SQLiteDatabase.OPEN_READONLY);
		
		Cursor results = null;
		if (spinner.getSelectedItemPosition() != 0){
			results = staffDb.rawQuery("SELECT " + returnFields + " FROM " + DatabaseHelper.TABLE_NAME + " WHERE upper(" + searchColumn + ") like upper(?);", new String [] {"%" + searchText + "%"});
		}
		else{
			results = staffDb.rawQuery("SELECT " + returnFields + " FROM " + DatabaseHelper.TABLE_NAME + " WHERE upper(lastName) like upper(?) OR upper(firstName) like upper(?) OR upper(officeLoc) like upper(?) OR upper(extension) like upper(?) OR upper(cellNum) like upper(?) OR upper(homeNum) like upper(?);", new String [] {"%" + searchText + "%","%" + searchText + "%","%" + searchText + "%","%" + searchText + "%","%" + searchText + "%","%" + searchText + "%"});
		}
		if (results.moveToFirst())
		{
			InputMethodManager keyboard = (InputMethodManager)
			getSystemService(Context.INPUT_METHOD_SERVICE);
			keyboard.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
			//keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			results.moveToPrevious(); // sets the pointer to one before so that the wile loop starts on the first entry
			this.setContentView(R.layout.search_results);
			final EditText dispResults = (EditText) findViewById(R.id.editText1);
			while(results.moveToNext()){
				//final EditText dispResults = (EditText) findViewById(R.id.editText1);
		        dispResults.append("First Name:        " + results.getString(DatabaseHelper.firstNameColumn) + "\r\n");
		        dispResults.append("Last Name:          " + results.getString(DatabaseHelper.lastNameColumn) + "\r\n");
		        dispResults.append("Office Location:  " + results.getString(DatabaseHelper.officeLocColumn) + "\r\n");
		        dispResults.append("Extension:           " + results.getString(DatabaseHelper.extensionColumn) + "\r\n");
		        dispResults.append("Cell Number:      " + results.getString(DatabaseHelper.cellNumColumn) + "\r\n");
		        dispResults.append("Home Number:  " + results.getString(DatabaseHelper.homeNumColumn) + "\r\n\r\n");
				
				numResults++;
			}
			
			//Linkify.addLinks(dispResults, Linkify.PHONE_NUMBERS | Linkify.WEB_URLS);
			Pattern patternFullPhoneNum = Pattern.compile("[(]*\\d{3}+[)]*[[.][\\s][-]]*\\d{3}+[[.][\\s][-]]*\\d{4}+");
		    String schemeFullPhoneNum = "tel:";
		    Linkify.addLinks(dispResults, patternFullPhoneNum, schemeFullPhoneNum);
			
			Pattern patternAreaCode = Pattern.compile("\\d{3}+[[.][\\s][-]]*\\d{4}+");
		    String schemeAreaCode = "tel:605";
		    Linkify.addLinks(dispResults, patternAreaCode, schemeAreaCode);
			
			Pattern patternExtension = Pattern.compile("\\d{4}+");
		    String schemeExtension = "tel:605 688";
		    Linkify.addLinks(dispResults, patternExtension, schemeExtension);
			
			Pattern patternSOHO = Pattern.compile("[S]+[O]+[H]+[O]+\\s*\\d*[[A-Z][a-z]]*");
		    String schemeSOHO = "geo:0,0?q=" + ("Old+Horticulture,+Brookings,+SD");
		    
			Pattern patternSAD = Pattern.compile("[S]+[A]+[D]+\\s*\\d*[[A-Z][a-z]]*");
		    String schemeSAD = "geo:0,0?q=" + ("Administration+Bldg,+Brookings,+SD");
		    
			Pattern patternSSU = Pattern.compile("[S]+[S]+[U]+\\s*\\d*[[A-Z][a-z]]*");
		    String schemeSSU = "geo:0,0?q=" + ("SDSU+Student+Union,+Brookings,+SD");
		    
			Pattern patternSPC = Pattern.compile("[S]+[P]+[C]+\\s*\\d*[[A-Z][a-z]]*");
		    String schemeSPC = "geo:0,0?q=" + ("Pugsley+Continuing+Education+Center,+Brookings,+SD");
		    
			Pattern patternSAG = Pattern.compile("[S]+[A]+[G]+\\s*\\d*[[A-Z][a-z]]*");
		    String schemeSAG = "geo:0,0?q=" + ("Agricultural+Hall,+Brookings,+SD");
		    
		    TransformFilter appendFilter = new TransformFilter() {
                public final String transformUrl(final Matcher match, String url) {
                    return new String(" ");
                }
            };

		    Linkify.addLinks(dispResults, patternSOHO, schemeSOHO, null, appendFilter);
			Linkify.addLinks(dispResults, patternSAD, schemeSAD, null, appendFilter);
			Linkify.addLinks(dispResults, patternSSU, schemeSSU, null, appendFilter);
			Linkify.addLinks(dispResults, patternSPC, schemeSPC, null, appendFilter);
			Linkify.addLinks(dispResults, patternSAG, schemeSAG, null, appendFilter);
			
		}
		Toast.makeText(StaffActivity.this, "Search returned " + numResults + " results", Toast.LENGTH_SHORT).show();
		return;
	}
	
	// Read from the EditText and convert to string returning string
	public String getStringFromUser(EditText userInput){
    	String temp = "";
    	try{
    		temp = userInput.getText().toString();
    	}
    	catch(IllegalArgumentException ex){
    		//Toast.makeText(StaffActivity.this, "Enter was pressed", Toast.LENGTH_SHORT).show();
    		temp = "";
    	}
    	return temp;
    }
	
	public String findColumn(int spinnerSelection){
		//Toast.makeText(StaffActivity.this, spinnerSelection, Toast.LENGTH_SHORT).show();
		
		if(spinnerSelection == 1){
			//Toast.makeText(StaffActivity.this, "== firstName", Toast.LENGTH_SHORT).show();
			return "firstName";
		}
		else if(spinnerSelection == 2){
			//Toast.makeText(StaffActivity.this, "== lastName", Toast.LENGTH_SHORT).show();
			return "lastName";
		}
		else if(spinnerSelection == 3){
			//Toast.makeText(StaffActivity.this, "== officeLoc", Toast.LENGTH_SHORT).show();
			return "officeLoc";
		}
		else if(spinnerSelection == 4){
			//Toast.makeText(StaffActivity.this, "== extension", Toast.LENGTH_SHORT).show();
			return "extension";
		}
		else if(spinnerSelection == 5){
			//Toast.makeText(StaffActivity.this, "== cellNum", Toast.LENGTH_SHORT).show();
			return "cellNum";
		}
		else if(spinnerSelection == 6){
			//Toast.makeText(StaffActivity.this, "== homeNum", Toast.LENGTH_SHORT).show();
			return "homeNum";
		}
		else{
			//Toast.makeText(StaffActivity.this, "== ALL", Toast.LENGTH_SHORT).show();
			//return "_id,lastName,firstName,officeLoc,extension,cellNum,homeNum";
			return "*";
		}
	}
}