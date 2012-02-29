package com.team03.fitsup.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
	
	//closes the database
	public void close() {
		mDbHelper.close();
	}
	
	public long createWorkout(String name, String description) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(WorkoutRoutineTable.COLUMN_NAME, name);
		initialValues.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);
		
		return mDb.insert(WorkoutRoutineTable.TABLE_WORKOUTROUTINE, null, initialValues);
	}
	
	public boolean deleteWorkout(long rowID) {
		return mDb.delete(WorkoutRoutineTable.TABLE_WORKOUTROUTINE, WorkoutRoutineTable.COLUMN_ID + " = " + rowID, null) >0;
	}
	

	
	public Cursor fetchAllWorkouts() {

        return mDb.query(WorkoutRoutineTable.TABLE_WORKOUTROUTINE, new String[] {WorkoutRoutineTable.COLUMN_ID, WorkoutRoutineTable.COLUMN_NAME,
        		WorkoutRoutineTable.COLUMN_DESCRIPTION}, null, null, null, null, null);
    }
	
	 public Cursor fetchWorkout(long rowId) throws SQLException {

	        Cursor mCursor =

	            mDb.query(true, WorkoutRoutineTable.TABLE_WORKOUTROUTINE, new String[] {WorkoutRoutineTable.COLUMN_ID, WorkoutRoutineTable.COLUMN_NAME,
	            		WorkoutRoutineTable.COLUMN_DESCRIPTION}, WorkoutRoutineTable.COLUMN_ID + "=" + rowId, null,
	                    null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;

	    }
	 
	 public boolean updateWorkout(long rowId, String name, String description) {
	        ContentValues args = new ContentValues();
	        args.put(WorkoutRoutineTable.COLUMN_NAME, name);
	        args.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);

	        return mDb.update(WorkoutRoutineTable.TABLE_WORKOUTROUTINE, args, WorkoutRoutineTable.COLUMN_ID + "=" + rowId, null) > 0;
	    }
}

