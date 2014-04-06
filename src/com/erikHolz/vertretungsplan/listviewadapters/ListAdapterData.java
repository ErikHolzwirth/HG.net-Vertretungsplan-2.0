package com.erikHolz.vertretungsplan.listviewadapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.erikHolz.vertretungsplan.R;
import com.erikHolz.vertretungsplan.database.Data;

public class ListAdapterData extends ArrayAdapter<String> {
	private final Context context;
	private final String[] lesson;
	private final List<Data> data;

	public ListAdapterData(Context context, String[] lesson,
			List<Data> data) {
		super(context, R.layout.fragment_list_entry_data, lesson);
		this.context = context;
		this.lesson = lesson;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View entry = inflater.inflate(R.layout.fragment_list_entry_data, parent,
				false);

		TextView stunde = (TextView) entry.findViewById(R.id.stunde);
		TextView zeit = (TextView) entry.findViewById(R.id.zeit);
		TextView raum_fach = (TextView) entry.findViewById(R.id.raum_fach);
		TextView ausfall = (TextView) entry.findViewById(R.id.ausfall);
		TextView vertretung = (TextView) entry.findViewById(R.id.vertretung);

		stunde.setText(lesson[position] + ". Stunde");
		
		switch (Integer.parseInt(data.get(position).getStunde())) {
		case 1:
			zeit.setText("8:00 - 8:45 Uhr");
			break;
		case 2:
			zeit.setText("8:45 - 9:30 Uhr");
			break;
		case 3:
			zeit.setText("9:50 - 10:35 Uhr");
			break;
		case 4:
			zeit.setText("10:45 - 11:30 Uhr");
			break;
		case 5:
			zeit.setText("11:40 - 12:25 Uhr");
			break;
		case 6:
			zeit.setText("13:00 - 13:45 Uhr");
			break;
		case 7:
			zeit.setText("13:50 - 14:35 Uhr");
			break;
		case 8:
			zeit.setText("14:35 - 15:20 Uhr");
			break;
		default: 
			zeit.setText("00:00 - 00:00 Uhr");
			break;
		}
		
		raum_fach.setText("Raum " + data.get(position).getRaum() + " "
				+ data.get(position).getFach());
		ausfall.setText(data.get(position).getAusfall());
		vertretung.setText(data.get(position).getVertretung());
		return entry;
	}
}