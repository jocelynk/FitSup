package com.team03.fitsup.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestClass2 extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the test2 tab");
        setContentView(textview);
    }

}
