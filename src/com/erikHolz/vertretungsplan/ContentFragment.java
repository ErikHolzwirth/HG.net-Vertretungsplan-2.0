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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.erikHolz.vertretungsplan.database.Data;
import com.erikHolz.vertretungsplan.database.DatabaseHelper;
import com.erikHolz.vertretungsplan.database.Menu;
import com.erikHolz.vertretungsplan.listviewadapters.ListAdapterData;
import com.erikHolz.vertretungsplan.listviewadapters.ListAdapterError;
import com.erikHolz.vertretungsplan.listviewadapters.ListAdapterMenu;

public class ContentFragment extends Fragment {
	public static final String ARG_MODE = "MODE";
	public static final String ARG_DATE_TODAY = "DATE_TODAY";
	public static final String ARG_DATE_TOMORROW = "DATE_TOMORROW";
	public static final String ARG_USERINPUT = "CLASS";

	String date;

	DatabaseHelper db;
	
	public void setDatabase(DatabaseHelper db) {
		this.db = db;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_content, null);
			
		// erstellen des Fragments zur Darstellung von Vertretungen, handelt es
		// sich um einen Vertretungsplan für den aktuellen Tag (ARG_MODE = 1)
		// oder den kommenden Tag (ARG_MODE = 2)
		if (getArguments().getInt(ARG_MODE) < 3) {
			List<Data> data;

			// Auslesen der Einträge für die entsprechende Klasse
			if (getArguments().getInt(ARG_MODE) == 1) {
				data = db.getAllDataByClass(
						getArguments().getString(ARG_USERINPUT), true);
				date = getArguments().getString(ARG_DATE_TODAY);
			} else {
				data = db.getAllDataByClass(
						getArguments().getString(ARG_USERINPUT), false);
				date = getArguments().getString(ARG_DATE_TOMORROW);
			}

			String[] amount = new String[data.size()];

			for (int i = 0; i < data.size(); i++)
				amount[i] = data.get(i).getStunde();

			// erstellen des Adapters, der die Liste mit den Daten aus der
			// Datenbank befüllt
			ArrayAdapter<?> adapter = null;

			// sind Einträge vorhanden, so wird der "normale" Adapter für den
			// Vertretungsplan verwendet
			if (amount.length > 0)
				adapter = new ListAdapterData(getActivity().getBaseContext(),
						amount, data);

			// falls nicht, wird ein gesonderter Adapter verwendet
			else if (getArguments().getInt(ARG_MODE) == 1) {
				if (db.isEmpty(true))
					adapter = new ListAdapterError(getActivity()
							.getBaseContext(), true);
				else
					adapter = new ListAdapterError(getActivity()
							.getBaseContext(), false);
			} else {
				if (db.isEmpty(false))
					adapter = new ListAdapterError(getActivity()
							.getBaseContext(), true);
				else
					adapter = new ListAdapterError(getActivity()
							.getBaseContext(), false);
			}

			// das Listenelement wird im Layout des Fragments gesucht
			ListView listView = (ListView) rootView.findViewById(R.id.listview_content);
			// der oben erstellte Adapter wird der Liste zugewiesen
			listView.setAdapter(adapter);

			// Verändern des Apptitels entsprechend dem Datum
			ActionBar bar = getActivity().getActionBar();

			bar.setTitle("Vertretungsplan");
			bar.setSubtitle(date);
		}

		// erstellen des Fragments zur Darstellung des Essensplans
		// (ARG_MODE = 3)
		else if (getArguments().getInt(ARG_MODE) == 3) {
			List<Menu> menu;
			menu = db.getAllMenus();

			Calendar cal = new GregorianCalendar();
			Calendar.getInstance();
			cal.setTime(new Date());

			// Datum auf Montag (erster Tag der Woche) setzen
			
			while(cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				if (cal.get(Calendar.DAY_OF_WEEK) < 2)
					cal.add(Calendar.DATE, 1);
				else if (cal.get(Calendar.DAY_OF_WEEK) > 2)
					cal.add(Calendar.DATE, -1);
			}

			String buffer = "für den " + String.valueOf(cal.get(Calendar.DATE))
					+ "." + String.valueOf((cal.get(Calendar.MONTH)) + 1) + ". - ";

			// Datum auf Sonntag (letzter Tag der Woche) setzen
			cal.add(Calendar.DATE, 6);

			buffer += String.valueOf(cal.get(Calendar.DATE)) + "."
					+ String.valueOf((cal.get(Calendar.MONTH)) + 1) + "."
					+ String.valueOf(cal.get(Calendar.YEAR));

			ActionBar bar = getActivity().getActionBar();

			bar.setTitle("Essensplan");
			bar.setSubtitle(buffer);

			String[] amount = new String[menu.size()];

			for (int i = 0; i < menu.size(); i++)
				amount[i] = menu.get(i).getTag();

			ArrayAdapter<?> adapter;

			if (amount.length > 0)
				adapter = new ListAdapterMenu(getActivity().getBaseContext(),
						amount, getArguments().getInt(ARG_MODE), menu);
			else
				adapter = new ListAdapterError(getActivity().getBaseContext(),
						true);

			ListView listView = (ListView) rootView.findViewById(R.id.listview_content);
			listView.setAdapter(adapter);

		}

		return rootView;
	}
}
