package com.team03.fitsup.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.ExerciseTable;
import com.team03.fitsup.data.WorkoutRoutineExerciseTable;
import com.team03.fitsup.data.WorkoutRoutineTable;

public class WorkoutRoutineView extends ListActivity {

	private static final String TAG = "WorkoutRoutineView";
	private static final boolean DEBUG = true;

	private DatabaseAdapter mDbAdapter;
	private TextView mNameText;
	private TextView mDescriptionText;
	private Long mRowId;
	private Long eRowId;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_VIEW = 1;

	private static final int INSERT_ID = Menu.FIRST;

	// private static final int DELETE_ID = Menu.FIRST + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workouts_options);

		if (DEBUG)
			Log.v(TAG, "+++ ON CREATE +++");

		mDbAdapter = new DatabaseAdapter(getApplicationContext());
		mDbAdapter.open();

		mNameText = (TextView) findViewById(R.id.name);
		mDescriptionText = (TextView) findViewById(R.id.description);

		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(WorkoutRoutineTable.COLUMN_ID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras
					.getLong(WorkoutRoutineTable.COLUMN_ID) : null;
		}

		populateFields();
		fillData();
		registerForContextMenu(getListView());
		// confirmButton.setOnClickListener(new View.OnClickListener() {

		// public void onClick(View view) {
		// setResult(RESULT_OK);
		// finish();
		// }

		// });
	}

	private void populateFields() {
		if (mRowId != null) {
			Cursor workout = mDbAdapter.fetchWorkout(mRowId);
			startManagingCursor(workout);
			mNameText.setText(workout.getString(workout
					.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_NAME)));
			mDescriptionText
					.setText(workout.getString(workout
							.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_DESCRIPTION)));
		}
	}

	private void fillData() {
		Cursor exercisesCursor = mDbAdapter.fetchAllWorkoutExercises(mRowId);
		startManagingCursor(exercisesCursor); // this is deprecated for above
												// 2.3, look up solution later,
												// something Alex said

		String[] from = new String[] { ExerciseTable.COLUMN_NAME };
		int[] to = new int[] { R.id.text1 };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter exercises = new SimpleCursorAdapter(this,
				R.layout.exercises_row, exercisesCursor, from, to);
		setListAdapter(exercises);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert_exercise);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
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
		getMenuInflater().inflate(R.menu.wr_exercise_context_menu, menu);
	}

	@Override
	// Change to View Exercise, Add Record, Delete Exercise
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.menu_delete:
			mDbAdapter.deleteExerciseFromWorkout(info.id);
			mDbAdapter.deleteRecordsByWRE(info.id);
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Cursor exercise_id = mDbAdapter.fetchExercisebyWRE(id);
		for (String col : exercise_id.getColumnNames())
			Log.v(TAG, col);

		String name = exercise_id.getString(exercise_id
				.getColumnIndexOrThrow(ExerciseTable.COLUMN_NAME));

		long e_id = exercise_id
				.getLong(exercise_id
						.getColumnIndexOrThrow(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID));

		Intent i = new Intent(this, ExerciseRecordUI.class);
		i.putExtra(WorkoutRoutineExerciseTable.COLUMN_ID, id);
		i.putExtra(ExerciseTable.COLUMN_NAME, name);
		i.putExtra(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID, e_id);
		startActivityForResult(i, ACTIVITY_VIEW);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}
}
