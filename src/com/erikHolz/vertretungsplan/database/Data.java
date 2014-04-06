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
