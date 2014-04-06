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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.erikHolz.vertretungsplan.R;
import com.erikHolz.vertretungsplan.database.Menu;

public class ListAdapterMenu extends ArrayAdapter<String> {
	private final Context context;
	private final List<Menu> menu;

	public ListAdapterMenu(Context context, String[] day, int mode,
			List<Menu> menu) {
		super(context, R.layout.fragment_list_entry_menu, day);
		this.context = context;
		this.menu = menu;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View entry = inflater.inflate(R.layout.fragment_list_entry_menu, parent,
				false);

		TextView tag 	= (TextView) entry.findViewById(R.id.tag);
		TextView essenA = (TextView) entry.findViewById(R.id.essenA);
		TextView essenB = (TextView) entry.findViewById(R.id.essenB);

		tag.setText(menu.get(position).getTag());
		essenA.setText(menu.get(position).getEssenA());
		essenB.setText(menu.get(position).getEssenB());

		return entry;
	}
}