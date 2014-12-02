package com.example.urmlauncher;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "LauncherDatabase";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "application";
	private final static String APPLICATION_NAME = "app_name";
	private final static String APPLICATION_COUNT = "app_count";
	private final static String APPLICATION_TIME = "app_time";
	private final static String EVENT_TIME = "event_time";
	private final static long TWO_DAYS = 2 * 24 * 60 * 60 * 1000;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "Create table " + TABLE_NAME + " (" + APPLICATION_NAME
				+ " VARCHAR, " + APPLICATION_COUNT + " INTEGER, "
				+ APPLICATION_TIME + " INTEGER, " + EVENT_TIME + " INTEGER)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public void updateCount(String appName) {
		Calendar calendar = Calendar.getInstance();
		long currentTime = calendar.getTimeInMillis();
		long eventTime = (calendar.get(Calendar.HOUR) * 60 * 60)
				+ (calendar.get(Calendar.MINUTE) * 60)
				+ calendar.get(Calendar.SECOND);

		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE (" + currentTime
				+ " - " + APPLICATION_TIME + " ) >= " + TWO_DAYS;
		db.execSQL(sql);

		sql = "SELECT " + APPLICATION_COUNT + " FROM " + TABLE_NAME + " WHERE "
				+ APPLICATION_NAME + " = '" + appName + "'";
		Cursor cursor = db.rawQuery(sql, null);

		// update the count if the app exists in the database else insert it
		if (cursor.moveToFirst()) {
			sql = "UPDATE " + TABLE_NAME + " SET " + APPLICATION_COUNT + " = "
					+ APPLICATION_COUNT + " + 1, " + APPLICATION_TIME + " = "
					+ currentTime + ", " + EVENT_TIME + " = " + eventTime
					+ "  WHERE " + APPLICATION_NAME + " = '" + appName + "'";
		} else {
			sql = "INSERT INTO " + TABLE_NAME + "( " + APPLICATION_NAME + ", "
					+ APPLICATION_COUNT + "," + APPLICATION_TIME + ", "
					+ EVENT_TIME + ") VALUES " + "( '" + appName + "', 1, "
					+ currentTime + ", " + eventTime + ")";
		}
		db.execSQL(sql);
	}

	public ArrayList<String> getAppNameList() {
		SQLiteDatabase db = this.getWritableDatabase();
		Calendar calendar = Calendar.getInstance();
		long eventTime = (calendar.get(Calendar.HOUR) * 60 * 60)
				+ (calendar.get(Calendar.MINUTE) * 60)
				+ calendar.get(Calendar.SECOND);

		String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + " abs("
				+ eventTime + " - " + EVENT_TIME + "), " + APPLICATION_COUNT
				+ " DESC , " + APPLICATION_TIME + " DESC";
		Cursor cursor = db.rawQuery(sql, null);
		int columnIndex = cursor.getColumnIndex(APPLICATION_NAME);
		ArrayList<String> appNameList = new ArrayList<>();
		if (cursor.moveToFirst()) {
			appNameList.add(cursor.getString(columnIndex));
			while (cursor.moveToNext()) {
				appNameList.add(cursor.getString(columnIndex));
			}
		}
		return appNameList;
	}
}
