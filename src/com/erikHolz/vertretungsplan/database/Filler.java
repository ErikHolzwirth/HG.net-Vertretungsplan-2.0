package com.erikHolz.vertretungsplan.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.os.Environment;

public class Filler {

	DatabaseHelper database;
	String file;
	int mode;

	int i;

	// database ist das Datenbank Objekt, in das geschrieben werden soll
	// file erhält den Dateinamen der einzulesenden Datei
	// Die Datenbank beinhaltet 3 Tabellen - für die Vertretungen von bis
	// zu 2 Tagen und den Essensplan, deshalb gibt es auch 3 Modi:
	// 1 - Tag 1, 2 - Tag 2, 3 - Essen
	public Filler(String filename, DatabaseHelper db, int mode) {
		this.database = db;
		this.file = filename;
		this.mode = mode;
	}

	// Funktion, um die Daten aus der txt Datei in die Datenbank zu
	// übertragen
	public void fill() {

		// Tag 1 oder 2
		if (mode == 1 || mode == 2) {
			try {
				BufferedReader input = new BufferedReader(new FileReader(
						Environment.getExternalStorageDirectory().getPath()
								+ "/" + file + ".txt"));

				// Buffer für die eingelesene Zeile
				String line = null;

				// Lesen bis zum Datei Ende innerhalb der Schleife
				while ((line = input.readLine()) != null) {
					String[] splittedLine = line.split("_");
					
					Data dt;
					
					if(splittedLine.length < 6)
						dt = new Data(i, file, splittedLine[0],"", "", "","Achtung", "Fehler möglich");
					else
						dt = new Data(i, file, splittedLine[0],
								splittedLine[1], splittedLine[2], splittedLine[3],
								splittedLine[4], splittedLine[5]);
					
					if (mode == 1)
						database.createData(dt, true);
					else
						database.createData(dt, false);
				}

				// Streams werden geschlossen
				input.close();

				// die Ursprünglichen Dateien werden gelöscht
				File f = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/" + file + ".txt");
				if (f.exists())
					f.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		else if (mode == 3) {
			try {
				BufferedReader input = new BufferedReader(new FileReader(
						Environment.getExternalStorageDirectory().getPath()
								+ "/" + file + ".txt"));

				// Buffer für die eingelesene Zeile
				String line = null;
				String tag = null;

				// Lesen bis zum Datei Ende innerhalb der Schleife
				while ((line = input.readLine()) != null) {
					String[] splittedLine = line.split("_");

					// Wochentag wurde als Zahlenwert übergeben, wird nun
					// als "Wort" gespeichert
					switch (Integer.parseInt(splittedLine[0])) {
					case 1:
						tag = "Montag";
						break;
					case 2:
						tag = "Dienstag";
						break;
					case 3:
						tag = "Mittwoch";
						break;
					case 4:
						tag = "Donnerstag";
						break;
					case 5:
						tag = "Freitag";
						break;
					case 6:
						tag = "Samstag";
						break;
					case 7:
						tag = "Sonntag";
						break;
					default:
						tag = "Wochentag";
						break;
					}

					Menu mn = new Menu(i, tag, splittedLine[1], splittedLine[2]);
					database.createMenu(mn);
				}

				// Streams werden geschlossen
				input.close();

				// die Ursprünglichen Dateien werden gelöscht
				File f = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/" + file + ".txt");
				if (f.exists())
					f.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
