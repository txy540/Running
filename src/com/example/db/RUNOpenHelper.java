package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RUNOpenHelper extends SQLiteOpenHelper {
	public static final String RUN_DATA="create table rundata("
			//+"id integer primary key autoincrement,"
			+"distance double,"
			+"speed double,"
			+"time double,"
			+"calorie double)";
	
	public RUNOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);//创建数据库
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) { //创建数据表
		db.execSQL(RUN_DATA);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		

	}

}
