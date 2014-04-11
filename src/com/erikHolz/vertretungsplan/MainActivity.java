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

package com.erikHolz.vertretungsplan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.erikHolz.vertretungsplan.DialogClassPicker.DialogListener;
import com.erikHolz.vertretungsplan.RefreshHandler.ThreadListener;
import com.erikHolz.vertretungsplan.database.DatabaseHelper;
import com.erikHolz.vertretungsplan.database.User;
import com.erikHolz.vertretungsplan.listviewadapters.DrawerEntry;
import com.erikHolz.vertretungsplan.listviewadapters.ListAdapterDrawer;

public class MainActivity extends Activity implements ThreadListener,
		DialogListener {

	public static final String PREFS_NAME = "UserSettings";

	private DrawerLayout mDrawerLayout;
	public ArrayList<DrawerEntry> mDrawerTitles;
	private LinearLayout mMenu;

	private ListView mDrawerList;
	private ListAdapterDrawer mListAdapterDrawer;

	private ActionBarDrawerToggle mDrawerToggle;

	DatabaseHelper db;

	boolean firstStart;

	Spinner classChooser;

	String[] dateDatabase;
	String userInput;

	boolean addedUser;

	int currentOrientation;

	// Aufruf, wenn die App gestartet wird
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Laden des Layouts
		setContentView(R.layout.activity_main);
		
		lockScreenOrientation();

		// SharedPreferences werden genutzt, um Einstellungen in Form primitiver
		// Datentypen (wie z.B. int, boolean, long, ..) zu speichern
		// es können wie bereits angedeutet allerdings keine komplexeren Daten
		// gespeichert werden, dafür bietet sich dann eine SQL Datenbank oder
		// eine txt Datei etc. an
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		// es wird überpüft, ob dies der erste Start der App ist
		// ist der Wert firstStart nicht belegt, also weder true noch false,
		// wird auf jeden Fall true zurück gegeben
		firstStart = settings.getBoolean("firstStart", true);

		// der NavigationDrawer als zentrales Naviagtionselement wird
		// eigerichtet
		try {
			initializeNavigationDrawer();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		// Erstellen des Datenbankobjektes, um die alten Daten
		// abzurufen oder neue einzutragen
		db = new DatabaseHelper(getApplicationContext());

		// der Spinner zur Auswahl der Klasse wird im Layoutfile gesucht und
		// gespeichert, um so Veränderungen etc. möglich zu machen
		classChooser = (Spinner) findViewById(R.id.user_spinner);

		// der Listener zur Reaktion auf Interaktion mit dem Spinner wird
		// erstellt
		classChooser
				.setOnItemSelectedListener(new ChooserItemSelectedListener());

		// da die App zum ersten mal gestartet wird, ist noch keine Klasse
		// erstellt worden, für die nach Vertretungen gesucht werden soll
		// deshalb muss die Klasse nun durch das Dialog Fenster zur
		// Klassenauswahl festgelegt werden
		if (firstStart)
			showSearchDialog();

		else {
			loadSpinnerData();

			// Erstellen des aktuellen Datums als String, um es mit dem Datum
			// der letzten Aktualisierung zu vergleichen
			String today = getDate();

			// falls das Datum der letzten Aktualisierung der Daten kleiner ist
			// als das aktuelle Datum, (also die Daten veraltet sind) werden
			// neue Daten bezogen
			// der Vergleich erfolgt durch die Konvertierung des Strings in eine
			// Zahl, wobei der 16. Februar 2014 kleiner als der 17. Februar 2014
			// ist, da: 20140216 < 20140217
						
			if (db.getStatus() < Integer.parseInt(today.replace(" ", ""))) {

				dateDatabase = new String[2];
				dateDatabase[0] = "";
				dateDatabase[1] = "";

				RefreshHandler handler = new RefreshHandler();
				handler.setContext(this);
				Thread download = new DownloadThread(null, "downloadThread",
						handler, db, this);
				download.start();

			}

			// ansonsten können einfach die alten Daten dargestellt werden
			else {
				// das aus der Datenbank wird zur Dartsellung im
				// NavigationDrawer bezogen
				dateDatabase = db.getDates();
			}
		}

		// starten des Service für Hintergrudn Daten
		Calendar cal = Calendar.getInstance();

		settings.edit().putBoolean("isRunning", true).commit();

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		Intent intent = new Intent(this, UpdateService.class);
		PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0,
				intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				Integer.parseInt(sharedPref.getString(
						SettingsFragment.PREF_KEY_BACKGROUNDDATA_INTERVALL,
						"45")) * 60 * 1000, pintent);
	}

	// Funktion, um das aktuelle Datum als String nach dem Schema "YYYY MM DD"
	// zu erstellen

	public String getDate() {
		String today;

		Calendar date = new GregorianCalendar();
		Calendar.getInstance();
		date.setTime(new Date());

		today = "";
		today += date.get(Calendar.YEAR);

		if (date.get(Calendar.MONTH) + 1 < 10)
			today += " 0" + (date.get(Calendar.MONTH) + 1);
		else
			today += " " + (date.get(Calendar.MONTH) + 1);

		if (date.get(Calendar.DATE) < 10)
			today += " 0" + date.get(Calendar.DATE);
		else
			today += " " + date.get(Calendar.DATE);

		return today;
	}

	private void initializeNavigationDrawer() throws NoSuchFieldException,
			IllegalAccessException, IllegalArgumentException {
		// Laden des Drawer - Layouts
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		Field mDragger = mDrawerLayout.getClass().getDeclaredField(
				"mLeftDragger");// mRightDragger for right obviously
		mDragger.setAccessible(true);
		ViewDragHelper draggerObj = (ViewDragHelper) mDragger
				.get(mDrawerLayout);

		Field mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
		mEdgeSize.setAccessible(true);
		int edge = mEdgeSize.getInt(draggerObj);

		mEdgeSize.setInt(draggerObj, edge * 12);
		mMenu = (LinearLayout) findViewById(R.id.menu);

		// Öffnen des Drawers verändert ActionBar
		mDrawerToggle = new ActionBarDrawerToggle(this, // die Activity, die den
														// Drawer beinhaltet
				mDrawerLayout, // der Drawer, mit dem interagiert wird
				R.drawable.ic_drawer, // Icon oben links, wenn drawer geöffnet
										// (3 Striche)
				R.string.drawer_open, // zum Vorlesen für Blinde, etc.
				R.string.drawer_close) {

			// wird aufgerufen, wenn der Drawer geschlossen wurde
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu();
			}

			// wird aufgerufen, wenn der Drawer geöffnet wurde
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
			}
		};

		// mDrawerToggle reagiert auf Interaktionen mit dem Drawer
		// --> wird zum "Listener"
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Logo der App kann benutzt werden, um Drawer zu schließen / öffnen
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Drawer Einträge werden durch eine Liste dargstellt
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerTitles = new ArrayList<DrawerEntry>();

		mListAdapterDrawer = new ListAdapterDrawer(this, mDrawerTitles);

		mDrawerList.setAdapter(mListAdapterDrawer);

		// der Listener zur Reaktion auf Interaktion mit dem NaviagtionDrawer
		// wird erstellt
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}

	// die Funktion wird genutzt, um den Spinner zur Nutzerauswahl mit Daten
	// aus der Datenbank befüllen
	private void loadSpinnerData() {
		// Spinner Drop down elements
		List<User> users = db.getAllUsers();
		String[] user = new String[users.size()];

		for (int i = 0; i < users.size(); i++)
			user[i] = "Klasse " + users.get(i).getKlasse();

		ArrayAdapter<String> dataAdapter;

		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, user);

		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		classChooser.setAdapter(dataAdapter);
	}

	static class CustomArrayAdapter<T> extends ArrayAdapter<T> {
		public CustomArrayAdapter(Context ctx, T[] objects) {
			super(ctx, android.R.layout.simple_spinner_item, objects);
		}

		// other constructors

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			View view = super.getView(position, convertView, parent);

			// we know that simple_spinner_item has android.R.id.text1 TextView:

			/* if(isDroidX) { */
			TextView text = (TextView) view.findViewById(android.R.id.text1);
			text.setTextColor(Color.RED);// choose your color :)
			/* } */

			return view;

		}
	}

	// die Funktion wird genutzt, um die Bezeichner für die Items im
	// NavigationDrawer
	// zu ändern
	public void setDrawerTitles() {

		// Layout der einzelnen Drawer Optionen und Listener für Auswahl der
		// Elemente festlegen

		mDrawerTitles.clear();
		mDrawerTitles.add(0, new DrawerEntry("Einträge"));

		mDrawerTitles.add(
				1,
				new DrawerEntry(R.drawable.ic_drawer_vp_default,
						dateDatabase[0], String.valueOf(db.getAmountByClass(
								userInput, true))));
		mDrawerTitles.add(
				2,
				new DrawerEntry(R.drawable.ic_drawer_vp_default,
						dateDatabase[1], String.valueOf(db.getAmountByClass(
								userInput, false))));

		mDrawerTitles.add(3, new DrawerEntry(
				R.drawable.ic_drawer_essen_default, "Essensplan", ""));

		mDrawerTitles.add(4, new DrawerEntry("Weiteres"));

		mDrawerTitles.add(5,
				new DrawerEntry(R.drawable.ic_drawer_einstellungen_default,
						"Einstellungen", ""));

		mDrawerTitles.add(6, new DrawerEntry(
				R.drawable.ic_drawer_homepage_default, "Homepage", ""));

		mDrawerTitles.add(7, new DrawerEntry(R.drawable.ic_drawer_info_default,
				"Info", ""));
		
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, 0);
		
		((TextView) findViewById(R.id.last_update)).setText("zuletzt aktualisiert um " + pref.getString("lastUpdate", "0:00"));

		mListAdapterDrawer.notifyDataSetChanged();
	}

	/**
	 * 
	 * die folgenden Funktionen stellen die Verbindung zwischen dem AsyncTask
	 * und der Activity dar. Der AsyncTask ruft die Funktionen auf (was möglich
	 * ist, da die Activity sein Listener ist, also auf seine "Bitten" "hört"),
	 * um bestimmte Veränderungen am UI durchzuführen
	 * 
	 */

	@Override
	public void onThreadStarted() {
		// verbietet die Veränderung der Display - Orientierung, bevor die
		// eigentliche Arbeit des AsyncTasks beginnt
		lockScreenOrientation();
	}

	// lädt automatisch den Vertretungsplan des aktuellen
	// Tages
	@Override
	public void onThreadFinished() {
		unlockScreenOrientation();

		// erst nachdem der Download der Daten nach einer Neuinstallation der
		// App abgeschlossen ist, sollen die Daten für den Spinner geladen
		// werden
		if (firstStart) {

			// der erste Start der App war erfolgreich, der nächste Start der
			// App ist nicht mehr länger der erste Start :)
			firstStart = false;

			// firstStart kann also auch in den SharedPreferences auf false
			// gesetzt werden
			SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME,
					0).edit();
			editor.putBoolean("firstStart", firstStart);
			editor.commit();

			// Laden der Daten für den Spinner
			loadSpinnerData();
		}

		// ein nun eventuell verändertes Datum wird aus der Datenbank geladen
		dateDatabase = db.getDates();

		// Datum der Aktualisierung speichern
		Calendar cal = new GregorianCalendar();
		Calendar.getInstance();
		cal.setTime(new Date());

		String time = "";
		if (cal.get(Calendar.HOUR_OF_DAY) < 10)
			time += "0" + String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		else
			time += String.valueOf(cal.get(Calendar.HOUR_OF_DAY));

		time += ":";
		
		if (cal.get(Calendar.MINUTE) < 10)
			time += "0" + String.valueOf(cal.get(Calendar.MINUTE));
		else
			time += String.valueOf(cal.get(Calendar.MINUTE));
		
		SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME,
				0).edit();
		editor.putString("lastUpdate", time);
		editor.commit();
		
		// die Bezeichner im NavigationDrawer werden nun wieder entsprechend des
		// jeweiligen Datums gesetzt
		setDrawerTitles();

		// die Darstellung des Vertretungsplanes für den aktuelle Tag wird
		// ausgelöst
		selectItem(1);
	}

	/**
	 * 
	 * die folgenden Funktionen werden immer dann aufgerufen, wenn es zu einer
	 * Interaktion mit dem NavigationDrawer kommt
	 * 
	 */

	// diese Klasse wird primär dazu verwendet, dem Adapter für das ListView
	// des NaviagtionDrawers mitzuteilen, welches Item momentan aktiv ist
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@SuppressWarnings("rawtypes")
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {

			// Position 0 und 4 sind Überschriften
			if (position != 0 && position != 4)
				// Funktion zur Darstellung des jeweiligen Inhalts wird
				// aufgerufen
				selectItem(position);

		}
	}

	// diese Klasse wird dazu verwendet, um auf die Auswahl der Klasse mithilfe
	// des
	// Spinners zu reagieren
	private class ChooserItemSelectedListener implements
			Spinner.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int id,
				long longId) {

			// liest den aktuell ausgewählten Wert aus
			String str = (String) classChooser.getItemAtPosition(id);

			// der ausgelesene Wert wird an den Leerzeichen gespaltet,
			// da nur die Angabe der Klasse benötigt wird
			// der Wert ist z.B. "Klasse 5/1 (Max Mustermann)"
			// buffer[0] = "Klasse"
			// buffer[1] = "5/1"
			// buffer[2] = "(Max Mustermann)"
			String[] buffer = str.split(" ");

			if (addedUser)
				addedUser = false;
			else
				userInput = buffer[1];

			setDrawerTitles();

			// es wird die Darstellung der Daten des aktuellen Tages
			// ausgewählt
			selectItem(1);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	// wird durch den DrawerItemClickListener aufgerufen, wenn ein Eintrag des
	// Drawers ausgewählt wurde
	private void selectItem(final int position) {

		// der Drawer wird wieder geschlossen
		mDrawerLayout.closeDrawer(mMenu);

		// 0 - Überschrift
		// 1 - Vertretungsplan heute
		// 2 - Vertretungsplan morgen
		// 3 - Essensplan
		// --> 1 - 3 werden alle durch eine neues Fragment dargestellt
		if (position > 0 && position < 4) {

			// Handler wird verwendet, damit NavigationDrawer frei von
			// Lags schließt
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

					// erstellt ein neues Fragment zur Darstellung der Daten
					Fragment fragment = new ContentFragment();
					((ContentFragment) fragment).setDatabase(db);

					// übergibt dem Fragment Argumente
					Bundle args = new Bundle();

					// die Position des ausgewählten Menüeintrags innerhalb des
					// Listviews --> daraus wird abgeleitet, ob ein
					// Vertretungsplan (für heute oder morgen) oder der
					// Essensplan dargstellt werden soll
					args.putInt(ContentFragment.ARG_MODE, position);

					// die Klasse, deren Vertretungen angezeigt werden sollen
					args.putString(ContentFragment.ARG_USERINPUT, userInput);

					// das Datum für den aktuellen Tag, zur Darstellung in
					// der ActionBar
					args.putString(ContentFragment.ARG_DATE_TODAY,
							dateDatabase[0]);

					// das Datum für den kommenden Tag, zur Darstellung in
					// der ActionBar
					args.putString(ContentFragment.ARG_DATE_TOMORROW,
							dateDatabase[1]);

					// das Bundle (= "Bündel") wurde "gepackt" und wird dem
					// Fragment zur Auswertung übergeben
					fragment.setArguments(args);

					// ersetzt das aktuelle Fragment durch das neue
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.content_frame, fragment).commit();

					// dem Adapter wird mitgeteilt, dass das Item an der Stelle
					// position momentan aktiv ist
					mListAdapterDrawer.setActive(position);
					// dadurch wird der Adapter dazu aufgefordert, die
					// dargestellten
					// Information (--> also das ListView) zu aktualisieren
					// dies ist notwendig, damit angezeigt werden kann, welches
					// Item
					// momentan aktiv ist
					mListAdapterDrawer.notifyDataSetChanged();
				}
			}, 250);

		}

		// 4 - Überschrift
		// 5 - Einstellungen
		if (position == 5) {

			// Handler wird verwendet, damit NavigationDrawer frei von
			// Lags schließt
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

					// erstellt ein neues Fragment zur Darstellung der
					// Einstellungen
					Fragment fragment = new SettingsFragment();

					// ersetzt das aktuelle Fragment durch das neue
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.content_frame, fragment).commit();
				}
			}, 250);

		}

		// 6 - Homepage
		// Aufruf im Browser des Nutzers
		else if (position == 6) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.humgym.net/"));
			startActivity(browserIntent);
		}

		else if (position == 7) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Wilhelm von Humboldt Gymnasium" 
							+ "\n Nordhausen 2014 " 
							+ "\n by Erik Holzwirth \n"
							+ "\n Der Quellcode ist frei einsehbar")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							})
					.setNeutralButton("Code",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent browserIntent = new Intent(Intent.ACTION_VIEW,
											Uri.parse("https://github.com/ErikHolzwirth/HG.net-Vertretungsplan-2.0"));
									startActivity(browserIntent);
								}
							});
			builder.create();
			builder.show();
		}

	}

	/**
	 * 
	 * die folgenden Funktionen bestimmen das Verhalten der App in Bezug auf
	 * Interaktionen mit der ActionBar
	 * 
	 */

	// enthält, wie die App auf ein auswählen der Aktionen in der ActionBar
	// reagieren soll
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Auswahl der Lupe zum Suchen nach Einträgen für eine andere Klasse
		// öffnet den Eingabedialog
		switch (item.getItemId()) {
		case R.id.action_search:
			showSearchDialog();
			return true;

			// Auswahl des Symbols zur Aktualisierung der Daten
		case R.id.action_reload:
			RefreshHandler handler = new RefreshHandler();
			handler.setContext(this);
			Thread download = new DownloadThread(null, "downloadThread",
					handler, db, this);
			download.start();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	// Aktionen der ActionBar verschwinden bei geöffnetem NavigationDrawer bzw.
	// tauchen
	// bei geschlossenem NavigationDrawer wieder auf
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Drawer ist geöffnet
		// --> drawerOpen = true
		// --> !drawerOpen = false
		// --> Sichtbarkeit des Items ist auch false

		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mMenu);
		menu.findItem(R.id.action_reload).setVisible(!drawerOpen);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * 
	 * die folgenden Funktionen sind notwendig, da eine Veränderung der Display-
	 * Orientierung während dem Download der Daten (also der Ausführung des
	 * AsyncTask) zu einer Exception führt dies geschieht (VEREINFACHT!)
	 * dadurch, dass der AsyncTask versucht,mit dem Objekt (--> der Activity) zu
	 * interagieren, dass ihn erstellt hat. Da Android bei einer Veränderung der
	 * Display-Orientierung allerdings dass Layout neu aufbaut und damit ein
	 * neus Objekt erstellt (--> das alte löscht), versucht der AsyncTask dann,
	 * mit einem Objekt zu interagieren, dass nicht länger existiert, es kommt
	 * zur einer NullPointerException
	 * 
	 */

	// Sperren der Änderung der Displayorientierung
	private void lockScreenOrientation() {
		currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	// Freigeben der Änderung der Displayorientierung
	private void unlockScreenOrientation() {
		setRequestedOrientation(currentOrientation);
	}

	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 
	 * die folgenden Funktionen werden für die Interaktion mit dem Dialog zur
	 * Eingabe der Klasse verwendet.
	 * 
	 * showSearchDialog - ein Objekt für die Darstellung des Dialogs wird
	 * erstellt und in der UI angezeigt
	 * 
	 * onDialogPositiveClick - die Funktion wird durch den Dialog aufgerufen und
	 * enthält die Reaktion auf eine Bestätigung der eingegebenen Daten
	 * 
	 * onDialogNegativeClick - die Funktion wird durch den Dialog aufgerufen und
	 * enthält die Reaktion auf ein Abbrechen des Eingabedialogs
	 * 
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	public void showSearchDialog() {
		DialogClassPicker dialog = new DialogClassPicker();
		FragmentManager fm = getFragmentManager();
		dialog.show(fm, "fragment_edit_name");
	}

	@Override
	public void onDialogPositiveClick(String userInput) {
		this.userInput = userInput;
		addedUser = true;

		// gesondertes Verhalten nach dem ersten Start der App nach der
		// Installation
		if (firstStart) {

			// Anlegen eines Eintrages für die angegebene Klasse in der
			// Datenbank
			db.createUser(new User(1, userInput));

			RefreshHandler handler = new RefreshHandler();
			handler.setContext(this);
			Thread download = new DownloadThread(null, "downloadThread",
					handler, db, this);
			download.start();
		}

		//
		else {
			// die neu eingegebene Klasse wird der Datenbank hinzugefügt
			db.createUser(new User(1, userInput));
			// der Spinner zur Anzeige der gespeicherten Klassen wird
			// aktualisiert
			loadSpinnerData();
			// die Darstellung der Vertretungen für den aktuellen Tag
			// wird geladen
			selectItem(1);
		}

		classChooser.setSelection(classChooser.getCount() - 1);

	}

	@Override
	public void onDialogNegativeClick() {
		// nach der Installation ist es unabdingbar, dass der Nutzer seine
		// Klasse eingibt!
		if (firstStart) {
			showSearchDialog();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		settings.edit()
				.putString("lastClass", userInput)
				.putInt("amountToday", db.getAmountByClass(userInput, true))
				.putInt("amountTomorrow", db.getAmountByClass(userInput, false))
				.putBoolean("isRunning", false).commit();

	}

	protected void onResume() {
		super.onResume();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		settings.edit().putBoolean("isRunning", true).commit();
	}

	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 
	 * die folgenden Funktionen haben keine direkte Funktion bei der Darstellung
	 * der Inhalte, müssen aber für den Ablauf der Activity vorhanden sein!
	 * 
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// erstellt das Layout für das Menu bzw. die Actionbar Aktionen
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
