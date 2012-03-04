package com.team03.fitsup.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.ExerciseTable;
import com.team03.fitsup.data.WorkoutRoutineTable;

public class WorkoutRoutineView extends ListActivity {
	private DatabaseAdapter mDbAdapter;
    private TextView mNameText;
    private TextView mDescriptionText;
    private Long mRowId;
    private Long eRowId;
    
	private static final int ACTIVITY_CREATE=0;

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
    
    private void fillData() {
    	Cursor exercisesCursor = mDbAdapter.fetchAllWorkoutExercises(mRowId);
        startManagingCursor(exercisesCursor); //this is deprecated for above 2.3, look up solution later, something Alex said

        String[] from = new String[] { ExerciseTable.COLUMN_NAME };
        int[] to = new int[] { R.id.text1 };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter exercises = new SimpleCursorAdapter(this, R.layout.workouts_row, exercisesCursor, from, to);
        setListAdapter(exercises);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createWorkoutExercise();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbAdapter.deleteExerciseFromWorkout(mRowId, info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    public void createWorkoutExercise() {
    	Intent i = new Intent(this, WorkoutExerciseEdit.class);
		i.putExtra(WorkoutRoutineTable.COLUMN_ID, mRowId);
    	startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
