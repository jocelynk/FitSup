package com.team03.fitsup.ui;

import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.WorkoutRoutineTable;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
import android.widget.EditText;

public class WorkoutRoutineView extends Activity {
	private DatabaseAdapter mDbAdapter;
    private EditText mNameText;
    private EditText mDescriptionText;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	mDbAdapter = new DatabaseAdapter(getApplicationContext());
    	mDbAdapter.open();

    	setContentView(R.layout.workouts_view_tab);

    	mNameText = (EditText) findViewById(R.id.name);
    	mDescriptionText = (EditText) findViewById(R.id.description);

    	//Button confirmButton = (Button) findViewById(R.id.confirm);

    	mRowId = (savedInstanceState == null) ? null :
    	    (Long) savedInstanceState.getSerializable(WorkoutRoutineTable.COLUMN_ID);
    	if (mRowId == null) {
    	    Bundle extras = getIntent().getExtras();
    	    mRowId = extras != null ? extras.getLong(WorkoutRoutineTable.COLUMN_ID)
    	                            : null;
    	}

    	populateFields();

    	//confirmButton.setOnClickListener(new View.OnClickListener() {

    	   // public void onClick(View view) {
    	    //    setResult(RESULT_OK);
    	    //    finish();
    	   // }

    	//});
    }
    
    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(WorkoutRoutineTable.COLUMN_ID, mRowId);
    }*/
    
    /*@Override
    protected void onPause() {
        super.onPause();
        saveState();
    }*/
    
    /* @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }*/
    
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
    
   /* private void saveState() {
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
    }*/
}
