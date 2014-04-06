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

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.ListView;

import com.erikHolz.vertretungsplan.DialogClassPicker.DialogListener;
import com.erikHolz.vertretungsplan.database.DatabaseHelper;
import com.erikHolz.vertretungsplan.database.User;
import com.erikHolz.vertretungsplan.listviewadapters.ListAdapterUserManagement;
import com.erikHolz.vertretungsplan.listviewadapters.ListAdapterUserManagement.ListAdapterUserManagementListener;

public class ManageUsersActivity extends Activity implements
		ListAdapterUserManagementListener, DialogListener {

	DatabaseHelper db;
	ListAdapterUserManagement adapter;
	ListView list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Laden des Layouts
		setContentView(R.layout.activity_manage_users);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		list = (ListView) findViewById(R.id.listview_users);

		db = new DatabaseHelper(getApplicationContext());

		onDatabaseEdited();

	}

	@Override
	public void onAllUsersDeleted() {
		showSearchDialog();
	}
	
	public void showSearchDialog() {
		DialogClassPicker dialog = new DialogClassPicker();
		FragmentManager fm = getFragmentManager();
		dialog.show(fm, "fragment_edit_name");
	}

	@Override
	public void onDialogPositiveClick(String userInput) {
		db.createUser(new User(1, userInput));
		adapter.notifyDataSetInvalidated();
		
		onDatabaseEdited();

	}

	@Override
	public void onDialogNegativeClick() {
		showSearchDialog();
	}
	
	public void onDatabaseEdited() {

		String[] users = new String[db.getUserAmount()];

		for (int i = 0; i < users.length; i++)
			users[i] = "";
		
		adapter = new ListAdapterUserManagement(this,
				users, db.getAllUsers(), db);

		list.setAdapter(adapter);
	}

}
