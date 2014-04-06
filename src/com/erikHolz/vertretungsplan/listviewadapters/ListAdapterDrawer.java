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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erikHolz.vertretungsplan.R;

public class ListAdapterDrawer extends ArrayAdapter<DrawerEntry> {
	private final Context context;
	private final ArrayList<DrawerEntry> drawerEntries;
	int idActive;
	int counter1;
	int counter2;

	public ListAdapterDrawer(Context context,
			ArrayList<DrawerEntry> mDrawerEntries) {
		super(context, R.layout.drawer_list_item, mDrawerEntries);
		this.context = context;
		this.drawerEntries = mDrawerEntries;
		idActive = 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View entry = null;

		if (drawerEntries.get(position).isGroupHeader()) {
			entry = inflater
					.inflate(R.layout.drawer_list_header, parent, false);

			TextView ueberschrift = (TextView) entry
					.findViewById(R.id.drawer_header);
			ueberschrift.setText(drawerEntries.get(position).getTitle());
		}

		else {
			entry = inflater.inflate(R.layout.drawer_list_item, parent, false);

			TextView eintrag = (TextView) entry.findViewById(R.id.drawer_title);
			eintrag.setText(drawerEntries.get(position).getTitle());

			TextView zaehler = (TextView) entry
					.findViewById(R.id.drawer_counter);

			if (!(drawerEntries.get(position).getCounter().equals("")))
				zaehler.setText(drawerEntries.get(position).getCounter());
			else
				zaehler.setVisibility(View.GONE);

			ImageView icon = (ImageView) entry.findViewById(R.id.drawer_icon);

			if (position == idActive) {
				if (position == 1 || position == 2)
					icon.setImageResource(R.drawable.ic_drawer_vp_selected);
				else if (position == 3)
					icon.setImageResource(R.drawable.ic_drawer_essen_selected);
				else if (position == 5)
					icon.setImageResource(R.drawable.ic_drawer_einstellungen_selected);
				else if (position == 6 || position == 7)
					icon.setImageResource(drawerEntries.get(position).getIcon());
			} else
				icon.setImageResource(drawerEntries.get(position).getIcon());

		}

		return entry;
	}

	public void setActive(int position) {
		if (position == 1 || position == 2 || position == 3 || position == 5)
			idActive = position;

	}

	/**
	 * 
	 * die Überschriften sollen nicht anklickbar sein, deshalb folgende
	 * Funktionen
	 * 
	 */

	public boolean areAllItemsEnabled() {
		return false;
	}

	public boolean isEnabled(int position) {
		if (position == 0 || position == 4)
			return false;

		else
			return true;
	}

}