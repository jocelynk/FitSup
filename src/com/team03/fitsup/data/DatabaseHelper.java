package com.team03.fitsup.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "FitSup.db";
	private static final int DATABASE_VERSION = 1;
	private static DatabaseHelper mInstance = null;
	
	public static DatabaseHelper getInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new DatabaseHelper(ctx.getApplicationContext());
		}
		return mInstance;
	}
	
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	//Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		WorkoutRoutineTable.onCreate(database);
		ExerciseTable.onCreate(database);
		WorkoutRoutineExerciseTable.onCreate(database);
		fillTestData(database);
		
	}
	
	//Method is called during an upgrade of the database,
	//e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		WorkoutRoutineTable.onUpgrade(database, oldVersion, newVersion);
		ExerciseTable.onUpgrade(database, oldVersion, newVersion);
		WorkoutRoutineExerciseTable.onUpgrade(database, oldVersion, newVersion);
	}
	
	//Seed Exercise Table
	private void fillTestData(SQLiteDatabase db) {
		db.execSQL("insert into Exercises (name, description, category) values ('Running', 'bad for knees', 'Cardio')");
		db.execSQL("insert into Exercises (name, description, category) values ('Swimming', 'good for knees', 'Cardio')");
		db.execSQL("insert into Exercises (name, description, category) values ('Bench Press', 'with weights', 'Strength Training')");
		
	}
}
