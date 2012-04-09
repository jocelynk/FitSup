package com.team03.fitsup.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.team03.fitsup.R;
import com.team03.fitsup.data.DatabaseAdapter;
import com.team03.fitsup.data.ExerciseTable;
import com.team03.fitsup.data.RecordTable;
import com.team03.fitsup.data.WorkoutRoutineExerciseTable;

//Things to Do:
//need to figure out what to do with date and how to add either datepicker or calendar, fragments? when you click on date?
//++++WorkoutRoutine Queries++++
//Go over UI again with Group to see what is the best way to code and how user flow goes, confused on Minji's layouts
//Edit table view - create a list view that populates with text for Name and text for unit of Attribute
//Specify hints for all EditTexts

public class ExerciseRecordUI extends Activity implements OnClickListener {
	private static final String TAG = "ExerciseRecordUI";
	private static final boolean DEBUG = true;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_VIEW = 1;
	private static final int ACTIVITY_EDIT = 2;

	private static final int INSERT_ID = Menu.FIRST;

	private DatabaseAdapter mDbAdapter;
	private Long wreRowId;
	private String date;

	private static final String tag = "SimpleCalendarViewActivity";

	private ImageView calendarToJournalButton;
	private Button selectedDayMonthYearButton;
	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private int month, year;
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_calendar_view);
		Log.v(TAG, "+++ ON CREATE +++");

		mDbAdapter = new DatabaseAdapter(getApplicationContext());
		mDbAdapter.open();

		wreRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(WorkoutRoutineExerciseTable.COLUMN_ID);
		if (wreRowId == null) {
			Bundle extras = getIntent().getExtras();
			wreRowId = extras != null ? extras
					.getLong(WorkoutRoutineExerciseTable.COLUMN_ID) : null;
		}

		_calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH) + 1;
		year = _calendar.get(Calendar.YEAR);
		Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
				+ year);

		selectedDayMonthYearButton = (Button) this
				.findViewById(R.id.selectedDayMonthYear);
		selectedDayMonthYearButton.setText("Selected: ");

		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) this.findViewById(R.id.currentMonth);
		currentMonth.setText(dateFormatter.format(dateTemplate,
				_calendar.getTime()));

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);

		// Initialised
		adapter = new GridCellAdapter(getApplicationContext(),
				R.id.calendar_day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	/**
	 * 
	 * @param month
	 * @param year
	 */
	
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "+ ON RESUME +");
		setGridCellAdapterToDate(month, year);
		
	}

	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(getApplicationContext(),
				R.id.calendar_day_gridcell, month, year);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(dateFormatter.format(dateTemplate, _calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

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
			createRecord();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public void createRecord() {
		Cursor exercise_id = mDbAdapter.fetchExerciseIDBYWRE(wreRowId);
		long e_id = exercise_id
				.getLong(exercise_id
						.getColumnIndexOrThrow(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID));
		Intent i = new Intent(this, ExerciseRecordEdit.class);
		i.putExtra(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
		i.putExtra(ExerciseTable.COLUMN_ID, e_id);
		exercise_id.close();
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public void onClick(View v) {
		Log.v("CLICK", "Date was clicked");
		if (v == prevMonth) {
			if (month <= 1) {
				month = 12;
				year--;
			} else {
				month--;
			}
			Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: "
					+ month + " Year: " + year);
			setGridCellAdapterToDate(month, year);
		}
		if (v == nextMonth) {
			if (month > 11) {
				month = 1;
				year++;
			} else {
				month++;
			}
			Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: "
					+ month + " Year: " + year);
			setGridCellAdapterToDate(month, year);
		}

	}

	@Override
	public void onDestroy() {
		Log.d(tag, "Destroying View ...");
		super.onDestroy();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////
	// Inner Class
	public enum Month {
		January, Februrary, March, April, May, June, July, August, September, October, November, December, NOVALUE;

		public static Month toDay(String str) {
			try {
				return valueOf(str);
			} catch (Exception ex) {
				return NOVALUE;
			}
		}
	}

	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private final int month, year;
		private int daysInMonth, prevMonthDays;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private TextView num_events_per_day;
		private final HashMap eventsPerMonthMap;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"dd-MM-yyyy");
		private String dbDate;

		// private final DateFormat dateFormatter2 = new DateFormat();
		// private static final String dateTemplate2 = "M-d-yyyy";
		// private final SimpleDateFormat dateFormatter2 = new
		// SimpleDateFormat("yyyy-MM-dd");

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			this.month = month;
			this.year = year;

			Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
					+ "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
			Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
			Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

			// Print Month
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}

		private String getMonthAsString(int i) {
			return months[i];

		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Prints Month
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy) {
			Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			// The number of days to leave blank at
			// the start of this month.
			int trailingSpaces = 0;
			int leadSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			Log.d(tag, "Current Month: " + " " + currentMonthName + " having "
					+ daysInMonth + " days.");

			// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
				Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			}

			// Compute how much to leave before before the first day of the
			// month.
			// getDay() returns 0 for Sunday.
			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			Log.d(tag, "Week Day:" + currentWeekDay + " is "
					+ getWeekDayAsString(currentWeekDay));
			Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
			Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
				++daysInMonth;
			}

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				Log.d(tag,
						"PREV MONTH:= "
								+ prevMonth
								+ " => "
								+ getMonthAsString(prevMonth)
								+ " "
								+ String.valueOf((daysInPrevMonth
										- trailingSpaces + DAY_OFFSET)
										+ i));
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY"
						+ "-"
						+ getMonthAsString(prevMonth)
						+ "-"
						+ prevYear);
			}
			
			Time today = new Time(Time.getCurrentTimezone());
			today.setToNow();

			
			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				Log.d(currentMonthName, String.valueOf(i) + " "
						+ getMonthAsString(currentMonth) + " " + yy);
				
				String day = mm + "-" + i + "-" + yy;
				Cursor records = mDbAdapter.fetchAllRecordAttrByDate(day, wreRowId);
				startManagingCursor(records);
				if (records != null) {
					records.moveToFirst();
				}
				if (records.getCount() > 0 && i != getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-RED" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				}
				else if (i == getCurrentDayOfMonth() && mm == (today.month + 1) && yy == today.year) {
					list.add(String.valueOf(i) + "-BLUE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ getMonthAsString(nextMonth) + "-" + nextYear);
			}
		}

		/**
		 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
		 * ALL entries from a SQLite database for that month. Iterate over the
		 * List of All entries, and get the dateCreated, which is converted into
		 * day.
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		private HashMap findNumberOfEventsPerMonth(int year, int month) {
			HashMap map = new HashMap<String, Integer>();
			// DateFormat dateFormatter2 = new DateFormat();
			//
			// String day = dateFormatter2.format("dd", dateCreated).toString();
			//
			// if (map.containsKey(day))
			// {
			// Integer val = (Integer) map.get(day) + 1;
			// map.put(day, val);
			// }
			// else
			// {
			// map.put(day, 1);
			// }
			return map;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.calendar_day_gridcell, parent,
						false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING

			Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];

			int intMonth = 0;
			if (themonth.equals("Janurary")) {
				intMonth = 1;
			} else if (themonth.equals("February")) {
				intMonth = 2;
			} else if (themonth.equals("March")) {
				intMonth = 3;
			} else if (themonth.equals("April")) {
				intMonth = 4;
			} else if (themonth.equals("May")) {
				intMonth = 5;
			} else if (themonth.equals("June")) {
				intMonth = 6;
			} else if (themonth.equals("July")) {
				intMonth = 7;
			} else if (themonth.equals("August")) {
				intMonth = 8;
			} else if (themonth.equals("September")) {
				intMonth = 9;
			} else if (themonth.equals("October")) {
				intMonth = 10;
			} else if (themonth.equals("November")) {
				intMonth = 11;
			} else if (themonth.equals("December")) {
				intMonth = 12;
			}

			String theyear = day_color[3];
			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				if (eventsPerMonthMap.containsKey(theday)) {
					num_events_per_day = (TextView) row
							.findViewById(R.id.num_events_per_day);
					Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
					num_events_per_day.setText(numEvents.toString());
				}
			}

			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(R.id.calendar_day_gridcell, theday + "-" + themonth
					+ "-" + theyear);
			gridcell.setTag(R.id.intMonth, intMonth + "-" + theday + "-"
					+ theyear);
			Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
					+ theyear);

			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(Color.LTGRAY);
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(Color.WHITE);
			}
			if (day_color[1].equals("BLUE")) {
				gridcell.setTextColor(getResources().getColor(
						R.color.static_text_color));
			}
			if (day_color[1].equals("RED")) {
				gridcell.setTextColor(Color.RED);
			}
			return row;
		}

		@Override
		public void onClick(View view) {
			String date_month_year = (String) view
					.getTag(R.id.calendar_day_gridcell);
			String month_date_year = (String) view.getTag(R.id.intMonth);
			selectedDayMonthYearButton.setText("Selected: " + date_month_year);
			Cursor exercise_id = mDbAdapter.fetchExercisebyWRE(wreRowId);
			
			String name = exercise_id.getString(exercise_id.getColumnIndexOrThrow(ExerciseTable.COLUMN_NAME));
			for (String col : exercise_id.getColumnNames()) Log.v(TAG, col);

			long e_id = exercise_id
			.getLong(exercise_id
					.getColumnIndexOrThrow(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID));
			Intent i = new Intent(getBaseContext(), RecordView.class);
			i.putExtra(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
			i.putExtra(RecordTable.COLUMN_DATE, month_date_year);
			i.putExtra(ExerciseTable.COLUMN_ID, e_id);
			i.putExtra(ExerciseTable.COLUMN_NAME, name);
			startActivityForResult(i, ACTIVITY_VIEW);

		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
	}

	/** Called when the activity is first created. */
	/*
	 * @Override public void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * setContentView(R.layout.records_by_exercise_index);
	 * 
	 * Log.v(TAG, "+++ ON CREATE +++");
	 * 
	 * mDbAdapter = new DatabaseAdapter(getApplicationContext());
	 * mDbAdapter.open();
	 * 
	 * wreRowId = (savedInstanceState == null) ? null : (Long)
	 * savedInstanceState
	 * .getSerializable(WorkoutRoutineExerciseTable.COLUMN_ID); if (wreRowId ==
	 * null) { Bundle extras = getIntent().getExtras(); wreRowId = extras !=
	 * null ? extras .getLong(WorkoutRoutineExerciseTable.COLUMN_ID) : null; }
	 * 
	 * fillData(); registerForContextMenu(getListView()); }
	 * 
	 * private void fillData() { // Get all of the notes from the database and
	 * create the item list Cursor recordsCursor =
	 * mDbAdapter.fetchAllDatesOfExercise(wreRowId);
	 * startManagingCursor(recordsCursor);
	 * 
	 * String[] from = new String[] { RecordTable.COLUMN_DATE }; int[] to = new
	 * int[] { R.id.text1 };
	 * 
	 * // Now create an array adapter and set it to display using our row
	 * SimpleCursorAdapter records = new SimpleCursorAdapter(this,
	 * R.layout.records_row, recordsCursor, from, to); setListAdapter(records);
	 * // should i change text1 to something else }
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu); menu.add(0, INSERT_ID, 0,
	 * R.string.menu_insert); return true; }
	 * 
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { switch (item.getItemId()) { case INSERT_ID: createRecord(); return
	 * true; }
	 * 
	 * return super.onMenuItemSelected(featureId, item); }
	 * 
	 * @Override public void onCreateContextMenu(ContextMenu menu, View v,
	 * ContextMenuInfo menuInfo) { super.onCreateContextMenu(menu, v, menuInfo);
	 * getMenuInflater().inflate(R.menu.record_context_menu, menu); }
	 * 
	 * @Override public boolean onContextItemSelected(MenuItem item) {
	 * AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	 * .getMenuInfo(); Cursor c = mDbAdapter.fetchAllDatesOfExercise(wreRowId);
	 * startManagingCursor(c); c.moveToPosition(info.position); String date =
	 * c.getString(c .getColumnIndexOrThrow(RecordTable.COLUMN_DATE)); // does
	 * this //Cursor exercise_id = mDbAdapter.fetchExercisebyWRE(wreRowId);
	 * //long e_id = exercise_id.getLong(exercise_id
	 * //.getColumnIndexOrThrow(ExerciseTable.COLUMN_ID)); Cursor exercise_id =
	 * mDbAdapter.fetchExerciseIDBYWRE(wreRowId); long e_id =
	 * exercise_id.getLong
	 * (exercise_id.getColumnIndexOrThrow(WorkoutRoutineExerciseTable
	 * .COLUMN_EXERCISE_ID)); switch (item.getItemId()) { case
	 * R.id.menu_delete_r: mDbAdapter.deleteRecord(date, wreRowId); c.close();
	 * fillData(); return true; case R.id.menu_view_r: Intent i = new
	 * Intent(this, RecordView.class); i.putExtra(RecordTable.COLUMN_ID,
	 * info.id); i.putExtra(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
	 * i.putExtra(RecordTable.COLUMN_DATE, date);
	 * i.putExtra(ExerciseTable.COLUMN_ID, e_id); c.close();
	 * startActivityForResult(i, ACTIVITY_VIEW);
	 * 
	 * /*case R.id.menu_edit_r: Log.v(TAG, "boahboah"); Intent j = new
	 * Intent(this, ExerciseRecordEdit.class); j.putExtra(RecordTable.COLUMN_ID,
	 * info.id); j.putExtra(ExerciseTable.COLUMN_ID, e_id); c.close();
	 * 
	 * startActivityForResult(j, ACTIVITY_EDIT); return true; } return
	 * super.onContextItemSelected(item); }
	 * 
	 * public void createRecord() { Cursor exercise_id =
	 * mDbAdapter.fetchExerciseIDBYWRE(wreRowId); long e_id =
	 * exercise_id.getLong
	 * (exercise_id.getColumnIndexOrThrow(WorkoutRoutineExerciseTable
	 * .COLUMN_EXERCISE_ID)); Intent i = new Intent(this,
	 * ExerciseRecordEdit.class); i.putExtra(RecordTable.COLUMN_WRKT_RTNE_E_ID,
	 * wreRowId); i.putExtra(ExerciseTable.COLUMN_ID, e_id);
	 * startActivityForResult(i, ACTIVITY_CREATE); }
	 * 
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent intent) { super.onActivityResult(requestCode,
	 * resultCode, intent); fillData(); }
	 */

}
