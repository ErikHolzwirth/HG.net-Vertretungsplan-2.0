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

public class Constants {
	
	public String getMonthAsString(int month) {
		switch (month) {
		case 1:
			return "Januar ";
		case 2:
			return "Februar ";
		case 3:
			return "März ";
		case 4:
			return"April ";
		case 5:
			return"Mai ";
		case 6:
			return"Juni ";
		case 7:
			return"Juli ";
		case 8:
			return"August ";
		case 9:
			return"September ";
		case 10:
			return"Oktober ";
		case 11:
			return"November ";
		case 12:
			return"Dezember ";
		default:
			return" Monat";
		}
	}
	
	public String getMonthAsString(String month) {
		switch (Integer.parseInt(month)) {
		case 1:
			return "Januar ";
		case 2:
			return "Februar ";
		case 3:
			return "März ";
		case 4:
			return"April ";
		case 5:
			return"Mai ";
		case 6:
			return"Juni ";
		case 7:
			return"Juli ";
		case 8:
			return"August ";
		case 9:
			return"September ";
		case 10:
			return"Oktober ";
		case 11:
			return"November ";
		case 12:
			return"Dezember ";
		default:
			return" Monat";
		}
	}
}
