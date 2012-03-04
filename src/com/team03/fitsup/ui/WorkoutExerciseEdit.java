package com.team03.fitsup.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.ExerciseTable;
import com.team03.fitsup.data.WorkoutRoutineTable;

public class WorkoutExerciseEdit extends Activity {

	private static final String TAG = "WorkoutExerciseEdit";
	private static final boolean DEBUG = true;
	
	private DatabaseAdapter mDbAdapter;
	private Long wRowId;
	private Long eRowId;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_exercise_to_workout);

		if (DEBUG) Log.v(TAG, "+++ ON CREATE +++");
		
		mDbAdapter = new DatabaseAdapter(getApplicationContext());
		mDbAdapter.open();

		Button confirmButton = (Button) findViewById(R.id.confirm);

		fillSpinner();

		wRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(WorkoutRoutineTable.COLUMN_ID);
		if (wRowId == null) {
			Bundle extras = getIntent().getExtras();
			wRowId = extras != null ? extras
					.getLong(WorkoutRoutineTable.COLUMN_ID) : null;
		}

		Log.v(TAG, "This is the workout Id: " + wRowId);

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				setResult(RESULT_OK);
				// pops activity off the "back stack" and destroys it. resumes the next activity on the back stack.
				finish();
			}
		});
	}

	private void fillSpinner() {

		Cursor c = mDbAdapter.fetchAllExercises();
		startManagingCursor(c);

		// create an array to specify which fields we want to display
		String[] from = new String[] { ExerciseTable.COLUMN_NAME };
		// create an array of the display item we want to bind our data to
		int[] to = new int[] { android.R.id.text1 };
		// create simple cursor adapter
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, c, from, to);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// get reference to our spinner
		Spinner s = (Spinner) findViewById(R.id.spinner);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			eRowId = parent.getSelectedItemId();
			Log.v(TAG, "This is the exercise Id: " + eRowId);

		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (DEBUG) Log.v(TAG, "ON SAVED INSTANCE STATE");
		outState.putSerializable(WorkoutRoutineTable.COLUMN_ID, wRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DEBUG) Log.v(TAG, "+ ON PAUSE +");
		save();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "+ ON RESUME +");
		fillSpinner();
	}

	private void save() {
		mDbAdapter.createWorkoutExercise(wRowId, eRowId);
	}
}