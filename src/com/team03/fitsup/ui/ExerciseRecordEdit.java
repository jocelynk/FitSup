package com.team03.fitsup.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.RecordTable;
import com.team03.fitsup.data.WorkoutRoutineTable;

public class ExerciseRecordEdit extends Activity {
	private static final String TAG = "WorkoutRoutineEdit";
	private static final boolean DEBUG = true;
	
	private DatabaseAdapter mDbAdapter;
    private EditText mDateText;
    private EditText mHourText;
    private EditText mMinuteText;
    private EditText mSecondText;
    private EditText mValueText;
    private Long wreRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	//switch statement hear
    	setContentView(R.layout.records_edit);
    	
    	if (DEBUG) Log.v(TAG, "+++ ON CREATE +++");
    	
    	mDbAdapter = new DatabaseAdapter(getApplicationContext());
    	mDbAdapter.open();

    	mDateText = (EditText) findViewById(R.id.date);
    	mHourText = (EditText) findViewById(R.id.hr);
    	mMinuteText = (EditText) findViewById(R.id.min);
    	mSecondText = (EditText) findViewById(R.id.sec);
    	mValueText = (EditText) findViewById(R.id.sec);
    	
    	Button confirmButton = (Button) findViewById(R.id.confirm);

    	wreRowId = (savedInstanceState == null) ? null :
    	    (Long) savedInstanceState.getSerializable(RecordTable.COLUMN_WRKT_RTNE_E_ID);
    	if (wreRowId == null) {
    	    Bundle extras = getIntent().getExtras();
    	    wreRowId = extras != null ? extras.getLong(RecordTable.COLUMN_WRKT_RTNE_E_ID)
    	                            : null;
    	}

    	//populateFields();

    	confirmButton.setOnClickListener(new View.OnClickListener() {

    	    public void onClick(View view) {
    	        setResult(RESULT_OK);
    	        finish();
    	    }

    	});
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		if (DEBUG) Log.v(TAG, "ON SAVED INSTANCE STATE");
        saveState();
        outState.putSerializable(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
		if (DEBUG) Log.v(TAG, "+ ON PAUSE +");
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
		Log.v(TAG, "+ ON RESUME +");
        //populateFields();
    }
    
    /*private void populateFields() {
        if (mRowId != null) {
            Cursor workout = mDbAdapter.fetchWorkout(mRowId);
            startManagingCursor(workout);
            mNameText.setText(workout.getString(
            		workout.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_NAME)));
            mDescriptionText.setText(workout.getString(
            		workout.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_DESCRIPTION)));
        }
    }*/
    
    private void saveState() { //need to write exceptions
    	//switch case statement - later need to get exercise id //create SQL query or reuse another
        String date = mDateText.getText().toString();
        String hour = mHourText.getText().toString();
        String minute = mMinuteText.getText().toString();
        String second = mSecondText.getText().toString();
        String value = mValueText.getText().toString(); //distance
        long hr = Long.parseLong(hour);
        long min = Long.parseLong(minute);
        long sec = Long.parseLong(second);
        long savedTime = (hr*60)+min+(sec/60);
        long val= Long.parseLong(value);

        //time 
        mDbAdapter.createRecord(date,savedTime,1,wreRowId);
                 
        //distance
        mDbAdapter.createRecord(date,val,2,wreRowId);
        
        
    }
}
