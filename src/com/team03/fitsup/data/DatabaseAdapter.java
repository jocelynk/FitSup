package com.team03.fitsup.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class DatabaseAdapter {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private Context mCtx;

	public DatabaseAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public DatabaseAdapter open() throws SQLException {
		mDbHelper = DatabaseHelper.getInstance(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	// closes the database
	public void close() {
		mDbHelper.close();
	}

	// WorkoutRoutine Queries

	public long createWorkout(String name, String description) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(WorkoutRoutineTable.COLUMN_NAME, name);
		initialValues.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);

		return mDb.insert(WorkoutRoutineTable.TABLE_WORKOUTROUTINE, null,
				initialValues);
	}

	public boolean deleteWorkout(long rowID) {
		return mDb.delete(WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
				WorkoutRoutineTable.COLUMN_ID + " = " + rowID, null) > 0;
	}

	public Cursor fetchAllWorkouts() {

		return mDb.query(WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
				new String[] { WorkoutRoutineTable.COLUMN_ID,
						WorkoutRoutineTable.COLUMN_NAME,
						WorkoutRoutineTable.COLUMN_DESCRIPTION }, null, null,
				null, null, null);
	}

	public Cursor fetchWorkout(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, WorkoutRoutineTable.TABLE_WORKOUTROUTINE, new String[] {
				WorkoutRoutineTable.COLUMN_ID, WorkoutRoutineTable.COLUMN_NAME,
				WorkoutRoutineTable.COLUMN_DESCRIPTION },
				WorkoutRoutineTable.COLUMN_ID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean updateWorkout(long rowId, String name, String description) {
		ContentValues args = new ContentValues();
		args.put(WorkoutRoutineTable.COLUMN_NAME, name);
		args.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);

		return mDb.update(WorkoutRoutineTable.TABLE_WORKOUTROUTINE, args,
				WorkoutRoutineTable.COLUMN_ID + "=" + rowId, null) > 0;
	}

	// WorkoutRoutineExercise Queries
	public boolean deleteExerciseFromWorkout(long wRowId, long eRowId) {
		return mDb.delete(
				WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE,
				WorkoutRoutineExerciseTable.COLUMN_WORKOUT_ID + " = " + wRowId
						+ " AND "
						+ WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID
						+ " = " + eRowId, null) > 0;
	}

	public Cursor fetchAllWorkoutExercises(long wRowId) {
		String[] columns = new String[] { ExerciseTable.COLUMN_ID,
				ExerciseTable.COLUMN_NAME, ExerciseTable.COLUMN_DESCRIPTION,
				ExerciseTable.COLUMN_CATEGORY };
		for (int i = 0; i < columns.length; i++) {
			// String column_name = ;
			columns[i] = ExerciseTable.TABLE_EXERCISE + "." + columns[i];
		}
		String[] tables = new String[] { ExerciseTable.TABLE_EXERCISE,
				WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
				WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE };

		String eQuery = "SELECT " + TextUtils.join(", ", columns) + " FROM "
				+ TextUtils.join(", ", tables) + " WHERE " + "WorkoutRoutines."
				+ WorkoutRoutineTable.COLUMN_ID + " = "
				+ "WorkoutRoutineExercises."
				+ WorkoutRoutineExerciseTable.COLUMN_WORKOUT_ID + " AND "
				+ "Exercises." + ExerciseTable.COLUMN_ID + " = "
				+ "WorkoutRoutineExercises."
				+ WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID + " AND "
				+ "WorkoutRoutineExercises."
				+ WorkoutRoutineExerciseTable.COLUMN_WORKOUT_ID + " = ?";
		Cursor mCursor = mDb.rawQuery(eQuery,
				new String[] { String.valueOf(wRowId) });

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchWorkoutExercise(long wRowId, long eRowId)
			throws SQLException {

		String[] columns = new String[] { ExerciseTable.COLUMN_ID,
				ExerciseTable.COLUMN_NAME, ExerciseTable.COLUMN_DESCRIPTION,
				ExerciseTable.COLUMN_CATEGORY };
		String[] tables = new String[] { ExerciseTable.TABLE_EXERCISE,
				WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
				WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE };
		for (int i = 0; i < columns.length; i++) {
			// String column_name = ;
			columns[i] = ExerciseTable.TABLE_EXERCISE + "." + columns[i];
		}
		String eQuery = "SELECT " + TextUtils.join(", ", columns) + " FROM "
				+ TextUtils.join(", ", tables) + " WHERE "
				+ ExerciseTable.TABLE_EXERCISE + " = ? AND "
				+ WorkoutRoutineTable.TABLE_WORKOUTROUTINE + " = ?";
		Cursor mCursor = mDb
				.rawQuery(
						eQuery,
						new String[] { String.valueOf(eRowId),
								String.valueOf(wRowId) });
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long createWorkoutExercise(long wRowId, long eRowId) {
		ContentValues initialValues = new ContentValues();
		initialValues
				.put(WorkoutRoutineExerciseTable.COLUMN_WORKOUT_ID, wRowId);
		initialValues.put(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID,
				eRowId);

		return mDb.insert(
				WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE,
				null, initialValues);
	}

	// Exercise Queries

	public Cursor fetchAllExercises() {

		return mDb.query(ExerciseTable.TABLE_EXERCISE,
				new String[] { ExerciseTable.COLUMN_ID,
						ExerciseTable.COLUMN_NAME,
						ExerciseTable.COLUMN_DESCRIPTION,
						ExerciseTable.COLUMN_CATEGORY }, null, null, null,
				null, null);
	}

}
