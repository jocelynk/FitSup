package com.team03.fitsup.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.team03.fitsup.R;
import com.team03.fitsup.data.WorkoutRoutineTable;

public class WorkoutRoutineEdit extends Activity {

	private EditText mNameText;
	private EditText mDescriptionText;
	private Long mRowId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workout_edit);
		setTitle(R.string.edit_workout);
		mNameText = (EditText) findViewById(R.id.name);
		mDescriptionText = (EditText) findViewById(R.id.description);
		
		Button confirmButton = (Button) findViewById(R.id.confirm);
		
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String name = extras.getString(WorkoutRoutineTable.COLUMN_NAME);
		    String description = extras.getString(WorkoutRoutineTable.COLUMN_DESCRIPTION);
		    mRowId = extras.getLong(WorkoutRoutineTable.COLUMN_ID);

		    if (name != null) {
		        mNameText.setText(name);
		    }
		    if (description != null) {
		        mDescriptionText.setText(description);
		    }
		}
		
		confirmButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		    	Bundle bundle = new Bundle();

		    	bundle.putString(WorkoutRoutineTable.COLUMN_NAME, mNameText.getText().toString());
		    	bundle.putString(WorkoutRoutineTable.COLUMN_DESCRIPTION, mDescriptionText.getText().toString());
		    	if (mRowId != null) {
		    	    bundle.putLong(WorkoutRoutineTable.COLUMN_ID, mRowId);
		    	}
		    	Intent mIntent = new Intent();
		    	mIntent.putExtras(bundle);
		    	setResult(RESULT_OK, mIntent);
		    	finish();

		    }

		});
		
	}

}
