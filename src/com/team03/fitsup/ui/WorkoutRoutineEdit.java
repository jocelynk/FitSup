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
import com.team03.fitsup.data.WorkoutRoutineTable;

public class WorkoutRoutineEdit extends Activity {
	
	private static final String TAG = "WorkoutRoutineEdit";
	private static final boolean DEBUG = true;
	
	private DatabaseAdapter mDbAdapter;
    private EditText mNameText;
    private EditText mDescriptionText;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.workouts_edit);
    	
    	if (DEBUG) Log.v(TAG, "+++ ON CREATE +++");
    	
    	mDbAdapter = new DatabaseAdapter(getApplicationContext());
    	mDbAdapter.open();

    	mNameText = (EditText) findViewById(R.id.name);
    	mDescriptionText = (EditText) findViewById(R.id.description);

    	Button confirmButton = (Button) findViewById(R.id.confirm);

    	mRowId = (savedInstanceState == null) ? null :
    	    (Long) savedInstanceState.getSerializable(WorkoutRoutineTable.COLUMN_ID);
    	if (mRowId == null) {
    	    Bundle extras = getIntent().getExtras();
    	    mRowId = extras != null ? extras.getLong(WorkoutRoutineTable.COLUMN_ID)
    	                            : null;
    	}

    	populateFields();

    	confirmButton.setOnClickListener(new View.OnClickListener() {

    	    public void onClick(View view) {
    	        setResult(RESULT_OK);
    	        finish();
    	    }

    	});
        /*super.onCreate(savedInstanceState);
        setContentView(R.layout.workouts_edit);
        setTitle(R.string.edit_workout);

        mNameText = (EditText) findViewById(R.id.name);
        mDescriptionText = (EditText) findViewById(R.id.description);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString(WorkoutRoutineTable.COLUMN_NAME);
            String description = extras.getString(WorkoutRoutineTable.COLUMN_DESCRIPTION);
            mRowId = extras.getLong(WorkoutRoutineTable.COLUMN_ID);

            if (name != null) {
                mNameText.setText(name);
            }
            if (description != null) {
                mDescriptionText.setText(description);
            }
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Bundle bundle = new Bundle();

                bundle.putString(WorkoutRoutineTable.COLUMN_NAME, mNameText.getText().toString());
                bundle.putString(WorkoutRoutineTable.COLUMN_DESCRIPTION, mDescriptionText.getText().toString());
                if (mRowId != null) {
                    bundle.putLong(WorkoutRoutineTable.COLUMN_ID, mRowId);
                }

                Intent mIntent = new Intent();
                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                finish();
            }

        });*/
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		if (DEBUG) Log.v(TAG, "ON SAVED INSTANCE STATE");
        saveState();
        outState.putSerializable(WorkoutRoutineTable.COLUMN_ID, mRowId);
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
        populateFields();
    }
    
    private void populateFields() {
        if (mRowId != null) {
            Cursor workout = mDbAdapter.fetchWorkout(mRowId);
            startManagingCursor(workout);
            mNameText.setText(workout.getString(
            		workout.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_NAME)));
            mDescriptionText.setText(workout.getString(
            		workout.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_DESCRIPTION)));
        }
    }
    
    private void saveState() {
        String name = mNameText.getText().toString();
        String description = mDescriptionText.getText().toString();

        if (mRowId == null) {
            long id = mDbAdapter.createWorkout(name, description);
            if (id > 0) {
                mRowId = id;
            }
        } else {
        	mDbAdapter.updateWorkout(mRowId, name, description);
        }
    }
    
    	
}
