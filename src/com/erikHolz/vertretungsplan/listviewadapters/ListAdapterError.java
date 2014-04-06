package com.erikHolz.vertretungsplan.listviewadapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.erikHolz.vertretungsplan.R;

public class ListAdapterError extends ArrayAdapter<String> {
	private final Context context;
	boolean databaseEmpty;

	public ListAdapterError(Context context, boolean databaseEmpty) {
		super(context, R.layout.fragment_list_entry_error, new String[]{"1"});
		this.context = context;
		this.databaseEmpty = databaseEmpty;
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

		if (databaseEmpty) {
		fehler.setText("Keine Daten!");
		hinweis1.setText("Versuchen sie, die Daten zu aktualisieren!");
		hinweis2.setText("Überprüfen sie ihre Internetverbindung!");
		}
		else
			fehler.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
			fehler.setText("Keine Vertretungen!");
			hinweis2.setText("Für den Tag sind keine Vertretungen notwendig!");
			hinweis1.setVisibility(View.GONE);	
		
		
		return entry;
	}
}