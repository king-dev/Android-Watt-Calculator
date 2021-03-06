package com.example.wattcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import java.lang.NumberFormatException;
import java.text.NumberFormat;
import java.util.HashMap;

public class WattCalculator extends Activity
{
    private EditText mWattText;
    private EditText mKWhText;
    private TextView mCostPerYearText;
    private TextView mCostPerDayText;
    private AutoCompleteTextView mState;
    private HashMap<String, String> mCostTable;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.main_layout);

	mCostPerYearText = (TextView)findViewById(R.id.cost_per_year_text);
	mCostPerDayText = (TextView)findViewById(R.id.cost_per_day_text);
	mWattText = (EditText)findViewById(R.id.watt_text);
	mKWhText = (EditText)findViewById(R.id.kilowatt_hour_text);
	mState = (AutoCompleteTextView)findViewById(R.id.state_autocomplete);

        mCostTable = (new AverageCostTable()).getCostTable();

	ArrayAdapter<String> adapter =
	    new ArrayAdapter<String>(this, R.layout.list_item,
				     (String[])mCostTable.keySet().toArray(new String[0]));
	mState.setAdapter(adapter);
	mWattText.addTextChangedListener(mCalculateWatcher);
	mKWhText.addTextChangedListener(mCalculateWatcher);
	setupStateListener();
	blankOutCalculationResults();
    }

    private void setupStateListener() {
	mState.addTextChangedListener(new TextWatcher() {
		public void afterTextChanged(Editable s) {
		    String stateText = mState.getText().toString();
		    String kwhText = mCostTable.get(stateText);
		    if (kwhText != null) {
			mKWhText.setText(kwhText);
		    }
		}
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
	    });
    }

    private TextWatcher mCalculateWatcher = new TextWatcher() {
	    public void afterTextChanged(Editable s) {
		if (canCalculationProceed()) {
		    calculate();
		} else {
		    blankOutCalculationResults();
		}
	    }
	    public void onTextChanged(CharSequence s, int start, int before, int count) {

	    }
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	    }
	};

    private void blankOutCalculationResults() {
	mCostPerDayText.setText(formatCurrency(0.0));
	mCostPerYearText.setText(formatCurrency(0.0));
    }

    private boolean canCalculationProceed() {
	return (isPositiveNumber(mWattText.getText().toString())
		&& isPositiveNumber(mKWhText.getText().toString()));
    }

    private boolean isPositiveNumber(String s) {
	try {
	    double num = Double.parseDouble(s);
	    return (num > 0);
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    private void calculate() {
	double costPerDay = Double.valueOf(mWattText.getText().toString()) / 1000
	    * Double.valueOf(mKWhText.getText().toString()) * 24;
	double costPerYear = costPerDay * 365;

	mCostPerDayText.setText(formatCurrency(costPerDay));
	mCostPerYearText.setText(formatCurrency(costPerYear));
    }

    private String formatCurrency(double money) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(money);
    }

    public void toggleStateVisibility(View v) {
	TableRow stateRow = (TableRow)findViewById(R.id.state_row);
	View helpIcon = findViewById(R.id.help_icon);
	if (stateRow.getVisibility() == View.VISIBLE) {
	    stateRow.setVisibility(View.GONE);
	    helpIcon.setVisibility(View.VISIBLE);
	} else {
            stateRow.setVisibility(View.VISIBLE);
	    helpIcon.setVisibility(View.GONE);
	}
    }
}
