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
