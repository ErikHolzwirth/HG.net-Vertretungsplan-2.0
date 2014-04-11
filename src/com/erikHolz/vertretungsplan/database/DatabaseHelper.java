/*
    Copyright (C) 2014  Erik Holzwirth

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.erikHolz.vertretungsplan.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.erikHolz.vertretungsplan.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "db_vertretungsplan.db";

	// Table Names
	private static final String TABLE_TODAY = "heute";
	private static final String TABLE_TOMORROW = "morgen";
	private static final String TABLE_MENU = "essen";
	private static final String TABLE_USER = "nutzer";

	// gemeinsame Spalte ID
	private static final String KEY_ID = "id";

	// Spalten für Vertrungsplan Tabellen
	private static final String KEY_DATUM = "datum";
	private static final String KEY_KLASSE = "klasse";
	private static final String KEY_STUNDE = "stunde";
	private static final String KEY_FACH = "fach";
	private static final String KEY_RAUM = "raum";
	private static final String KEY_AUSFALL = "ausfall";
	private static final String KEY_VERTRETUNG = "vertretung";

	// Spalten für Essensplan Tabelle
	private static final String KEY_TAG = "tag";
	private static final String KEY_ESSEN_A = "essen_a";
	private static final String KEY_ESSEN_B = "essen_b";


	// SQL Codes zur Erstellung der Datenbanken

	// Datenbank Heute
	private static final String CREATE_TABLE_TODAY = "CREATE TABLE "
			+ TABLE_TODAY + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATUM
			+ " TEXT," + KEY_KLASSE + " TEXT," + KEY_STUNDE + " TEXT,"
			+ KEY_FACH + " TEXT," + KEY_RAUM + " TEXT," + KEY_AUSFALL
			+ " TEXT," + KEY_VERTRETUNG + " TEXT" + ")";

	// Datenbank Morgen
	private static final String CREATE_TABLE_TOMORROW = "CREATE TABLE "
			+ TABLE_TOMORROW + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_DATUM + " TEXT," + KEY_KLASSE + " TEXT," + KEY_STUNDE
			+ " TEXT," + KEY_FACH + " TEXT," + KEY_RAUM + " TEXT,"
			+ KEY_AUSFALL + " TEXT," + KEY_VERTRETUNG + " TEXT" + ")";

	// Datenbank Essen
	private static final String CREATE_TABLE_MENU = "CREATE TABLE "
			+ TABLE_MENU + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TAG
			+ " TEXT," + KEY_ESSEN_A + " TEXT," + KEY_ESSEN_B + " TEXT" + ")";

	// Datenbank Nutzer
	private static final String CREATE_TABLE_USER = "CREATE TABLE "
			+ TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_KLASSE
			+ " TEXT" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// benötigte Tabellen erstellen
		db.execSQL(CREATE_TABLE_TODAY);
		db.execSQL(CREATE_TABLE_TOMORROW);
		db.execSQL(CREATE_TABLE_MENU);
		db.execSQL(CREATE_TABLE_USER);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Löschen der alten Tabellen
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODAY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOMORROW);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

		// Erstellen neuer Tabellen
		onCreate(db);
	}

	// Methoden um mit der Datenbank zu interagieren

	// eine Vertretung erstellen
	public long createData(Data data, boolean today) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_KLASSE, data.getKlasse());
		values.put(KEY_DATUM, data.getDatum());
		values.put(KEY_STUNDE, data.getStunde());
		values.put(KEY_RAUM, data.getRaum());
		values.put(KEY_FACH, data.getFach());
		values.put(KEY_AUSFALL, data.getAusfall());
		values.put(KEY_VERTRETUNG, data.getVertretung());

		long row_id;

		// Zeile einfügen
		if (today)
			row_id = db.insert(TABLE_TODAY, null, values);
		else
			row_id = db.insert(TABLE_TOMORROW, null, values);

		return row_id;
	}

	// einen Essenseintrag erstellen
	public long createMenu(Menu menu) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TAG, menu.getTag());
		values.put(KEY_ESSEN_A, menu.getEssenA());
		values.put(KEY_ESSEN_B, menu.getEssenB());

		long row_id;

		// Zeile einfügen
		row_id = db.insert(TABLE_MENU, null, values);

		return row_id;
	}

	// einen Nutzer erstellen
	public long createUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE "
				+ KEY_KLASSE + " = '" + user.getKlasse() + "'";

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			db.delete(TABLE_USER, KEY_KLASSE + "= '" + user.getKlasse() + "'",
					null);
		}

		// looping through all rows and adding to list
		ContentValues values = new ContentValues();
		values.put(KEY_KLASSE, user.getKlasse());

		// Zeile einfügen
		long row_id = db.insert(TABLE_USER, null, values);

		return row_id;
	}

	// einen Nutzer löschen
	public void deleteUser(String klasse) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_USER, KEY_KLASSE + "= '" + klasse + "'", null);
	}

	// eine Vertretung auslesen
	public Data getData(long row_id, boolean today) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery;

		if (today)
			selectQuery = "SELECT  * FROM " + TABLE_TODAY + " WHERE " + KEY_ID
					+ " = " + row_id;
		else
			selectQuery = "SELECT  * FROM " + TABLE_TOMORROW + " WHERE "
					+ KEY_ID + " = " + row_id;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Data dt = new Data(c.getColumnIndex(KEY_ID), c.getString(c
				.getColumnIndex(KEY_DATUM)), c.getString(c
				.getColumnIndex(KEY_KLASSE)), c.getString(c
				.getColumnIndex(KEY_STUNDE)), c.getString(c
				.getColumnIndex(KEY_FACH)), c.getString(c
				.getColumnIndex(KEY_RAUM)), c.getString(c
				.getColumnIndex(KEY_AUSFALL)), c.getString(c
				.getColumnIndex(KEY_VERTRETUNG)));

		return dt;
	}

	// einen Essenseintrag auslesen
	public Menu getMenu(long row_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery;

		selectQuery = "SELECT  * FROM " + TABLE_MENU + " WHERE " + KEY_ID
				+ " = " + row_id;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Menu mn = new Menu(c.getColumnIndex(KEY_ID), c.getString(c
				.getColumnIndex(KEY_TAG)), c.getString(c
				.getColumnIndex(KEY_ESSEN_A)), c.getString(c
				.getColumnIndex(KEY_ESSEN_B)));

		return mn;
	}

	// alle Vertretungen einer Klasse auslesen
	public List<Data> getAllDataByClass(String name_klasse, boolean today) {
		List<Data> datas = new ArrayList<Data>();

		String selectQuery;

		if (today)
			selectQuery = "SELECT * FROM " + TABLE_TODAY + " WHERE "
					+ KEY_KLASSE + " = '" + name_klasse + "'" + " ORDER BY "
					+ KEY_STUNDE + " ASC";
		else
			selectQuery = "SELECT * FROM " + TABLE_TOMORROW + " WHERE "
					+ KEY_KLASSE + " = '" + name_klasse + "'" + " ORDER BY "
					+ KEY_STUNDE + " ASC";
		;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Data dt = new Data(c.getColumnIndex(KEY_ID), c.getString(c
						.getColumnIndex(KEY_DATUM)), c.getString(c
						.getColumnIndex(KEY_KLASSE)), c.getString(c
						.getColumnIndex(KEY_STUNDE)), c.getString(c
						.getColumnIndex(KEY_FACH)), c.getString(c
						.getColumnIndex(KEY_RAUM)), c.getString(c
						.getColumnIndex(KEY_AUSFALL)), c.getString(c
						.getColumnIndex(KEY_VERTRETUNG)));

				// adding to todo list
				datas.add(dt);
			} while (c.moveToNext());
		}

		return datas;
	}

	// alle Essenseinträge auslesen
	public List<Menu> getAllMenus() {
		List<Menu> menus = new ArrayList<Menu>();

		String selectQuery;

		selectQuery = "SELECT * FROM " + TABLE_MENU;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Menu mn = new Menu(c.getColumnIndex(KEY_ID), c.getString(c
						.getColumnIndex(KEY_TAG)), c.getString(c
						.getColumnIndex(KEY_ESSEN_A)), c.getString(c
						.getColumnIndex(KEY_ESSEN_B)));

				// adding to todo list
				menus.add(mn);
			} while (c.moveToNext());
		}

		return menus;
	}

	// alle Nutzer auslesen
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();

		String selectQuery;

		selectQuery = "SELECT * FROM " + TABLE_USER;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				User us = new User(c.getColumnIndex(KEY_ID), c.getString(c
						.getColumnIndex(KEY_KLASSE))
						);

				// adding to todo list
				users.add(us);
			} while (c.moveToNext());
		}

		return users;
	}

	// Datenbank leeren
	public void clearDatabaseData(boolean today) {
		SQLiteDatabase db = this.getWritableDatabase();
		if (today)
			db.delete(TABLE_TODAY, null, null);
		else
			db.delete(TABLE_TOMORROW, null, null);
	}

	// Essensplan leeren
	public void clearDatabaseMenu() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MENU, null, null);
	}

	// gibt das Datum der letzten Aktualisierung zurück
	public int getStatus() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery;

		selectQuery = "SELECT * FROM " + TABLE_TODAY + " WHERE " + KEY_ID
				+ " = " + 1;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			String date = c.getString(c.getColumnIndex(KEY_DATUM)).replace(" ",
					"");
			return Integer.parseInt(date);
		}

		else
			return 0;
	}

	public boolean isEmpty(boolean today) {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery;

		if (today)
			selectQuery = "SELECT * FROM " + TABLE_TODAY;
		else
			selectQuery = "SELECT * FROM " + TABLE_TOMORROW;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst())
			return false;

		else
			return true;
	}

	public String[] getDates() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery;
		String result[] = new String[2];
		String[] buffer = new String[3];
		
		Calendar cal = new GregorianCalendar();
		Calendar.getInstance();
		cal.setTime(new Date());

		// Datum für den ersten Tag bestimmen
		selectQuery = "SELECT * FROM " + TABLE_TODAY;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		// Eintrag vorhanden, Datum wird übernommen
		if (c.moveToFirst()) {
			result[0] = c.getString(c.getColumnIndex(KEY_DATUM));
			buffer = result[0].split(" ");
		}
		
		// kein Eintrag, Datum wird erstellt
		else {			
			if(cal.get(Calendar.DAY_OF_WEEK) == 7)
				cal.add(Calendar.DATE, 2);
			else if (cal.get(Calendar.DAY_OF_WEEK) == 0)
				cal.add(Calendar.DATE, 1);

			buffer[0] = String.valueOf(cal.get(Calendar.YEAR));
			buffer[1] = String.valueOf(cal.get(Calendar.MONTH));
			buffer[2] = String.valueOf(cal.get(Calendar.DATE));
			
			cal.add(Calendar.DATE, 1);
		}
		
		if (buffer[2].charAt(0) == '0') 
			result[0] = buffer[2].charAt(1) + ". ";
		else
			result[0] = buffer[2] + ". ";

		Constants con = new Constants();
		result[0] += con.getMonthAsString(buffer[1]);
		result[0] += buffer[0];

		// Datum für den Folgetag bestimmen
		selectQuery = "SELECT * FROM " + TABLE_TOMORROW;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);
		c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst())
			result[1] = c.getString(c.getColumnIndex(KEY_DATUM));

		if (result[1] != null)
			buffer = result[1].split(" ");
		else {
			if(cal.get(Calendar.DAY_OF_WEEK) == 7)
				cal.add(Calendar.DATE, 2);
			else if (cal.get(Calendar.DAY_OF_WEEK) == 0)
				cal.add(Calendar.DATE, 1);

			buffer[0] = String.valueOf(cal.get(Calendar.YEAR));
			buffer[1] = String.valueOf(cal.get(Calendar.MONTH));
			buffer[2] = String.valueOf(cal.get(Calendar.DATE));
		}

		result[1] = buffer[2] + ". ";
		result[1] += con.getMonthAsString(buffer[1]);
		result[1] += buffer[0];

		return result;
	}

	public int getAmountByClass(String name_klasse, boolean today) {
		String selectQuery;
		int amount = 0;

		if (today)
			selectQuery = "SELECT * FROM " + TABLE_TODAY + " WHERE "
					+ KEY_KLASSE + " = '" + name_klasse + "'";
		else
			selectQuery = "SELECT * FROM " + TABLE_TOMORROW + " WHERE "
					+ KEY_KLASSE + " = '" + name_klasse + "'";

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst())
			do
				amount++;
			while (c.moveToNext());

		return amount;

	}

	public int getUserAmount() {
		String selectQuery;
		int amount = 0;

		selectQuery = "SELECT * FROM " + TABLE_USER;

		// LogCat Eintrag für den SQL Vorgang anlegen
		Log.d(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst())
			do
				amount++;
			while (c.moveToNext());

		return amount;
	}

	// Datenbank schließen
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}
}
