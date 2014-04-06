package com.erikHolz.vertretungsplan;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.erikHolz.vertretungsplan.database.DatabaseHelper;
import com.erikHolz.vertretungsplan.database.Filler;

public class DownloadTask {

	private static final int PROGRESS = 1;

	String update;

	String[] validLinkname;
	String[] fileDestination;

	boolean fileExistsToday;
	boolean fileExistsTomorrow;
	boolean fileExistsMenu;

	int progress;

	DatabaseHelper db;

	RefreshHandler handler;

	// wird 3 mal im Code verwendet, beim ersten Mal erhält es die
	// Anzahl der gefundenen Dateien auf dem Server, beim zweiten
	// Mal die Anzahl der tatsächlich heruntergeladenen Dateien
	// und beim dritten Mal die Anzahl der erfolgreich konvertierten
	// Dateien
	// dies ist vorallem für die Ausgabe des prozentualen Fortschritts
	// wichtig
	int fileAmount;

	public DownloadTask(DatabaseHelper db) {
		super();
		this.db = db;
		this.handler = null;

		db.clearDatabaseData(true);
		db.clearDatabaseData(false);
		db.clearDatabaseMenu();

		progress = 0;
	}

	public DownloadTask(DatabaseHelper db, RefreshHandler handler) {
		super();
		this.db = db;
		this.handler = handler;

		db.clearDatabaseData(true);
		db.clearDatabaseData(false);
		db.clearDatabaseMenu();

		progress = 0;
	}

	private void setProgress(int value) {
		progress = value;
		incrementProgressBy(0);
	}
	
	private void incrementProgressBy(float value) {
		incrementProgressBy(Math.round(value));
	}

	private void incrementProgressBy(int value) {
		progress += value;

		if (handler != null) {
			handler.setProgress(progress);
			Message msg = handler.obtainMessage(PROGRESS);
			handler.sendMessage(msg);			
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Einzelnfuktionen aus Gründen der Übersicht nicht direkt
	// bei doInBackground():
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Funktion zur Erstellung möglicher Dateinamen YYYY MM DD für
	// den aktuellen und die nächsten 16 Tage
	// (ermöglicht das Abrufen der Vertretung für Montag nach 2 wöchigen
	// Ferien am Freitag)
	// es wird überprüft, ob der erstellte Link auf einen tatsächlich
	// existierenden Vertretungsplan verweist, falls ja, wird der Dateiname
	// gespeichert
	public void createLinknames() {

		// Zähler für die Benennung des gefundenen Vertretungsplanes
		int j = 0;
		// Zähler für die Tage die nach dem Fund des "aktuellen"
		// Vertretungsplanes weiter durchsucht werden soll
		// z.B. für den Fall, dass am Freitag einer gefunden wurde:
		// 2 - Freitag gefunden
		// Samstag & Sonntag werden übersprungen
		// 5 - Montag gefunden?

		int k = 0;

		String possibleLinkname;
		validLinkname = new String[4];

		Calendar date = new GregorianCalendar();
		Calendar.getInstance();
		date.setTime(new Date());

		incrementProgressBy(1);

		// speichern der Uhrzeit, zu der das Laden der Daten erfolgt
		if (date.get(Calendar.MINUTE) < 10)
			update = String.valueOf(date.get(Calendar.HOUR_OF_DAY)) + ":0"
					+ String.valueOf(date.get(Calendar.MINUTE));
		else
			update = String.valueOf(date.get(Calendar.HOUR_OF_DAY)) + ":"
					+ String.valueOf(date.get(Calendar.MINUTE));

		incrementProgressBy(2);

		// das Datum an Stelle i auswerten und in den String
		// possibleLinkname an Stelle i eintragen
		for (int i = 0; i < 17; i++) {

			if (k > 2) {
				validLinkname[2] = "";
				validLinkname[3] = "";
				break;
			}

			possibleLinkname = "";

			// Samstag & Sonntag wird es keine Vertretung geben müssen ;)
			if (!(date.get(Calendar.DAY_OF_WEEK) == 1)
					&& !(date.get(Calendar.DAY_OF_WEEK) == 7)) {

				// Hinzufügen des Jahres
				possibleLinkname += date.get(Calendar.YEAR);

				// Hinzufügen des Monats, wobei Januar den Wert 0
				// hat, weswegen 1 zu jedem Monatswert addiert
				// werden muss

				// einstellige Monate (also Januar bis September)
				// erhalten im Dateinamen eine zusätzliche 0
				// durch %200 statt %20, wobei %20 ein Leerzeichen
				// im Dateinamen darstellt, welches bei Hyperlinks
				// sonst nicht erlaubt ist
				if (date.get(Calendar.MONTH) + 1 < 10)
					possibleLinkname += "%200" + (date.get(Calendar.MONTH) + 1);
				else
					possibleLinkname += "%20" + (date.get(Calendar.MONTH) + 1);

				// Hinzufügen des Tages

				// selbes Verhalten bezüglich des Leerzeichens wie
				// bei den Monaten
				if (date.get(Calendar.DATE) < 10)
					possibleLinkname += "%200" + date.get(Calendar.DATE);
				else
					possibleLinkname += "%20" + date.get(Calendar.DATE);

				try {
					// mögliche URL zum Vertretungsplan des Tages
					// Linkname nach dem Schema YYYY%20MM%20DD
					URL checkURL = new URL(
							"http://humgym.net/vertretungsplan.html?file=tl_files/Vertretungsplaene/"
									+ possibleLinkname + ".pdf");

					try {
						HttpURLConnection urlConnection = (HttpURLConnection) checkURL
								.openConnection();

						// Verbindung kann hergestellt werden, der
						// Vertretungsplan existiert also
						if (urlConnection.getContentType().equalsIgnoreCase(
								"application/pdf")) {
							validLinkname[j] = possibleLinkname;
							j++;
							k++;
							fileAmount++;

							// mögliche URL zum eventuell vorhandenen Nachtrag
							// des
							// Tages
							// Linkname nach dem Schema YYYY%20MM%20DD%20N
							URL checkURLN = new URL(
									"http://humgym.net/vertretungsplan.html?file=tl_files/Vertretungsplaene/"
											+ possibleLinkname + "%20N.pdf");

							HttpURLConnection urlConnectionN = (HttpURLConnection) checkURLN
									.openConnection();

							// nun wird überprüft, ob für das Datum auch ein
							// Nachtrag besteht und falls ja, also die
							// Verbindung wieder hergestellt werden kann, ´
							// wird auch dieser Link gespeichert
							if (urlConnectionN.getContentType()
									.equalsIgnoreCase("application/pdf")) {
								validLinkname[j] = possibleLinkname + "%20N";
								fileAmount++;
							} else
								validLinkname[j] = "";

							j++;
						}

						// es wurde bereits ein Plan für den aktuellen Tag
						// gefunden
						// --> k = 1 > 0, nun beginnt der Zähler für die zu
						// durchsuchenden Folgetage
						if (k > 0)
							k++;

						// wenn j > 2 ist, wurde mindestens für den Folgetag,
						// vielleicht sogar für den Nachtrag des Folgetages
						// vergeben
						if (j > 2)
							break;
					} catch (NullPointerException e) {
						// LogCat Eintrag anlegen
						Log.e("DataDownloader",
								"Fehler beim Zugriff auf moegliche URL");
					}
				} catch (Exception e) {
					// LogCat Eintrag anlegen
					Log.e("DataDownloader",
							"Fehler beim Erstellen des URL Objektes");
				}
			}

			// Datum um 1 erhöhen
			date.add(Calendar.DAY_OF_YEAR, 1);

			incrementProgressBy(1);
		}
		
		setProgress(20);
	}

	public void downloadFiles() {

		for (int i = 0; i < 4; i++)
			if (!(validLinkname[i].matches("")))
				fileAmount++;

		// Eingangsbedingung, ob wenigstens eine Datei besteht, um Speicher
		// zu sparen
		if (fileAmount > 0) {

			// speichert die Dateinamen der heruntergeladenen PDFs
			// die Namensgebung ist hierbei YYYY MM DD
			fileDestination = new String[4];

			try {
				for (int i = 0; i < 4; i++) {
					// initialisiert die Strings
					fileDestination[i] = "";

					// Überprüfung, ob der Linkname nicht "leer" ist
					if (!(validLinkname[i].equals(""))) {

						// Speichern der URL, an der sich der Vertretungsplan
						// befindet
						URL url = new URL(
								"http://humgym.net/vertretungsplan.html?file=tl_files/Vertretungsplaene/"
										+ validLinkname[i] + ".pdf");

						URLConnection connection = url.openConnection();
						connection.connect();

						// InputStream als Eingang der Daten von der URl
						// OutputStream als Ausgang der Daten in die PDF Datei
						// auf dem Handy
						InputStream input = new BufferedInputStream(
								url.openStream());
						OutputStream output = new FileOutputStream(new File(
								Environment.getExternalStorageDirectory()
										.getPath(), validLinkname[i].replace(
										"%20", " ") + ".pdf"));

						// Herunterladen der Daten Byte für Byte und schreiben
						// in die neue Datei
						byte data[] = new byte[1024];
						int count;

						while ((count = input.read(data)) != -1) {
							output.write(data, 0, count);
						}

						// die Objekte für Ein- und Ausgang der Daten werden
						// geschlossen um Speicher zu sparen
						output.flush();
						output.close();
						input.close();

						// Speichern des Dateinamens
						fileDestination[i] = validLinkname[i].replace("%20",
								" ");
						
						incrementProgressBy((16 / fileAmount));

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		setProgress(36);
		
	}

	public void downloadMenu() {
		try {
			// open connection to URL
			URL url = new URL(
					"http://www.lift-nordhausen.de/index.php/speiseplan.html");

			int count;
			byte data[] = new byte[1024];

			// siehe downloadData()
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(new File(Environment
					.getExternalStorageDirectory().getPath(), "menu.html"));

			while ((count = input.read(data)) != -1) {
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setProgress(40);
	}

	public void convertPDF() {
		boolean isN = false;

		fileAmount = 0;

		for (int i = 0; i < 4; i++)
			if (!(fileDestination[i].matches("")))
				fileAmount++;

		for (int i = 0; i < 4; i++) {
			if (!(fileDestination[i].matches(""))) {

				if (i == 0)
					fileExistsToday = true;
				else if (i == 2)
					fileExistsTomorrow = true;

				// Dateiname wird am " " Zeichen geteilt, um Überprüfung
				// auf Nachtrag / oder normalen Plan im nächsten Schritt
				// zu ermöglichen
				String[] splittedLine = fileDestination[i].split(" ");

				// Überprüfung, ob die momentan verarbeitete Datei ein
				// Nachtrag ist
				// 2013 03 17 N --> 4 teiliger Dateiname
				if (splittedLine.length > 3)
					isN = true;

				// Erstellen des Converters
				Converter converter = new Converter(fileDestination[i]);

				try {
					// Umwandeln der PDF in txt
					converter.parsePDF();
					incrementProgressBy(1 / fileAmount * 12);

					// PDF Steuerzeichen entfernen
					converter.cleanTXT();
					incrementProgressBy(1 / fileAmount * 4);

					// letzte Anpassung an der Datenstruktur innerhalb der
					// txt Datei
					converter.finish();
					incrementProgressBy(1 / fileAmount * 4);

					// ist die Datei ein Nachtrag, so werden ihre Daten an
					// die Hauptddatei gehangen
					// also Daten aus YYYY MM DD N werden in YYYY MM DD
					// kopiert
					if (isN) {
						converter.merge();
						isN = false;

					}
					incrementProgressBy(1 / fileAmount * 4);


				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		setProgress(66);
	}

	public void convertMenu() {
		// erstellen des Converters für den Essensplan
		Converter converter = new Converter("-->menu<--");

		try {
			// Entfernen der HTML Steuerzeichen
			converter.cleanMenu();
			fileExistsMenu = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		setProgress(70);
	}

	public void insertData() {
		if (fileExistsToday) {
			Filler filler = new Filler(fileDestination[0], db, 1);
			filler.fill();
			
			if (!fileExistsTomorrow) 
				incrementProgressBy(24);
			else
				incrementProgressBy(12);
		}

		if (fileExistsTomorrow) {
			Filler filler = new Filler(fileDestination[2], db, 2);
			filler.fill();
			
			incrementProgressBy(12);
		}
	}

	public void insertMenu() {
		// Dateiname für den Essensplan ist unabhängig vom Datum
		if (fileExistsMenu) {
			Filler filler = new Filler("essenBuffer", db, 3);
			filler.fill();
			
			incrementProgressBy(6);

		}
		
		setProgress(100);
	}

}
