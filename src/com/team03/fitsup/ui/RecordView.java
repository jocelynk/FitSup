package com.team03.fitsup.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;
import com.team03.fitsup.R;
import com.team03.fitsup.data.AttributeTable;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.RecordTable;


//Needs a lot of thinking and work
public class RecordView extends Activity {
	
	private static final String TAG = "RecordView";
	private static final boolean DEBUG = true;	
	
	private DatabaseAdapter mDbAdapter;
    private TextView mDateText;
    private TextView mValueText;
    private TextView mHrText;
    private TextView mMinText;
    private TextView mSecText;
    private Long mRowId;
    private Long wreRowId;
    private Long e_id;
    private String date;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.records_view);

    	if (DEBUG) Log.v(TAG, "+++ ON CREATE +++");
    	
    	mDbAdapter = new DatabaseAdapter(getApplicationContext());
    	mDbAdapter.open();

    	mDateText = (TextView) findViewById(R.id.date);
    	mValueText = (TextView) findViewById(R.id.value);
    	mHrText = (TextView) findViewById(R.id.hr);
    	mMinText = (TextView) findViewById(R.id.min);
    	mSecText = (TextView) findViewById(R.id.sec);

    	mRowId = (savedInstanceState == null) ? null :
    	    (Long) savedInstanceState.getSerializable(RecordTable.COLUMN_ID);
    	if (mRowId == null) {
    	    Bundle extras = getIntent().getExtras();
    	    mRowId = extras != null ? extras.getLong(RecordTable.COLUMN_ID)
    	                            : null;
    	}
    	
    	date = (savedInstanceState == null) ? null :
    	    (String) savedInstanceState.getSerializable(RecordTable.COLUMN_DATE);
    	if (date == null) {
    	    Bundle extras = getIntent().getExtras();
    	    date = extras != null ? extras.getString(RecordTable.COLUMN_DATE)
    	                            : null;
    	}
    	
    	wreRowId = (savedInstanceState == null) ? null :
    	    (Long) savedInstanceState.getSerializable(RecordTable.COLUMN_WRKT_RTNE_E_ID);
    	if (wreRowId == null) {
    	    Bundle extras = getIntent().getExtras();
    	    wreRowId = extras != null ? extras.getLong(RecordTable.COLUMN_WRKT_RTNE_E_ID)
    	                            : null;
    	}
    	//mdbAdapter.fetchExerciseByWREAndRecord(wreRowId)
    	//switch(exercise_id)
    	//case 1
    	
    	populateFields();
    
    }
    
    
    private void populateFields() {
            Cursor records = mDbAdapter.fetchAllRecordAttrByDate(date, wreRowId);
            startManagingCursor(records);
            if(records != null){
            	records.moveToFirst();
            }
            while(records.isAfterLast() == false)
            {
            	long rowId = records.getLong(records.getColumnIndexOrThrow(RecordTable.COLUMN_ID));
            	if(DEBUG) Log.v(TAG,""+rowId );
            	Cursor record = mDbAdapter.fetchAttributeOfRecord(rowId);
            	if(DEBUG) Log.v(TAG,"error start here?" );
            	String attribute = record.getString(record.getColumnIndexOrThrow(AttributeTable.COLUMN_NAME));
            	if(DEBUG) Log.v(TAG, attribute );
            	if(attribute.equals("Time")) {
            		//fix this later
            		double value = records.getDouble(records.getColumnIndexOrThrow(RecordTable.COLUMN_VALUE));
            		if(DEBUG) Log.v(TAG, ""+value );
            		double hour = Math.floor(value/60);
            		double min = Math.floor(((value/60) - hour)*60);
            		double sec = ((((value/60) - hour)*60) - min)*60;
            		mHrText.setText(String.valueOf(hour));
            		mMinText.setText(String.valueOf(min));
            		mSecText.setText(String.valueOf(sec));
            		record.close();
            		
            	} else {
            		mValueText.setText(records.getString(records.getColumnIndexOrThrow(RecordTable.COLUMN_VALUE)));
            		record.close();
            	}
            	//need to iterate through one record and check to see if it's time
            	
            	records.moveToNext();
            }
            records.close();
            mDateText.setText(date);
    }

}
