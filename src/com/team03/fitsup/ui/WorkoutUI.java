package com.team03.fitsup.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.WorkoutRoutineTable;

/*when i do stuff with the GUI, then use getContext(),
 b/c UI is specific to single Activity, screen, so give it
 Application context would crash, but context basically holds 
 everything related to activity, so if passing contexts around, 
 then can get stuck in a situation where a certain object is 
 holding on to an activity and even if the activity gets 
 destroyed and even if it gets destroyed will hold onto 
 it and then cause memory leak*/

public class WorkoutUI extends ListActivity {

	private static final String TAG = "WorkoutUI";
	private static final boolean DEBUG = true;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_VIEW = 1;
	private static final int ACTIVITY_EDIT = 2;

	private static final int INSERT_ID = Menu.FIRST;

	private DatabaseAdapter mDbAdapter;

	// private Cursor workoutsCursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workouts_index);
		Log.v(TAG, "+++ ON CREATE +++");

		mDbAdapter = new DatabaseAdapter(getApplicationContext());
		mDbAdapter.open();
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		// Get all of the notes from the database and create the item list
		Cursor workoutsCursor = mDbAdapter.fetchAllWorkouts();
		startManagingCursor(workoutsCursor);

		String[] from = new String[] { WorkoutRoutineTable.COLUMN_NAME };
		int[] to = new int[] { R.id.text1 };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter workouts = new SimpleCursorAdapter(this,
				R.layout.workouts_row, workoutsCursor, from, to);
		setListAdapter(workouts);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // TODO
	 * Auto-generated method stub boolean result =
	 * super.onCreateOptionsMenu(menu); menu.add(0, INSERT_ID, 0,
	 * R.string.menu_insert); return result;
	 * 
	 * }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createWorkout();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.workoutroutine_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Cursor c = mDbAdapter.fetchWRE(info.id);

		switch (item.getItemId()) {
		case R.id.menu_delete:
			mDbAdapter.deleteRecordByWR(info.id);
			mDbAdapter.deleteWorkoutExercise(info.id);
			mDbAdapter.deleteWorkout(info.id);

			/*
			 * while (c.isAfterLast() == false) { Log.v(TAG, "Infinite loop");
			 * long rowId = c.getLong(c
			 * .getColumnIndexOrThrow(WorkoutRoutineExerciseTable.COLUMN_ID));
			 * mDbAdapter.deleteRecordsByWRE(rowId); }
			 */
			fillData();
			return true;
		case R.id.menu_edit:
			Intent j = new Intent(this, WorkoutRoutineEdit.class);
			j.putExtra(WorkoutRoutineTable.COLUMN_ID, info.id);
			startActivityForResult(j, ACTIVITY_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case INSERT_ID: createWorkout(); return true; }
	 * 
	 * return super.onOptionsItemSelected(item); }
	 */

	/*
	 * public void createWorkout() { String workoutName = "Workout " +
	 * mWorkoutNumber++; mDbAdapter.createWorkout(workoutName, ""); fillData();
	 * }
	 */

	public void createWorkout() {
		Intent i = new Intent(this, WorkoutRoutineEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, WorkoutRoutineView.class);
		i.putExtra(WorkoutRoutineTable.COLUMN_ID, id);
		startActivityForResult(i, ACTIVITY_VIEW);
	}

	/*
	 * @Override protected void onListItemClick(ListView l, View v, int
	 * position, long id) { super.onListItemClick(l, v, position, id); Cursor c
	 * = mWorkoutsCursor; c.moveToPosition(position); Intent i = new
	 * Intent(this, WorkoutRoutineEdit.class);
	 * i.putExtra(WorkoutRoutineTable.COLUMN_ID, id);
	 * i.putExtra(WorkoutRoutineTable.COLUMN_NAME, c.getString(
	 * c.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_NAME)));
	 * i.putExtra(WorkoutRoutineTable.COLUMN_DESCRIPTION, c.getString(
	 * c.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_DESCRIPTION)));
	 * startActivityForResult(i, ACTIVITY_EDIT); }
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	public void onDestroy() {
		Log.d(TAG, "Destroying View ...");
		mDbAdapter.close();
		super.onDestroy();
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent intent) { super.onActivityResult(requestCode,
	 * resultCode, intent); Bundle extras = intent.getExtras();
	 * switch(requestCode) { case ACTIVITY_CREATE: String name =
	 * extras.getString(WorkoutRoutineTable.COLUMN_NAME); String description =
	 * extras.getString(WorkoutRoutineTable.COLUMN_DESCRIPTION);
	 * mDbAdapter.createWorkout(name, description); fillData(); break; case
	 * ACTIVITY_EDIT: Long rowId =
	 * extras.getLong(WorkoutRoutineTable.COLUMN_ID); if (rowId != null) {
	 * String editName = extras.getString(WorkoutRoutineTable.COLUMN_NAME);
	 * String editDescription =
	 * extras.getString(WorkoutRoutineTable.COLUMN_DESCRIPTION);
	 * mDbAdapter.updateWorkout(rowId, editName, editDescription); } fillData();
	 * break; } }
	 */

}
