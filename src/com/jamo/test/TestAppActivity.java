package com.jamo.test;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TestAppActivity extends Activity {
	private Button calculator=null;
	private Button staff=null;
	private Button exitButton=null;
	Intent intent;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        
        calculator = (Button)findViewById(R.id.goToCalculator);
        calculator.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
	    		intent = new Intent(TestAppActivity.this,CalculatorActivity.class);
	    	
	    		 try {
	                    startActivity(intent);
	                }
	             catch (ActivityNotFoundException e){
	                    Toast.makeText(TestAppActivity.this, "NO Viewer", Toast.LENGTH_SHORT).show();
	                }
	    	}
	    }
	    );
        
        staff = (Button)findViewById(R.id.goToStaff);
        staff.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
	    		intent = new Intent(TestAppActivity.this,StaffActivity.class);
	    	
	    		 try {
	                    startActivity(intent);
	                }
	             catch (ActivityNotFoundException e){
	                    Toast.makeText(TestAppActivity.this, "NO Viewer", Toast.LENGTH_SHORT).show();
	                }
	    	}
	    }
	    );
        
        exitButton = (Button)findViewById(R.id.exit);
        exitButton.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view){
	    		finish();
	    	}
	    }
	    );
    }
}