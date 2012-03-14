package com.team03.fitsup.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;
import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.RecordTable;


//Needs a lot of thinking and work
public class RecordView extends Activity {
	
	private static final String TAG = "RecordView";
	private static final boolean DEBUG = true;	
	
	private DatabaseAdapter mDbAdapter;
    private TextView mIdText;
    private TextView mValueText;
    private Long mRowId;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.records_view);

    	if (DEBUG) Log.v(TAG, "+++ ON CREATE +++");
    	
    	mDbAdapter = new DatabaseAdapter(getApplicationContext());
    	mDbAdapter.open();

    	mIdText = (TextView) findViewById(R.id.record_id);
    	mValueText = (TextView) findViewById(R.id.value);

    	mRowId = (savedInstanceState == null) ? null :
    	    (Long) savedInstanceState.getSerializable(RecordTable.COLUMN_ID);
    	if (mRowId == null) {
    	    Bundle extras = getIntent().getExtras();
    	    mRowId = extras != null ? extras.getLong(RecordTable.COLUMN_ID)
    	                            : null;
    	}
    	
    	populateFields();
    
    }
    
    
    private void populateFields() {
        if (mRowId != null) {
            Cursor workout = mDbAdapter.fetchRecord(mRowId);
            startManagingCursor(workout);
            mIdText.setText(workout.getString(
            		workout.getColumnIndexOrThrow(RecordTable.COLUMN_ID)));
            mValueText.setText(workout.getString(
            		workout.getColumnIndexOrThrow(RecordTable.COLUMN_VALUE)));
        }
    }

}
