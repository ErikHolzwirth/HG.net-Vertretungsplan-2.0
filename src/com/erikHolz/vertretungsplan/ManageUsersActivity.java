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
