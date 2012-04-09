package com.team03.fitsup.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.team03.fitsup.R;
import com.team03.fitsup.data.AttributeTable;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.ExerciseTable;
import com.team03.fitsup.data.RecordTable;
import com.team03.fitsup.data.WorkoutRoutineTable;

//Needs a lot of thinking and work
public class RecordView extends Activity {

	private static final String TAG = "RecordView";
	private static final boolean DEBUG = true;
	private static final int ACTIVITY_DELETE = 0;

	private static final int DELETE_ID = Menu.FIRST;

	private DatabaseAdapter mDbAdapter;
	private TextView mDateText;
	private TextView mValueText;
	private TextView mHrText;
	private TextView mMinText;
	private TextView mSecText;
	private TextView mSetText;
	private TextView mRepText;
	private TextView mWeightText;
	private TextView mNameText;
	// private Long mRowId;
	private Long wreRowId;
	private Long eRowId;
	private String date;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (DEBUG)
			Log.v(TAG, "+++ ON CREATE +++");

		mDbAdapter = new DatabaseAdapter(getApplicationContext());
		mDbAdapter.open();

		date = (savedInstanceState == null) ? null
				: (String) savedInstanceState
						.getSerializable(RecordTable.COLUMN_DATE);
		if (date == null) {
			Bundle extras = getIntent().getExtras();
			date = extras != null ? extras.getString(RecordTable.COLUMN_DATE)
					: null;
		}
		
		name = (savedInstanceState == null) ? null
				: (String) savedInstanceState
						.getSerializable(ExerciseTable.COLUMN_NAME);
		if (name == null) {
			Bundle extras = getIntent().getExtras();
			name = extras != null ? extras.getString(ExerciseTable.COLUMN_NAME)
					: null;
		}

		wreRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(RecordTable.COLUMN_WRKT_RTNE_E_ID);
		if (wreRowId == null) {
			Bundle extras = getIntent().getExtras();
			wreRowId = extras != null ? extras
					.getLong(RecordTable.COLUMN_WRKT_RTNE_E_ID) : null;
		}
		Log.v(TAG, "" + wreRowId);

		eRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(ExerciseTable.COLUMN_ID);
		if (eRowId == null) {
			Bundle extras = getIntent().getExtras();
			eRowId = extras != null ? extras.getLong(ExerciseTable.COLUMN_ID)
					: null;
		}
		Log.v(TAG, "" + eRowId);

		Cursor records = mDbAdapter.fetchAllRecordAttrByDate(date, wreRowId);
		startManagingCursor(records);
		if (records != null) {
			records.moveToFirst();
		}

		switch (eRowId.intValue()) {
		case 1:
		case 2:
		case 3:
			if (records.getCount() > 0) {
				setContentView(R.layout.cardio_view);
				mNameText = (TextView) findViewById(R.id.exercise_name);
				mDateText = (TextView) findViewById(R.id.date);
				mValueText = (TextView) findViewById(R.id.value);
				mHrText = (TextView) findViewById(R.id.hr);
				mMinText = (TextView) findViewById(R.id.min);
				mSecText = (TextView) findViewById(R.id.sec);
			} else {
				setContentView(R.layout.no_records);
			}

			break;
		case 4:
		case 5:
		case 6:
			if (records.getCount() > 0) {
				setContentView(R.layout.strength);
				mNameText = (TextView) findViewById(R.id.exercise_name);
				mDateText = (TextView) findViewById(R.id.date);
				mSetText = (TextView) findViewById(R.id.value);
				mRepText = (TextView) findViewById(R.id.value2);
				mWeightText = (TextView) findViewById(R.id.value3);
			} else {
				setContentView(R.layout.no_records);
			}

			break;

		}
		populateFields();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete_r);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			mDbAdapter.deleteRecord(date, wreRowId);
			finish();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}


	private void populateFields() {
		Cursor records = mDbAdapter.fetchAllRecordAttrByDate(date, wreRowId);
		startManagingCursor(records);
		if (records != null) {
			records.moveToFirst();
		}
		switch (eRowId.intValue()) {
		case 1:
		case 2:
		case 3:
			if (records.getCount() > 0) {
				while (records.isAfterLast() == false) {
					long rowId = records.getLong(records
							.getColumnIndexOrThrow(RecordTable.COLUMN_ID));
					Cursor record = mDbAdapter.fetchAttributeOfRecord(rowId);
					String attribute = record.getString(record
							.getColumnIndexOrThrow(AttributeTable.COLUMN_NAME));
					if (attribute.equals("Time")) {
						// fix this later
						double value = records
								.getDouble(records
										.getColumnIndexOrThrow(RecordTable.COLUMN_VALUE));
						if (DEBUG)
							Log.v(TAG, "" + value);
						int hour = (int) Math.floor(value / 60);
						int min = (int) Math.floor(((value / 60) - hour) * 60);

						int sec = (int) Math
								.round(((((value / 60) - hour) * 60) - min) * 60);
						mHrText.setText(String.valueOf(hour));
						mMinText.setText(String.valueOf(min));
						mSecText.setText(String.valueOf(sec));
						record.close();

					} else {
						mValueText
								.setText(records.getString(records
										.getColumnIndexOrThrow(RecordTable.COLUMN_VALUE)));
						record.close();
					}
					records.moveToNext();
				}
				mDateText.setText(date);
				mNameText.setText(name);
			}
			records.close();
			break;
		case 4:
		case 5:
		case 6:
			if (records.getCount() > 0) {
				while (records.isAfterLast() == false) {
					long rowId = records.getLong(records
							.getColumnIndexOrThrow(RecordTable.COLUMN_ID));
					Cursor record = mDbAdapter.fetchAttributeOfRecord(rowId);
					String attribute = record.getString(record
							.getColumnIndexOrThrow(AttributeTable.COLUMN_NAME));
					int value = records.getInt(records
							.getColumnIndexOrThrow(RecordTable.COLUMN_VALUE));
					if (attribute.equals("Set")) {
						mSetText.setText(String.valueOf(value));
						record.close();

					} else if (attribute.equals("Reps")) {
						mRepText.setText(String.valueOf(value));
						record.close();
					} else {
						mWeightText.setText(String.valueOf(value));
						record.close();
					}
					records.moveToNext();
				}
				mDateText.setText(date);
				mNameText.setText(name);

			}
			records.close();
			break;

		}

	}

}
