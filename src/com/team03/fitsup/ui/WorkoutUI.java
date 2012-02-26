package com.team03.fitsup.ui;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.WorkoutRoutineTable;
/*when i do stuff with the GUI, then use getContext(),
b/c UI is specific to single ACtivity, screen, so give it
Application context would crash, but context basically holds 
everything related to activity, so if passing contexts around, 
then can get stuck in a situation where a certain object is 
holding on to an activity and even if the activity gets 
destroyed and even if it gets destroyed will hold onto 
it and then cause memory leak*/

public class WorkoutUI extends ListActivity {
    private int mWorkoutNumber = 1;
    private DatabaseAdapter mDbAdapter; 
    public static final int INSERT_ID = Menu.FIRST;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workouts_index);
        mDbAdapter = new DatabaseAdapter(getApplicationContext());
        mDbAdapter.open();
        fillData();
        //getContext
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return result;
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case INSERT_ID:
            createWorkout();
            return true;
        }
       
        return super.onOptionsItemSelected(item);
    }
        
    public void createWorkout() {
    	String workoutName = "Workout " + mWorkoutNumber++;
    	mDbAdapter.createWorkout(workoutName, "");
    	fillData();
    }
    
    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbAdapter.fetchAllWorkouts();
        startManagingCursor(c);

        String[] from = new String[] { WorkoutRoutineTable.COLUMN_NAME };
        int[] to = new int[] { R.id.text1 };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.workouts_row, c, from, to);
        setListAdapter(notes);
    }
}
