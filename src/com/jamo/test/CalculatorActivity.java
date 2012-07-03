package com.jamo.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CalculatorActivity extends Activity {
	private EditText userInput=null;
	private String totalString=null;
	private double decInput=0;
	private double total=0;
	private char operand = ' ';
	private boolean newCalc = true;
	private boolean lastWasEqual = false;
	private boolean failed = true;
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator);
       
        userInput = (EditText) findViewById(R.id.calculatorInput);
        userInput.setHintTextColor(-16777216);
        
        final InputMethodManager keyboard = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(userInput, 0);
        
        if (keyboard != null){
        	keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
      
        final Button plus = (Button) findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	calculate('+');
            }
        });
        
        final Button minus = (Button) findViewById(R.id.minus);
        minus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	calculate('-');
            }
        });
        
        final Button mul = (Button) findViewById(R.id.mul);
        mul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	calculate('*');
            }
        });
        
        final Button div = (Button) findViewById(R.id.div);
        div.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	calculate('/');
            }
        });
        
        final Button equal = (Button) findViewById(R.id.equal);
        equal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Perform action on click
            	calculate('=');
            }
        });
        
        final Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Perform action on click
            	newCalc = true;
            	lastWasEqual = false;
            	total = 0;
            	decInput = 0;
            	userInput.setText("");
            	userInput.setHint("");
            }
        });
        
        final Button exitButton = (Button) findViewById(R.id.exit2);
        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Perform action on click
            	keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            	finish();
            }
        });
    }
    
    public void calculate(char passedOperand){
    	if(newCalc){
    		total = getDecFromUser(total);
    		decInput = 0;
			operand = passedOperand;
			newCalc = false;
    	}
    	else{
    		if (!lastWasEqual){
    			decInput = getDecFromUser(decInput);
    			doMath();
    		}
    		else if (lastWasEqual && passedOperand != '='){
    			decInput = getDecFromUser(decInput);
    			if (failed){
    				operand = passedOperand;
    			}
    			else {
    				total = getDecFromUser(total);
    	    		decInput = 0;
    				operand = passedOperand;
    			}
    		}
    		else{
    			doMath();
    		}
    		
			if (passedOperand != '=')
			{
				operand = passedOperand;
				lastWasEqual = false;
			}
			else{
				lastWasEqual = true;
			}
    	}
    	totalString = Double.toString(total);
    	userInput.setText("");
    	userInput.setHint(totalString);
    	//Toast.makeText(CalculatorActivity.this, totalString, Toast.LENGTH_SHORT).show();
    	return;
    }
    
    public void doMath(){
    	switch(operand){
    	case '+':	total = total + decInput;
    				break;
    	case '-': 	total = total - decInput;
					break;
    	case '*': 	total = total * decInput;
					break;
    	case '/': 	total = total / decInput;
					break;
    	case '=': 	break;
    	default : 	Toast.makeText(CalculatorActivity.this, "ERROR 002", Toast.LENGTH_SHORT).show();
					break;
    	}
    	return;
    }
    
    public double getDecFromUser(double passedNum){
    	double temp = passedNum;
    	try{
    		passedNum = Double.parseDouble(userInput.getText().toString());
    		failed = false;
    	}
    	catch(IllegalArgumentException ex){
    		//Toast.makeText(CalculatorActivity.this, "ERROR 001", Toast.LENGTH_SHORT).show();
    		passedNum = temp;
    		failed = true;
    	}
    	return passedNum;
    }
}