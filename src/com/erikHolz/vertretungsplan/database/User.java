package com.erikHolz.vertretungsplan.database;

	
public class User {
	int id;
	String klasse;
	
	public User(int id, String klasse) {
		this.id = id;
		this.klasse = klasse;
	}

	public int getId() {
		return id;
	}


	public String getKlasse() {
		return klasse;
	}

	public void setId(int id) {
		this.id = id;
	}


	public void setKlasse(String klasse) {
		this.klasse = klasse;
	}

	
	
}
