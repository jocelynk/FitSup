/**package com.team03.fitsup.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;

import com.team03.fitsup.data.DatabaseHelper;

public class FitSupContentProvider extends ContentProvider {

	//database
	private DatabaseHelper database;
	
	//Used for the UriMatcher
	private static final int WORKOUTROUTINES = 10;
	private static final int WORKOUTROUTINE_ID = 20;
	
	private static final String AUTHORITY = "com.team03.fitsup.contentprovider";
	
	private static final String BASE_PATH = "workoutroutine";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/workoutroutines";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/workoutroutine";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, WORKOUTROUTINES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", WORKOUTROUTINE_ID);
		
	}
	
	@Override
	public boolean onCreate() {
		database = new DatabaseHelper(getContext());
		return false;
	}
	
	
}
**/