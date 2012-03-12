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

		fillSpinnerCategory();

		wRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(WorkoutRoutineTable.COLUMN_ID);
		if (wRowId == null) {
			Bundle extras = getIntent().getExtras();
			wRowId = extras != null ? extras
					.getLong(WorkoutRoutineTable.COLUMN_ID) : null;
		}
		
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				setResult(RESULT_OK);
				// pops activity off the "back stack" and destroys it. resumes the next activity on the back stack.
				finish();
			}
		});
	}

	private void fillSpinnerCategory() {

		Cursor c = mDbAdapter.fetchAllCategories();
		startManagingCursor(c);

		// create an array to specify which fields we want to display
		String[] from = new String[] { ExerciseTable.COLUMN_CATEGORY };
		// create an array of the display item we want to bind our data to
		int[] to = new int[] { android.R.id.text1 };
		// create simple cursor adapter
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, c, from, to);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// get reference to our spinner
		Spinner s = (Spinner) findViewById(R.id.spinner1);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}
	
	private void fillSpinnerExercise(String category) {

		Cursor c = mDbAdapter.fetchAllExercisesByCategory(category);
		startManagingCursor(c);
		String[] from = new String[] { ExerciseTable.COLUMN_NAME };
		int[] to = new int[] { android.R.id.text1 };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, c, from, to);
		adapter.notifyDataSetChanged();
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner s = (Spinner) findViewById(R.id.spinner2);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			if(parent.getId() == R.id.spinner1) {			
				Cursor c = (Cursor) parent.getAdapter().getItem(pos); 
				fillSpinnerExercise(c.getString(c.getColumnIndex(ExerciseTable.COLUMN_CATEGORY)));
			} else if(parent.getId() == R.id.spinner2) {
				eRowId = parent.getSelectedItemId();
			}
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
		if(DEBUG) Log.v(TAG, "+ ON RESUME +");
		fillSpinnerCategory();
	}

	private void save() {
		mDbAdapter.createWorkoutExercise(wRowId, eRowId);
	}
}