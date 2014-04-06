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
