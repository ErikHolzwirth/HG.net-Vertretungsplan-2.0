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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erikHolz.vertretungsplan.R;
import com.erikHolz.vertretungsplan.database.DatabaseHelper;
import com.erikHolz.vertretungsplan.database.User;

public class ListAdapterUserManagement extends ArrayAdapter<String> {
	private final Context context;
	private final List<User> user;
	private final ListAdapterUserManagementListener listener;
	
	private DatabaseHelper db;

	public ListAdapterUserManagement(Context context, String[] users,
			List<User> user, DatabaseHelper db) {
		super(context, R.layout.usermanagement_list_item, users);
		this.context = context;
		this.user = user;
		this.listener = (ListAdapterUserManagementListener) context;
		this.db = db;
	}

	public interface ListAdapterUserManagementListener {
		public void onAllUsersDeleted();
		public void onDatabaseEdited();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View item = inflater.inflate(R.layout.usermanagement_list_item, parent,
				false);

		TextView tv = (TextView) item.findViewById(R.id.user_name);
		tv.setText(user.get(position).getKlasse());

		ImageView delete = (ImageView) item.findViewById(R.id.user_delete);

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				db.deleteUser(user.get(position).getKlasse());
				if (db.getUserAmount() == 0)
					listener.onAllUsersDeleted();
				else
					listener.onDatabaseEdited();

			}
		});

		return item;
	}

	/**
	 * 
	 * die Eintr�ge sollen nicht anklickbar sein, sondern nur die Buttons,
	 * deshalb folgende Funktionen
	 * 
	 */

	public boolean areAllItemsEnabled() {
		return false;
	}

	public boolean isEnabled(int position) {
		return false;
	}

}