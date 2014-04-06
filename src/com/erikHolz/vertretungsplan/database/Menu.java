package com.erikHolz.vertretungsplan.database;

public class Menu {
	int id;
	String tag;
	String essenA;
	String essenB;
	
	public Menu(int id, String tag, String essenA, String essenB) {
		this.id		= id;
		this.tag	= tag;
		this.essenA	= essenA;
		this.essenB	= essenB;
	}

	public int getId() {
		return id;
	}

	public String getTag() {
		return tag;
	}

	public String getEssenA() {
		return essenA;
	}

	public String getEssenB() {
		return essenB;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setEssenA(String essenA) {
		this.essenA = essenA;
	}

	public void setEssenB(String essenB) {
		this.essenB = essenB;
	}

	
}
