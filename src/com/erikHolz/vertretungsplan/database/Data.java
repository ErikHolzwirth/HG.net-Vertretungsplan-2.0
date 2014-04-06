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

public class Data {
	int id;
	String datum;
	String klasse;
	String stunde;
	String fach;
	String raum;
	String ausfall;
	String vertretung;

	private static final String[] shortNames = { "ma", "ph", "ch", "bi", "as",
			"if", "de", "en", "fr", "la", "ru", "sn", "ge", "sk", "wr", "et",
			"mu", "ku", "sp", "mnt", "wu" };

	private static final String[] longNames = { "Mathematik", "Physik",
			"Chemie", "Biologie", "Astronomie", "Informatik", "Deutsch",
			"Englisch", "Französisch", "Latein", "Russisch", "Spanisch",
			"Geschichte", "Sozialkunde", "Wirtschaft", "Ethik", "Musik",
			"Kunst", "Sport", "MNT", "wahlunterricht" };

	public Data(int id, String datum, String klasse, String stunde,
			String fach, String raum, String ausfall, String vertretung) {
		this.id = id;
		this.datum = datum;
		this.klasse = klasse;
		this.stunde = stunde;
		
		int i = 0;
		while(!(fach.equals(shortNames[i])) && i < 20) 
			i++;
		
		if (!(fach.equals(shortNames[i]))) 
			this.fach = fach;
		else
			this.fach = longNames[i];
				
		this.raum = raum;
		this.ausfall = ausfall;
		this.vertretung = vertretung;
	}

	public int getId() {
		return id;
	}

	public String getDatum() {
		return datum;
	}

	public String getKlasse() {
		return klasse;
	}

	public String getStunde() {
		return stunde;
	}

	public String getFach() {
		return fach;
	}

	public String getRaum() {
		return raum;
	}

	public String getAusfall() {
		String[] ausfallClean = ausfall.split(",");
		return ausfallClean[0];
	}

	public String getVertretung() {
		String[] vertretungClean = vertretung.split(",");
		return vertretungClean[0];
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public void setKlasse(String klasse) {
		this.klasse = klasse;
	}

	public void setStunde(String stunde) {
		this.stunde = stunde;
	}

	public void setFach(String fach) {
		this.fach = fach;
	}

	public void setRaum(String raum) {
		this.raum = raum;
	}

	public void setAusfall(String ausfall) {
		this.ausfall = ausfall;
	}

	public void setVertretung(String vertretung) {
		this.vertretung = vertretung;
	}

}
