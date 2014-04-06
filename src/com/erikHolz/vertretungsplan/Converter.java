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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.Environment;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class Converter {

	// Dateiname
	String fileName = "";
	// Dateipfad
	String fileDest = "";

	// Konstruktor setzt Dateinamen / Dateipfad abhängig vom übergebenen
	// Parameter

	public Converter(String filename) {
		fileDest = Environment.getExternalStorageDirectory().getPath() + "/"
				+ filename;
		fileName = filename;
	}

	// Konvertieren der PDF in txt - Format
	// das Codebeispiel stammt von
	// http://svn.code.sf.net/p/itext/code/book/src/part4/chapter15/ExtractPageContentSorted1.java
	// Dateiname der Ausgabe Datei nach dem Schema YYYY MM DD__.txt

	public void parsePDF() throws IOException {

		PdfReader reader = new PdfReader(fileDest + ".pdf");
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		PrintWriter out = new PrintWriter(new FileOutputStream(fileDest
				+ "__.txt"));

		TextExtractionStrategy strategy;
		for (int intI = 1; intI <= reader.getNumberOfPages(); intI++) {
			strategy = parser.processContent(intI,
					new LocationTextExtractionStrategy());
			out.println(strategy.getResultantText());
		}

		out.flush();
		out.close();
		reader.close();

		// löschen der ursprünglichen pdf
		File f = new File(fileDest + ".pdf");
		if (f.exists())
			f.delete();
	}

	// von hier an erfolgt die Reinigung der erstellten txt - Datei von den
	// Steuerzeichen der pdf

	public void cleanTXT() {

		try {
			// signalisiert, ob die gelesene Zeile gespeichert werden soll
			// (false) oder nicht (true)
			boolean boolSkip = false;

			// signalisiert das Ende der zu lesenden (wichtigen) Zeilen
			boolean boolEnd = false;

			// Objekt, um aus der erstellten txt zu lesen
			BufferedReader input = new BufferedReader(new FileReader(fileDest
					+ "__.txt"));

			// Objekt, um in eine neue txt (zwischen) zu speichern
			// Dateiname der Ausgabedatei nach dem Schema YYYY MM DD_.txt
			BufferedWriter output = new BufferedWriter(new FileWriter(new File(
					fileDest + "_.txt"), false));

			// Zwischenspeicher für die gerade eingelesene Zeile
			String line = null;

			// Zwischenspeicher für einzelne Zeichen
			char[] charBuffer = null;

			// zeilenweises Lesen der Datei bis zum Dateiende
			// wobei boolEnd durch das Programm belegt wird und
			// das "natürliche" Ende der Datei input.readLine()
			// null zurückgibt
			while ((line = input.readLine()) != null && !boolEnd) {

				// einige Zählvariablen, werden später benötigt
				Integer intJ = 0;
				Integer intK = 0;
				Integer intL = 0;

				// die eingelesene Zeile wird an den Leerzeichen geteilt
				// und jedes Einzielglied in einen String - Array gespeichert
				// (in der PDF sind die einzelnen Spalten und ihre Bestandteile
				// jeweils durch Leerzeichen getrennt:
				// Klassenstufe / Klasse Fach Raum Herr Lehrer Frau Lehrerin
				String[] splittedLine = line.split(" ");

				// der String line aus dem Schleifenkopf kann hier weiter
				// verwendet werden, da seine Daten gesplitted in
				// splittedLine[] gespeichert sind
				// dies spart den Speicher den ein neuer String benötigen
				// würde
				line = "";

				for (int intI = 0; intI < splittedLine.length; intI++) {
					// in besonderen Fällen steht an erster Steller des
					// ersten Strings (also an Position [0] ein "*"
					// das das Ende relevanter Informationen ankündigt
					charBuffer = splittedLine[0].toCharArray();

					// intI = splittedLine.length lässt den Zähler innerhalb der
					// Zeile in die letzte Spalte springen, um unnötige
					// Überprüfungen der restlichen Felder der Zeile zu umgehen

					// die Signalwörter treten nur im Tiel bzw. in den
					// Überschriften
					// der Spalten auf, die ganze Zeile kann übersprungen werden
					if (splittedLine[intI].equals("Vertretungsplan")) {
						intI = splittedLine.length;
						boolSkip = true;
					} else if (splittedLine[intI].equals("Klasse/Block")) {
						intI = splittedLine.length;
						boolSkip = true;
					} else if (splittedLine[intI].equals("Nachtrag")) {
						intI = splittedLine.length;
						boolSkip = true;
					} else if (splittedLine[intI].equals("Klasse")) {
						intI = splittedLine.length;
						boolSkip = true;
					}

					// die Signalwärter treten nur in der Zeile nach den
					// Vertretungen
					// auf und im folgenden Teil werden Informationen
					// dargestellt,
					// die nicht in der App dargestellt werden sollen
					// --> ab hier kann das speichern in die neue Datei
					// abgebrochen
					// werden
					else if (splittedLine[intI].equals("Aufsicht:")) {
						intI = splittedLine.length;
						boolEnd = true;
					} else if (splittedLine[intI].equals("Aufsichten:")) {
						intI = splittedLine.length;
						boolEnd = true;
					} else if (splittedLine[intI].equals("Aufsicht")) {
						intI = splittedLine.length;
						boolEnd = true;
					} else if (charBuffer[0] == '*') {
						intI = splittedLine.length;
						boolEnd = true;
					}

					// in den Felder 0,1,2 stehen die Daten Klassenstufe, "/"
					// und Klasse, diese können zusammengehörig in line
					// gespeichert werden
					else if (intI <= 2)
						line += splittedLine[intI];

					// alle anderen Felder enthalten die restlichen Daten
					else {

						// Sonderfall, wenn das Feld "Herr" oder "Frau" enthält,
						// da die Anrede und der Name in der entstandenen txt
						// getrennt wurden
						// einserseits sollen "Herr/Frau" und "Name" als eine
						// Einheit gespeichert werden, anderseits, muss der
						// Sonderfall von Doppelnamen wie "Herr Dr. Klose"
						// berücksichtigt werden, so dass nicht davon
						// ausgegangen werden kann das intI und intI + 1
						// den vollständigen Namen repräsentieren
						if (splittedLine[intI].equals("Herr")
								|| splittedLine[intI].equals("Frau")) {

							// zunächst wird ein "_" als Trenner zum vorherigen
							// Feld eingefügt, anschließend der Inhalt des
							// aktuellen Feldes, also "Herr" oder "Frau"
							line += "_" + splittedLine[intI];

							// nun wird der zugehörige Name des Lehrers gesucht
							if (intJ == 0 && intK == 0) {

								// das nächste Feld, in dem "Herr" oder "Frau"
								// steht,
								// wird gesucht (also in der Spalte "Ausfall")
								for (intJ = intI + 1; intJ < splittedLine.length; intJ++)
									if (splittedLine[intJ].equals("Herr")
											|| splittedLine[intJ]
													.equals("Frau"))
										break;

								// dennoch ist es möglich, das dies nicht zu
								// finden ist, da es keine Vertretung, sodnern
								// Ausfall geben wird
								// somit wird gesucht, ob ein Feld mit dem
								// Inhalt "Ausfall" existiert
								for (intK = intI + 1; intK < splittedLine.length; intK++)
									if (splittedLine[intK].equals("Ausfall"))
										break;

								// springen zum nächsten Feld
								intI++;

								// bei dieser Schleife wurde jedes Feld von
								// splittedLines
								// überprüft und jedes mal der jeweilige Zähler
								// erhöht
								// gibt es Vertretung, wurde kein Aufall
								// gefunden, folglich
								// ist der Zähler intJ kleiner als intK
								// gibt es Ausfall, aber keine Vertretung, so
								// ist der Zähler
								// intJ größer als intK

								if (intJ < intK) {
									while (intI < intJ) {
										// es werden nun alle Felder bis zum
										// Signalfeld "Herr"
										// oder "Frau" dem String hinzugefügt
										line += " " + splittedLine[intI];
										intI++;
									}
								}

								else if (intJ > intK) {
									while (intI < intK) {
										// es werden nun alle Felder bis zum
										// Signalfeld "Aufall"
										// dem String hinzugefügt
										line += " " + splittedLine[intI];
										intI++;
									}
								}

								// Rückkehr zum vorherigen Feld, damit die
								// Schleife
								// dies durch intI++ weiter handhaben kann, ohne
								// dass ein Feld übersprungen wird
								intI--;

								// es wurde erneut ein "Herr" oder "Frau"
								// gefunden, da intJ bzw. intK aber nicht mehr
								// 0 sind, muss das vorherige Feld den Ausfall
								// gezeigt haben und dieses aktuelle Feld muss
								// die Vertretung anzeigen
								// da es das (vor)letzte Feld ist, können nun
								// alle übrigen Felder der Zeile hinzugefügt
								// werden
							} else {
								intI++;
								for (intL = intI; intL < splittedLine.length; intL++)
									line += " " + splittedLine[intL];

								// intI wird auf intL, also den letzten Index
								// des Arrays gesetzt, somit ist die Zeile
								// vollständig eingelesen und die
								// Schleifenbedingung nicht mehr erfüllt,
								// das Einlesen der Zeile in den String endet
								// hiermit
								intI = intL;
							}

							// das Feld enthät z.B. den das Fach oder den Raum
							// und kann ohne weitere Anpassung einfach in den
							// String geschrieben werden
						} else
							line += "_" + splittedLine[intI];
					}
				}

				// die eingelesene (verarbeitete) Zeile wird in die txt-Datei
				// geschrieben
				output.write(line);

				// wenn eine Zeile geschrieben wurde die Datei nicht zu Ende ist
				// wird das Steuerzeichen für eine neue Zeile hinzugefügt
				if (boolSkip == false && boolEnd == false) {
					output.newLine();
				} else
					boolSkip = false;
			}

			// schließen der Streams, um Speicher frei zu machen
			input.close();
			output.close();

			// löschen der Zwischenspeicherdatei
			File f = new File(fileDest + "__.txt");
			if (f.exists())
				f.delete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// abschließende Veränderungen an der neu erstellten txt
	// Einträge wie 5/1,2,3 werden in 3 Eitnräge zu "5/1", 5"2" und "5/3"
	// zerlegt, außerdem werden für Einträge wie "12/A" Einträge für "12/1",
	// "12/2" und "12/3" angelegt, da diese nicht eindeutig zuzuordnen sind

	public void finish() {

		try {

			// auch hier werden Objekte zum Lesen der alten und schreiben der
			// neuen (endgültigen) Datei angelegt
			BufferedReader input = new BufferedReader(new FileReader(fileDest
					+ "_.txt"));
			BufferedWriter output = new BufferedWriter(new FileWriter(new File(
					fileDest + ".txt"), false));

			// der Buffer für die eingelesene Zeile
			String line = null;

			// die Schleife in der bis zum Dateiende eingelesen wird
			while ((line = input.readLine()) != null) {
				// Teilung der Zeile am im vorherigen Schritt gesetzten "_"
				String[] splittedLine = line.split("_");

				// Zählvariable
				int intI = 0;

				// line wird weiter als Buffer verwendet, siehe clean
				line = "";

				// Überprüfung, ob eine Zeile mehrere Klassen verkörpert
				// also z.B. "5/1,2,3"
				// ab > 4 Zeichen sind es mehrere Klassen: 5 / 1 , 2
				if (splittedLine[0].length() > 4) {
					// buffer1[0] enthält dann "5"
					// buffer1[1] 1,2,3
					String[] buffer1 = splittedLine[0].split("/");
					// dieser buffer1[1] wird dann an den ","
					// gespaltet
					String[] buffer2 = buffer1[1].split(",");

					// schließlich wird jetzt für jede Klasse eine eigene
					// Zeile hinzugefügt
					for (intI = 0; intI < buffer2.length; intI++) {
						
						for (int i = 0; i < splittedLine.length; i++) 
							if (i == 0)
								line += buffer1[0] + "/" + buffer2[intI] + "_";
							else {
								line += splittedLine[i];
								if (i < splittedLine.length)
									line += "_";
							}
						
						output.write(line);
						output.newLine();
						line = "";
					}
				}

				// Überprüfung, ob der Klassenname Buchstaben entält,
				// z.B. 12/A
				else {
					String[] buffer1 = splittedLine[0].split("/");

					boolean forAllClasses = false;

					if ((buffer1[0].equals("11") || buffer1[0]
							.equals("12"))
							&& (!splittedLine[2].equals("de")
									&& !splittedLine[2]
										.equals("en")))
						forAllClasses = true;

					if (forAllClasses) {
						// intI < 4 bedeutet, dass der Eintrag für 3 Klassen
						// also "12/1", "12/2" und "12/3" angelegt wird
						for (intI = 1; intI < 4; intI++) {
							
							for (int i = 0; i < splittedLine.length; i++) 
								if (i == 0)
									line += buffer1[0] + "/" + intI + "_";
								else {
									line += splittedLine[i];
									if (i < splittedLine.length)
										line += "_";
								}
							
							output.write(line);
							output.newLine();
							line = "";
						}

						// ansonsten wird die Zeile genau wie vorher wieder
						// geschrieben
					} else {
						
						for (int i = 0; i < splittedLine.length; i++) 
								line += splittedLine[i] + "_";
						
						output.write(line);
						output.newLine();
					}
				}

			}

			// schließen der Streams
			input.close();
			output.close();

			// Löschen des vorherigen Zwischenspeichers
			File f = new File(fileDest + "_.txt");
			if (f.exists())
				f.delete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Zusammenfügen des regulären Vertretungsplanes mit dem möglicherweise
	// existierenden Nachtrag

	public void merge() {

		try {
			// der Datei Name YYYY MM DD N wird in seine Einzelteile
			// gespaltet
			String[] filenameSplitted = fileName.split(" ");

			// davon ausgehend muss die Datei des eigentlichen Planes
			// YYYY MM DD ohne das N am Schluss heißen
			String mainFile = filenameSplitted[0] + " " + filenameSplitted[1]
					+ " " + filenameSplitted[2];

			// Erstellung der Objekte zum Lesen und Schreiben der Daten
			// hier gibt es zwei Lesende: für den Nachtrag und den eigentlichen
			// Vertretungsplan
			BufferedReader input = new BufferedReader(new FileReader(
					Environment.getExternalStorageDirectory().getPath() + "/"
							+ mainFile + ".txt"));
			BufferedReader inputN = new BufferedReader(new FileReader(fileDest
					+ ".txt"));

			// _b als kurzer Zwischenspeicher
			BufferedWriter outputB = new BufferedWriter(new FileWriter(
					new File(fileDest + "_b.txt"), true));

			// Buffer für die eingelesene Zeile
			String line = null;

			// Lesen bis zum Datei Ende innerhalb der Schleife
			while ((line = input.readLine()) != null) {
				// Datei wird 1:1 geschrieben
				outputB.write(line);
				outputB.newLine();
			}

			// Lesen bis zum Datei Ende innerhalb der Schleife
			while ((line = inputN.readLine()) != null) {
				// Inhalt wird 1:1 in die selbe Datei dazugeschrieben
				outputB.write(line);
				outputB.newLine();
			}

			// Streams werden geschlossen
			input.close();
			inputN.close();
			outputB.close();

			// die Ursprünglichen Dateien werden gelöscht
			File f1 = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/" + mainFile + ".txt");
			if (f1.exists())
				f1.delete();
			File f2 = new File(fileDest + ".txt");
			if (f2.exists())
				f2.delete();

			// die erstellte Datei YYYY MM DD_b.txt wird umbenannt zu
			// YYYY MM DD.txt
			File oldFile = new File(fileDest + "_b.txt");

			oldFile.renameTo(new File(Environment.getExternalStorageDirectory()
					.getPath() + "/" + mainFile + ".txt"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Befreien des heruntergeladenen Essensplanes von HTML Steuerzeichen

	public void cleanMenu() throws IOException {

		// Schritt 1:

		// Objekte zum Lesen der ursprünglichen HTML Datei und zum Schreiben
		// der ersten txt-Datei
		BufferedReader inStep1 = new BufferedReader(new FileReader(Environment
				.getExternalStorageDirectory().getPath() + "/" + "menu.html"));
		BufferedWriter outStep1 = new BufferedWriter(new FileWriter(new File(
				Environment.getExternalStorageDirectory().getPath() + "/"
						+ "essenBufferStep1.txt"), false));

		// Erstellen der bekannten Buffer wie in den vorhergegangenen Funktionen
		String line = null;
		String[] lineSplitted;
		String[] buffer = null;

		while ((line = inStep1.readLine()) != null) {
			// ce_speiseplan_day_label als Kennzeichner für
			// Container des jeweiligen Tages
			lineSplitted = line.split("ce_speiseplan_day_label");
			// ist der Kennzeichner nicht vorhanden, entspricht
			// lineSplitted[0] line, sie kann also komplett
			// vernachlässigt werden
			if (!(line.equals(lineSplitted[0])))
				for (int intI = 0; intI < lineSplitted.length - 1; intI++) {
					if (intI + 1 == lineSplitted.length) {
						buffer = lineSplitted[intI]
								.split("/Speiseplan - Startseite_files/lift_footer_verlauf.png");
						outStep1.write(buffer[0] + "\n");
					} else
						outStep1.write(lineSplitted[intI + 1] + "\n");
				}
		}

		inStep1.close();
		outStep1.close();

		// Schritt 2:

		BufferedReader inStep2 = new BufferedReader(new FileReader(Environment
				.getExternalStorageDirectory().getPath()
				+ "/"
				+ "essenBufferStep1.txt"));
		BufferedWriter outStep2 = new BufferedWriter(new FileWriter(new File(
				Environment.getExternalStorageDirectory().getPath() + "/"
						+ "essenBufferStep2.txt"), false));

		// Aufteilen in einzelne Zeilen mit ">" als Signalzeichen
		// für Trennung
		while ((line = inStep2.readLine()) != null) {
			lineSplitted = line.split(">");
			for (int intJ = 0; intJ < lineSplitted.length; intJ++)
				outStep2.write(lineSplitted[intJ] + "\n");
		}

		inStep2.close();
		outStep2.close();

		// Schritt 3:

		BufferedReader inStep3 = new BufferedReader(new FileReader(Environment
				.getExternalStorageDirectory().getPath()
				+ "/"
				+ "essenBufferStep2.txt"));
		BufferedWriter outStep3 = new BufferedWriter(new FileWriter(new File(
				Environment.getExternalStorageDirectory().getPath() + "/"
						+ "essenBufferStep3.txt"), false));

		// Zeilen die Bezeichnungen des Essens enthalten, haben am Ende
		// ein "</strong", die folgende Zeile besteht aus "</span"
		while ((line = inStep3.readLine()) != null) {

			// Trennen der Zeile an "</strong"
			lineSplitted = line.split("</strong");

			// entspricht die Zeile nicht </span" so wird
			// sie an diesem als Signalzeichen geteilt
			if (!(line.equals("</span")))
				buffer = line.split("</span");

			// ansonsten wird die Zeile an buffer[0] übergeben
			else
				buffer[0] = line;

			if (!(line.equals(lineSplitted[0])) || !(line.equals(buffer[0])))
				outStep3.write(lineSplitted[0] + "\n");
		}

		inStep3.close();
		outStep3.close();

		// Schritt 4:

		BufferedReader inStep4 = new BufferedReader(new FileReader(Environment
				.getExternalStorageDirectory().getPath()
				+ "/"
				+ "essenBufferStep3.txt"));
		BufferedWriter outStep4 = new BufferedWriter(new FileWriter(new File(
				Environment.getExternalStorageDirectory().getPath() + "/"
						+ "essenBufferStep4.txt"), false));

		while ((line = inStep4.readLine()) != null) {
			lineSplitted = line.split("</span");
			if (!(lineSplitted[0].equals("1")))
				outStep4.write(lineSplitted[0] + "\n");
		}

		inStep4.close();
		outStep4.close();

		// some last layouting to make easier to read

		BufferedReader inStep5 = new BufferedReader(new FileReader(Environment
				.getExternalStorageDirectory().getPath()
				+ "/"
				+ "essenBufferStep4.txt"));
		BufferedWriter outStep5 = new BufferedWriter(new FileWriter(new File(
				Environment.getExternalStorageDirectory().getPath() + "/"
						+ "essenBuffer.txt"), false));

		int intMergeCount = 0;

		while ((line = inStep5.readLine()) != null) {
			if (line.equals("MONTAG")) {
				line = "1";
				intMergeCount = 3;
			}
			if (line.equals("DIENSTAG")) {
				line = "2";
				intMergeCount = 3;
			}
			if (line.equals("MITTWOCH")) {
				line = "3";
				intMergeCount = 3;
			}
			if (line.equals("DONNERSTAG")) {
				line = "4";
				intMergeCount = 3;
			}
			if (line.equals("FREITAG")) {
				line = "5";
				intMergeCount = 3;
			}
			if (line.equals("SAMSTAG")) {
				line = "6";
				intMergeCount = 3;
			}
			if (line.equals("SONNTAG")) {
				line = "7";
				intMergeCount = 3;
			}

			if (intMergeCount > 0) {
				intMergeCount -= 1;
				if (intMergeCount == 0)
					outStep5.write(line + "\n");
				else
					outStep5.write(line + "_");
			}

		}

		inStep5.close();
		outStep5.close();

		// delete buffer files

		File f = null;

		f = new File(Environment.getExternalStorageDirectory().getPath() + "/"
				+ "menu.html");
		if (f.exists())
			f.delete();
		f = new File(Environment.getExternalStorageDirectory().getPath() + "/"
				+ "essenBufferStep1.txt");
		if (f.exists())
			f.delete();
		f = new File(Environment.getExternalStorageDirectory().getPath() + "/"
				+ "essenBufferStep2.txt");
		if (f.exists())
			f.delete();
		f = new File(Environment.getExternalStorageDirectory().getPath() + "/"
				+ "essenBufferStep3.txt");
		if (f.exists())
			f.delete();
		f = new File(Environment.getExternalStorageDirectory().getPath() + "/"
				+ "essenBufferStep4.txt");
		if (f.exists())
			f.delete();

	}

}