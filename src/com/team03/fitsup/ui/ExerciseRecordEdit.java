package com.team03.fitsup.ui;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.team03.fitsup.R;
import com.team03.fitsup.data.AttributeTable;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.ExerciseAttributeTable;
import com.team03.fitsup.data.ExerciseTable;
import com.team03.fitsup.data.RecordTable;
import com.team03.fitsup.data.WorkoutRoutineExerciseTable;
import com.team03.fitsup.data.WorkoutRoutineTable;

public class ExerciseRecordEdit extends Activity {
	private static final String TAG = "ExerciseRecordEdit";
	private static final boolean DEBUG = true;

	private DatabaseAdapter mDbAdapter;
	private EditText mHourText;
	private EditText mMinuteText;
	private EditText mSecondText;
	private EditText mValueText;
	private EditText mSetText;
	private EditText mRepText;
	private EditText mWeightText;
	private Long wreRowId;
	private Long eRowId;
	private Button confirmButton;

	// for the date picker

	private int mYear;
	private int mMonth;
	private int mDay;

	private TextView mDateDisplay;
	private Button mPickDate;

	static final int DATE_DIALOG_ID = 0;

	// end date picker code

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (DEBUG)
			Log.v(TAG, "+++ ON CREATE +++");

		mDbAdapter = new DatabaseAdapter(getApplicationContext());
		mDbAdapter.open();
		wreRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(RecordTable.COLUMN_WRKT_RTNE_E_ID);
		if (wreRowId == null) {
			Bundle extras = getIntent().getExtras();
			wreRowId = extras != null ? extras
					.getLong(RecordTable.COLUMN_WRKT_RTNE_E_ID) : null;
		}

		eRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(ExerciseTable.COLUMN_ID);
		if (eRowId == null) {
			Bundle extras = getIntent().getExtras();
			eRowId = extras != null ? extras.getLong(ExerciseTable.COLUMN_ID)
					: null;
		}

		switch (eRowId.intValue()) {
		case 1:
		case 2:
		case 3:
			setContentView(R.layout.records_edit);
			mHourText = (EditText) findViewById(R.id.hr);
			mMinuteText = (EditText) findViewById(R.id.min);
			mSecondText = (EditText) findViewById(R.id.sec);
			mValueText = (EditText) findViewById(R.id.value);
			confirmButton = (Button) findViewById(R.id.confirm);

			break;
		case 4:
		case 5:
		case 6:
			setContentView(R.layout.records_edit2);
			mSetText = (EditText) findViewById(R.id.value);
			mRepText = (EditText) findViewById(R.id.value2);
			mWeightText = (EditText) findViewById(R.id.value3);
			confirmButton = (Button) findViewById(R.id.confirm);

			break;

		}

		// populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});

		mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
		mPickDate = (Button) findViewById(R.id.pickDate);

		mPickDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		Log.v(TAG, "" + mYear);
		mMonth = c.get(Calendar.MONTH);
		Log.v(TAG, "" + mMonth);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		Log.v(TAG, "" + mDay);

		// display the current date
		updateDisplay();
	}

	// date picker helper method

	private void updateDisplay() {
		this.mDateDisplay.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("-").append(mDay).append("-")
				.append(mYear));
	}

	// date picker helper class

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	// date picker helper method

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (DEBUG)
			Log.v(TAG, "ON SAVED INSTANCE STATE");

		saveState();

		// Toast.makeText(

		// getApplicationContext(), "Please enter in the record." ,
		// Toast.LENGTH_LONG).show();

		// }
		outState.putSerializable(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DEBUG)
			Log.v(TAG, "+ ON PAUSE +");

		saveState();

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "+ ON RESUME +");
		// populateFields();
	}

	private void saveState() { // need to write exceptions for formatting errors
		// switch case statement - later need to get exercise id //create SQL
		// query or reuse another
		String date = mDateDisplay.getText().toString();

		switch (eRowId.intValue()) {
		case 1:
		case 2:
		case 3: // later need to have individual cases for all
			String hour = mHourText.getText().toString();
			String minute = mMinuteText.getText().toString();
			String second = mSecondText.getText().toString();
			String value = mValueText.getText().toString(); // distance

			double hr = !TextUtils.isEmpty(hour) ? Double.parseDouble(hour) : 0;
			double min = !TextUtils.isEmpty(minute) ? Double
					.parseDouble(minute) : 0;
			double sec = !TextUtils.isEmpty(second) ? Double
					.parseDouble(second) : 0;

			double savedTime = (hr == 0.0 && min == 0.0 && sec == 0.0) ? -1
					: ((hr * 60.0) + min + (sec / 60.0));
			double val = !TextUtils.isEmpty(value) ? Double.parseDouble(value)
					: -1;
			// time
			Cursor c = mDbAdapter.fetchRecord(date, 1, wreRowId);
			// Cursor a = mDbAdapter.fetchAttribute("Time");
			// long a_id =
			// a.getLong(a.getColumnIndexOrThrow(AttributeTable.COLUMN_ID));
			// Cursor ea = mDbAdapter.fetchExerciseAttribute(a_id, eRowId);
			// long ea_id =
			// ea.getLong(a.getColumnIndexOrThrow(ExerciseAttributeTable.COLUMN_ID));
			// Cursor d = mDbAdapter.fetchRecord(date, 2, wreRowId); Is this
			// okay? assuming if one value is filled, other is filled too
			// if(c!=null && c.getCount()>0 && d!=null && d.getCount()>0)
			if (c != null && c.getCount() > 0) {
				if (savedTime != -1 && val != -1) {
					mDbAdapter.updateRecord(date, savedTime, 1, wreRowId);
					mDbAdapter.updateRecord(date, val, 2, wreRowId);
				}
			} else {
				if (savedTime != -1 && val != -1) {
					// time
					mDbAdapter.createRecord(date, savedTime, 1, wreRowId);
					// distance
					mDbAdapter.createRecord(date, val, 2, wreRowId);
				}
			}
			break;

		case 4:
		case 5:
		case 6:
			String setString = mSetText.getText().toString();
			String repString = mRepText.getText().toString();
			String weightString = mWeightText.getText().toString();
			double set = !TextUtils.isEmpty(setString) ? Double
					.parseDouble(setString) : -1;
			double rep = !TextUtils.isEmpty(repString) ? Double
					.parseDouble(repString) : -1;
			double weight = !TextUtils.isEmpty(weightString) ? Double
					.parseDouble(weightString) : -1;
			// create Query that will query for the name of the attribute
			// and take in ExerciseId to replace in createRecord() method
			Cursor e = mDbAdapter.fetchRecord(date, 6, wreRowId);

			if (e != null && e.getCount() > 0) {
				if (set != -1 && rep != -1 && weight != -1) {
					mDbAdapter.updateRecord(date, set, 6, wreRowId);
					mDbAdapter.updateRecord(date, rep, 7, wreRowId);
					mDbAdapter.updateRecord(date, weight, 8, wreRowId);
				}
			} else {
				if (set != -1 && rep != -1 && weight != -1) {

					mDbAdapter.createRecord(date, set, 6, wreRowId);
					mDbAdapter.createRecord(date, rep, 7, wreRowId);
					mDbAdapter.createRecord(date, weight, 8, wreRowId);
				}
			}

		}
	}
}
