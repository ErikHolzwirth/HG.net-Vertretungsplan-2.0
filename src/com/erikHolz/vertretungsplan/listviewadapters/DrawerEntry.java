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
