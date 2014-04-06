package com.erikHolz.vertretungsplan.listviewadapters;

public class DrawerEntry {
	private int icon;
	private String title;
	private String counter;

	private boolean isGroupHeader = false;

	// Erstellen einer Überschrift
	public DrawerEntry(String title) {
		this(-1, title, null);
		isGroupHeader = true;
	}

	// Erstellen eines Eintrags
	public DrawerEntry(int icon, String title, String counter) {
		super();
		this.icon = icon;
		this.title = title;
		this.counter = counter;
	}

	public int getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public String getCounter() {
		return counter;
	}

	public boolean isGroupHeader() {
		return isGroupHeader;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public void setGroupHeader(boolean isGroupHeader) {
		this.isGroupHeader = isGroupHeader;
	}
	
	
}
