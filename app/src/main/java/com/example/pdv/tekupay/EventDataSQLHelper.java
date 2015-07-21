package com.example.pdv.tekupay;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/** Helper to the database, manages versions and creation */
public class EventDataSQLHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "transactions.db";
	private static final int DATABASE_VERSION = 1;

	// Table name
	public static final String TABLE = "transactions";

	// Columns
	public static final String PAN = "pan";
	public static final String FECHA = "fecha";
	public static final String HORA = "hora";
	public static final String MONTO = "monto";
	public static final String STATUS = "transaction_status";
	public static final String TIPO_TARJETA = "tipo_tarjeta";
	public static final String CIERRE = "cierre";
	public static final String REFERENCIA = "referencia";

	public EventDataSQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + "( " + BaseColumns._ID + " integer primary key autoincrement, " + PAN + " text not null, " + FECHA + " text not null, " + HORA + " text not null, " + MONTO + " text not null, " + STATUS + " integer not null, " + TIPO_TARJETA + " text not null, " + CIERRE + " int not null, " + REFERENCIA + " text not null);";
		Log.d("EventsData", "onCreate: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion)
			return;

		String sql = null;
		if (oldVersion == 1) 
			sql = "alter table " + TABLE + " add note text;";
		if (oldVersion == 2)
			sql = "";

		Log.d("EventsData", "onUpgrade	: " + sql);
		if (sql != null)
			db.execSQL(sql);
	}

}
