package com.zsoft.hubdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

public class CalculatorFragment extends Fragment {

	protected OnCalculationRequestedListener mListener = null;
	protected ShowAllListener mShowAllListener = null;
	protected OnDisconnectionRequestedListener mDisconnectListener = null;
	protected EditText mValue1TextBox = null; 
	protected EditText mValue2TextBox = null; 
	protected RadioButton mPlusButton = null;
	protected CheckBox mShowAllCheckBox = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.calculator_fragment, container, false);
	    
	    mValue1TextBox = (EditText) view.findViewById(R.id.value1);
	    mValue2TextBox = (EditText) view.findViewById(R.id.value2);
	    mPlusButton = (RadioButton) view.findViewById(R.id.radioPlus);
	    Button button = (Button) view.findViewById(R.id.btnCalculate);
	    button.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	    	  mListener.
	    	  calculate(Integer.parseInt(mValue1TextBox.getText().toString()),
	    			    Integer.parseInt(mValue2TextBox.getText().toString()),
	    			    mPlusButton.isChecked() ? "Add" : "Sub" );
	      }
	    });
	    Button disconnectButton = (Button) view.findViewById(R.id.btnDisconnect);
	    disconnectButton.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	    	  mDisconnectListener.DisconnectionRequested();
	      }
	    });
	    mShowAllCheckBox = (CheckBox) view.findViewById(R.id.chkShowAllCalculations);
		mShowAllCheckBox.setChecked(mShowAllListener.getShowAll());
	    mShowAllCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(mShowAllListener!=null)
					mShowAllListener.setShowAll(isChecked);
			}
		});
	    
	    return view;
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		if(activity instanceof OnCalculationRequestedListener)
		{
			mListener = (OnCalculationRequestedListener)activity;
		}
		else
			throw new ClassCastException(activity.toString() + " must implemenet CalculatorFragment.OnCalculationRequested");
		
		if(activity instanceof ShowAllListener)
		{
			mShowAllListener = (ShowAllListener)activity;
		}

		if(activity instanceof OnDisconnectionRequestedListener)
		{
			mDisconnectListener = (OnDisconnectionRequestedListener)activity;
		}
}

	public interface OnCalculationRequestedListener
	{
		public void calculate(int value1, int value2, String operator);
	}
	
	public interface ShowAllListener
	{
		public void setShowAll(Boolean value);
		public Boolean getShowAll();
	}
	
}
