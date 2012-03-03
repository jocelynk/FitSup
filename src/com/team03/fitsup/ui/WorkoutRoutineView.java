package com.team03.fitsup.ui;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.ExerciseTable;
import com.team03.fitsup.data.WorkoutRoutineTable;

public class WorkoutRoutineView extends ListActivity {
	private DatabaseAdapter mDbAdapter;
    private TextView mNameText;
    private TextView mDescriptionText;
    private Long mRowId;
    
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	mDbAdapter = new DatabaseAdapter(getApplicationContext());
    	mDbAdapter.open();

    	setContentView(R.layout.workouts_options);

    	mNameText = (TextView) findViewById(R.id.name);
    	mDescriptionText = (TextView) findViewById(R.id.description);

    	//Button confirmButton = (Button) findViewById(R.id.confirm);

    	mRowId = (savedInstanceState == null) ? null :
    	    (Long) savedInstanceState.getSerializable(WorkoutRoutineTable.COLUMN_ID);
    	if (mRowId == null) {
    	    Bundle extras = getIntent().getExtras();
    	    mRowId = extras != null ? extras.getLong(WorkoutRoutineTable.COLUMN_ID)
    	                            : null;
    	}
    	
    	populateFields();
    	fillData();
    	registerForContextMenu(getListView());
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
    
    private void fillData() {
        // Get all of the notes from the database and create the item list
    	Cursor exercisesCursor = mDbAdapter.fetchAllWorkoutExercises(mRowId);
        startManagingCursor(exercisesCursor); //this is deprecated, look up solution later, something Alex said

        String[] from = new String[] { ExerciseTable.COLUMN_NAME };
        int[] to = new int[] { R.id.text1 };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter exercises = new SimpleCursorAdapter(this, R.layout.workouts_row, exercisesCursor, from, to);
        setListAdapter(exercises);
    }
}
