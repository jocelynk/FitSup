package com.team03.fitsup.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.team03.fitsup.R;
import com.team03.fitsup.data.WorkoutRoutineTable;

public class WorkoutTabWidget extends TabActivity {
	
	private Long mRowId;
	private static final int ACTIVITY_EDIT=1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workouts_tabs);
		
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; //Resusable TabSpec for each tab
		Intent intent; //Reusable Intent for each tab
		
		mRowId = (savedInstanceState == null) ? null :
    	    (Long) savedInstanceState.getSerializable(WorkoutRoutineTable.COLUMN_ID);
    	if (mRowId == null) {
    	    Bundle extras = getIntent().getExtras();
    	    mRowId = extras != null ? extras.getLong(WorkoutRoutineTable.COLUMN_ID)
    	                            : null;
    	}
		
		//intent = new Intent().setClass(this, WorkoutRoutineView.class);
	   // spec = tabHost.newTabSpec("albums").setIndicator("Albums",
	                   //   res.getDrawable(R.drawable.ic_tab_artists_grey))
	                 // .setContent(intent);
	   // tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, TestClass.class);
		spec = tabHost.newTabSpec("tab1").setIndicator("Tab1", res.getDrawable(R.drawable.ic_tab_artists_grey)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, TestClass2.class);
		spec = tabHost.newTabSpec("tab2").setIndicator("Tab2", res.getDrawable(R.drawable.ic_tab_artists_grey)).setContent(intent);
		tabHost.addTab(spec);
		
		
		intent = new Intent().setClass(this, WorkoutRoutineEdit.class);
		intent.putExtra(WorkoutRoutineTable.COLUMN_ID, mRowId);
        startActivityForResult(intent, ACTIVITY_EDIT);
		spec = tabHost.newTabSpec("tab3").setIndicator("Tab3", res.getDrawable(R.drawable.ic_tab_artists_grey)).setContent(intent);
		tabHost.addTab(spec);
		
	    tabHost.setCurrentTab(0);


	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
    

}
