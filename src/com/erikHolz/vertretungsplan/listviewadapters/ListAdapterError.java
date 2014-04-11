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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.erikHolz.vertretungsplan.R;

public class ListAdapterError extends ArrayAdapter<String> {
	private final Context context;
	int errorType;

	public ListAdapterError(Context context, int errorType) {
		super(context, R.layout.fragment_list_entry_error, new String[]{"1"});
		this.context = context;
		this.errorType = errorType;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View entry = inflater.inflate(R.layout.fragment_list_entry_error, parent,
				false);

		TextView fehler 	= (TextView) entry.findViewById(R.id.fehler);
		TextView hinweis1 = (TextView) entry.findViewById(R.id.hinweis1);
		TextView hinweis2 = (TextView) entry.findViewById(R.id.hinweis2);

		if (errorType == 1) {
		fehler.setText("Keine Daten!");
		hinweis1.setText("Versuchen sie, die Daten zu aktualisieren!");
		hinweis2.setText("�berpr�fen sie ihre Internetverbindung!");
		}
		
		else if (errorType == 2) {
			fehler.setText("Keine Vertretungen!");
			hinweis2.setText("F�r den Tag sind keine Vertretungen notwendig!");
			hinweis1.setVisibility(View.GONE);	
		}
		
		else if (errorType == 3) {
			fehler.setText("Kein Essensplan!");
			hinweis1.setText("Es existieren keine Daten f�r den Essensplan!");
			hinweis2.setText("Vermutlich wurde die LIFT-Seite nicht aktualisert!");
		}
		
		
		return entry;
	}
}